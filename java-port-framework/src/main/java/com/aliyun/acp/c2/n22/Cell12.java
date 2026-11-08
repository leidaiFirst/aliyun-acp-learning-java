package com.aliyun.acp.c2.n22;

/** 原 Notebook 2_2 cell 12：将索引保存为本地文件。 */
public final class Cell12 {
    public static void main(String[] args) throws Exception {
        // 将索引保存为本地文件。独立运行时先从课程文档建立索引。
        var index = N22Rag.buildStore();
        N22Rag.saveStore(index);
        System.out.println("索引文件保存到了" + N22Rag.indexFile().getParent());
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # 将索引保存为本地文件
