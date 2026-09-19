import os
import re

def replace_in_file(filepath, replacements, add_imports):
    with open(filepath, 'r') as f:
        content = f.read()
        
    for old, new in replacements:
        content = content.replace(old, new)
        
    for imp in add_imports:
        if imp not in content:
            content = content.replace('import androidx.compose.runtime.Composable', f'import androidx.compose.runtime.Composable\n{imp}')
            
    with open(filepath, 'w') as f:
        f.write(content)

# ProfileScreen.kt
replace_in_file('app/src/main/java/com/example/zeno/features/profile/presentation/ProfileScreen.kt', 
    [('val authRepository = ServiceLocator.authRepository', 'val authRepository: com.example.zeno.features.auth.data.AuthRepository = koinInject()'), 
     ('import com.example.zeno.core.di.ServiceLocator', '')],
    ['import org.koin.compose.koinInject'])

# SetupProfileScreen.kt
replace_in_file('app/src/main/java/com/example/zeno/features/setup/presentation/SetupProfileScreen.kt', 
    [('val userRepo = ServiceLocator.studentRepository', 'val userRepo: com.example.zeno.features.student.data.repository.StudentRepository = koinInject()'), 
     ('import com.example.zeno.core.di.ServiceLocator', '')],
    ['import org.koin.compose.koinInject'])

# grade.kt
replace_in_file('app/src/main/java/com/example/zeno/features/setup/grade.kt', 
    [('val authRepository = ServiceLocator.authRepository', 'val authRepository: com.example.zeno.features.auth.data.AuthRepository = koinInject()'), 
     ('import com.example.zeno.core.di.ServiceLocator', '')],
    ['import org.koin.compose.koinInject'])

# PremiumScreen.kt
replace_in_file('app/src/main/java/com/example/zeno/features/premium/PremiumScreen.kt', 
    [('viewModel: PremiumViewModel = viewModel(factory = object : ViewModelProvider.Factory {\\n        override fun <T : ViewModel> create(modelClass: Class<T>): T = \\n            PremiumViewModel(ServiceLocator.subscriptionRepository) as T\\n    })', 'viewModel: PremiumViewModel = koinViewModel()'), 
     ('import com.example.zeno.core.di.ServiceLocator', '')],
    ['import org.koin.androidx.compose.koinViewModel'])

# ChatDropUp.kt
replace_in_file('app/src/main/java/com/example/zeno/features/session/ChatDropUp.kt', 
    [('val chatRepository = ServiceLocator.legacyChatRepository', 'val chatRepository: com.example.zeno.data.repository.ChatRepository = koinInject(org.koin.core.qualifier.named("legacyChat"))'), 
     ('import com.example.zeno.core.di.ServiceLocator', '')],
    ['import org.koin.compose.koinInject'])

