import re
import os

# Update libs.versions.toml
with open('gradle/libs.versions.toml', 'r') as f:
    toml = f.read()

toml = re.sub(r'hilt = "2.51.1"\n', 'koin = "3.5.3"\n', toml)
toml = re.sub(r'hilt-android = \{ id = "com\.google\.dagger\.hilt\.android", version\.ref = "hilt" \}\n', '', toml)
toml = re.sub(r'hilt-android = \{ group = "com\.google\.dagger", name = "hilt-android", version\.ref = "hilt" \}\n', 'koin-androidx-compose = { group = "io.insert-koin", name = "koin-androidx-compose", version.ref = "koin" }\n', toml)
toml = re.sub(r'hilt-compiler = \{ group = "com\.google\.dagger", name = "hilt-compiler", version\.ref = "hilt" \}\n', '', toml)
toml = re.sub(r'androidx-hilt-navigation-compose = \{ group = "androidx\.hilt", name = "hilt-navigation-compose", version = "1\.2\.0" \}\n', '', toml)

with open('gradle/libs.versions.toml', 'w') as f:
    f.write(toml)

# Update project build.gradle.kts
with open('build.gradle.kts', 'r') as f:
    project_gradle = f.read()
project_gradle = re.sub(r'\s*alias\(libs\.plugins\.hilt\.android\) apply false\n', '\n', project_gradle)
with open('build.gradle.kts', 'w') as f:
    f.write(project_gradle)

# Update app build.gradle.kts
with open('app/build.gradle.kts', 'r') as f:
    app_gradle = f.read()
app_gradle = re.sub(r'\s*alias\(libs\.plugins\.hilt\.android\)\n', '\n', app_gradle)
app_gradle = re.sub(r'\s*implementation\(libs\.hilt\.android\)\n', '\n', app_gradle)
app_gradle = re.sub(r'\s*ksp\(libs\.hilt\.compiler\)\n', '\n', app_gradle)
app_gradle = re.sub(r'\s*implementation\(libs\.androidx\.hilt\.navigation\.compose\)\n', '\n    implementation(libs.koin.androidx.compose)\n', app_gradle)

with open('app/build.gradle.kts', 'w') as f:
    f.write(app_gradle)
