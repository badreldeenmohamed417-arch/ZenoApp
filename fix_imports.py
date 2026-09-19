import re
import os

files_to_fix = [
    "app/src/main/java/com/example/zeno/features/auth/presentation/LanguageSelectionScreen.kt",
    "app/src/main/java/com/example/zeno/features/chat/presentation/ChatScreen.kt",
    "app/src/main/java/com/example/zeno/features/home/presentation/HomeScreen.kt"
]

for file_path in files_to_fix:
    if not os.path.exists(file_path): continue
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    if "com.example.zeno.core.txt" not in content and "txt(" in content:
        content = re.sub(r'(package com\.example\.zeno.*?\n)', r'\1\nimport com.example.zeno.core.txt\n', content)

    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

