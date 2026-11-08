# 阿里云 ACP Java 框架对照版

这是一个**独立 Maven 项目**。原有 [`java-port`](../java-port/README.md) 保持原样，方便并排比较。两个项目使用同样的 Java 包名和 Cell 类名；本项目另外补充了课程 `2_2` 的 RAG 建索引、保存、加载与多轮对话示例。

## 选型与范围

- `C2/2_1` 问答与流式模型调用：LangChain4j 的 `OpenAiChatModel` / `OpenAiStreamingChatModel`，连接课程使用的百炼 OpenAI 兼容端点。
- `C3/3_1` Agent 示例：AgentScope Java 的 `ReActAgent`、`Toolkit`、`@Tool` 和结构化输出。
- `C2/2_2/Cell10` RAG：LangChain4j 的文档解析、`EmbeddingStoreIngestor`、`OpenAiEmbeddingModel`、`EmbeddingStoreContentRetriever` 与流式 `AiServices`。Embedding 模型沿用源 Notebook 的 `text-embedding-v2`。
- `C2/2_2/Cell12–19` 索引与多轮对话：LangChain4j 的 `InMemoryEmbeddingStore.serializeToFile/fromFile` 保存和加载向量索引；用历史对话改写独立问题，再检索和流式回答。

目前有 **24 个独立 main**：旧版已翻译的 17 个 Cell 均在这里有对应入口，另有 `2_2` 章节的 7 个 Cell。该章节所有非空代码 Cell 均有对应入口。中文课程其余 Cell 仍见 [CELL_INDEX.md](CELL_INDEX.md)。

## 本地运行

本项目复用 `../java-port/.tools` 中的 JDK 21 和 Maven；Qoder 可直接打开 `java-port-framework` 作为 Maven 项目。仓库根目录 `.env` 中需填写 `DASHSCOPE_API_KEY=...`，也可以设置环境变量。不要把密钥写入源码或提交到 Git。

```bash
cd java-port-framework
./run-cell.sh c2.n21.Cell06
./run-cell.sh c3.n31.Cell13
./run-cell.sh c2.n22.Cell08
./run-cell.sh c2.n22.Cell10
./run-cell.sh c2.n22.Cell12
./run-cell.sh c2.n22.Cell13
./run-cell.sh c2.n22.Cell15
./run-cell.sh c2.n22.Cell17
./run-cell.sh c2.n22.Cell19
```

`DASHSCOPE_BASE_URL` 可选，用于 LangChain4j 的百炼兼容接口；默认值与 Python 课程相同。AgentScope 的 DashScope 扩展使用它自己的默认服务端点。

`Cell10` 按源 Cell 演示一次性建索引与问答。`Cell12` 从 `course_core/docs` 顶层的 PDF、DOCX、MD 文件重新建索引，保存到本项目 `knowledge_base/test/embeddings.json`；`Cell13` 从该文件加载索引，运行前需先运行 `Cell12`。`Cell15`、`Cell17` 和 `Cell19` 优先加载保存的索引，若文件不存在则自动建索引并保存，所以都能作为独立入口运行。修改课程文档后，重新运行 `Cell12` 更新索引。`Cell19` 还会打印改写后的独立问题，便于与 Python 输出对照。

建索引会调用百炼 Embedding 模型；回答和改写会调用千问模型，实际运行会产生 API 用量。索引文件已加入 `.gitignore`，不会随源码提交。`Cell10` 的一次性内存索引不会自动保存。

没有配置 API Key 时，模型相关 Cell 会在发起请求前提示配置。由于当前 `.env` 中的 Key 为空，项目已完成 Maven 编译，尚未执行真实模型调用。

## 逐文件比较

见 [COMPARE.md](COMPARE.md)。每个 Java Cell 的末尾保留了源 Python Cell 的注释原文，便于核对。
