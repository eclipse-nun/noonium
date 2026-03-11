package ncatt.noonium

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ManageAccounts
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ncatt.noonium.ui.theme.nooniumTheme
import ncatt.noonium.ui.theme.Theme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            val sharedPref = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }
            val theme = remember { mutableStateOf(Theme.valueOf(sharedPref.getString("theme", "SYSTEM") ?: "SYSTEM")) }
            val language = remember { mutableStateOf(sharedPref.getString("language", "en") ?: "en") }

            CompositionLocalProvider(LocalContext provides createLocaleContext(language.value)) {
                nooniumTheme(theme = theme.value) {
                    nooniumApp(theme, language)
                }
            }
        }
    }
}

@Composable
fun createLocaleContext(language: String): Context {
    val context = LocalContext.current
    val locale = Locale(language)
    Locale.setDefault(locale)
    val configuration = Configuration(context.resources.configuration)
    configuration.setLocale(locale)
    return context.createConfigurationContext(configuration)
}

@Composable
fun nooniumApp(theme: MutableState<Theme>, language: MutableState<String>) {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = { Icon(destination.icon, contentDescription = stringResource(id = destination.label)) },
                    label = { Text(stringResource(id = destination.label)) },
                    selected = destination == currentDestination,
                    onClick = { currentDestination = destination }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentDestination) {
                    AppDestinations.HOME -> HomeScreen()
                    AppDestinations.SETTINGS -> SettingsScreen(theme = theme, language = language)
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = stringResource(id = R.string.home),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = stringResource(id = R.string.system_information),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Main info card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6)), // Light blueish/purple
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column {
                // Top Header Section
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFC5CAE9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.GridView,
                            contentDescription = null,
                            tint = Color(0xFF3F51B5),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = stringResource(id = R.string.rom_information),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "${Build.MANUFACTURER} ${Build.MODEL}",
                            fontSize = 15.sp,
                            color = Color.DarkGray
                        )
                    }
                }

                Row(
                    modifier = Modifier.padding(start = 24.dp, bottom = 24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.History,
                        contentDescription = null,
                        tint = Color(0xFF1976D2),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(id = R.string.android_version, Build.VERSION.RELEASE),
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "  |  ",
                        color = Color(0xFF1976D2).copy(alpha = 0.4f),
                        fontSize = 15.sp
                    )
                    Text(
                        text = stringResource(id = R.string.api_level, Build.VERSION.SDK_INT),
                        color = Color(0xFF1976D2),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                // Bottom List Section (White card part)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(32.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        DeviceInfoItem(
                            icon = Icons.Outlined.Verified,
                            label = stringResource(id = R.string.system_version),
                            value = Build.DISPLAY,
                            iconColor = Color(0xFF2196F3)
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp, horizontal = 56.dp), color = Color.LightGray.copy(alpha = 0.4f))
                        DeviceInfoItem(
                            icon = Icons.Outlined.History,
                            label = stringResource(id = R.string.rom_version),
                            value = Build.ID,
                            iconColor = Color(0xFF7E57C2)
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp, horizontal = 56.dp), color = Color.LightGray.copy(alpha = 0.4f))
                        DeviceInfoItem(
                            icon = Icons.Outlined.Person,
                            label = stringResource(id = R.string.build_user),
                            value = Build.USER,
                            iconColor = Color(0xFF5C6BC0)
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp, horizontal = 56.dp), color = Color.LightGray.copy(alpha = 0.4f))
                        DeviceInfoItem(
                            icon = Icons.Outlined.Code,
                            label = stringResource(id = R.string.kernel_version_label),
                            value = System.getProperty("os.version") ?: "unknown",
                            iconColor = Color(0xFF9575CD)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceInfoItem(icon: ImageVector, label: String, value: String, iconColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp), tint = iconColor)
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 13.sp, color = Color.Gray)
            Text(text = value, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        }
    }
}

@Composable
fun SettingsScreen(modifier: Modifier = Modifier, theme: MutableState<Theme>, language: MutableState<String>) {
    val context = LocalContext.current
    val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName
    var themeExpanded by remember { mutableStateOf(false) }
    var languageExpanded by remember { mutableStateOf(false) }
    val sharedPref = remember { context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE) }

    Column(modifier = modifier
        .fillMaxSize()
        .padding(24.dp)) {
        Text(
            text = stringResource(id = R.string.settings),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(stringResource(id = R.string.theme), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { themeExpanded = true }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(ThemeName(theme.value), modifier = Modifier.weight(1f))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                    }
                    DropdownMenu(expanded = themeExpanded, onDismissRequest = { themeExpanded = false }) {
                        Theme.entries.forEach { themeValue ->
                            DropdownMenuItem(text = { Text(ThemeName(themeValue)) }, onClick = { 
                                theme.value = themeValue
                                sharedPref.edit().putString("theme", themeValue.name).apply()
                                themeExpanded = false
                            })
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(stringResource(id = R.string.language), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { languageExpanded = true }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (language.value == "en") "English" else "Tiếng Việt", modifier = Modifier.weight(1f))
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
                    }
                    DropdownMenu(expanded = languageExpanded, onDismissRequest = { languageExpanded = false }) {
                        DropdownMenuItem(text = { Text("English") }, onClick = { 
                            language.value = "en"
                            sharedPref.edit().putString("language", "en").apply()
                            languageExpanded = false
                        })
                        DropdownMenuItem(text = { Text("Tiếng Việt") }, onClick = { 
                            language.value = "vi"
                            sharedPref.edit().putString("language", "vi").apply()
                            languageExpanded = false
                        })
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { 
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://youtu.be/V61jZBMFjUs?si=mGFYyU4Y8XE1kZ4K"))
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                },
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(text = "noonium", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "ver. $versionName", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f))
            }
        }
    }
}

@Composable
fun ThemeName(theme: Theme): String {
    return when (theme) {
        Theme.LIGHT -> stringResource(id = R.string.theme_light)
        Theme.DARK -> stringResource(id = R.string.theme_dark)
        Theme.SYSTEM -> stringResource(id = R.string.theme_system)
    }
}

enum class AppDestinations(
    @StringRes val label: Int,
    val icon: ImageVector,
) {
    HOME(R.string.home, Icons.Outlined.Home),
    SETTINGS(R.string.settings, Icons.Outlined.ManageAccounts),
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    nooniumTheme {
        HomeScreen()
    }
}
