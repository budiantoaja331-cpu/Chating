package com.example

import android.os.Bundle
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.ui.res.stringResource

import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.filled.Add

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

import android.Manifest
import android.os.Build
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log

class MainActivity : ComponentActivity() {

    override fun attachBaseContext(newBase: android.content.Context) {
        val updatedContext = LanguageHelper.applyLanguage(newBase)
        super.attachBaseContext(updatedContext)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
        } else {
            Log.d("MainActivity", "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LanguageHelper.applyLanguage(this)
        enableEdgeToEdge()
        
        askNotificationPermission()
        
        // Fetch FCM token on startup
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("MainActivity", "FCM Token: $token")
            // Can update this to Firestore under the current user's document
        }
        
        setContent {
            MyApplicationTheme {
                RootScreen()
            }
        }
    }
    
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // Permission is already granted
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}

sealed class Screen(
    val route: String, 
    val titleRes: Int, 
    val activeIcon: ImageVector, 
    val inactiveIcon: ImageVector
) {
    object Nearby : Screen("nearby", R.string.nav_nearby, Icons.Filled.LocationOn, Icons.Outlined.LocationOn)
    object Story : Screen("feed", R.string.nav_feed, Icons.Filled.DynamicFeed, Icons.Outlined.DynamicFeed)
    object CreatePost : Screen("create_post", R.string.nav_create_post, Icons.Filled.Add, Icons.Filled.Add)
    object Chat : Screen("chat", R.string.nav_chat, Icons.AutoMirrored.Filled.Chat, Icons.AutoMirrored.Outlined.Chat)
    object Notifications : Screen("notifications", R.string.nav_notifications, Icons.Filled.Notifications, Icons.Outlined.Notifications)
    object CallHistory : Screen("call_history", R.string.nav_calls, Icons.Filled.Call, Icons.Outlined.Call)
    object Profile : Screen("profile", R.string.nav_profile, Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun RootScreen() {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()
    val context = LocalContext.current
    
    Box(modifier = Modifier.fillMaxSize()) {
        when (val state = authState) {
            is AuthState.Success -> {
                if (state.isProfileComplete) {
                    LaunchedEffect(state.userId) {
                        android.widget.Toast.makeText(context, "Selamat datang, ${state.userName}!", android.widget.Toast.LENGTH_SHORT).show()
                    }
                    MainAppScreen(state.userId, state.userName, state.profileImageUrl, onSignOut = { authViewModel.signOut() })
                } else {
                    val profileViewModel = remember(state.userId) { UserProfileViewModel(state.userId, state.userName, state.profileImageUrl) }
                    ProfileSetupScreen(
                        viewModel = profileViewModel,
                        onProfileComplete = {
                            authViewModel.markProfileComplete()
                        }
                    )
                }
            }
            else -> {
                LoginScreen(authViewModel)
            }
        }
        
        FirebaseDiagnosticUI()
    }
}

@Composable
fun MainAppScreen(userId: String, userName: String, profileImageUrl: String? = null, onSignOut: () -> Unit = {}) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val callManager = remember(userId) { ActiveCallManager(userId) }
    val incomingCall by callManager.incomingCall.collectAsState()

    DisposableEffect(userId) {
        callManager.startListening()
        PresenceManagerInstance.instance.startTracking(userId)
        UserSessionManager.startListening(userId)
        onDispose { 
            callManager.stopListening() 
            PresenceManagerInstance.instance.stopTracking()
        }
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
    
    val appViewModelFactory = remember { AppViewModelFactory(profileImageUrl, userId, userName) }
    
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
                    val titleText = stringResource(screen.titleRes)
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                imageVector = if (selected) screen.activeIcon else screen.inactiveIcon, 
                                contentDescription = titleText 
                            ) 
                        },
                        label = { Text(titleText) },
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
                NearbyScreen(viewModel = viewModel(factory = appViewModelFactory), onNavigateToProfile = { otherUserId ->
                    navController.navigate("friendProfile/$otherUserId")
                }) 
            }
            composable(Screen.Story.route) { 
                StoryScreen(
                    viewModel = viewModel(factory = appViewModelFactory),
                    onNavigateToCreatePost = { navController.navigate(Screen.CreatePost.route) },
                    onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) },
                    onNavigateToUserProfile = { userId -> navController.navigate("friendProfile/$userId") }
                ) 
            }
            composable(Screen.Notifications.route) {
                NotificationScreen(
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            composable(Screen.CreatePost.route) {
                CreatePostScreen(
                    viewModel = viewModel(factory = appViewModelFactory),
                    userProfileViewModel = viewModel(factory = appViewModelFactory),
                    onNavigateBack = { navController.navigateUp() }
                )
            }
            composable(Screen.CallHistory.route) { 
                CallHistoryScreen(viewModel = viewModel(factory = appViewModelFactory), onNavigateToCall = { otherId, otherName, isVideo -> 
                    val channelId = "call_" + minOf(userId, otherId) + "_" + maxOf(userId, otherId)
                    callManager.initiateCall(userName, otherId, otherName, isVideo, channelId)
                    navController.navigate("callScreen/$channelId/$isVideo") 
                }) 
            }
            composable(Screen.Chat.route) { 
                ChatListScreen(viewModel = viewModel(factory = appViewModelFactory), onNavigateToChat = { otherUserId, otherUserName ->
                    navController.navigate("chatRoom/$otherUserId/${Uri.encode(otherUserName)}")
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
            composable(Screen.Profile.route) { 
                UserProfileScreen(
                    onSignOut = onSignOut,
                    viewModel = viewModel(factory = appViewModelFactory), 
                    storyViewModel = viewModel(factory = appViewModelFactory)
                ) 
            }
            
            composable(
                route = "friendProfile/{friendId}",
                arguments = listOf(navArgument("friendId") { type = NavType.StringType })
            ) { backStackEntry ->
                val friendId = backStackEntry.arguments?.getString("friendId") ?: ""
                FriendProfileScreen(
                    friendId = friendId,
                    onNavigateBack = { navController.navigateUp() },
                    onNavigateToChat = { targetUserId, targetUserName ->
                        navController.navigate("chatRoom/$targetUserId/${Uri.encode(targetUserName)}")
                    }
                )
            }
            
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
                    onNavigateToProfile = { targetUserId ->
                        navController.navigate("friendProfile/$targetUserId")
                    },
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
