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
                "qwen3-235b-a22b-thinking-2507", // 此处以 qwen3-235b-a22b-thinking-2507 为例，可按需更换模型名称
                List.of(QwenClient.user("9.9和9.11谁大")),
                null, null,
                chunk -> {
                    // 如果chunk.choices为空，则打印usage
                    var choices = chunk.path("choices");
                    if (!choices.isArray() || choices.isEmpty()) {
                        System.out.println("\nUsage:");
                        System.out.println(chunk.path("usage"));
                        return;
                    }
                    var delta = choices.path(0).path("delta");
                    var reasoning = delta.path("reasoning_content");
                    // 打印思考过程
                    if (!reasoning.isMissingNode() && !reasoning.isNull()) {
                        System.out.print(reasoning.asText());
                        System.out.flush();
                        reasoningContent.append(reasoning.asText());
                    } else {
                        var content = delta.path("content");
                        if (content.isMissingNode() || content.isNull()) return;
                        // 开始回复
                        if (!content.asText().isEmpty() && isAnswering.compareAndSet(false, true)) {
                            System.out.println("\n" + "=".repeat(20) + "完整回复" + "=".repeat(20) + "\n");
                        }
                        // 打印回复过程
                        System.out.print(content.asText());
                        System.out.flush();
                        answerContent.append(content.asText());
                    }
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
