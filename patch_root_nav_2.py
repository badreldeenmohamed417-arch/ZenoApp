import re

file_path = '/home/badr-eldeen/AndroidStudioProjects/Zeno/app/src/main/java/com/example/zeno/core/navigation/RootNavGraph.kt'
with open(file_path, 'r') as f:
    content = f.read()

old_logic_2 = """                    } else {
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

new_logic_2 = """                    } else {
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

content = content.replace(old_logic_2, new_logic_2)

with open(file_path, 'w') as f:
    f.write(content)
print("Patched second occurrence")
