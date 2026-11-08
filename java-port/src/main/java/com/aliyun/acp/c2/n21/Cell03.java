package com.aliyun.acp.c2.n21;

import com.aliyun.acp.common.Config;

/** 原 Notebook cell 3：配置 API Key。 */
public final class Cell03 {
    public static void main(String[] args) {
        // 加载百炼的 API Key 用于调用千问大模型
        String key = Config.require("DASHSCOPE_API_KEY");
        System.out.println("你配置的 API Key 是：" + key.substring(0, Math.min(5, key.length())) + "*****");
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # 加载百炼的 API Key 用于调用千问大模型
