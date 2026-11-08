package com.aliyun.acp.c2.n21;

import com.aliyun.acp.common.QwenClient;
import java.util.List;

/** 原 Notebook cell 25：注入企业背景知识。 */
public final class Cell25 {
    public static void main(String[] args) throws Exception {
        String userQuestion = "我是软件一组的，请问项目管理应该用什么工具";
        String knowledge = """
                公司项目管理工具有两种选择：
                  1. **Jira**：对于软件开发团队来说，Jira 是一个非常强大的工具，支持敏捷开发方法，如Scrum和Kanban。它提供了丰富的功能，包括问题跟踪、时间跟踪等。

                  2. **Microsoft Project**：对于大型企业或复杂项目，Microsoft Project 提供了详细的计划制定、资源分配和成本控制等功能。它更适合那些需要严格控制项目时间和成本的场景。

                  在一般情况下请使用Microsoft Project，公司购买了完整的许可证。软件研发一组、三组和四组正在使用Jira，计划于2026年之前逐步切换至Microsoft Project。
                """;
        // 将公司项目管理工具相关的知识作为背景信息传入系统提示词
        new QwenClient().printStream("qwen-max", List.of(
                QwenClient.system("你负责教育内容开发公司的答疑，你的名字叫公司小蜜，你要回答学员的问题。" + knowledge),
                QwenClient.user(userQuestion)
        ), 0.7, 0.8);
        System.out.println();
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # 将公司项目管理工具相关的知识作为背景信息传入系统提示词
