package com.aliyun.acp.c2.n21;

import com.aliyun.acp.common.QwenClient;
import java.util.ArrayList;
import java.util.List;

/** 原 Notebook cell 8：多轮对话。 */
public final class Cell08 {
    public static void main(String[] args) throws Exception {
        multiTurnChat();
    }

    static void multiTurnChat() throws Exception {
        QwenClient client = new QwenClient();
        // 初始化对话历史，包含系统提示词
        List<QwenClient.Message> conversationHistory = new ArrayList<>();
        conversationHistory.add(QwenClient.system("你负责教育内容开发公司的答疑，你的名字叫公司小蜜，你要回答同事们的问题。"));

        // 模拟多轮对话
        String[] userQuestions = {
                "我们公司项目管理应该用什么工具？",
                "那我怎么申请这个工具的账号呢？",
                "申请一般需要多久能批下来？"
        };
        for (String question : userQuestions) {
            System.out.println("👤 用户：" + question);
            // 将用户问题添加到对话历史
            conversationHistory.add(QwenClient.user(question));
            // 调用大模型，传入完整的对话历史
            String assistantMessage = client.chat("qwen-max", conversationHistory);
            // 获取模型回复
            System.out.println("🤖 小蜜：" + assistantMessage + "\n");
            // 将模型回复也添加到对话历史，以便下一轮对话使用
            conversationHistory.add(QwenClient.assistant(assistantMessage));
        }
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # 初始化对话历史，包含系统提示词
// # 模拟多轮对话
// # 将用户问题添加到对话历史
// # 调用大模型，传入完整的对话历史
// # 包含所有历史消息
// # 获取模型回复
// # 将模型回复也添加到对话历史，以便下一轮对话使用
