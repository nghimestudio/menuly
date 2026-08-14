package com.menuly.app

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.menuly.app.R
import com.menuly.app.ui.MenulyViewModel
import com.menuly.app.ui.screens.AnalyzingScreen
import com.menuly.app.ui.screens.HistoryScreen
import com.menuly.app.ui.screens.HomeScreen
import com.menuly.app.ui.screens.LanguageSelectScreen
import com.menuly.app.ui.screens.ResultScreen
import com.menuly.app.ui.screens.ScanScreen
import com.menuly.app.ui.theme.AccentPink
import com.menuly.app.ui.theme.MenulyBlack
import com.menuly.app.ui.theme.MenulyTheme
import com.menuly.app.ui.theme.MenulyWhite
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = application as MenulyApp
        setContent {
            MenulyTheme {
                MenulyNav(app)
            }
        }
    }
}

private object Routes {
    const val Language = "language"
    const val Home = "home"
    const val Scan = "scan"
    const val Analyzing = "analyzing"
    const val Result = "result"
    const val History = "history"
}

@Composable
private fun MenulyNav(app: MenulyApp) {
    val nav = rememberNavController()
    val vm: MenulyViewModel = viewModel(factory = MenulyViewModel.factory(app.container))
    val state by vm.ui.collectAsState()
    val history by vm.history.collectAsState()
    val languageTag by vm.languageTag.collectAsState()
    val snackbar = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var bootReady by remember { mutableStateOf(false) }
    var startOnLanguage by remember { mutableStateOf(false) }
    var pendingLangTag by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val chosen = app.container.localeStore.hasChosenLanguage.first()
        val tag = app.container.localeStore.languageTag.first()
        pendingLangTag = tag
        startOnLanguage = !chosen
        bootReady = true
    }

    LaunchedEffect(languageTag) {
        pendingLangTag = languageTag
    }

    var hasCamera by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasCamera = granted }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbar.showSnackbar(it)
            vm.clearError()
        }
    }

    LaunchedEffect(state.isAnalyzing, state.result, state.error) {
        when {
            state.isAnalyzing -> {
                if (nav.currentDestination?.route != Routes.Analyzing) {
                    nav.navigate(Routes.Analyzing) { launchSingleTop = true }
                }
            }
            state.result != null && !state.isAnalyzing -> {
                if (nav.currentDestination?.route != Routes.Result) {
                    nav.navigate(Routes.Result) {
                        popUpTo(Routes.Home) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            }
            state.error != null &&
                !state.isAnalyzing &&
                state.result == null &&
                nav.currentDestination?.route == Routes.Analyzing -> {
                nav.popBackStack(Routes.Scan, inclusive = false)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MenulyBlack),
    ) {
        if (!bootReady) {
            CircularProgressIndicator(
                color = AccentPink,
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            NavHost(
                navController = nav,
                startDestination = if (startOnLanguage) Routes.Language else Routes.Home,
            ) {
                composable(Routes.Language) {
                    LanguageSelectScreen(
                        selectedTag = pendingLangTag,
                        onSelect = { pendingLangTag = it },
                        onContinue = {
                            scope.launch {
                                vm.setLanguage(pendingLangTag, markChosen = true)
                                nav.navigate(Routes.Home) {
                                    popUpTo(Routes.Language) { inclusive = true }
                                }
                            }
                        },
                    )
                }
                composable(Routes.Home) {
                    HomeScreen(
                        state = state,
                        onSelectMood = vm::selectMood,
                        onScan = {
                            vm.startScanSession()
                            nav.navigate(Routes.Scan)
                        },
                        onHistory = { nav.navigate(Routes.History) },
                        onLanguage = { nav.navigate(Routes.Language) },
                    )
                }
                composable(Routes.Scan) {
                    androidx.compose.runtime.key(state.scanEpoch) {
                        ScanScreen(
                            onBack = { nav.popBackStack() },
                            onCaptured = vm::onFrameCaptured,
                            hasCameraPermission = hasCamera,
                            onRequestPermission = {
                                permissionLauncher.launch(Manifest.permission.CAMERA)
                            },
                        )
                    }
                }
                composable(Routes.Analyzing) {
                    AnalyzingScreen(message = state.analyzeMessage)
                }
                composable(Routes.Result) {
                    val result = state.result
                    if (result != null) {
                        ResultScreen(
                            result = result,
                            menuText = state.menuText,
                            followUpNote = state.customNote,
                            isAsking = state.isAskingFollowUp,
                            onFollowUpChange = vm::setCustomNote,
                            onAskFollowUp = vm::askFollowUp,
                            onBack = {
                                vm.clearResult()
                                nav.popBackStack(Routes.Home, inclusive = false)
                            },
                            onScanAgain = {
                                vm.clearResult()
                                vm.startScanSession()
                                nav.navigate(Routes.Scan) {
                                    popUpTo(Routes.Home) { inclusive = false }
                                }
                            },
                        )
                    }
                }
                composable(Routes.History) {
                    HistoryScreen(
                        historyItems = history,
                        onBack = { nav.popBackStack() },
                        onOpen = { entity ->
                            vm.loadHistoryResult(entity)
                            nav.navigate(Routes.Result)
                        },
                        onDelete = vm::deleteHistory,
                    )
                }
            }
        }

        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
        ) { data ->
            Snackbar(
                action = {
                    TextButton(onClick = { data.dismiss() }) {
                        Text(stringResource(R.string.ok), color = MenulyWhite)
                    }
                },
            ) {
                Text(data.visuals.message)
            }
        }
    }
}
