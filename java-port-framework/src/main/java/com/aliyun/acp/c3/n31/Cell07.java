package com.aliyun.acp.c3.n31;

/** 原 Notebook cell 7：关键词路由器。 */
public final class Cell07 {
    public static void main(String[] args) {
        // 原 cell 仅定义函数。这里提供独立运行示例，便于观察路由结果。
        for (String input : new String[]{"找 Transformer 论文", "搜索最新资料", "总结这篇文章", "你好"}) {
            System.out.println(input + " -> " + routeToTool(input));
        }
    }

    static String routeToTool(String userInput) {
        // 伪代码：一个脆弱的、基于关键字的工具路由器
        if (userInput.contains("论文") || userInput.contains("arxiv")) return "search_arxiv_paper";
        else if (userInput.contains("搜索") || userInput.contains("查找") || userInput.contains("资料")) return "web_search";
        else if (userInput.contains("总结") || userInput.contains("内容") || userInput.contains("http")) return "fetch_webpage_content";
        // ... 你需要在这里不断添加 elif 判断
        else return "no_tool_needed";
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # 伪代码：一个脆弱的、基于关键字的工具路由器
// # ... 你需要在这里不断添加 elif 判断
