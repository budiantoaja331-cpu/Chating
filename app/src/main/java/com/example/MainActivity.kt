package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.theme.ChatMicAllTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatMicAllTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel(factory = AppViewModelFactory())
    val authState by authViewModel.authState.collectAsState()

    when (authState) {
        is AuthState.Unauthenticated -> {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = { }
            )
        }
        else -> {
            val items = listOf(
                Screen.Chat,
                Screen.Nearby,
                Screen.Story,
                Screen.Profile
            )
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            val showBottomBar = currentRoute in items.map { it.route }

            Scaffold(
                bottomBar = {
                    if (showBottomBar) {
                        NavigationBar {
                            items.forEach { screen ->
                                val selected = currentRoute == screen.route
                                NavigationBarItem(
                                    icon = {
                                        Icon(
                                            if (selected) screen.selectedIcon else screen.unselectedIcon,
                                            contentDescription = screen.title
                                        )
                                    },
                                    label = { Text(screen.title) },
                                    selected = selected,
                                    onClick = {
                                        navController.navigate(screen.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
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
                }
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = Screen.Chat.route,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    composable(Screen.Chat.route) {
                        val chatListViewModel: ChatListViewModel = viewModel(factory = AppViewModelFactory())
                        ChatListScreen(
                            viewModel = chatListViewModel,
                            onChannelClick = { channelId, channelName ->
                                navController.navigate("chat_room/$channelId/$channelName")
                            }
                        )
                    }
                    composable("chat_room/{channelId}/{channelName}") { backStackEntry ->
                        val channelId = backStackEntry.arguments?.getString("channelId") ?: ""
                        val channelName = backStackEntry.arguments?.getString("channelName") ?: ""
                        val chatRoomViewModel: ChatRoomViewModel = viewModel(
                            factory = ChatRoomViewModelFactory(channelId)
                        )
                        ChatRoomScreen(
                            channelId = channelId,
                            channelName = channelName,
                            viewModel = chatRoomViewModel,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable(Screen.Nearby.route) {
                        val nearbyViewModel: NearbyViewModel = viewModel(factory = AppViewModelFactory())
                        NearbyScreen(
                            viewModel = nearbyViewModel,
                            onStartChat = { userId, userName ->
                                navController.navigate("chat_room/direct_$userId/$userName")
                            }
                        )
                    }
                    composable(Screen.Story.route) {
                        val storyViewModel: StoryViewModel = viewModel(factory = AppViewModelFactory())
                        StoryScreen(viewModel = storyViewModel)
                    }
                    composable(Screen.Profile.route) {
                        val profileViewModel: UserProfileViewModel = viewModel(factory = AppViewModelFactory())
                        UserProfileScreen(
                            viewModel = profileViewModel,
                            onSignOut = { authViewModel.signOut() }
                        )
                    }
                }
            }
        }
    }
}
