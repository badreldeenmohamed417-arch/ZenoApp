package com.example.zeno.core.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.zeno.R
import com.example.zeno.core.theme.ZenoTheme
import com.example.zeno.core.widgets.ZenoButton
import com.example.zeno.core.widgets.ZenoCard
import com.example.zeno.core.widgets.ZenoTextField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ZenoDesignDemo() {
    var text by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        stringResource(id = R.string.demo_title), 
                        style = MaterialTheme.typography.titleLarge 
                    ) 
                },
                navigationIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_zeno_logo),
                        contentDescription = "Zeno Logo",
                        modifier = Modifier
                            .padding(16.dp)
                            .size(24.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            
            // Typography Showcase
            Text(
                text = "Welcome to Zeno",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
            
            Text(
                text = "مرحباً بك في زينو",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Widgets Showcase
            ZenoCard(
                title = stringResource(id = R.string.demo_card_title),
                description = stringResource(id = R.string.demo_card_desc)
            )

            ZenoTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = stringResource(id = R.string.demo_textfield_hint)
            )
            
            ZenoTextField(
                value = "Wrong answer",
                onValueChange = { },
                placeholder = "",
                isError = true,
                errorMessage = stringResource(id = R.string.demo_error)
            )

            Spacer(modifier = Modifier.weight(1f))

            ZenoButton(
                text = stringResource(id = R.string.demo_button),
                onClick = { }
            )
            
            ZenoButton(
                text = stringResource(id = R.string.demo_loading),
                onClick = { },
                isLoading = true
            )
        }
    }
}

@Preview(
    name = "Light Mode", 
    uiMode = Configuration.UI_MODE_NIGHT_NO, 
    showBackground = true,
    locale = "en"
)
@Composable
fun ZenoDesignDemoLightPreview() {
    ZenoTheme(darkTheme = false) {
        ZenoDesignDemo()
    }
}

@Preview(
    name = "Dark Mode", 
    uiMode = Configuration.UI_MODE_NIGHT_YES, 
    showBackground = true,
    locale = "ar"
)
@Composable
fun ZenoDesignDemoDarkPreview() {
    ZenoTheme(darkTheme = true) {
        ZenoDesignDemo()
    }
}
