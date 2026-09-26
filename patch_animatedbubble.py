import re

file_path = '/home/badr-eldeen/AndroidStudioProjects/Zeno/app/src/main/java/com/example/zeno/features/session/ChatDropUp.kt'
with open(file_path, 'r') as f:
    content = f.read()

old_uimsg = """    // Creating a ChatMessage from MessageEntity to pass to ChatBubble
    val uiMsg = com.example.zeno.features.chat.domain.ChatMessage(
        id = msg.id,
        text = displayedText,
        isUser = msg.role == "user",
        timestamp = 0L // Doesn't matter for display
    )
    com.example.zeno.core.widgets.ChatBubble(uiMsg)"""

new_uimsg = """    val uiMsg = com.example.zeno.data.model.server.MessageResponse(
        id = msg.id,
        conversationId = msg.conversationId,
        role = msg.role,
        content = displayedText,
        createdAt = msg.createdAt
    )
    com.example.zeno.core.widgets.ChatBubble(uiMsg)"""

content = content.replace(old_uimsg, new_uimsg)

with open(file_path, 'w') as f:
    f.write(content)
print("AnimatedBubble patched")
