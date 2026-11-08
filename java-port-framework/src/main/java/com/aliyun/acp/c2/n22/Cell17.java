package com.aliyun.acp.c2.n22;

/** 原 Notebook 2_2 cell 17：使用封装后的 RAG 加载、创建引擎及提问方法。 */
public final class Cell17 {
    public static void main(String[] args) throws Exception {
        // 引文在前面的步骤中已经建立了索引，因此这里可以直接加载索引。如果需要重建索引，可以增加一行代码：rag.indexing()
        var index = N22Rag.loadOrBuildStore();
        var queryEngine = N22Rag.queryEngine(index);
        N22Rag.ask(queryEngine, "我们公司项目管理应该用什么工具");
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # 引文在前面的步骤中已经建立了索引，因此这里可以直接加载索引。如果需要重建索引，可以增加一行代码：rag.indexing()
