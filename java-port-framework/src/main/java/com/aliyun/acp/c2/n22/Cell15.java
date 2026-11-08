package com.aliyun.acp.c2.n22;

/** 原 Notebook 2_2 cell 15：加载索引后重新建立流式提问引擎并提问。 */
public final class Cell15 {
    public static void main(String[] args) throws Exception {
        var index = N22Rag.loadOrBuildStore();
        System.out.println("正在创建提问引擎...");
        // 设置为流式输出。
        // 此处使用 qwen-plus 模型，你也可以使用阿里云提供的其它 qwen 文本生成模型。
        var queryEngine = N22Rag.queryEngine(index);
        System.out.println("正在生成回复...");
        System.out.println("回答是：");
        N22Rag.ask(queryEngine, "我们公司项目管理应该用什么工具");
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # 设置为流式输出
// # 此处使用qwen-plus模型，你也可以使用阿里云提供的其它qwen的文本生成模型：https://help.aliyun.com/zh/model-studio/getting-started/models#9f8890ce29g5u
