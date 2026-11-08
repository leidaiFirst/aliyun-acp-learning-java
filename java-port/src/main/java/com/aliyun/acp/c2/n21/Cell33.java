package com.aliyun.acp.c2.n21;

import com.aliyun.acp.common.QwenClient;
import java.util.List;

/** 原 Notebook cell 33：联网搜索。 */
public final class Cell33 {
    public static void main(String[] args) throws Exception {
        String completion = new QwenClient().chat(
                "qwen-max", // 此处以qwen-max为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                List.of(QwenClient.system("You are a helpful assistant."), QwenClient.user("巴黎奥运会中国队金牌数")),
                null, null, true); // extra_body={"enable_search": True}
        System.out.println(completion);
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # 此处以qwen-max为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
