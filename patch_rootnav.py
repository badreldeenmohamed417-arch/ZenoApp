import re

file_path = '/home/badr-eldeen/AndroidStudioProjects/Zeno/app/src/main/java/com/example/zeno/core/navigation/RootNavGraph.kt'
with open(file_path, 'r') as f:
    lines = f.readlines()

start_idx = -1
end_idx = -1

for i, line in enumerate(lines):
    if "val startDestination = if (!userManager.isInitialLanguageSelected()) {" in line:
        start_idx = i
    if "composable(\"auth\") {" in line:
        end_idx = i
        break

if start_idx != -1 and end_idx != -1:
    new_block = """    val startDestination = if (!userManager.isInitialLanguageSelected()) {
        "language_selection"
    } else if (authRepository.isLoggedIn()) {
        if (!userManager.isOnboarded()) {
            "setup"
        } else if (!userManager.isAssessmentCompleted()) {
            "assessment"
        } else {
            "main"
        }
    } else {
        "auth"
    }

    LaunchedEffect(authRepository.isLoggedIn()) {
        if (authRepository.isLoggedIn()) {
            try {
                val result = studentRepository.getProfile()
                if (result.isSuccess) {
                    val profile = result.getOrNull()
                    val isUsernameMissing = profile?.username.isNullOrBlank()
                    val isGradeMissing = profile?.grade.isNullOrBlank()
                    val isSystemMissing = profile?.schoolSystem.isNullOrBlank()
                    val isServerOnboarded = profile?.isOnboarded == true

                    val isSetupIncomplete = !isServerOnboarded || isUsernameMissing || isGradeMissing || isSystemMissing

                    userManager.saveOnboardingStatus(!isSetupIncomplete)
                    userManager.saveRequiresUsername(isUsernameMissing)

                    val currentRoute = navController.currentDestination?.route
                    
                    if (profile?.isVerified == false && profile.authProvider != "google") {
                        if (currentRoute != "email_verification") {
                            navController.navigate("email_verification") { popUpTo(0) }
                        }
                    } else if (isSetupIncomplete && currentRoute == "main") {
                        navController.navigate("setup") { popUpTo(0) }
                    }
                } else {
                    val exception = result.exceptionOrNull()
                    if (exception is retrofit2.HttpException && exception.code() == 401) {
                        authRepository.logout()
                        userManager.clearUserData()
                        navController.navigate("auth") { popUpTo(0) }
                    }
                }
            } catch (e: Exception) {
                // Ignore network errors in background
            }
        }
    }

    NavHost(navController = navController, startDestination = startDestination) {

        composable("language_selection") {
            LanguageSelectionScreen(
                onContinue = {
                    val nextDest = if (authRepository.isLoggedIn()) {
                        if (!userManager.isOnboarded()) "setup"
                        else if (!userManager.isAssessmentCompleted()) "assessment"
                        else "main"
                    } else {
                        "auth"
                    }
                    navController.navigate(nextDest) {
                        popUpTo("language_selection") { inclusive = true }
                    }
                }
            )
        }
        
"""
    lines[start_idx:end_idx] = [new_block]
    
    with open(file_path, 'w') as f:
        f.writelines(lines)
    print("Patched RootNavGraph startDestination and removed splash")
else:
    print(f"Could not find indices: {start_idx}, {end_idx}")
