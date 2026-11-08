package com.aliyun.acp.c3.n31;

import com.aliyun.acp.common.AgentModels;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.message.UserMessage;
import io.agentscope.core.tool.Tool;
import io.agentscope.core.tool.ToolParam;
import io.agentscope.core.tool.Toolkit;
import java.util.List;

/** 原 Notebook cell 13：原生工具调用；由 AgentScope 负责 schema、调用与回传。 */
public final class Cell13 {
    public static final class PaperTools {
        // 1. 定义工具列表，包含每个函数的JSON Schema描述
        @Tool(name = "search_arxiv_paper", description = "在 Arxiv.org 上搜索学术论文")
        public String searchArxivPaper(@ToolParam(name = "query", description = "论文的标题或关键词") String query) {
            // 这里我们模拟函数执行结果
            System.out.println("模型决定调用工具: `search_arxiv_paper`");
            System.out.println("参数: {\"query\":\"" + query + "\"}");
            String toolResult = """
                    {"paper_id":"1706.03762","url":"https://arxiv.org/abs/1706.03762","title":"Attention Is All You Need"}
                    """.strip();
            System.out.println("工具执行结果: " + toolResult);
            return toolResult;
        }
    }

    public static void main(String[] args) {
        // 2. 发起第一次API调用，让模型决策
        Toolkit toolkit = new Toolkit();
        toolkit.registerTool(new PaperTools());
        try (ReActAgent agent = ReActAgent.builder()
                .name("PaperFinder")
                .sysPrompt("你是论文研究助理。收到论文查找请求时，先调用 search_arxiv_paper，再依据工具结果回答。")
                .model(AgentModels.qwenPlus(null))
                .toolkit(toolkit)
                .build()) {
            // 3. 检查模型是否决定调用工具并执行
            // 4. 将模型的决策和工具的执行结果一起传回，让模型生成最终答复
            var reply = agent.call(List.of(new UserMessage(
                    "帮我找一下那篇经典的 Transformer 论文 'Attention Is All You Need'")),
                    RuntimeContext.empty()).block();
            System.out.println("\n模型的最终回复:");
            System.out.println(reply.getTextContent());
        }
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # 1. 定义工具列表，包含每个函数的JSON Schema描述
// # 2. 发起第一次API调用，让模型决策
// # 3. 检查模型是否决定调用工具并执行
// # 这里我们模拟函数执行结果
// # 4. 将模型的决策和工具的执行结果一起传回，让模型生成最终答复
