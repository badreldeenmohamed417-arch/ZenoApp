import re

# Add missing dependencies to build.gradle.kts
with open('app/build.gradle.kts', 'r') as f:
    gradle = f.read()

deps = """
    // RevenueCat
    implementation("com.revenuecat.purchases:purchases:8.2.3")
    
    // Koin for Compose
    implementation("io.insert-koin:koin-androidx-compose:3.5.3")
"""
if 'com.revenuecat.purchases' not in gradle:
    gradle = gradle.replace('implementation("com.google.firebase:firebase-messaging")', 'implementation("com.google.firebase:firebase-messaging")\n' + deps)
    with open('app/build.gradle.kts', 'w') as f:
        f.write(gradle)

# Fix AppModule.kt ViewModels
with open('app/src/main/java/com/example/zeno/core/di/AppModule.kt', 'r') as f:
    app_module = f.read()

app_module = app_module.replace('ProfileViewModel(get(), get(), get(), get())', 'ProfileViewModel(get())')
app_module = app_module.replace('SessionsViewModel(get(), get())', 'SessionsViewModel(get())')
app_module = app_module.replace('PremiumViewModel(get(), get())', 'PremiumViewModel(get())')
app_module = app_module.replace('HomeViewModel(get(), get(), get())', 'HomeViewModel(get())')

with open('app/src/main/java/com/example/zeno/core/di/AppModule.kt', 'w') as f:
    f.write(app_module)

