import re
import os

# For composable screens, use koinInject()
files_to_fix = [
    'app/src/main/java/com/example/zeno/features/profile/presentation/ProfileScreen.kt',
    'app/src/main/java/com/example/zeno/features/setup/presentation/SetupProfileScreen.kt',
    'app/src/main/java/com/example/zeno/features/setup/grade.kt',
    'app/src/main/java/com/example/zeno/features/session/ChatDropUp.kt',
    'app/src/main/java/com/example/zeno/features/main/SettingsScreen.kt'
]

for filepath in files_to_fix:
    if os.path.exists(filepath):
        with open(filepath, 'r') as f:
            content = f.read()
            
        orig = content
        
        # Add import org.koin.compose.koinInject
        if 'import org.koin.compose.koinInject' not in content:
            content = content.replace('import androidx.compose.runtime.Composable', 'import androidx.compose.runtime.Composable\nimport org.koin.compose.koinInject')
            
        # Replace GlobalContext... with koinInject()
        content = re.sub(r'org\.koin\.core\.context\.GlobalContext\.get\(\)\.get<([\w\.]+)>', r'koinInject<\1>', content)
        
        if orig != content:
            with open(filepath, 'w') as f:
                f.write(content)

