file_path = '/home/badr-eldeen/AndroidStudioProjects/Zeno/app/src/main/java/com/example/zeno/core/navigation/RootNavGraph.kt'
with open(file_path, 'r') as f:
    lines = f.readlines()

for i, line in enumerate(lines):
    if line.startswith("package "):
        # Move package to top
        pkg_line = lines.pop(i)
        lines.insert(0, pkg_line)
        break

with open(file_path, 'w') as f:
    f.writelines(lines)
print("Fixed RootNavGraph")
