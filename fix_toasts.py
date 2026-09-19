import re
import os

files_to_fix = [
    "app/src/main/java/com/example/zeno/features/profile/presentation/AccountDataScreen.kt",
    "app/src/main/java/com/example/zeno/features/profile/presentation/ProfileScreen.kt"
]

for file_path in files_to_fix:
    if not os.path.exists(file_path): continue
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # We want to replace Toast.makeText(context, msg, ...
    # with Toast.makeText(context, context.txtStr(msg), ...
    content = re.sub(r'Toast\.makeText\((context),\s*(msg),\s*(Toast\.LENGTH_\w+)\)', r'Toast.makeText(\1, \1.txtStr(\2), \3)', content)
    content = re.sub(r'Toast\.makeText\((context),\s*(toastMsg),\s*(Toast\.LENGTH_\w+)\)', r'Toast.makeText(\1, \1.txtStr(\2), \3)', content)
    content = re.sub(r'Toast\.makeText\((context),\s*(resetPasswordSentMsg),\s*(Toast\.LENGTH_\w+)\)', r'Toast.makeText(\1, \1.txtStr(\2), \3)', content)
    content = re.sub(r'Toast\.makeText\((context),\s*(mismatchMsg),\s*(Toast\.LENGTH_\w+)\)', r'Toast.makeText(\1, \1.txtStr(\2), \3)', content)

    # We also need to import txtStr!
    if "com.example.zeno.core.txtStr" not in content and "txtStr" in content:
        # insert import com.example.zeno.core.txtStr right after package declaration
        content = re.sub(r'(package com\.example\.zeno.*?\n)', r'\1\nimport com.example.zeno.core.txtStr\n', content)

    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

