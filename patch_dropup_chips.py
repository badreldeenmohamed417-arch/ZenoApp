import re

file_path = '/home/badr-eldeen/AndroidStudioProjects/Zeno/app/src/main/java/com/example/zeno/features/session/ChatDropUp.kt'
with open(file_path, 'r') as f:
    content = f.read()

old_dropdown = """                                val quizTitle = stringResource(R.string.chat_action_quiz_title)
                                val quizPrompt = stringResource(R.string.auto_str_أنشئ_اختبار_تفاعلي)
                                val handoutTitle = stringResource(R.string.chat_action_handout_title)
                                val handoutPrompt = stringResource(R.string.auto_str_قم_بعمل_ملزمة)"""

new_dropdown = """                                val quizTitle = stringResource(R.string.chat_action_quiz_title)
                                val quizPrompt = stringResource(R.string.auto_str_أنشئ_اختبار_تفاعلي)
                                val handoutTitle = stringResource(R.string.chat_action_handout_title)
                                val handoutPrompt = stringResource(R.string.auto_str_قم_بعمل_ملزمة)
                                val solveTitle = "حل مسألة (دقيق)"
                                val solvePrompt = "قم بحل هذه المسألة بخطوات تفصيلية دقيقة واستخدم قوانين الفيزياء/الرياضيات (استخدم اللاتكس للمعادلات):" """

content = content.replace(old_dropdown, new_dropdown)

old_quiz_menu = """                                DropdownMenuItem(
                                    text = {"""

new_quiz_menu = """                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("حل فيزياء/رياضيات", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Box(modifier = Modifier.background(Color(0xFF8B5CF6).copy(alpha = 0.2f), RoundedCornerShape(6.dp)).border(0.5.dp, Color(0xFF8B5CF6), RoundedCornerShape(6.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                                Text("عميق", color = Color(0xFFC4B5FD), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    },
                                    leadingIcon = {
                                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF8B5CF6), modifier = Modifier.size(22.dp))
                                    },
                                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                                    onClick = {
                                        showActionMenu = false
                                        selectedActionChip = ChatActionChipData(Icons.Default.AutoAwesome, solveTitle, solvePrompt)
                                    }
                                )
                                DropdownMenuItem(
                                    text = {"""

content = content.replace(old_quiz_menu, new_quiz_menu, 1)

with open(file_path, 'w') as f:
    f.write(content)
print("Patched ChatDropUp Chips")
