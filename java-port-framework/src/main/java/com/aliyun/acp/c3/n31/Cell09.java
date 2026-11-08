package com.aliyun.acp.c3.n31;

import com.aliyun.acp.common.AgentModels;
import io.agentscope.core.ReActAgent;
import io.agentscope.core.agent.RuntimeContext;
import io.agentscope.core.message.UserMessage;
import io.agentscope.core.tool.Toolkit;
import java.util.List;

/** 原 Notebook cell 9：让模型做工具路由决策。 */
public final class Cell09 {
    public static void main(String[] args) {
        // --- 测试用例 ---
        String request = "帮我找一下那篇经典的 Transformer 论文，标题是 'Attention Is All You Need'";
        String decision = getToolDecisionFromLlm(request);
        System.out.println("用户请求: \"" + request + "\"");
        System.out.println("模型决策: " + decision + "\n");
    }

    static String getToolDecisionFromLlm(String userRequest) {
        // 使用 dedent 移除为了代码美观而添加的缩进，避免传递给大模型
        String prompt = """
                你是一个智能助理的路由模块。你的任务是根据用户的请求，从下面的工具列表中选择最合适的工具来解决问题，并给出入参。

                [可用工具列表]
                1. web_search: 用于在互联网上搜索通用信息。
                2. search_arxiv_paper: 用于在 Arxiv.org 上搜索学术论文。
                3. fetch_webpage_content: 用于获取指定URL的网页内容。

                [用户请求]
                "%s"

                [决策]
                请告诉用户你的决定。
                """.formatted(userRequest).stripIndent();
        try (ReActAgent agent = ReActAgent.builder()
                .name("ToolRouter")
                .sysPrompt("根据用户请求选择最合适的工具，说明工具名称与参数。")
                .model(AgentModels.qwenPlus(0.0)) // 使用低 temperature 以获得更确定的决策
                .toolkit(new Toolkit())
                .build()) {
            return agent.call(List.of(new UserMessage(prompt)), RuntimeContext.empty()).block().getTextContent();
        }
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # 使用 dedent 移除为了代码美观而添加的缩进，
// # 避免这些格式化用的tab/空格被传递给大模型
// # 使用低 temperature 以获得更确定的决策
// # --- 测试用例 ---
