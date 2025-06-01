package com.example.ltdd.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ltdd.R
import com.example.ltdd.ui.components.BottomNavigationBar
import com.example.ltdd.ui.theme.mediumPink

val lightBlue = Color(0xFFE1F5FE)
val mediumBlue = Color(0xFF00BCD4)
val darkGray = Color(0xFF37474F)

data class AccountMenuItem(val title: String, val icon: ImageVector, val onClick: () -> Unit)

fun logout(context: Context, navController: NavController) {
    val sharedPref = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    sharedPref.edit().clear().apply()

    navController.navigate("login") {
        popUpTo(0) { inclusive = true }
    }
}

@Composable
fun AccountScreen(username: String, userId: String, navController: NavController) {
    val context = LocalContext.current

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController, userId = userId) },
        containerColor = lightBlue
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.banner_thaigiao),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = username,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = darkGray
                        )
                        Text(
                            text = "Xem trang cá nhân",
                            color = mediumPink,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                // TODO: Xử lý chuyển sang trang cá nhân
                            }
                        )
                    }
                }
            }

            val accountMenuItems = listOf(
                AccountMenuItem("Thông tin cá nhân", Icons.Default.Person) {
                    // TODO: Xử lý chuyển trang Thông tin cá nhân
                },
                AccountMenuItem("Cài đặt ứng dụng", Icons.Default.Settings) {
                    // TODO: Xử lý chuyển trang Cài đặt
                },
                AccountMenuItem("Thông báo", Icons.Default.Notifications) {
                    // TODO: Xử lý Thông báo
                },
                AccountMenuItem("Hỗ trợ", Icons.Default.HelpOutline) {
                    // TODO: Xử lý Hỗ trợ
                },
                AccountMenuItem("Đăng xuất", Icons.Default.ExitToApp) {
                    logout(context, navController)
                }
            )

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column {
                    accountMenuItems.forEachIndexed { index, item ->
                        AccountMenuItemRow(item = item)
                        if (index < accountMenuItems.lastIndex) {
                            Divider(color = mediumPink.copy(alpha = 0.3f), thickness = 0.8.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AccountMenuItemRow(item: AccountMenuItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { item.onClick() }
            .padding(vertical = 16.dp, horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.title,
            tint = mediumBlue,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = item.title,
            fontSize = 16.sp,
            color = darkGray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AccountScreenPreview() {
    val userId = 123
    AccountScreen(username = "Người dùng thử", userId = userId.toString(), navController = rememberNavController())
}
