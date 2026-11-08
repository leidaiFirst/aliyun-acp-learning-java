package com.aliyun.acp.common;

import io.agentscope.core.model.GenerateOptions;
import io.agentscope.core.model.Model;
import io.agentscope.extensions.model.dashscope.DashScopeChatModel;
import io.agentscope.extensions.model.dashscope.formatter.DashScopeChatFormatter;

/** AgentScope Java 的百炼模型配置。 */
public final class AgentModels {
    private AgentModels() {}

    public static Model qwenPlus(Double temperature) {
        var builder = DashScopeChatModel.builder()
                .apiKey(Config.require("DASHSCOPE_API_KEY"))
                .modelName("qwen-plus")
                .stream(true)
                .formatter(new DashScopeChatFormatter());
        if (temperature != null) {
            builder.defaultOptions(GenerateOptions.builder().temperature(temperature).build());
        }
        return builder.build();
    }
}
