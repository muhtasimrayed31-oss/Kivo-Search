package com.example

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.network.SearchItem
import com.example.ui.theme.KivoAiPurple
import com.example.ui.theme.KivoAiRed
import com.example.ui.theme.KivoBlueEnd
import com.example.ui.theme.KivoBlueStart
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.KivoViewModel
import com.example.viewmodel.KivoViewModelFactory
import kotlinx.coroutines.delay
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val factory = remember { KivoViewModelFactory(applicationContext) }
            val viewModel: KivoViewModel = viewModel(factory = factory)

            val isSystemDark = isSystemInDarkTheme()
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

            // Initialize dark mode mapping from device theme setting
            LaunchedEffect(Unit) {
                viewModel.toggleTheme(isSystemDark)
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                KivoMainScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun KivoMainScreen(viewModel: KivoViewModel) {
    val navTab by viewModel.currentNavTab.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_scaffold"),
        bottomBar = {
            KivoBottomNavigationBar(
                activeTab = navTab,
                onTabSelected = { viewModel.setNavTab(it) }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (navTab) {
                "home" -> KivoHomeScreen(viewModel = viewModel)
                "search" -> KivoSearchResultsScreen(viewModel = viewModel)
                "settings" -> KivoSettingsScreen(viewModel = viewModel)
            }

            // Global Language Localization Alert
            val languageNotify by viewModel.languageNotification.collectAsStateWithLifecycle()
            languageNotify?.let { message ->
                LanguageIndicatorPopup(
                    message = message,
                    onDismiss = { viewModel.clearLanguageNotification() }
                )
            }

            // Global Login Dialog
            val isAuthDialogOpen by viewModel.isShowingAuthDialog.collectAsStateWithLifecycle()
            if (isAuthDialogOpen) {
                KivoAuthDialog(
                    onDismiss = { viewModel.toggleAuthDialog(false) },
                    onSignInSuccess = { email -> viewModel.signInUser(email) }
                )
            }
        }
    }
}

@Composable
fun KivoBottomNavigationBar(
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier.testTag("bottom_nav_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            modifier = Modifier.testTag("nav_home"),
            selected = activeTab == "home",
            onClick = { onTabSelected("home") },
            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home index"
                )
            }
        )
        NavigationBarItem(
            modifier = Modifier.testTag("nav_search"),
            selected = activeTab == "search",
            onClick = { onTabSelected("search") },
            label = { Text("Search", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search listings"
                )
            }
        )
        NavigationBarItem(
            modifier = Modifier.testTag("nav_settings"),
            selected = activeTab == "settings",
            onClick = { onTabSelected("settings") },
            label = { Text("Settings", fontSize = 11.sp, fontWeight = FontWeight.Medium) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings configuration"
                )
            }
        )
    }
}

@Composable
fun KivoHomeScreen(viewModel: KivoViewModel) {
    val localCity by viewModel.localCity.collectAsStateWithLifecycle()
    val localTemp by viewModel.localTemp.collectAsStateWithLifecycle()
    val userEmail by viewModel.currentUserEmail.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    var isSearchBarFocused by remember { mutableStateOf(false) }
    val history by viewModel.searchHistory.collectAsStateWithLifecycle()
    val typedQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    // Setup speech search launcher
    val voiceLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spokenText = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrBlank()) {
                viewModel.updateSearchQuery(spokenText)
                viewModel.performSearch(spokenText)
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            // Header: Weather Widget & User Account indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Weather button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .clickable {
                            viewModel.fetchLocationAndWeather()
                            Toast
                                .makeText(context, "$localCity location resolved", Toast.LENGTH_SHORT)
                                .show()
                        }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WbSunny,
                        contentDescription = "Sunny Weather",
                        tint = Color(0xFFFBBC05),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$localCity • $localTemp",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }

                // Auth Profile Icon
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (userEmail != null) Color(0xFF00796B)
                            else MaterialTheme.colorScheme.surface
                        )
                        .clickable {
                            if (userEmail == null) {
                                viewModel.toggleAuthDialog(true)
                            } else {
                                viewModel.setNavTab("settings")
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (userEmail != null) {
                        Text(
                            text = userEmail!!.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "User account setup",
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))

            // Brand Logo & Title Area
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                KivoLogoCanvas(modifier = Modifier.size(90.dp))
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "KIVO",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1.5).sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        item {
            // Pill Search text input field
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = typedQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = {
                        Text(
                            "Search with KIVO",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            fontSize = 15.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(32.dp))
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(32.dp))
                        .onFocusChanged { isSearchBarFocused = it.isFocused }
                        .testTag("home_search_bar"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        unfocusedBorderColor = Color.Transparent,
                        focusedLabelColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(32.dp),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search,
                        keyboardType = KeyboardType.Text
                    ),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (typedQuery.isNotBlank()) {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            viewModel.performSearch(typedQuery)
                        }
                    }),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search glyph",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier
                                .clickable {
                                    if (typedQuery.isNotBlank()) {
                                        viewModel.performSearch(typedQuery)
                                    }
                                }
                                .padding(8.dp)
                        )
                    },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (typedQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear query",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    )
                                }
                            }
                            IconButton(
                                modifier = Modifier.testTag("voice_trigger_btn"),
                                onClick = {
                                    val speechIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak to Search KIVO")
                                    }
                                    try {
                                        voiceLauncher.launch(speechIntent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Voice recognizer not available", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Microphone voice dictation",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                )
            }
        }

        // Search Suggestion Dropdown: Displays when focused
        item {
            AnimatedVisibility(
                visible = isSearchBarFocused,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .testTag("suggestions_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                        if (history.isEmpty()) {
                            Text(
                                text = "No search history yet",
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        } else {
                            Text(
                                text = "RECENT SEARCHES",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            history.forEach { entry ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            focusManager.clearFocus()
                                            viewModel.performSearch(entry.query)
                                        }
                                        .padding(horizontal = 20.dp, vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.History,
                                            contentDescription = "History indicator",
                                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = entry.query,
                                            fontSize = 14.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Delete record",
                                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                        modifier = Modifier
                                            .size(18.dp)
                                            .clickable { viewModel.deleteHistoryItem(entry.query) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "TRENDING SEARCHES",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.secondary
                        )

                        val trends = listOf(
                            "Artificial Intelligence 2026",
                            "Space exploration latest",
                            "Climate change solutions"
                        )
                        trends.forEach { term ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        focusManager.clearFocus()
                                        viewModel.performSearch(term)
                                    }
                                    .padding(horizontal = 20.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = "Trending icon",
                                    tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = term,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(35.dp))
            // Curated news cards header
            Text(
                text = "Discover More",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )
        }

        // Beautiful news cards feed
        val headlines = listOf(
            NewsCardItem(
                title = "The Future of AI in 2026: What's Next?",
                source = "TechCrunch",
                domain = "techcrunch.com",
                imageUrl = "https://images.unsplash.com/photo-1677442136019-21780ecad995?auto=format&fit=crop&w=600&q=80"
            ),
            NewsCardItem(
                title = "Exploring the Deepest Oceans: New Discoveries",
                source = "National Geographic",
                domain = "nationalgeographic.com",
                imageUrl = "https://images.unsplash.com/photo-1582967788606-a171c1080cb0?auto=format&fit=crop&w=600&q=80"
            ),
            NewsCardItem(
                title = "Global Markets Rally as Tech Stocks Surge",
                source = "Bloomberg",
                domain = "bloomberg.com",
                imageUrl = "https://images.unsplash.com/photo-1611974789855-9c2a0a7236a3?auto=format&fit=crop&w=600&q=80"
            )
        )

        items(headlines) { item ->
            NewsCardWidget(item = item, onClick = { viewModel.performSearch(item.title) })
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun KivoSearchResultsScreen(viewModel: KivoViewModel) {
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val subTab by viewModel.activeSearchSubTab.collectAsStateWithLifecycle()
    val loading by viewModel.isLoading.collectAsStateWithLifecycle()
    val results by viewModel.searchResults.collectAsStateWithLifecycle()
    val aiOverview by viewModel.aiOverview.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("results_screen")
    ) {
        // Headers: Logo bar, search bar input fields
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(top = 28.dp)
                .shadow(2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mini Logo
                Row(
                    modifier = Modifier
                        .clickable { viewModel.setNavTab("home") }
                        .padding(end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    KivoLogoCanvas(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "KIVO",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // Inner Search input box
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("results_search_box"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(25.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (query.isNotBlank()) {
                            keyboardController?.hide()
                            viewModel.performSearch(query)
                        }
                    }),
                    trailingIcon = {
                        IconButton(onClick = {
                            if (query.isNotBlank()) {
                                viewModel.performSearch(query)
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Query",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                )
            }

            // Chips Navigation: All, Images, Videos, News
            val options = listOf("all" to "All", "image" to "Images", "video" to "Videos", "news" to "News")
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                options.forEach { (key, display) ->
                    val isSelected = subTab == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                else Color.Transparent,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { viewModel.setSubTab(key) }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = display,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Load Indicator
        if (loading && results.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
            ) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        // Layout containing scrolling results list
        if (query.isBlank() && results.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search details",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Enter query keywords to explore web summaries.",
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("results_list"),
                contentPadding = PaddingValues(bottom = 40.dp, top = 12.dp)
            ) {
                // SGE AI Overview layout (shows up on 'all' tab)
                if (subTab == "all") {
                    item {
                        AnimatedVisibility(
                            visible = aiOverview != null || loading,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(
                                                Color(0xFFE3E3FF).copy(alpha = if (isSystemInDarkTheme()) 0.15f else 0.85f),
                                                Color(0xFFF4F4FF).copy(alpha = if (isSystemInDarkTheme()) 0.08f else 0.85f)
                                            )
                                        )
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFFC0C0F0).copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                    .padding(20.dp)
                                    .testTag("ai_overview_box")
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = "info SGE indicator",
                                            tint = KivoAiPurple,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Deep Search with KIVO AI 1",
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = KivoAiPurple
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(12.dp))

                                    if (aiOverview == null && loading) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                strokeWidth = 2.dp,
                                                color = KivoAiPurple
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                "KIVO generative model loading summaries...",
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                            )
                                        }
                                    } else {
                                        Text(
                                            text = aiOverview ?: "Analyzing global facts...",
                                            fontSize = 15.sp,
                                            lineHeight = 22.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )

                                        Spacer(modifier = Modifier.height(14.dp))
                                        Text(
                                            text = "Generative AI is experimental. Summarized from official networks.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                if (results.isEmpty() && !loading) {
                    item {
                        Text(
                            text = "No web index entries found.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                } else if (subTab == "image") {
                    // Image grid layout representation
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                        ) {
                            results.chunked(2).forEach { chunk ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    chunk.forEach { imageItem ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .padding(vertical = 5.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(MaterialTheme.colorScheme.surface)
                                                .border(
                                                    width = 1.dp,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                                .clickable {
                                                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(imageItem.link))
                                                    context.startActivity(browserIntent)
                                                }
                                        ) {
                                            Column {
                                                AsyncImage(
                                                    model = imageItem.link,
                                                    contentDescription = "Result Image",
                                                    modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(130.dp),
                                                    contentScale = ContentScale.Crop
                                                )
                                                Text(
                                                    text = imageItem.title ?: "Image Result",
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Medium,
                                                    modifier = Modifier.padding(8.dp),
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }
                                        }
                                    }
                                    if (chunk.size == 1) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // Standard text Search results
                    items(results) { item ->
                        SearchResultRowWidget(item = item)
                    }
                }

                // Show Paginated Load button
                if (results.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Button(
                                onClick = { viewModel.loadMoreResults() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                shape = RoundedCornerShape(24.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("load_more_btn"),
                                border = borderStrokeForPrimary()
                            ) {
                                if (loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                } else {
                                    Text("More results", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun borderStrokeForPrimary(): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(
        width = 1.6.dp,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
    )
}

@Composable
fun SearchResultRowWidget(item: SearchItem) {
    val context = LocalContext.current
    val domainUrl = remember(item.link) {
        try {
            Uri.parse(item.link).host ?: "kivo.com"
        } catch (e: Exception) {
            "kivo.com"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                try {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.link))
                    context.startActivity(browserIntent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Cannot open external website link", Toast.LENGTH_SHORT).show()
                }
            }
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Dynamic Favicon using standard google service
            AsyncImage(
                model = "https://www.google.com/s2/favicons?domain=$domainUrl&sz=32",
                contentDescription = "favicon source logo",
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color.White)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = domainUrl,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.title ?: "Untitled Index",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = item.snippet ?: "No overview detailed snippet is available.",
            fontSize = 14.sp,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun KivoSettingsScreen(viewModel: KivoViewModel) {
    val context = LocalContext.current
    val isDark by viewModel.isDarkTheme.collectAsStateWithLifecycle()
    val usernameEmail by viewModel.currentUserEmail.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .testTag("settings_screen")
    ) {
        Text(
            text = "Settings",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 24.dp, top = 20.dp)
        )

        // Dark Theme Switch Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isDark) Icons.Default.Brightness4 else Icons.Default.Brightness7,
                        contentDescription = "dark theme control",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "Dark Theme",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Optimize visual light spectrums",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
                Switch(
                    checked = isDark,
                    onCheckedChange = { viewModel.toggleTheme(it) },
                    modifier = Modifier.testTag("theme_switch"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = MaterialTheme.colorScheme.primary,
                        checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                    )
                )
            }
        }

        // Account management Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = "Account settings",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Identity",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = usernameEmail ?: "Not Signed In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (usernameEmail != null) "KIVO User Account synced" else "Sync history across search portals",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (usernameEmail != null) {
                    Button(
                        onClick = { viewModel.signOutUser() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("sign_out_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Sign Out", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { viewModel.toggleAuthDialog(true) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("sign_in_trigger"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Sign In to KIVO", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Clean Database / clear cache parameters
        Spacer(modifier = Modifier.height(12.dp))
        Button(
            onClick = {
                viewModel.clearHistory()
                Toast.makeText(context, "Search History Deleted", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("clear_history_btn"),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            ),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
        ) {
            Text("Clear Searched history entries", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

// --- High-Quality Composite Vector Drawing for logo ---

@Composable
fun KivoLogoCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val widthVal = size.width
        val heightVal = size.height

        val strokeWidthPx = widthVal * 0.11f

        val gradient = Brush.linearGradient(
            colors = listOf(KivoBlueStart, KivoBlueEnd),
            start = androidx.compose.ui.geometry.Offset(0f, 0f),
            end = androidx.compose.ui.geometry.Offset(widthVal, heightVal)
        )

        // Draw left vertical bar of 'K' symbol
        drawLine(
            brush = gradient,
            start = androidx.compose.ui.geometry.Offset(widthVal * 0.32f, heightVal * 0.18f),
            end = androidx.compose.ui.geometry.Offset(widthVal * 0.32f, heightVal * 0.82f),
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Round
        )

        // Draw chevron bracket '<' of 'K' symbol
        val pathOfBracket = androidx.compose.ui.graphics.Path().apply {
            moveTo(widthVal * 0.72f, heightVal * 0.18f)
            lineTo(widthVal * 0.36f, heightVal * 0.5f)
            lineTo(widthVal * 0.72f, heightVal * 0.82f)
        }

        drawPath(
            path = pathOfBracket,
            brush = gradient,
            style = Stroke(
                width = strokeWidthPx,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

// --- Curated helper classes ---

data class NewsCardItem(
    val title: String,
    val source: String,
    val domain: String,
    val imageUrl: String
)

@Composable
fun NewsCardWidget(item: NewsCardItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clickable { onClick() }
            .testTag("news_card_${item.source.lowercase()}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = "https://www.google.com/s2/favicons?domain=${item.domain}&sz=32",
                        contentDescription = "Src favicon",
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = item.source,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
        }
    }
}

@Composable
fun LanguageIndicatorPopup(
    message: String,
    onDismiss: () -> Unit
) {
    var visible by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(4000)
        visible = false
        onDismiss()
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 40.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                modifier = Modifier
                    .wrapContentHeight()
                    .widthIn(max = 500.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .shadow(8.dp)
                    .background(MaterialTheme.colorScheme.surface)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = message,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(
                        onClick = {
                            visible = false
                            onDismiss()
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Language warning",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KivoAuthDialog(
    onDismiss: () -> Unit,
    onSignInSuccess: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isNewAccountMode by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = { onDismiss() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("auth_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                KivoLogoCanvas(modifier = Modifier.size(60.dp))
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isNewAccountMode) "Create Account" else "Sign in",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = if (isNewAccountMode) "Join KIVO networks today" else "Use your KIVO Account",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email address") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        if (email.isNotBlank()) {
                            onSignInSuccess(email)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isNewAccountMode) "Sign Up" else "Next")
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isNewAccountMode) "Already have an account? Sign In" else "Create account",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .clickable { isNewAccountMode = !isNewAccountMode }
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

