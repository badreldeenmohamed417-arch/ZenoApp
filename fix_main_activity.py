import re

with open('app/src/main/java/com/example/zeno/MainActivity.kt', 'r') as f:
    content = f.read()

# Add Koin imports
if 'import org.koin.android.ext.android.inject' not in content:
    content = content.replace('import android.os.Bundle', 'import android.os.Bundle\nimport org.koin.android.ext.android.inject')

# Add billing manager injection
if 'val billingManager: BillingManager by inject()' not in content:
    content = content.replace('class MainActivity : ComponentActivity() {', 'class MainActivity : ComponentActivity() {\n    private val billingManager: BillingManager by inject()')

# Remove manual initializations
start = content.find('        ApiClient.initialize(this.applicationContext)')
end = content.find('        val pushNotificationService = com.example.zeno.features.notification.data.ZenoFirebaseMessagingServiceImpl()')

if start != -1 and end != -1:
    content = content[:start] + '        billingManager.initialize()\n\n' + content[end:]

# Replace RootNavGraph call
root_nav_graph = '''                        RootNavGraph(
                            authRepository = authRepository,
                            studentRepository = studentRepository,
                            progressRepository = progressRepository,
                            chatRepository = chatRepository,
                            sessionRepository = sessionRepository,
                            studyPlanRepository = studyPlanRepository,
                            configRepository = configRepository
                        )'''
if root_nav_graph in content:
    content = content.replace(root_nav_graph, '                        RootNavGraph()')

with open('app/src/main/java/com/example/zeno/MainActivity.kt', 'w') as f:
    f.write(content)
