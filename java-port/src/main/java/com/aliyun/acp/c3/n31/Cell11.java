package com.aliyun.acp.c3.n31;

import com.aliyun.acp.common.QwenClient;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;

/** 原 Notebook cell 11：JSON 工具决策及校验重试。 */
public final class Cell11 {
    private static final ObjectMapper JSON = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        // 使用：假设 client 变量已在别处初始化；独立 main 在这里初始化。
        String userRequest = "帮我找一下那篇经典的 Transformer 论文，标题是 'Attention Is All You Need'";
        JsonNode result = getStructuredOutput(userRequest, 2);
        if (result != null) System.out.println(JSON.writerWithDefaultPrettyPrinter().writeValueAsString(result));
    }

    static String buildPrompt(String userRequest) {
        // 3. 构建提示词，包含清晰的指令、工具定义和示例
        return """
                你的任务是根据用户的请求，从可用工具列表中选择最合适的工具，并以严格的 JSON 格式输出调用信息。

                # 可用工具:
                - `web_search(query: str)`: 当需要搜索通用信息、新闻或非学术性内容时使用。
                - `search_arxiv_paper(query: str)`: 当需要搜索学术论文，特别是来自 Arxiv.org 的论文时使用。

                # 输出格式要求:
                你必须严格按照以下 JSON 结构输出，不要包含任何额外的自然语言解释。

                {
                  "tool_name": "工具名称",
                  "parameters": {"参数名": "参数值"}
                }

                # 示例:
                用户请求: "最近AI领域有什么好玩的新闻？"
                你的输出:
                {"tool_name": "web_search", "parameters": {"query": "AI领域最新新闻"}}

                # 用户请求:
                "%s"

                # 你的输出:
                """.formatted(userRequest).stripIndent();
    }

    static JsonNode getStructuredOutput(String userRequest, int maxRetries) throws Exception {
        // 1. 为与课程研究相关的工具定义参数约束：query 必须是字符串
        // 2. 将工具名与对应参数绑定，最终决策只能是两种调用中的一种
        List<QwenClient.Message> messages = new ArrayList<>();
        messages.add(QwenClient.user(buildPrompt(userRequest)));
        QwenClient client = new QwenClient();
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            String rawOutput = client.chat("qwen-plus", messages, 0.0, null, false);
            try {
                // 解析并验证，等价于原 Python TypeAdapter(ToolCall) 的字段约束
                String stripped = rawOutput.trim().replaceFirst("^```(?:json)?\\s*", "").replaceFirst("\\s*```$", "");
                JsonNode data = JSON.readTree(stripped);
                String tool = data.path("tool_name").asText();
                JsonNode query = data.path("parameters").path("query");
                if (!(tool.equals("web_search") || tool.equals("search_arxiv_paper")) || !query.isTextual()) {
                    throw new IllegalArgumentException("tool_name 或 parameters.query 不符合工具定义");
                }
                return data;
            } catch (Exception e) {
                // 如果失败，则将错误信息和原始输出加入对话历史，要求模型修正
                messages.add(QwenClient.assistant(rawOutput));
                messages.add(QwenClient.user("格式错误: " + e.getMessage() + "，请严格按照JSON格式重新输出"));
            }
        }
        return null;
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # 1. 为与课程研究相关的工具定义 Pydantic 参数模型
// # 2. 定义工具调用模型，将工具名与对应的参数模型绑定
// # 使用 Union 类型，表示模型最终的决策是这两种调用中的一种
// # 3. 构建提示词，包含清晰的指令、工具定义和示例
// # 4. 调用并验证（带重试）
// # 此处为对大模型服务的API调用，为保证课程的通用性，具体实现已省略
// # 你可以替换成自己的代码，例如 client.chat.completions.create(...)
// # 解析并验证
// # 使用Pydantic模型验证解析后的数据
// # 如果失败，则将错误信息和原始输出都加入到对话历史中，以便模型进行修正
// # 使用
// # 假设 client 变量已在别处初始化
