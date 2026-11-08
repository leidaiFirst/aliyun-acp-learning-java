#!/usr/bin/env python3
"""列出中文主课程所有非空代码 cell，并检查对应 Java main 是否存在。"""
import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[2]
JAVA_PORT = ROOT / "java-port"
COURSE = ROOT / "大模型ACP认证教程"
JAVA = JAVA_PORT / "src/main/java/com/aliyun/acp"

rows = []
notebooks = sorted(COURSE.glob("C*/*.ipynb"))
for notebook in notebooks:
    data = json.loads(notebook.read_text(encoding="utf-8"))
    chapter = notebook.parent.name.split("_")[0].lower()
    section = "n" + "".join(notebook.stem.split("_")[:2])
    for zero_based, cell in enumerate(data["cells"]):
        source = "".join(cell.get("source", [])).strip()
        if cell["cell_type"] != "code" or not source:
            continue
        class_name = f"Cell{zero_based:02d}"
        package = f"com.aliyun.acp.{chapter}.{section}"
        java_file = JAVA / chapter / section / f"{class_name}.java"
        status = "已翻译" if java_file.is_file() else "待翻译"
        first_line = next((line.strip().lstrip("# ") for line in source.splitlines() if line.strip()), "")
        first_line = first_line.replace("|", "\\|").replace("`", "'")[:65]
        link = f"[{class_name}](src/main/java/com/aliyun/acp/{chapter}/{section}/{class_name}.java)" if java_file.is_file() else class_name
        rows.append((notebook.relative_to(ROOT).as_posix(), zero_based, package, link, status, first_line))

done = sum(row[4] == "已翻译" for row in rows)
lines = [
    "# 中文主课程 Cell 对照表",
    "",
    f"共 {len(notebooks)} 个 Notebook、{len(rows)} 个非空代码 Cell；已翻译 {done} 个。",
    "",
    "Cell 编号采用 `.ipynb` 文件内的 **0 起始索引**，与源文件的 `cells[index]` 一一对应。Markdown 和空代码 Cell 无运行入口。",
    "",
    "| Notebook | Cell | Java 主类 | 状态 | 原单元首行 |",
    "| --- | ---: | --- | --- | --- |",
]
for notebook, cell, package, link, status, first_line in rows:
    lines.append(f"| `{notebook}` | {cell} | `{package}.`{link} | {status} | {first_line} |")
(JAVA_PORT / "CELL_INDEX.md").write_text("\n".join(lines) + "\n", encoding="utf-8")
print(f"已更新 CELL_INDEX.md：{done}/{len(rows)} 个代码 Cell")
