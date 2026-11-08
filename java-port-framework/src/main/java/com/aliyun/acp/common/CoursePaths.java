package com.aliyun.acp.common;

import java.nio.file.Files;
import java.nio.file.Path;

public final class CoursePaths {
    private CoursePaths() {}

    public static Path documents() {
        Path current = Path.of("").toAbsolutePath();
        while (current != null) {
            Path docs = current.resolve("大模型ACP认证教程/course_core/docs");
            if (Files.isDirectory(docs)) return docs;
            current = current.getParent();
        }
        throw new IllegalStateException("未找到 大模型ACP认证教程/course_core/docs；请从课程仓库运行。");
    }
}
