package com.aliyun.acp.c2.n21;

import com.aliyun.acp.common.QwenClient;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/** 原 Notebook cell 35：展示思考过程与完整回复。 */
public final class Cell35 {
    public static void main(String[] args) throws Exception {
        StringBuilder reasoningContent = new StringBuilder(); // 定义完整思考过程
        StringBuilder answerContent = new StringBuilder();    // 定义完整回复
        AtomicBoolean isAnswering = new AtomicBoolean(false); // 判断是否结束思考过程并开始回复

        // 创建聊天完成请求
        System.out.println("\n" + "=".repeat(20) + "思考过程" + "=".repeat(20) + "\n");
        new QwenClient().stream(
                "qwen-plus", // Use qwen-plus with thinking mode enabled.
                List.of(QwenClient.user("9.9和9.11谁大")), null, null, true,
                content -> {
                    // 开始回复
                    if (!content.isEmpty() && isAnswering.compareAndSet(false, true)) {
                        System.out.println("\n" + "=".repeat(20) + "完整回复" + "=".repeat(20) + "\n");
                    }
                    // 打印回复过程
                    System.out.print(content);
                    System.out.flush();
                    answerContent.append(content);
                },
                reasoning -> {
                    // 打印思考过程
                    System.out.print(reasoning);
                    System.out.flush();
                    reasoningContent.append(reasoning);
                });
        System.out.println();
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # 定义完整思考过程
// # 定义完整回复
// # 判断是否结束思考过程并开始回复
// # 创建聊天完成请求
// # 此处以 qwen3-235b-a22b-thinking-2507 为例，可按需更换模型名称
// # 解除以下注释会在最后一个chunk返回Token使用量
// # stream_options={
// #     "include_usage": True
// # }
// # 如果chunk.choices为空，则打印usage
// # 打印思考过程
// # 开始回复
// # 打印回复过程
