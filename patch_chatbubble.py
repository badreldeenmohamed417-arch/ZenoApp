import re

file_path = '/home/badr-eldeen/AndroidStudioProjects/Zeno/app/src/main/java/com/example/zeno/core/widgets/ChatBubble.kt'
with open(file_path, 'r') as f:
    content = f.read()

old_box = """            Box(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = bottomStart,
                            bottomEnd = bottomEnd
                        )
                    )
                    .background(if (isUser) AppColors.Accent else AppColors.SurfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.content,
                    color = if (isUser) AppColors.AccentInk else AppColors.TextPrimary,
                    fontSize = 15.sp,
                    lineHeight = 20.sp
                )
            }"""

new_box = """            Box(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = bottomStart,
                            bottomEnd = bottomEnd
                        )
                    )
                    .background(if (isUser) AppColors.Accent else AppColors.SurfaceVariant)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                if (isUser) {
                    Text(
                        text = message.content,
                        color = AppColors.AccentInk,
                        fontSize = 15.sp,
                        lineHeight = 20.sp
                    )
                } else {
                    MarkdownMathView(
                        text = message.content,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }"""

content = content.replace(old_box, new_box)

with open(file_path, 'w') as f:
    f.write(content)
print("Patched ChatBubble")
