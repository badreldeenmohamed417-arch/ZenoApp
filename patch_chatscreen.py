import re

file_path = '/home/badr-eldeen/AndroidStudioProjects/Zeno/app/src/main/java/com/example/zeno/features/chat/presentation/ChatScreen.kt'
with open(file_path, 'r') as f:
    content = f.read()

old_has_input = "val hasInput = inputText.trim().isNotBlank() || selectedActionChip != null"
new_has_input = "val hasInput = inputText.trim().isNotBlank()"
content = content.replace(old_has_input, new_has_input)

old_send = """                                    val fullMessage = listOfNotNull(selectedActionChip?.prompt, inputText.trim().ifBlank { null }).joinToString(" ")
                                    if (fullMessage.isNotBlank() && !isTyping) {
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                        selectedActionChip = null
                                        viewModel.sendMessage(fullMessage)
                                        inputText = ""
                                    }"""

new_send = """                                    if (inputText.trim().isNotBlank() && !isTyping) {
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                        
                                        val intentStr = if (selectedActionChip?.title == "اختبار قصير") "[intent: CREATE_TEST]" else if (selectedActionChip?.title?.contains("ملخص") == true) "[intent: CREATE_HANDOUT]" else null
                                        val textToSend = inputText.trim()
                                        
                                        selectedActionChip = null
                                        viewModel.sendMessage(textToSend, intentStr)
                                        inputText = ""
                                    }"""
content = content.replace(old_send, new_send)

with open(file_path, 'w') as f:
    f.write(content)
print("Patched ChatScreen")
