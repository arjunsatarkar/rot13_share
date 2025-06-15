package net.arjunsatarkar.rot13share

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.mikepenz.aboutlibraries.ui.compose.android.rememberLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import net.arjunsatarkar.rot13share.ui.theme.Rot13ShareTheme

val LINK_STYLE = SpanStyle(
    color = Color(0xFF87CEFA), // lightskyblue CSS colour
    textDecoration = TextDecoration.Underline
)

enum class Screen {
    Main, About, Licenses
}

class LauncherActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Rot13ShareTheme {
                OuterApp()
            }
        }
    }
}

@Composable
fun OuterApp() {
    var currentScreen by remember { mutableStateOf(Screen.Main) }

    Scaffold(topBar = {
        MenuBar(onChangeScreen = { newScreen -> currentScreen = newScreen })
    }) { innerPadding ->
        when (currentScreen) {
            Screen.Main -> {
                MainScreen(
                    innerPadding
                )
            }

            Screen.About -> {
                AboutScreen(
                    innerPadding, onChangeScreen = { newScreen -> currentScreen = newScreen })
            }

            Screen.Licenses -> {
                LicensesScreen(innerPadding, onBack = { currentScreen = Screen.About })
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuBar(onChangeScreen: (newScreen: Screen) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    TopAppBar(title = { Text(stringResource(R.string.app_name)) }, actions = {
        Box {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
            DropdownMenu(
                expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(text = { Text("About") }, onClick = {
                    onChangeScreen(Screen.About)
                    expanded = false
                })
            }
        }
    })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(innerPadding: PaddingValues) {
    var inputText by remember { mutableStateOf("") }
    var rotatedText by remember { mutableStateOf("") }
    var rotateBy by remember { mutableStateOf("13") }

    val mainTextFieldHeight = 150.dp

    // This effect recalculates the cipher whenever the input text or rotation value changes.
    LaunchedEffect(inputText, rotateBy) {
        val rotation = rotateBy.toIntOrNull() ?: 0
        rotatedText = rot(rotation, inputText)
    }


    Column(
        modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "ROT",
            )
            OutlinedTextField(
                value = rotateBy,
                onValueChange = { value ->
                    if (value.isDigitsOnly()) {
                        val valueInt = value.toIntOrNull()
                        if (valueInt == null || valueInt in 0..26) {
                            rotateBy = value.trimStart { it == '0' }
                        }
                    }
                },

                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }

        OutlinedTextField(
            value = inputText,
            onValueChange = { inputText = it },
            placeholder = { Text("Enter text...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(mainTextFieldHeight)
        )

        Icon(
            painter = painterResource(id = R.drawable.arrow_downward_24px),
            contentDescription = null,
        )

        OutlinedTextField(
            value = rotatedText,
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(mainTextFieldHeight)
        )
    }
}

@Composable
fun AboutScreen(innerPadding: PaddingValues, onChangeScreen: (newScreen: Screen) -> Unit) {
    BackHandler(onBack = { onChangeScreen(Screen.Main) })

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(buildAnnotatedString {
            append("Version ${BuildConfig.VERSION_NAME}.\n")
            withLink(LinkAnnotation.Url("https://github.com/arjunsatarkar/rot13_share")) {
                withStyle(LINK_STYLE) {
                    append("source code")
                }
            }
        })
        Text("Copyright © 2025-present Arjun Satarkar.")
        Text(buildAnnotatedString {
            append("ROT13 Share is available under the terms of the MIT License. See ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Licenses")
            }
            append(" below for a copy of this as well as licenses used by dependencies of this app.")
        })

        TextButton(
            onClick = { onChangeScreen(Screen.Licenses) }, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Licenses")
        }
    }
}

@Composable
fun LicensesScreen(innerPadding: PaddingValues, onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    val libraries by rememberLibraries(R.raw.aboutlibraries)

    LibrariesContainer(
        libraries = libraries,
        modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize(),
    )
}

@Preview(showBackground = true)
@Composable
fun RotPreview() {
    Rot13ShareTheme {
        OuterApp()
    }
}
