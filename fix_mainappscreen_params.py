import re

with open('app/src/main/java/com/example/zeno/features/main/presentation/MainAppScreen.kt', 'r') as f:
    content = f.read()

old_params = '''    studentRepository: StudentRepository, 
    progressRepository: ProgressRepository,
    chatRepository: ChatRepository,
    sessionRepository: SessionRepository,
    studyPlanRepository: StudyPlanRepository,
    configRepository: com.example.zeno.core.config.data.repository.ConfigRepository,'''

if old_params in content:
    content = content.replace(old_params, '')

# also remove val subscriptionRepository = ServiceLocator.subscriptionRepository
if 'val subscriptionRepository = ServiceLocator.subscriptionRepository' in content:
    content = content.replace('val subscriptionRepository = ServiceLocator.subscriptionRepository', '')

with open('app/src/main/java/com/example/zeno/features/main/presentation/MainAppScreen.kt', 'w') as f:
    f.write(content)
