package com.aliyun.acp.c2.n22;

import com.aliyun.acp.common.Config;
import com.aliyun.acp.common.CoursePaths;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/** 原 Notebook 2_2 cell 10：LangChain4j 实现文档解析、切片、向量索引与 RAG 问答。 */
public final class Cell10 {
    public interface Assistant {
        TokenStream chat(String question);
    }

    public static void main(String[] args) throws Exception {
        // 导入依赖：LangChain4j 的 Tika 解析器、EmbeddingStore、百炼兼容模型
        String key = Config.require("DASHSCOPE_API_KEY");
        String baseUrl = Config.get("DASHSCOPE_BASE_URL") == null
                ? "https://dashscope.aliyuncs.com/compatible-mode/v1"
                : Config.get("DASHSCOPE_BASE_URL");
        Path docsPath = CoursePaths.documents();

        System.out.println("正在解析文件...");
        // LlamaIndex 的 SimpleDirectoryReader 对应 FileSystemDocumentLoader。
        // 这里只读取课程问答所需的文档格式，跳过 notebook 文件。
        List<Document> documents;
        try (Stream<Path> files = Files.list(docsPath)) {
            documents = files.filter(Files::isRegularFile)
                    .filter(Cell10::isCourseDocument)
                    .map(path -> FileSystemDocumentLoader.loadDocument(path.toString()))
                    .toList();
        }
        if (documents.isEmpty()) throw new IllegalStateException("docs 目录中没有可解析的 PDF、DOCX 或 MD 文档。");

        System.out.println("正在创建索引...");
        // from_documents 方法包含切片与建立索引步骤；显式指定与原 Cell 相同的 embedding 模型。
        // 百炼 text-embedding-v2 单次请求最多 25 条文本，需限制每批切片数量。
        var embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(key).baseUrl(baseUrl).modelName("text-embedding-v2")
                .maxSegmentsPerBatch(25).build();
        InMemoryEmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel)
                .embeddingStore(store)
                .build()
                .ingest(documents);

        System.out.println("正在创建提问引擎...");
        // 此处使用 qwen-plus 模型，你也可以使用其它 qwen 文本生成模型。
        var chatModel = OpenAiStreamingChatModel.builder()
                .apiKey(key).baseUrl(baseUrl).modelName("qwen-plus").build();
        var retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(store).embeddingModel(embeddingModel).maxResults(2).build();
        Assistant assistant = AiServices.builder(Assistant.class)
                .streamingChatModel(chatModel)
                .contentRetriever(retriever)
                .build();

        System.out.println("正在生成回复...");
        System.out.println("回答是：");
        // 采用流式输出
        CompletableFuture<Void> finished = new CompletableFuture<>();
        assistant.chat("我们公司项目管理应该用什么工具")
                .onPartialResponse(System.out::print)
                .onCompleteResponse(ignored -> finished.complete(null))
                .onError(finished::completeExceptionally)
                .start();
        finished.join();
        System.out.println();
    }

    private static boolean isCourseDocument(Path file) {
        String name = file.getFileName().toString().toLowerCase();
        return name.endsWith(".pdf") || name.endsWith(".docx") || name.endsWith(".md");
    }
}

// --- 原 Python Cell 注释原文（按原顺序保留） ---
// # 导入依赖
// # 跳过NLTK下载，因为LlamaIndex默认使用NLTK进行文本预处理，但在某些环境中可能会因为网络问题导致下载失败，设置环境变量LLAMA_INDEX_DISABLE_NLTK为1可以禁用NLTK的使用。
// # 这两行代码是用于消除 WARNING 警告信息，避免干扰阅读学习，生产环境中建议根据需要来设置日志级别
// # LlamaIndex提供了SimpleDirectoryReader方法，可以直接将指定文件夹中的文件加载为document对象，对应着解析过程
// # 这里只读取课程问答所需的文档格式，跳过 notebook 文件，避免触发 nbconvert 解析导致示例变慢。
// # from_documents方法包含切片与建立索引步骤
// # 指定embedding 模型
// # 你也可以使用阿里云提供的其它embedding模型：https://help.aliyun.com/zh/model-studio/getting-started/models#3383780daf8hw
// # 设置为流式输出
// # 此处使用qwen-plus模型，你也可以使用阿里云提供的其它qwen的文本生成模型：https://help.aliyun.com/zh/model-studio/getting-started/models#9f8890ce29g5u
// # 采用流式输出
