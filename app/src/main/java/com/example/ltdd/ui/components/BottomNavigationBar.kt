package com.example.ltdd.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(
    navController: NavController,
    userId: String
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = navBackStackEntry?.destination?.route ?: ""

    val pinkPastel = Color(0xFFFFC0CB)
    val greenPastel = Color(0xFFB2DFDB)

    NavigationBar(containerColor = Color.White, tonalElevation = 6.dp) {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    tint = pinkPastel
                )
            },
            label = { Text("Trang chủ", style = MaterialTheme.typography.labelSmall) },
            selected = currentRoute.startsWith("home"),
            onClick = {
                navController.navigate("home/$userId") {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Care",
                    tint = greenPastel
                )
            },
            label = { Text("Chăm sóc", style = MaterialTheme.typography.labelSmall) },
            selected = currentRoute.startsWith("care"),
            onClick = {
                navController.navigate("care/$userId") {
                    launchSingleTop = true
                }
            }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = "Shop",
                    tint = pinkPastel
                )
            },
            label = { Text("Nhịp đập", style = MaterialTheme.typography.labelSmall) },
            selected = currentRoute.startsWith("shop"),
            onClick = {
                navController.navigate("shop/$userId") {
                    launchSingleTop = true
                }
            }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "Messages",
                    tint = greenPastel
                )
            },
            label = { Text("Nhắn tin", style = MaterialTheme.typography.labelSmall) },
            selected = currentRoute.startsWith("chat"),
            onClick = {
                navController.navigate("chat/$userId") {
                    launchSingleTop = true
                }
            }
        )

        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Account",
                    tint = pinkPastel
                )
            },
            label = { Text("Tài khoản", style = MaterialTheme.typography.labelSmall) },
            selected = currentRoute.startsWith("account"),
            onClick = {
                navController.navigate("account/$userId") {
                    launchSingleTop = true
                }
            }
        )
    }
}
