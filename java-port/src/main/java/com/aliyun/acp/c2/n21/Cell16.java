package com.aliyun.acp.c2.n21;

import com.aliyun.acp.common.QwenClient;
import java.util.List;

/** 原 Notebook cell 16：温度参数实验。 */
public final class Cell16 {
    public static void main(String[] args) throws Exception {
        // Qwen-Max模型：temperature的取值范围是[0, 2)，默认值为0.7
        // 设置temperature=0
        printQwenStreamResponse("马也可以叫做", "请帮我续写内容，字数要求是4个汉字以内。", 0.0, 0.8, 10);
    }

    static void printQwenStreamResponse(String userPrompt, String systemPrompt, double temperature, double topP, int iterations) throws Exception {
        QwenClient client = new QwenClient();
        for (int i = 0; i < iterations; i++) {
            System.out.print("输出 " + (i + 1) + " : ");
            // 防止限流，添加延迟
            Thread.sleep(500);
            StringBuilder outputContent = new StringBuilder();
            client.stream("qwen-max", List.of(QwenClient.system(systemPrompt), QwenClient.user(userPrompt)), temperature, topP, chunk -> {
                var content = chunk.path("choices").path(0).path("delta").path("content");
                if (!content.isMissingNode() && !content.isNull()) outputContent.append(content.asText());
            });
            System.out.println(outputContent);
        }
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # temperature，top_p的默认值使用Qwen-Max模型的默认值
// ## 防止限流，添加延迟
// # Qwen-Max模型：temperature的取值范围是[0, 2)，默认值为0.7
// # 设置temperature=0
