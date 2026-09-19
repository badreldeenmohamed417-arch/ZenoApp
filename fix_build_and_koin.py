import re

# Fix app/build.gradle.kts
with open('app/build.gradle.kts', 'r') as f:
    app_gradle = f.read()

if 'firebase-bom' not in app_gradle:
    app_gradle += '\n    // Firebase\n    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))\n    implementation("com.google.firebase:firebase-messaging")\n'
    with open('app/build.gradle.kts', 'w') as f:
        f.write(app_gradle)

