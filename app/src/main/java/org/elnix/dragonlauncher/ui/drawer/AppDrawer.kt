package org.elnix.dragonlauncher.ui.drawer

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.elnix.dragonlauncher.data.stores.DrawerSettingsStore
import org.elnix.dragonlauncher.ui.helpers.AppGrid
import org.elnix.dragonlauncher.utils.AppDrawerViewModel
import org.elnix.dragonlauncher.utils.actions.launchSwipeAction

@Suppress("AssignedValueIsNeverRead")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun AppDrawerScreen(
    appsViewModel: AppDrawerViewModel,
    initialPage: Int,
    showIcons: Boolean,
    showLabels: Boolean,
    gridSize: Int,
    searchBarBottom: Boolean,
    onClose: () -> Unit
) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    val userApps by appsViewModel.userApps.collectAsState()
    val workApps by appsViewModel.workProfileApps.collectAsState()
    val systemApps by appsViewModel.systemApps.collectAsState()
    val allApps by appsViewModel.allApps.collectAsState()

    val pages = mutableListOf("User", "System", "All")
    if (workApps.isNotEmpty()) {
        pages.add(1, "Work")
    }
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        pageCount = { pages.size }
    )


    val icons by appsViewModel.icons.collectAsState()

    val autoLaunchSingleMatch by DrawerSettingsStore.getAutoLaunchSingleMatch(ctx)
        .collectAsState(initial = true)


    var query by remember { mutableStateOf(TextFieldValue("")) }
    var dialogApp by remember { mutableStateOf<AppModel?>(null) }

    val filteredUser = remember(query.text, userApps) {
        userApps.filter { it.name.contains(query.text, ignoreCase = true) }
    }

    val filteredAll = remember(query.text, allApps) {
        allApps.filter { it.name.contains(query.text, ignoreCase = true) }
    }

    val filteredSystem = remember(query.text, systemApps) {
        systemApps.filter { it.name.contains(query.text, ignoreCase = true) }
    }

    val keyboard = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    var isFocused by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!isFocused) {
            focusRequester.requestFocus()
            isFocused = true
        }
    }


    LaunchedEffect(pagerState.currentPage) {
        scope.launch {
            DrawerSettingsStore.setInitialPage(ctx, pagerState.currentPage)
        }
    }

    LaunchedEffect(filteredUser, filteredSystem, filteredAll) {
        if (autoLaunchSingleMatch) {
            when (pagerState.currentPage) {
                0 -> {
                    if (filteredUser.size == 1) {
                        launchSwipeAction(ctx, filteredUser.first().action)
                        onClose()
                    }
                }
                1 -> {
                    if (filteredSystem.size == 1) {
                        launchSwipeAction(ctx, filteredSystem.first().action)
                        onClose()
                    }
                }
                2 -> {
                    if (filteredAll.size == 1) {
                        launchSwipeAction(ctx, filteredAll.first().action)
                        onClose()
                    }
                }
            }
        }
    }



    @Composable
    fun DrawerTextInput() {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .focusable(true),
            placeholder = { Text("Search apps…") },
            textStyle = LocalTextStyle.current.copy(color = Color.White),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedLabelColor = Color.White
            ),
            trailingIcon = {
                if (query.text != ""){
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier
                            .clickable { query = TextFieldValue("") }
                            .padding(5.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .clickable { onClose() }
                            .padding(5.dp)
                    )
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(WindowInsets().asPaddingValues())
            .imePadding()
            .padding(15.dp)
    ) {

        if (!searchBarBottom) {
            DrawerTextInput()
            Spacer(Modifier.height(12.dp))
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { pageIndex ->

            val list = when (pageIndex) {
                0 -> filteredUser
                1 -> filteredSystem
                else -> filteredAll
            }

            AppGrid(
                apps = list,
                icons = icons,
                gridSize = gridSize,
                txtColor = MaterialTheme.colorScheme.onSurface,
                showIcons = showIcons,
                showLabels = showLabels,
                onLongClick = { dialogApp = it }
            ) {
                launchSwipeAction(ctx, it.action)
                onClose()
            }
        }

        if (searchBarBottom) {

            Spacer(Modifier.height(12.dp))
            DrawerTextInput()
        }

    }

    if (dialogApp != null) {
        val app = dialogApp!!
        AppLongPressDialog(
            app = app,
            onDismiss = {
                dialogApp = null
            },
            onOpen = {
                launchSwipeAction(ctx, app.action)
                onClose()
            },
            onSettings = {
                val i = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = "package:${app.packageName}".toUri()
                }
                ctx.startActivity(i)
                onClose()
            },
            onUninstall = {
                val intent = Intent(Intent.ACTION_DELETE).apply {
                    data = "package:${app.packageName}".toUri()
                }
                ctx.startActivity(intent)
                onClose()
            }
        )
    }
}
