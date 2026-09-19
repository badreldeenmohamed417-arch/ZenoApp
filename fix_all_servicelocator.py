import os
import re

repos = {
    'studentRepository': 'com.example.zeno.features.student.data.repository.StudentRepository',
    'authRepository': 'com.example.zeno.features.auth.data.AuthRepository',
    'chatRepository': 'com.example.zeno.features.chat.data.repository.ChatRepository',
    'legacyChatRepository': 'com.example.zeno.data.repository.ChatRepository',
    'sessionRepository': 'com.example.zeno.features.session.data.repository.SessionRepository',
    'studyPlanRepository': 'com.example.zeno.features.session.data.repository.StudyPlanRepository',
    'subscriptionRepository': 'com.example.zeno.features.premium.data.repository.SubscriptionRepository',
    'configRepository': 'com.example.zeno.core.config.data.repository.ConfigRepository',
}

for root, _, files in os.walk('app/src/main/java/com/example/zeno'):
    for file in files:
        if file.endswith('.kt'):
            path = os.path.join(root, file)
            with open(path, 'r') as f:
                content = f.read()
                
            orig = content
            for repo, class_path in repos.items():
                # Replace inline usage
                if repo == 'legacyChatRepository':
                    content = content.replace(f'ServiceLocator.{repo}', f'org.koin.core.context.GlobalContext.get().get<{class_path}>(org.koin.core.qualifier.named("legacyChat"))')
                else:
                    content = content.replace(f'ServiceLocator.{repo}', f'org.koin.core.context.GlobalContext.get().get<{class_path}>()')
            
            # Remove any leftover import of ServiceLocator
            content = re.sub(r'import com\.example\.zeno\.core\.di\.ServiceLocator\n?', '', content)
            
            # Since I already messed up some files by replacing with koinInject, let me fix them too
            if 'koinInject' in content:
                for repo, class_path in repos.items():
                    if repo == 'legacyChatRepository':
                        content = re.sub(r'val \w+:.*? = koinInject\(org\.koin\.core\.qualifier\.named\("legacyChat"\)\)', f'val {repo} = org.koin.core.context.GlobalContext.get().get<{class_path}>(org.koin.core.qualifier.named("legacyChat"))', content)
                    else:
                        content = re.sub(fr'val \w+: {class_path} = koinInject\(\)', f'val {repo} = org.koin.core.context.GlobalContext.get().get<{class_path}>()', content)
                content = content.replace('import org.koin.compose.koinInject\n', '')
            
            if content != orig:
                with open(path, 'w') as f:
                    f.write(content)

