import re

with open('app/src/main/java/com/example/zeno/features/main/presentation/MainAppScreen.kt', 'r') as f:
    content = f.read()

# Replace viewModels with koinViewModels
content = re.sub(r'val (.*?)ViewModel: (.*?)ViewModel = viewModel\(factory = object : ViewModelProvider\.Factory \{[\s\S]*?override fun <T : ViewModel> create\(modelClass: Class<T>\): T = .*? as T\n\s*\}\)', r'val \1ViewModel: \2ViewModel = koinViewModel()', content)

# Also fix the import
if 'import org.koin.androidx.compose.koinViewModel' not in content:
    content = content.replace('import androidx.compose.runtime.Composable', 'import androidx.compose.runtime.Composable\nimport org.koin.androidx.compose.koinViewModel')

with open('app/src/main/java/com/example/zeno/features/main/presentation/MainAppScreen.kt', 'w') as f:
    f.write(content)
