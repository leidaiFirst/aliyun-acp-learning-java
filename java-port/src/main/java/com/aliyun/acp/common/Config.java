package com.aliyun.acp.common;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class Config {
    private Config() {}

    public static String get(String name) {
        String value = System.getenv(name);
        if (value != null && !value.isBlank()) return value;
        Path directory = Path.of("").toAbsolutePath();
        while (directory != null) {
            Path env = directory.resolve(".env");
            if (Files.isRegularFile(env)) {
                try {
                    List<String> lines = Files.readAllLines(env);
                    for (String line : lines) {
                        String prefix = name + "=";
                        if (line.startsWith(prefix)) {
                            String result = line.substring(prefix.length()).trim();
                            if (result.length() >= 2 && result.startsWith("\"") && result.endsWith("\"")) {
                                result = result.substring(1, result.length() - 1);
                            }
                            if (!result.isBlank()) return result;
                        }
                    }
                } catch (IOException e) {
                    throw new IllegalStateException("无法读取 " + env, e);
                }
            }
            directory = directory.getParent();
        }
        return null;
    }

    public static String require(String name) {
        String value = get(name);
        if (value == null) throw new IllegalStateException("请在项目根目录 .env 中配置 " + name + "，或设置同名环境变量。");
        return value;
    }
}
