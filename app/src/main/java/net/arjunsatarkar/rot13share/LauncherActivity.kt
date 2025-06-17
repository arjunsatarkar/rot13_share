package net.arjunsatarkar.rot13share

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
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
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.mikepenz.aboutlibraries.ui.compose.android.rememberLibraries
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import kotlinx.serialization.Serializable
import net.arjunsatarkar.rot13share.ui.theme.Rot13ShareTheme

val LINK_STYLE = SpanStyle(
    color = Color(0xFF87CEFA), // lightskyblue CSS colour
    textDecoration = TextDecoration.Underline
)

sealed class Route {
    @Serializable
    data object MainScreen : Route()

    @Serializable
    data object AboutScreen : Route()

    @Serializable
    data object LicensesScreen : Route()
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
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val onNavigateTo: (route: Route) -> Unit = { route ->
        navController.navigate(route, navOptions = navOptions {
            popUpTo(route) {
                inclusive = true
            }
        })
    }

    Scaffold(topBar = {
        MenuBar(
            main = currentDestination?.hasRoute(Route.MainScreen::class) == true,
            onNavigateTo = onNavigateTo,
            onBack = navController::popBackStack
        )
    }) { innerPadding ->
        NavHost(navController = navController, startDestination = Route.MainScreen) {
            composable<Route.MainScreen> { MainScreen(innerPadding) }
            composable<Route.AboutScreen> { AboutScreen(innerPadding, onNavigateTo = onNavigateTo) }
            composable<Route.LicensesScreen> { LicensesScreen(innerPadding) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuBar(main: Boolean, onNavigateTo: (route: Route) -> Unit, onBack: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    val navigationIcon: @Composable (() -> Unit) = if (main) {
        { }
    } else {
        {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                )
            }
        }
    }

    CenterAlignedTopAppBar(
        title = { Text(stringResource(R.string.app_name)) },
        navigationIcon = navigationIcon,
        actions = {
            Box {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text("About") }, onClick = {
                        onNavigateTo(Route.AboutScreen)
                        expanded = false
                    })
                }
            }
        })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(innerPadding: PaddingValues) {
    var inputText by rememberSaveable { mutableStateOf("") }
    var rotateBy by rememberSaveable { mutableStateOf("13") }

    val mainTextFieldHeight = 150.dp

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
            value = rot(rotateBy.toIntOrNull() ?: 0, inputText),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(mainTextFieldHeight)
        )
    }
}


@Composable
fun AboutScreen(innerPadding: PaddingValues, onNavigateTo: (route: Route) -> Unit) {
    val uriHandler = LocalUriHandler.current
    val appSourceUrl = stringResource(R.string.app_source_url)

    Column(
        modifier = Modifier
            .padding(innerPadding)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text("Version ${BuildConfig.VERSION_NAME}.")
        Text(buildAnnotatedString {
            append("Copyright © 2025-present ")
            withLink(LinkAnnotation.Url(stringResource(R.string.author_site))) {
                withStyle(LINK_STYLE) {
                    append("Arjun Satarkar")
                }
            }
            append(".")
        })
        Text(buildAnnotatedString {
            append("ROT13 Share is available under the terms of the MIT License. See ")
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Licenses")
            }
            append(" below for a copy of this as well as licenses used by dependencies of this app.")
        })

        Column {
            TextButton(
                onClick = { uriHandler.openUri(appSourceUrl) }, modifier = Modifier.fillMaxWidth()
            ) {
                Text("Source Code")
            }
            TextButton(
                onClick = { onNavigateTo(Route.LicensesScreen) }, modifier = Modifier.fillMaxWidth()
            ) {
                Text("Licenses")
            }
        }
    }
}

@Composable
fun LicensesScreen(innerPadding: PaddingValues) {
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
