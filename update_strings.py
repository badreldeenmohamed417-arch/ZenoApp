import re
import os

files_to_check = [
    "app/src/main/java/com/example/zeno/features/assessment/presentation/AssessmentChatViewModel.kt",
    "app/src/main/java/com/example/zeno/features/assessment/data/LearningProfile.kt",
    "app/src/main/java/com/example/zeno/features/premium/PremiumScreen.kt",
    "app/src/main/java/com/example/zeno/features/session/presentation/SessionsScreen.kt",
    "app/src/main/java/com/example/zeno/features/chat/presentation/ChatScreen.kt",
    "app/src/main/java/com/example/zeno/features/home/presentation/HomeScreen.kt",
    "app/src/main/java/com/example/zeno/features/main/SettingsScreen.kt",
    "app/src/main/java/com/example/zeno/core/SplashScreen.kt",
    "app/src/main/java/com/example/zeno/core/sections/setup/NameAge/main.kt"
]

def contains_arabic(text):
    return any('\u0600' <= c <= '\u06FF' for c in text)

for file_path in files_to_check:
    if not os.path.exists(file_path):
        continue
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # Find all string literals
    matches = re.finditer(r'"([^"\\]*(\\.[^"\\]*)*)"', content)
    for m in matches:
        text = m.group(1)
        if contains_arabic(text):
            print(f"{file_path}: {text}")
