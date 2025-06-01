package com.example.ltdd

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ltdd.ui.HomeScreen
import com.example.ltdd.ui.screens.*
import com.example.ltdd.ui.theme.LTDDTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LTDDTheme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login") {

        // Đăng nhập
        composable("login") {
            LoginScreen(
                onLoginSuccess = { name, userId ->
                    // Mã hóa tên người dùng và điều hướng đến trang chủ
                    val encodedName = Uri.encode(name)
                    navController.navigate("home/$encodedName/$userId") {
                        popUpTo("login") { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.popBackStack("login", false)
                }
            )
        }

        composable(
            route = "home/{username}/{userId}",
            arguments = listOf(
                navArgument("username") { type = NavType.StringType },
                navArgument("userId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val username = backStackEntry.arguments?.getString("username") ?: "Mẹ Bầu"
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            Log.d("Navigation", "Navigating to home with username: $username, userId: $userId")
            HomeScreen(userId = userId, username = username, navController = navController)
        }

        composable(
            route = "care/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            val userId = it.arguments?.getString("userId") ?: ""
            CareScreen(navController, userId)
        }


        composable(
            route = "shop/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            val userId = it.arguments?.getString("userId") ?: ""
            ShopScreen(navController, userId)
        }


        composable(
            route = "chat/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            val userId = it.arguments?.getString("userId") ?: ""
            ChatScreen(navController, userId)
        }

        composable(
            route = "account/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) {
            val userId = it.arguments?.getString("userId") ?: ""
            AccountScreen(username = "Mẹ Bầu", navController = navController, userId = userId)
        }
        composable(
            "child_info/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId")
            ChildInfoScreen(navController, userId)
        }

        composable("growth/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val factory = remember(userId) { GrowthViewModelFactory(userId) }

            GrowthScreen(
                navController = navController,
                userId = userId,
                viewModelFactory = factory
            )
        }

        composable(
            "weight_height/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            CareGuideScreen(navController, userId)
        }

        composable(
            "crisis/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            CrisisWeekScreen(navController, userId)
        }

        composable(
            "moments/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            SanPhamScreen(navController)
        }
        composable(
            "medical_record/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            MedicalRecordScreen(navController, userId)
        }
        composable(
            "soKhamThai/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            SoKhamThaiScreen(navController, userId)
        }
        composable(
            "soTayDinhDuong/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            SoTayDinhDuongScreen(navController, userId)
        }

        composable(
            "dauAnThaiKy/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            DauAnThaiKyScreen(navController, userId)
        }
        composable(
            "vitamin/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            BoSungVitaminScreen(navController, userId)
        }
        composable(
            "plan/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
           LapTrinhSinhScreen(navController, userId)
        }
    }
    }
