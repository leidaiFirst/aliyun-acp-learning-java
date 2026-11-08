package com.aliyun.acp.c2.n21;

/** 原 Notebook cell 19：低 top_p 实验。 */
public final class Cell19 {
    public static void main(String[] args) throws Exception {
        // Qwen-Max模型：top_p取值范围为(0,1]，默认值为0.8。
        // 设置top_p=0.001
        Cell16.printQwenStreamResponse("为一款智能游戏手机取名，可以是", "请帮我取名，字数要求是4个汉字以内。", 0.7, 0.001, 10);
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # Qwen-Max模型：top_p取值范围为(0,1]，默认值为0.8。
// # 设置top_p=0.001
