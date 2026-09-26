import re

file_path = '/home/badr-eldeen/AndroidStudioProjects/Zeno/app/src/main/java/com/example/zeno/features/auth/data/AuthRepository.kt'
with open(file_path, 'r') as f:
    content = f.read()

old_header = """class AuthRepository(
    private val authApi: AuthApi,
    private val authStorage: AuthStorage,
    private val userManager: UserManager
) {"""

new_header = """import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class AuthRepository(
    private val authApi: AuthApi,
    private val authStorage: AuthStorage,
    private val userManager: UserManager,
    private val context: Context
) {"""

content = content.replace(old_header, new_header)

old_logout = """    fun logout() {
        authStorage.clearToken()
    }"""

new_logout = """    fun logout() {
        authStorage.clearToken()
        userManager.clearUserData()
        CoroutineScope(Dispatchers.IO).launch {
            com.example.zeno.data.local.db.AppDatabase.getDatabase(context).clearAllTables()
        }
    }"""

content = content.replace(old_logout, new_logout)

with open(file_path, 'w') as f:
    f.write(content)
print("Patched AuthRepository")
