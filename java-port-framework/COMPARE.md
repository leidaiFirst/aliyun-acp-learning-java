# 旧版与框架版的优化点

| 对照位置 | 原 `java-port` | 本项目 `java-port-framework` |
| --- | --- | --- |
| 百炼调用 | [`QwenClient.java`](../java-port/src/main/java/com/aliyun/acp/common/QwenClient.java) 自行构造 HTTP 请求并解析 JSON/SSE | [`QwenClient.java`](src/main/java/com/aliyun/acp/common/QwenClient.java) 通过 LangChain4j 的模型与流式回调调用 |
| 流式思考 | [`Cell35.java`](../java-port/src/main/java/com/aliyun/acp/c2/n21/Cell35.java) 自行读取 `reasoning_content` | [`Cell35.java`](src/main/java/com/aliyun/acp/c2/n21/Cell35.java) 使用 `onPartialThinking` 与 `onPartialResponse` |
| Agent 工具调用 | [`Cell13.java`](../java-port/src/main/java/com/aliyun/acp/c3/n31/Cell13.java) 手工声明 JSON Schema、执行工具并回传消息 | [`Cell13.java`](src/main/java/com/aliyun/acp/c3/n31/Cell13.java) 通过 AgentScope `@Tool`、`Toolkit`、`ReActAgent` 完成调用循环 |
| 结构化决策 | [`Cell11.java`](../java-port/src/main/java/com/aliyun/acp/c3/n31/Cell11.java) 解析模型输出并校验 | [`Cell11.java`](src/main/java/com/aliyun/acp/c3/n31/Cell11.java) 使用 AgentScope 的结构化输出，再校验课程的业务约束 |
| RAG | 原 Java 项目尚未实现 | [`2_2/Cell10.java`](src/main/java/com/aliyun/acp/c2/n22/Cell10.java) 用 LangChain4j 实现解析、切片、Embedding、检索与流式回答 |
| 索引保存与加载 | 原 Java 项目尚未实现 | [`2_2/Cell12.java`](src/main/java/com/aliyun/acp/c2/n22/Cell12.java) 保存索引；[`Cell13.java`](src/main/java/com/aliyun/acp/c2/n22/Cell13.java) 加载索引；[`Cell15.java`](src/main/java/com/aliyun/acp/c2/n22/Cell15.java) 和 [`Cell17.java`](src/main/java/com/aliyun/acp/c2/n22/Cell17.java) 用加载后的索引提问 |
| RAG 多轮对话 | 原 Java 项目尚未实现 | [`2_2/Cell19.java`](src/main/java/com/aliyun/acp/c2/n22/Cell19.java) 将历史对话与追问改写成独立问题，再检索并流式回答 |

两个目录的 Python Notebook 来源相同，Java 类名也保持一致。AgentScope 的 `ReActAgent` 会自行组织 Agent 推理和工具循环，因此 Agent 章节的请求细节可能与旧版手写 HTTP 不完全相同；对比时以功能步骤、工具调用和最终结果为主。
