# 阿里云 ACP 中文课程 Java 对照项目

原始 Notebook 保存在仓库的 `大模型ACP认证教程` 下。Java 源码按 `章节/Notebook/Cell` 对应：例如 Python 文件 `C2_构造问答系统/2_1_用大模型构建新人答疑机器人.ipynb` 的 `cells[6]` 对应 `com.aliyun.acp.c2.n21.Cell06`。每个已翻译 Cell 都有独立的 `public static void main(String[] args)`；原注释保留在对应 Java 文件中。完整进度见 [CELL_INDEX.md](CELL_INDEX.md)。

## 本地运行

项目内已下载 JDK 21 和 Maven，存放在忽略 Git 的 `.tools/`，无需系统安装。Qoder 打开本目录后可按 Maven 项目导入。

在仓库根目录 `.env` 填入 `DASHSCOPE_API_KEY=你的百炼密钥`，或设置环境变量。不要把密钥写入 Java 源码。`DASHSCOPE_BASE_URL` 可选，默认沿用课程里的 `https://dashscope.aliyuncs.com/compatible-mode/v1`。

```bash
cd java-port
./run-cell.sh Cell06
```

`CellXX` 简写对应 `2_1` Notebook；其他入口使用 `章节.Notebook.Cell`，例如 `./run-cell.sh c3.n31.Cell07`。所有已翻译源码也可直接在 IDE 中运行 `main`。`2_1` 的 `Cell16`、`Cell17`、`Cell19` 和 `Cell20` 各调用 API 十次，与原 Python 实验一致。

当前完成 `2_1` Notebook 的全部 11 个代码 Cell，以及 `3_1` Notebook 的前 6 个代码 Cell；其余代码 Cell 见对照表中的“待翻译”。没有配置百炼密钥时，API 相关入口会提示配置，尚不能执行实际模型调用。
