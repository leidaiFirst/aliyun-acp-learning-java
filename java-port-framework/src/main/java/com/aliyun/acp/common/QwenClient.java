package com.aliyun.acp.common;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.PartialThinking;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.Consumer;

/** 百炼 OpenAI 兼容端点的 LangChain4j 适配层；不自行组装 HTTP 或解析 SSE。 */
public final class QwenClient {
    public record Message(String role, String content) {}

    private final String key = Config.require("DASHSCOPE_API_KEY");
    private final String baseUrl = Config.get("DASHSCOPE_BASE_URL") == null
            ? "https://dashscope.aliyuncs.com/compatible-mode/v1"
            : Config.get("DASHSCOPE_BASE_URL");

    public static Message system(String content) { return new Message("system", content); }
    public static Message user(String content) { return new Message("user", content); }
    public static Message assistant(String content) { return new Message("assistant", content); }

    private List<ChatMessage> asChatMessages(List<Message> messages) {
        List<ChatMessage> result = new ArrayList<>();
        for (Message message : messages) {
            result.add(switch (message.role()) {
                case "system" -> SystemMessage.from(message.content());
                case "user" -> UserMessage.from(message.content());
                case "assistant" -> AiMessage.from(message.content());
                default -> throw new IllegalArgumentException("未知角色: " + message.role());
            });
        }
        return result;
    }

    public String chat(String model, List<Message> messages) {
        return chat(model, messages, null, null, false);
    }

    public String chat(String model, List<Message> messages, Double temperature, Double topP, boolean search) {
        var builder = OpenAiChatModel.builder()
                .apiKey(key).baseUrl(baseUrl).modelName(model);
        if (temperature != null) builder.temperature(temperature);
        if (topP != null) builder.topP(topP);
        if (search) builder.customParameters(Map.of("enable_search", true));
        return builder.build().chat(asChatMessages(messages)).aiMessage().text();
    }

    /** 等待完整流结束，分别回调回答与思考片段。 */
    public void stream(String model, List<Message> messages, Double temperature, Double topP,
                       Consumer<String> onText, Consumer<String> onThinking) {
        stream(model, messages, temperature, topP, false, onText, onThinking);
    }

    public void stream(String model, List<Message> messages, Double temperature, Double topP,
                       boolean enableThinking, Consumer<String> onText, Consumer<String> onThinking) {
        var builder = OpenAiStreamingChatModel.builder()
                .apiKey(key).baseUrl(baseUrl).modelName(model).returnThinking(true);
        if (temperature != null) builder.temperature(temperature);
        if (topP != null) builder.topP(topP);
        if (enableThinking) builder.customParameters(Map.of("enable_thinking", true));
        CompletableFuture<Void> finished = new CompletableFuture<>();
        builder.build().chat(asChatMessages(messages), new StreamingChatResponseHandler() {
            @Override public void onPartialResponse(String text) { onText.accept(text); }
            @Override public void onPartialThinking(PartialThinking thinking) { onThinking.accept(thinking.text()); }
            @Override public void onCompleteResponse(ChatResponse response) { finished.complete(null); }
            @Override public void onError(Throwable error) { finished.completeExceptionally(error); }
        });
        try {
            finished.join();
        } catch (CompletionException e) {
            throw new IllegalStateException("百炼流式调用失败", e.getCause());
        }
    }

    public void printStream(String model, List<Message> messages, Double temperature, Double topP) {
        stream(model, messages, temperature, topP, System.out::print, ignored -> {});
    }
}
