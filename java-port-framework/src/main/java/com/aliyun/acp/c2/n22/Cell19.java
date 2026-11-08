package com.aliyun.acp.c2.n22;

import com.aliyun.acp.common.Config;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiChatModel;
import java.util.List;

/** 原 Notebook 2_2 cell 19：根据历史对话改写问题，再用改写后的问题检索并回答。 */
public final class Cell19 {
    public static void main(String[] args) throws Exception {
        // 定义问题改写的提示词模板
        String customPrompt = """
                给定一段对话历史（人类与助手之间）和人类的后续问题，
                请将该问题改写为一个独立的问题，包含对话中所有相关的上下文信息。

                <对话历史>
                %s

                <后续问题>
                %s

                <改写后的独立问题>
                """;

        // 模拟历史对话信息
        List<ChatMessage> customChatHistory = List.of(
                UserMessage.from("内容开发工程师有哪些细分类型？"),
                AiMessage.from("内容开发工程师是综合性技术岗位。"));

        // 创建查询引擎。独立运行时优先加载 Cell12 保存的索引；没有索引则建立并保存。
        var index = N22Rag.loadOrBuildStore();
        var queryEngine = N22Rag.queryEngine(index);

        // 创建支持多轮对话的聊天引擎：先把历史对话和追问改写成独立问题。
        var model = OpenAiChatModel.builder()
                .apiKey(Config.require("DASHSCOPE_API_KEY"))
                .baseUrl(N22Rag.baseUrl())
                .modelName("qwen-plus")
                .build();
        StringBuilder history = new StringBuilder();
        for (ChatMessage message : customChatHistory) {
            if (message instanceof UserMessage user) {
                history.append("用户：").append(user.singleText()).append('\n');
            } else if (message instanceof AiMessage assistant) {
                history.append("助手：").append(assistant.text()).append('\n');
            }
        }

        // 提问时只说「核心职责是什么」，不提及「内容开发工程师」
        String question = "核心职责是什么？";
        String rewrittenQuestion = model.chat(customPrompt.formatted(history.toString().strip(), question)).strip();
        // 开启详细输出，可以看到改写后的问题
        System.out.println("chat：" + customPrompt.formatted(history.toString().strip(), question));
        System.out.println("改写后的独立问题：" + rewrittenQuestion);
        N22Rag.ask(queryEngine, rewrittenQuestion);
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # 定义问题改写的提示词模板
// # 模拟历史对话信息
// # 创建查询引擎
// # 创建支持多轮对话的聊天引擎
// # 开启详细输出，可以看到改写后的问题
// # 提问时只说「核心职责是什么」，不提及「内容开发工程师」
