package com.aliyun.acp.c2.n21;

/** 原 Notebook cell 20：默认 top_p 实验。 */
public final class Cell20 {
    public static void main(String[] args) throws Exception {
        // 设置top_p=0.8
        Cell16.printQwenStreamResponse("为一款智能游戏手机取名，可以是", "请帮我取名，字数要求是4个汉字以内。", 0.7, 0.8, 10);
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # 设置top_p=0.8
