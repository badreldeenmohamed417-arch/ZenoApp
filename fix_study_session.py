import re
import os

file_path = "app/src/main/java/com/example/zeno/features/session/StudySessionScreen.kt"

with open(file_path, 'r', encoding='utf-8') as f:
    content = f.read()

if "com.example.zeno.core.txtStr" not in content and "txtStr" in content:
    content = re.sub(r'(package com\.example\.zeno.*?\n)', r'\1\nimport com.example.zeno.core.txtStr\n', content)

with open(file_path, 'w', encoding='utf-8') as f:
    f.write(content)

