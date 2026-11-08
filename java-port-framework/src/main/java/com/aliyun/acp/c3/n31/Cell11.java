package com.aliyun.acp.c3.n31;

import com.aliyun.acp.common.AgentModels;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.message.UserMessage;
import io.agentscope.core.tool.Toolkit;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

/** 原 Notebook cell 11：AgentScope 原生结构化输出。 */
public final class Cell11 {
    // 1. 为与课程研究相关的工具定义参数模型
    public record Parameters(String query) {}
    // 2. 将工具名与对应参数绑定
    public record ToolCall(String tool_name, Parameters parameters) {}

    public static void main(String[] args) {
        String userRequest = "帮我找一下那篇经典的 Transformer 论文，标题是 'Attention Is All You Need'";
        ToolCall result = getStructuredOutput(userRequest, 2);
        if (result != null) {
            try {
                System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(result));
            } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
                throw new IllegalStateException("无法输出工具决策 JSON", e);
            }
        }
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

                {"tool_name": "工具名称", "parameters": {"query": "关键词"}}

                # 示例:
                用户请求: "最近AI领域有什么好玩的新闻？"
                你的输出:
                {"tool_name": "web_search", "parameters": {"query": "AI领域最新新闻"}}

                # 用户请求:
                "%s"
                """.formatted(userRequest).stripIndent();
    }

    static ToolCall getStructuredOutput(String userRequest, int maxRetries) {
        // 4. 调用并验证（带重试）：框架生成 JSON Schema 并解析为 Java record
        String prompt = buildPrompt(userRequest);
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try (ReActAgent agent = ReActAgent.builder()
                    .name("StructuredToolRouter")
                    .sysPrompt("只输出符合工具定义的结构化调用信息。")
                    .model(AgentModels.qwenPlus(0.0))
                    .toolkit(new Toolkit())
                    .build()) {
                var result = agent.call(List.of(new UserMessage(prompt)), ToolCall.class,
                        RuntimeContext.empty()).block().getStructuredData(ToolCall.class);
                // 使用与原 Pydantic Union 对应的业务约束
                if (result == null || result.parameters() == null || result.parameters().query() == null
                        || !(result.tool_name().equals("web_search") || result.tool_name().equals("search_arxiv_paper"))) {
                    throw new IllegalArgumentException("工具名称或 query 参数无效");
                }
                return result;
            } catch (RuntimeException e) {
                // 如果失败，则将错误信息加入提示，要求模型修正
                prompt += "\n格式错误: " + e.getMessage() + "，请严格按照JSON格式重新输出";
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
