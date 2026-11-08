package com.aliyun.acp.c2.n22;

/** 原 Notebook 2_2 cell 13：从本地文件加载索引。 */
public final class Cell13 {
    public static void main(String[] args) {
        // 将本地索引文件加载为索引。
        var index = N22Rag.loadStore();
        System.out.println("成功从" + N22Rag.indexFile().getParent() + "路径加载索引：" + index.getClass().getSimpleName());
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # 将本地索引文件加载为索引
