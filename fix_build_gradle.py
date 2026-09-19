import re

# Update libs.versions.toml
with open('gradle/libs.versions.toml', 'r') as f:
    toml = f.read()

if 'kotlin-android =' not in toml:
    toml = toml.replace('[plugins]\n', '[plugins]\nkotlin-android = { id = "org.jetbrains.kotlin.android", version.ref = "kotlin" }\n')
    with open('gradle/libs.versions.toml', 'w') as f:
        f.write(toml)

# Update project build.gradle.kts
with open('build.gradle.kts', 'r') as f:
    project_gradle = f.read()

if 'hilt.android' not in project_gradle:
    project_gradle = project_gradle.replace('}', '    alias(libs.plugins.kotlin.android) apply false\n    id("com.google.dagger.hilt.android") version "2.51.1" apply false\n}')
    with open('build.gradle.kts', 'w') as f:
        f.write(project_gradle)

# Update app build.gradle.kts
with open('app/build.gradle.kts', 'r') as f:
    app_gradle = f.read()

if 'hilt.android' not in app_gradle:
    app_gradle = app_gradle.replace('alias(libs.plugins.android.application)', 'alias(libs.plugins.android.application)\n    alias(libs.plugins.kotlin.android)')
    app_gradle = app_gradle.replace('id("com.google.gms.google-services") version "4.5.0" apply false', 'id("com.google.gms.google-services")\n    id("com.google.dagger.hilt.android")')
    
    # dependencies
    app_gradle = app_gradle.replace('ksp(libs.androidx.room.compiler)\n', 'ksp(libs.androidx.room.compiler)\n\n    implementation("com.google.dagger:hilt-android:2.51.1")\n    ksp("com.google.dagger:hilt-compiler:2.51.1")\n    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")\n')
    with open('app/build.gradle.kts', 'w') as f:
        f.write(app_gradle)

