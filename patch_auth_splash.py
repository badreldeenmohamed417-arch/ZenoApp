import re

file_path = '/home/badr-eldeen/AndroidStudioProjects/Zeno/app/src/main/java/com/example/zeno/core/navigation/RootNavGraph.kt'
with open(file_path, 'r') as f:
    content = f.read()

# For auth
old_auth_success = """                onAuthSuccess = {
                    navController.navigate("splash") {
                        popUpTo("auth") { inclusive = true }
                    }
                }"""
new_auth_success = """                onAuthSuccess = {
                    val nextDest = if (!userManager.isOnboarded()) "setup"
                    else if (!userManager.isAssessmentCompleted()) "assessment"
                    else "main"
                    navController.navigate(nextDest) {
                        popUpTo("auth") { inclusive = true }
                    }
                }"""
content = content.replace(old_auth_success, new_auth_success)

# For email verification launched effect
old_email_effect = """            LaunchedEffect(isVerified) {
                if (isVerified) {
                    navController.navigate("splash") {
                        popUpTo("email_verification") { inclusive = true }
                    }
                }
            }"""
new_email_effect = """            LaunchedEffect(isVerified) {
                if (isVerified) {
                    val nextDest = if (!userManager.isOnboarded()) "setup"
                    else if (!userManager.isAssessmentCompleted()) "assessment"
                    else "main"
                    navController.navigate(nextDest) {
                        popUpTo("email_verification") { inclusive = true }
                    }
                }
            }"""
content = content.replace(old_email_effect, new_email_effect)

# For email verification callback
old_email_verified = """                onVerified = {
                    navController.navigate("splash") {
                        popUpTo("email_verification") { inclusive = true }
                    }
                },"""
new_email_verified = """                onVerified = {
                    val nextDest = if (!userManager.isOnboarded()) "setup"
                    else if (!userManager.isAssessmentCompleted()) "assessment"
                    else "main"
                    navController.navigate(nextDest) {
                        popUpTo("email_verification") { inclusive = true }
                    }
                },"""
content = content.replace(old_email_verified, new_email_verified)

with open(file_path, 'w') as f:
    f.write(content)
print("Patched auth and email verification navigation")
