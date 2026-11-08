package com.aliyun.acp.c3.n31;

import com.aliyun.acp.common.QwenClient;
import java.util.List;

/** 原 Notebook cell 5：工具执行结果交给模型整理。 */
public final class Cell05 {
    public static void main(String[] args) throws Exception {
        // 1. 用户的原始请求
        String userRequest = "你好，请帮我搜集一些关于 Transformer 模型的最新资料。";
        // 2. “硬编码”执行工具函数
        String toolResult = webSearch(userRequest);
        System.out.println("用户请求: " + userRequest);
        System.out.println("工具结果: " + toolResult + "\n");
        // 3. 将用户请求和工具结果拼接成一个提示，发送给大模型
        //    目标是让模型基于结构化的工具输出，生成一句人类易于理解的回复。
        String finalResponse = new QwenClient().chat("qwen-plus", List.of(
                QwenClient.system("你是一位课程研究助理，你的任务是根据工具的执行结果，向用户生成一句友好和清晰的回复。"),
                QwenClient.user("用户原始请求: \"" + userRequest + "\"\n工具执行结果: " + toolResult)
        ));
        // 4. 输出模型的最终回复
        System.out.println("模型生成的最终回复:\n" + finalResponse);
    }

    /** 模拟执行网络搜索并返回JSON格式的结果。 */
    static String webSearch(String query) {
        // 这里的 web_search 函数是模拟的，以聚焦于核心逻辑
        System.out.println("--- [工具执行中] 正在搜索: " + query + " ---");
        // 真实场景中，这里会调用一个真正的搜索引擎API
        return """
                {
                    "results": [
                        {"title": "Attention Is All You Need (Transformer 论文原文)", "url": "https://arxiv.org/abs/1706.03762", "snippet": "The dominant sequence transduction models are based on complex recurrent or convolutional neural networks... We propose a new simple network architecture, the Transformer, based solely on attention mechanisms..."},
                        {"title": "The Illustrated Transformer – Jay Alammar", "url": "https://jalammar.github.io/illustrated-transformer/", "snippet": "A visual and intuitive explanation of the Transformer model."}
                    ]
                }
                """;
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # 1. 用户的原始请求
// # 2. “硬编码”执行工具函数
// # 这里的 web_search 函数是模拟的，以聚焦于核心逻辑
// # 真实场景中，这里会调用一个真正的搜索引擎API
// # 3. 将用户请求和工具结果拼接成一个提示，发送给大模型
// #    目标是让模型基于结构化的工具输出，生成一句人类易于理解的回复。
// # 4. 输出模型的最终回复
