import re

# Update libs.versions.toml
with open('gradle/libs.versions.toml', 'r') as f:
    toml = f.read()

if 'hilt =' not in toml:
    toml = toml.replace('[versions]\n', '[versions]\nhilt = "2.51.1"\n')
if 'hilt-android = { id' not in toml:
    toml = toml.replace('[plugins]\n', '[plugins]\nhilt-android = { id = "com.google.dagger.hilt.android", version.ref = "hilt" }\n')
if 'hilt-android = { group' not in toml:
    toml = toml.replace('[libraries]\n', '[libraries]\nhilt-android = { group = "com.google.dagger", name = "hilt-android", version.ref = "hilt" }\nhilt-compiler = { group = "com.google.dagger", name = "hilt-compiler", version.ref = "hilt" }\nandroidx-hilt-navigation-compose = { group = "androidx.hilt", name = "hilt-navigation-compose", version = "1.2.0" }\n')
with open('gradle/libs.versions.toml', 'w') as f:
    f.write(toml)

# Update project build.gradle.kts
with open('build.gradle.kts', 'r') as f:
    project_gradle = f.read()

project_gradle = re.sub(r'id\("com\.google\.dagger\.hilt\.android"\).*', 'alias(libs.plugins.hilt.android) apply false', project_gradle)
with open('build.gradle.kts', 'w') as f:
    f.write(project_gradle)

# Update app build.gradle.kts
with open('app/build.gradle.kts', 'r') as f:
    app_gradle = f.read()

app_gradle = re.sub(r'id\("com\.google\.dagger\.hilt\.android"\).*', 'alias(libs.plugins.hilt.android)', app_gradle)
app_gradle = app_gradle.replace('implementation("com.google.dagger:hilt-android:2.52.0")', 'implementation(libs.hilt.android)')
app_gradle = app_gradle.replace('ksp("com.google.dagger:hilt-compiler:2.52.0")', 'ksp(libs.hilt.compiler)')
app_gradle = app_gradle.replace('implementation("com.google.dagger:hilt-android:2.51.1")', 'implementation(libs.hilt.android)')
app_gradle = app_gradle.replace('ksp("com.google.dagger:hilt-compiler:2.51.1")', 'ksp(libs.hilt.compiler)')
app_gradle = app_gradle.replace('implementation("androidx.hilt:hilt-navigation-compose:1.2.0")', 'implementation(libs.androidx.hilt.navigation.compose)')

with open('app/build.gradle.kts', 'w') as f:
    f.write(app_gradle)

