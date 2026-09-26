import re

file_path = '/home/badr-eldeen/AndroidStudioProjects/Zeno/app/src/main/java/com/example/zeno/features/chat/presentation/ChatViewModel.kt'
with open(file_path, 'r') as f:
    content = f.read()

old_func_def = "    fun sendMessage(text: String) {"
new_func_def = "    fun sendMessage(text: String, hiddenPrefix: String? = null) {"
content = content.replace(old_func_def, new_func_def)

old_send_repo = "            val result = repository.sendConversationMessage(activeId, text)"
new_send_repo = """            val fullText = if (hiddenPrefix != null) "$hiddenPrefix $text" else text
            val result = repository.sendConversationMessage(activeId, fullText)"""
content = content.replace(old_send_repo, new_send_repo)

with open(file_path, 'w') as f:
    f.write(content)
print("Patched ChatViewModel")
