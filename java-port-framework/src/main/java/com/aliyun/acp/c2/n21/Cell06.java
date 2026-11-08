package com.aliyun.acp.c2.n21;

import com.aliyun.acp.common.QwenClient;
import java.util.List;

/** 原 Notebook cell 6：基础问答。 */
public final class Cell06 {
    public static void main(String[] args) throws Exception {
        QwenClient client = new QwenClient();
        String response = getQwenResponse(client, "我们公司项目管理应该用什么工具");
        System.out.println(response);
    }

    static String getQwenResponse(QwenClient client, String prompt) throws Exception {
        return client.chat("qwen-max", List.of(
                // system message 用于设置大模型的角色和任务
                QwenClient.system("你负责教育内容开发公司的答疑，你的名字叫公司小蜜，你要回答同事们的问题。"),
                // user message 用于输入用户的问题
                QwenClient.user(prompt)
        ));
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # system message 用于设置大模型的角色和任务
// # user message 用于输入用户的问题
