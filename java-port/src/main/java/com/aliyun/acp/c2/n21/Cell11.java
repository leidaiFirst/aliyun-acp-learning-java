package com.aliyun.acp.c2.n21;

import com.aliyun.acp.common.QwenClient;
import java.util.List;

/** 原 Notebook cell 11：流式问答。 */
public final class Cell11 {
    public static void main(String[] args) throws Exception {
        getQwenStreamResponse("我们公司项目管理应该用什么工具", "你负责教育内容开发公司的答疑，你的名字叫公司小蜜，你要回答同事们的问题。");
    }

    static void getQwenStreamResponse(String userPrompt, String systemPrompt) throws Exception {
        new QwenClient().printStream("qwen-max", List.of(QwenClient.system(systemPrompt), QwenClient.user(userPrompt)), null, null);
        System.out.println();
    }
}
