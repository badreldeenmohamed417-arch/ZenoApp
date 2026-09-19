import re
import os
import xml.etree.ElementTree as ET

tree = ET.parse('app/src/main/res/values-ar/strings.xml')
root = tree.getroot()
str_map = {}
for string_elem in root.findall('string'):
    key = string_elem.get('name')
    val = string_elem.text
    if val:
        str_map[val.strip()] = key

files_to_check = [
    "app/src/main/java/com/example/zeno/features/profile/presentation/ProfileViewModel.kt",
    "app/src/main/java/com/example/zeno/features/student/data/repository/StudentRepository.kt",
    "app/src/main/java/com/example/zeno/features/assessment/presentation/AssessmentChatScreen.kt",
    "app/src/main/java/com/example/zeno/features/assessment/presentation/AssessmentChatViewModel.kt",
    "app/src/main/java/com/example/zeno/features/assessment/data/LearningProfile.kt",
    "app/src/main/java/com/example/zeno/features/setup/presentation/SetupProfileScreen.kt",
    "app/src/main/java/com/example/zeno/features/premium/presentation/PremiumViewModel.kt",
    "app/src/main/java/com/example/zeno/features/session/StudySessionScreen.kt",
    "app/src/main/java/com/example/zeno/features/session/data/dto/StudyPlanDTOs.kt",
    "app/src/main/java/com/example/zeno/features/auth/presentation/RegisterScreen.kt",
    "app/src/main/java/com/example/zeno/features/auth/presentation/LanguageSelectionScreen.kt",
    "app/src/main/java/com/example/zeno/features/chat/presentation/ChatScreen.kt",
    "app/src/main/java/com/example/zeno/features/chat/presentation/ChatViewModel.kt",
    "app/src/main/java/com/example/zeno/features/home/presentation/HomeScreen.kt",
    "app/src/main/java/com/example/zeno/features/main/SettingsScreen.kt",
    "app/src/main/java/com/example/zeno/core/sections/main/middle.kt",
    "app/src/main/java/com/example/zeno/core/network/TokenAuthenticator.kt",
    "app/src/main/java/com/example/zeno/core/widgets/isThinking.kt"
]

def contains_arabic(text):
    return any('\u0600' <= c <= '\u06FF' for c in text)

for file_path in files_to_check:
    if not os.path.exists(file_path): continue
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
        
    matches = list(re.finditer(r'"([^"\\]*(\\.[^"\\]*)*)"', content))
    
    # We replace from bottom to top to not mess up indices
    for m in reversed(matches):
        text = m.group(1)
        if contains_arabic(text):
            stripped_text = text.strip()
            if stripped_text in str_map:
                key = str_map[stripped_text]
                start = m.start()
                end = m.end()
                
                # Check if it's a UI file
                if file_path.endswith("Screen.kt") or file_path.endswith("middle.kt") or file_path.endswith("isThinking.kt"):
                    replacement = f'txt("{key}")'
                else:
                    replacement = f'"{key}"'
                
                content = content[:start] + replacement + content[end:]

    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)

