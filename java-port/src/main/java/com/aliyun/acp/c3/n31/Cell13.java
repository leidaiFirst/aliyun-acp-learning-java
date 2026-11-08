package com.aliyun.acp.c3.n31;

import com.aliyun.acp.common.QwenClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/** 原 Notebook cell 13：模型原生工具调用、执行及二次回复。 */
public final class Cell13 {
    public static void main(String[] args) throws Exception {
        ObjectMapper json = new ObjectMapper();
        QwenClient client = new QwenClient();
        // 1. 定义工具列表，包含每个函数的JSON Schema描述
        ArrayNode tools = json.createArrayNode();
        ObjectNode function = tools.addObject().put("type", "function").putObject("function");
        function.put("name", "search_arxiv_paper");
        function.put("description", "在 Arxiv.org 上搜索学术论文");
        ObjectNode parameters = function.putObject("parameters");
        parameters.put("type", "object");
        parameters.putObject("properties").putObject("query")
                .put("type", "string").put("description", "论文的标题或关键词");
        parameters.putArray("required").add("query");

        // 2. 发起第一次API调用，让模型决策
        ArrayNode messages = json.createArrayNode();
        messages.addObject().put("role", "user")
                .put("content", "帮我找一下那篇经典的 Transformer 论文 'Attention Is All You Need'");
        ObjectNode request = json.createObjectNode();
        request.put("model", "qwen-plus");
        request.set("messages", messages);
        request.set("tools", tools);
        request.put("tool_choice", "auto");
        JsonNode response = client.chatRaw(request);
        JsonNode responseMessage = response.path("choices").path(0).path("message");

        // 3. 检查模型是否决定调用工具并执行
        JsonNode toolCalls = responseMessage.path("tool_calls");
        if (toolCalls.isArray() && !toolCalls.isEmpty()) {
            JsonNode toolCall = toolCalls.get(0);
            String functionName = toolCall.path("function").path("name").asText();
            JsonNode functionArgs = json.readTree(toolCall.path("function").path("arguments").asText());
            System.out.println("模型决定调用工具: `" + functionName + "`");
            System.out.println("参数: " + functionArgs);

            // 这里我们模拟函数执行结果
            String toolResult = json.writeValueAsString(json.createObjectNode()
                    .put("paper_id", "1706.03762")
                    .put("url", "https://arxiv.org/abs/1706.03762")
                    .put("title", "Attention Is All You Need"));
            System.out.println("工具执行结果: " + toolResult);
            // 4. 将模型的决策和工具的执行结果一起传回，让模型生成最终答复
            messages.add(responseMessage.deepCopy());
            messages.addObject()
                    .put("tool_call_id", toolCall.path("id").asText())
                    .put("role", "tool")
                    .put("name", functionName)
                    .put("content", toolResult);
            ObjectNode finalRequest = json.createObjectNode();
            finalRequest.put("model", "qwen-plus");
            finalRequest.set("messages", messages);
            JsonNode finalResponse = client.chatRaw(finalRequest);
            System.out.println("\n模型的最终回复:");
            System.out.println(finalResponse.path("choices").path(0).path("message").path("content").asText());
        } else {
            System.out.println("模型未调用工具：" + responseMessage.path("content").asText());
        }
    }
}
