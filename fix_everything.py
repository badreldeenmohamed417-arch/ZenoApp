import re
import os

# 1. MainActivity: Restore userManager
with open('app/src/main/java/com/example/zeno/MainActivity.kt', 'r') as f:
    main = f.read()

if 'val userManager = com.example.zeno.data.local.UserManager(this)' not in main:
    main = main.replace('super.onCreate(savedInstanceState)\n', 'super.onCreate(savedInstanceState)\n        val userManager = com.example.zeno.data.local.UserManager(this)\n')
    with open('app/src/main/java/com/example/zeno/MainActivity.kt', 'w') as f:
        f.write(main)

# 2. AppModule: Restore correct parameters for ViewModels
with open('app/src/main/java/com/example/zeno/core/di/AppModule.kt', 'r') as f:
    app_module = f.read()

app_module = app_module.replace('ProfileViewModel(get())', 'ProfileViewModel(get(), get(), get(), get())')
app_module = app_module.replace('SessionsViewModel(get())', 'SessionsViewModel(get(), get())')
app_module = app_module.replace('PremiumViewModel(get())', 'PremiumViewModel(get(), get())')
app_module = app_module.replace('HomeViewModel(get())', 'HomeViewModel(get(), get(), get())')

with open('app/src/main/java/com/example/zeno/core/di/AppModule.kt', 'w') as f:
    f.write(app_module)

# 3. Add import org.koin.compose.koinInject safely using package line as anchor
files_to_fix = [
    'app/src/main/java/com/example/zeno/features/profile/presentation/ProfileScreen.kt',
    'app/src/main/java/com/example/zeno/features/setup/presentation/SetupProfileScreen.kt',
    'app/src/main/java/com/example/zeno/features/setup/grade.kt',
    'app/src/main/java/com/example/zeno/features/session/ChatDropUp.kt',
    'app/src/main/java/com/example/zeno/features/main/SettingsScreen.kt',
    'app/src/main/java/com/example/zeno/core/navigation/RootNavGraph.kt'
]

for filepath in files_to_fix:
    if os.path.exists(filepath):
        with open(filepath, 'r') as f:
            content = f.read()
            
        if 'import org.koin.compose.koinInject' not in content:
            content = re.sub(r'(package com\.example\.zeno[^\n]*\n)', r'\1\nimport org.koin.compose.koinInject\n', content)
            with open(filepath, 'w') as f:
                f.write(content)

