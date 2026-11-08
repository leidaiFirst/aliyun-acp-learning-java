package com.aliyun.acp.common;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;

/** 与原 Python OpenAI 客户端使用同一百炼 OpenAI 兼容接口。 */
public final class QwenClient {
    public record Message(String role, String content) {}
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build();
    private final String key = Config.require("DASHSCOPE_API_KEY");
    private final String baseUrl = Config.get("DASHSCOPE_BASE_URL") == null
            ? "https://dashscope.aliyuncs.com/compatible-mode/v1"
            : Config.get("DASHSCOPE_BASE_URL");

    public static Message system(String content) { return new Message("system", content); }
    public static Message user(String content) { return new Message("user", content); }
    public static Message assistant(String content) { return new Message("assistant", content); }

    private ObjectNode requestBody(String model, List<Message> messages, boolean stream, Double temperature, Double topP, boolean search) {
        ObjectNode body = mapper.createObjectNode();
        body.put("model", model);
        body.put("stream", stream);
        ArrayNode array = body.putArray("messages");
        for (Message message : messages) {
            ObjectNode item = array.addObject();
            item.put("role", message.role());
            item.put("content", message.content());
        }
        if (temperature != null) body.put("temperature", temperature);
        if (topP != null) body.put("top_p", topP);
        if (search) body.put("enable_search", true);
        return body;
    }

    private HttpRequest request(ObjectNode body) {
        return HttpRequest.newBuilder(URI.create(baseUrl.replaceAll("/+$", "") + "/chat/completions"))
                .timeout(Duration.ofMinutes(3))
                .header("Authorization", "Bearer " + key)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString(), StandardCharsets.UTF_8))
                .build();
    }

    public String chat(String model, List<Message> messages) throws IOException, InterruptedException {
        return chat(model, messages, null, null, false);
    }

    public String chat(String model, List<Message> messages, Double temperature, Double topP, boolean search) throws IOException, InterruptedException {
        JsonNode json = chatRaw(requestBody(model, messages, false, temperature, topP, search));
        return json.path("choices").path(0).path("message").path("content").asText();
    }

    public JsonNode chatRaw(ObjectNode body) throws IOException, InterruptedException {
        HttpResponse<String> response = http.send(request(body),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() / 100 != 2) throw new IOException("百炼 HTTP " + response.statusCode() + ": " + response.body());
        return mapper.readTree(response.body());
    }

    /** 逐个 SSE 片段返回；保留 reasoning_content 和 usage，供对应 cell 使用。 */
    public void stream(String model, List<Message> messages, Double temperature, Double topP, Consumer<JsonNode> onChunk) throws IOException, InterruptedException {
        HttpResponse<java.io.InputStream> response = http.send(request(requestBody(model, messages, true, temperature, topP, false)),
                HttpResponse.BodyHandlers.ofInputStream());
        if (response.statusCode() / 100 != 2) {
            try (var stream = response.body()) {
                throw new IOException("百炼 HTTP " + response.statusCode() + ": " + new String(stream.readAllBytes(), StandardCharsets.UTF_8));
            }
        }
        try (var reader = new BufferedReader(new InputStreamReader(response.body(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.startsWith("data: ")) continue;
                String data = line.substring(6);
                if (data.equals("[DONE]")) break;
                onChunk.accept(mapper.readTree(data));
            }
        }
    }

    public void printStream(String model, List<Message> messages, Double temperature, Double topP) throws IOException, InterruptedException {
        stream(model, messages, temperature, topP, chunk -> {
            JsonNode content = chunk.path("choices").path(0).path("delta").path("content");
            if (!content.isMissingNode() && !content.isNull()) System.out.print(content.asText());
        });
    }
}
