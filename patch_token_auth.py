import re

file_path = '/home/badr-eldeen/AndroidStudioProjects/Zeno/app/src/main/java/com/example/zeno/core/network/TokenAuthenticator.kt'
with open(file_path, 'r') as f:
    content = f.read()

old_catch = """                    } catch (e: Exception) {
                        authStorage.clearToken()
                        showSessionExpiredToast()
                    }
                } else {
                    authStorage.clearToken()
                    showSessionExpiredToast()
                }"""

new_catch = """                    } catch (e: retrofit2.HttpException) {
                        if (e.code() == 401 || e.code() == 403) {
                            authStorage.clearToken()
                            showSessionExpiredToast()
                        }
                    } catch (e: Exception) {
                        // Network errors should not log the user out!
                        e.printStackTrace()
                    }
                } else {
                    authStorage.clearToken()
                    showSessionExpiredToast()
                }"""

if old_catch in content:
    content = content.replace(old_catch, new_catch)
    with open(file_path, 'w') as f:
        f.write(content)
    print("Patched TokenAuthenticator")
else:
    print("Failed to find catch block in TokenAuthenticator")
