import re

with open('app/src/main/java/com/example/zeno/core/navigation/RootNavGraph.kt', 'r') as f:
    content = f.read()

# Add koinInject import
if 'import org.koin.compose.koinInject' not in content:
    content = content.replace('import androidx.compose.runtime.Composable', 'import androidx.compose.runtime.Composable\nimport org.koin.compose.koinInject')

# Replace parameters
old_params = '''    authRepository: AuthRepository, 
    studentRepository: StudentRepository,
    progressRepository: ProgressRepository,
    chatRepository: ChatRepository,
    sessionRepository: SessionRepository,
    studyPlanRepository: StudyPlanRepository,
    configRepository: com.example.zeno.core.config.data.repository.ConfigRepository'''
    
if old_params in content:
    content = content.replace(old_params, '')

# Add injects
injects = '''    val authRepository: AuthRepository = koinInject()
    val studentRepository: StudentRepository = koinInject()
    val progressRepository: ProgressRepository = koinInject()
    val chatRepository: ChatRepository = koinInject()
    val sessionRepository: SessionRepository = koinInject()
    val studyPlanRepository: StudyPlanRepository = koinInject()
    val configRepository: com.example.zeno.core.config.data.repository.ConfigRepository = koinInject()
'''

if 'val authRepository: AuthRepository = koinInject()' not in content:
    content = content.replace('val navController = rememberNavController()', injects + '    val navController = rememberNavController()')

with open('app/src/main/java/com/example/zeno/core/navigation/RootNavGraph.kt', 'w') as f:
    f.write(content)
