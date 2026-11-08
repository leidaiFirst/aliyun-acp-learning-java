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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/** 2_2 章节各 Cell 共用的索引读写和查询步骤；每个 Cell 仍有自己的 main。 */
final class N22Rag {
    private N22Rag() {}

    interface Assistant {
        TokenStream chat(String question);
    }

    static Path indexFile() {
        Path repository = CoursePaths.documents().getParent().getParent().getParent();
        return repository.resolve("java-port-framework/knowledge_base/test/embeddings.json");
    }

    static String baseUrl() {
        String configured = Config.get("DASHSCOPE_BASE_URL");
        return configured == null ? "https://dashscope.aliyuncs.com/compatible-mode/v1" : configured;
    }

    static OpenAiEmbeddingModel embeddingModel() {
        return OpenAiEmbeddingModel.builder()
                .apiKey(Config.require("DASHSCOPE_API_KEY"))
                .baseUrl(baseUrl())
                .modelName("text-embedding-v2")
                .maxSegmentsPerBatch(25)
                .build();
    }

    static InMemoryEmbeddingStore<TextSegment> buildStore() throws IOException {
        Path docsPath = CoursePaths.documents();
        List<Document> documents;
        try (Stream<Path> files = Files.list(docsPath)) {
            documents = files.filter(Files::isRegularFile)
                    .filter(N22Rag::isCourseDocument)
                    .map(path -> FileSystemDocumentLoader.loadDocument(path.toString()))
                    .toList();
        }
        if (documents.isEmpty()) {
            throw new IllegalStateException("docs 目录中没有可解析的 PDF、DOCX 或 MD 文档。");
        }
        InMemoryEmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();
        EmbeddingStoreIngestor.builder()
                .embeddingModel(embeddingModel())
                .embeddingStore(store)
                .build()
                .ingest(documents);
        return store;
    }

    static void saveStore(InMemoryEmbeddingStore<TextSegment> store) throws IOException {
        Path file = indexFile();
        Files.createDirectories(file.getParent());
        store.serializeToFile(file);
    }

    static InMemoryEmbeddingStore<TextSegment> loadStore() {
        Path file = indexFile();
        if (!Files.isRegularFile(file)) {
            throw new IllegalStateException("未找到索引 " + file + "；请先运行 c2.n22.Cell12。");
        }
        return InMemoryEmbeddingStore.fromFile(file);
    }

    static InMemoryEmbeddingStore<TextSegment> loadOrBuildStore() throws IOException {
        if (Files.isRegularFile(indexFile())) {
            System.out.println("正在加载索引：" + indexFile());
            return loadStore();
        }
        System.out.println("尚无保存的索引，正在解析文档并创建索引...");
        InMemoryEmbeddingStore<TextSegment> store = buildStore();
        saveStore(store);
        System.out.println("索引已保存：" + indexFile());
        return store;
    }

    static Assistant queryEngine(InMemoryEmbeddingStore<TextSegment> store) {
        String key = Config.require("DASHSCOPE_API_KEY");
        var retriever = EmbeddingStoreContentRetriever.builder()
                .embeddingStore(store)
                .embeddingModel(embeddingModel())
                .maxResults(2)
                .build();
        var model = OpenAiStreamingChatModel.builder()
                .apiKey(key).baseUrl(baseUrl()).modelName("qwen-plus").build();
        return AiServices.builder(Assistant.class)
                .streamingChatModel(model)
                .contentRetriever(retriever)
                .build();
    }

    static void ask(Assistant assistant, String question) {
        CompletableFuture<Void> finished = new CompletableFuture<>();
        assistant.chat(question)
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
