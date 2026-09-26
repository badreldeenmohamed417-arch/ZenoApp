import re

file_path = '/home/badr-eldeen/AndroidStudioProjects/Zeno/app/src/main/java/com/example/zeno/data/local/UserManager.kt'
with open(file_path, 'r') as f:
    content = f.read()

old_clear = """    fun clearUserData() {
        preferences.edit().clear().apply()
    }"""

new_clear = """    fun clearUserData() {
        val editor = preferences.edit()
        
        // Backup App Settings
        val lang = preferences.getString("app_language", null)
        val initialLangSelected = preferences.getBoolean("is_initial_language_selected", false)
        val themeStr = preferences.getString("theme_mode_str", null)
        val themeBool = preferences.getBoolean("theme_mode", true)
        
        // Clear all
        editor.clear().apply()
        
        // Restore App Settings
        val restoreEditor = preferences.edit()
        if (lang != null) restoreEditor.putString("app_language", lang)
        restoreEditor.putBoolean("is_initial_language_selected", initialLangSelected)
        if (themeStr != null) restoreEditor.putString("theme_mode_str", themeStr)
        restoreEditor.putBoolean("theme_mode", themeBool)
        restoreEditor.apply()
    }"""
content = content.replace(old_clear, new_clear)

with open(file_path, 'w') as f:
    f.write(content)
print("Patched UserManager clearUserData")
