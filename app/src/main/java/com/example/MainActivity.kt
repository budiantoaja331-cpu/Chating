package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.DynamicFeed
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                RootScreen()
            }
        }
    }
}

sealed class Screen(
    val route: String, 
    val title: String, 
    val activeIcon: ImageVector, 
    val inactiveIcon: ImageVector
) {
    object Nearby : Screen("nearby", "Teman", Icons.Filled.LocationOn, Icons.Outlined.LocationOn)
    object Story : Screen("story", "Story", Icons.Filled.DynamicFeed, Icons.Outlined.DynamicFeed)
    object Chat : Screen("chat", "Obrolan", Icons.Filled.Chat, Icons.Outlined.Chat)
    object CallHistory : Screen("call_history", "Panggilan", Icons.Filled.Call, Icons.Outlined.Call)
    object Profile : Screen("profile", "Profil", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun RootScreen() {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    when (val state = authState) {
        is AuthState.Success -> {
            MainAppScreen(state.userId, state.userName)
        }
        else -> {
            LoginScreen(authViewModel)
        }
    }
}

@Composable
fun MainAppScreen(userId: String, userName: String) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val callManager = remember(userId) { ActiveCallManager(userId) }
    val incomingCall by callManager.incomingCall.collectAsState()

    DisposableEffect(userId) {
        callManager.startListening()
        onDispose { callManager.stopListening() }
    }

    incomingCall?.let { call ->
        IncomingCallDialog(
            call = call,
            onAccept = {
                callManager.acceptCall(call.id)
                navController.navigate("callScreen/${call.channelId}/${call.isVideoCall}")
            },
            onReject = {
                callManager.rejectCall(call.id)
            }
        )
    }
    
    val appViewModelFactory = remember { AppViewModelFactory(userId, userName) }
    
    val items = listOf(
        Screen.Nearby,
        Screen.Story,
        Screen.Chat,
        Screen.CallHistory,
        Screen.Profile
    )
    
    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                items.forEach { screen ->
                    val selected = currentRoute == screen.route
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                imageVector = if (selected) screen.activeIcon else screen.inactiveIcon, 
                                contentDescription = screen.title 
                            ) 
                        },
                        label = { Text(screen.title) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController, 
            startDestination = Screen.Story.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Nearby.route) { 
                NearbyScreen(viewModel = viewModel(factory = appViewModelFactory), onNavigateToChat = { otherUserId, otherUserName ->
                    navController.navigate("chatRoom/$otherUserId/$otherUserName")
                }) 
            }
            composable(Screen.Story.route) { StoryScreen(viewModel = viewModel(factory = appViewModelFactory)) }
            composable(Screen.CallHistory.route) { 
                CallHistoryScreen(viewModel = viewModel(factory = appViewModelFactory), onNavigateToCall = { otherId, otherName, isVideo -> 
                    val channelId = "call_" + minOf(userId, otherId) + "_" + maxOf(userId, otherId)
                    callManager.initiateCall(userName, otherId, otherName, isVideo, channelId)
                    navController.navigate("callScreen/$channelId/$isVideo") 
                }) 
            }
            composable(Screen.Chat.route) { 
                ChatListScreen(viewModel = viewModel(factory = appViewModelFactory), onNavigateToChat = { otherUserId, otherUserName ->
                    navController.navigate("chatRoom/$otherUserId/$otherUserName")
                })
            }
            composable(
                route = "callScreen/{channelId}/{isVideo}",
                arguments = listOf(
                    navArgument("channelId") { type = NavType.StringType },
                    navArgument("isVideo") { type = NavType.BoolType }
                )
            ) { backStackEntry ->
                val channelId = backStackEntry.arguments?.getString("channelId") ?: ""
                val isVideo = backStackEntry.arguments?.getBoolean("isVideo") ?: false
                CallScreen(
                    channelName = channelId,
                    isVideoCall = isVideo,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            composable(Screen.Profile.route) { UserProfileScreen(viewModel(factory = appViewModelFactory)) }
            
            composable(
                route = "chatRoom/{otherUserId}/{otherUserName}",
                arguments = listOf(
                    navArgument("otherUserId") { type = NavType.StringType },
                    navArgument("otherUserName") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val otherUserId = backStackEntry.arguments?.getString("otherUserId") ?: ""
                val otherUserName = backStackEntry.arguments?.getString("otherUserName") ?: ""
                ChatRoomScreen(
                    onNavigateToCall = { targetUserId, isVideo ->
                        val channelId = "call_" + minOf(userId, targetUserId) + "_" + maxOf(userId, targetUserId)
                        callManager.initiateCall(userName, targetUserId, otherUserName, isVideo, channelId)
                        navController.navigate("callScreen/$channelId/$isVideo")
                    },
                    currentUserId = userId,
                    currentUserName = userName,
                    otherUserId = otherUserId,
                    otherUserName = otherUserName,
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}
