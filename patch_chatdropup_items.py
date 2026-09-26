import re

file_path = '/home/badr-eldeen/AndroidStudioProjects/Zeno/app/src/main/java/com/example/zeno/features/session/ChatDropUp.kt'
with open(file_path, 'r') as f:
    content = f.read()

old_items = """                items(localMessages, key = { it.id }) { msg ->
                    ChatBubble(msg)
                }"""

new_items = """                items(localMessages, key = { it.id }) { msg ->
                    AnimatedChatBubble(msg, isLatest = (msg.id == localMessages.lastOrNull()?.id))
                }"""

content = content.replace(old_items, new_items)

with open(file_path, 'w') as f:
    f.write(content)

print("Items patched")
