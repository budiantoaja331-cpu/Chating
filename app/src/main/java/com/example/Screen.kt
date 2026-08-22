package com.example

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Share
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Chat : Screen("chat", "Obrolan", Icons.Filled.Email, Icons.Outlined.Email)
    object Nearby : Screen("nearby", "Teman", Icons.Filled.LocationOn, Icons.Outlined.LocationOn)
    object Story : Screen("story", "Story", Icons.Filled.Share, Icons.Outlined.Share)
    object Profile : Screen("profile", "Profil", Icons.Filled.Person, Icons.Outlined.Person)
}
