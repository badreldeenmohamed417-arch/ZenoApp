import re

file_path = '/home/badr-eldeen/AndroidStudioProjects/Zeno/app/src/main/java/com/example/zeno/core/navigation/RootNavGraph.kt'
with open(file_path, 'r') as f:
    content = f.read()

# We want to replace the `else` blocks that handle network failure (not 401).

old_logic_1 = """                        } else {
                            val nextDest = if (!userManager.isOnboarded()) {
                                "setup"
                            } else if (!userManager.isAssessmentCompleted()) {
                                "assessment"
                            } else {
                                "main"
                            }
                            navController.navigate(nextDest) {
                                popUpTo("splash") { inclusive = true }
                            }
                        }"""

new_logic = """                        } else {
                            // No internet or timeout
                            if (userManager.isOnboarded() && userManager.isAssessmentCompleted()) {
                                navController.navigate("main") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            } else {
                                authRepository.logout()
                                navController.navigate("auth") {
                                    popUpTo("splash") { inclusive = true }
                                }
                            }
                        }"""

# Replace both occurrences
content = content.replace(old_logic_1, new_logic)

with open(file_path, 'w') as f:
    f.write(content)
print("Patched RootNavGraph fallback logic")
