package com.example.ltdd.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun MomentsScreen(navController: NavHostController, userId: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Lưu Giữ Khoảnh Khắc", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { }) {
            Text("Xem khoảnh khắc đã lưu")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Các khoảnh khắc của bạn sẽ được hiển thị ở đây.", style = MaterialTheme.typography.bodyMedium)
    }
}

@Preview
@Composable
fun MomentsScreenPreview() {
    MomentsScreen(navController = rememberNavController(), userId = "123")
}
