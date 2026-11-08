package com.aliyun.acp.c2.n22;

import com.aliyun.acp.common.Config;

/** 原 Notebook 2_2 cell 8：加载百炼 API Key，打印掩码确认配置。 */
public final class Cell08 {
    public static void main(String[] args) {
        // 加载百炼的 API Key 用于调用千问大模型
        String key = Config.require("DASHSCOPE_API_KEY");
        // 生产环境中请勿将 API Key 输出到日志中，避免泄露
        System.out.println("你配置的 API Key 是：" + key.substring(0, Math.min(5, key.length())) + "*****");
        // Java 版不使用 NLTK，因此不需要下载 NLTK 数据。
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # 加载百炼的 API Key 用于调用千问大模型
// # 生产环境中请勿将 API Key 输出到日志中，避免泄露
