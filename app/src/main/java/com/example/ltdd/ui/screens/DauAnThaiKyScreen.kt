package com.example.ltdd.ui.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

import com.example.ltdd.models.FoodPost
import com.example.ltdd.remote.RetrofitClient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DauAnThaiKyScreen(navController: NavController, userId: String) {
    var posts by remember { mutableStateOf<List<FoodPost>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        try {
            posts = RetrofitClient.foodPostService.getFoodPosts()
        } catch (e: Exception) {
            Log.e("FoodPost", "Lỗi khi lấy bài viết dinh dưỡng: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dấu Ấn Thai Kỳ") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary, titleContentColor = Color.White)
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            if (posts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("Không có bài viết nào để hiển thị.")
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(posts) { post ->
                        FoodPostCard(post)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodPostCard(post: FoodPost) {

    val BASE_IMAGE_URL = "  https://cd89-2001-ee0-4b6d-f0a0-bc15-4b82-50c3-ee65.ngrok-free.app/mevabe_api/"

    val fullImageUrl = if (!post.imageUrl.isNullOrEmpty() && !post.imageUrl.startsWith("http")) {
        BASE_IMAGE_URL + post.imageUrl
    } else {
        post.imageUrl ?: ""
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FFF8)),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(post.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))


            if (fullImageUrl.isNotBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(fullImageUrl), // <-- Sử dụng URL đã ghép
                    contentDescription = null, // Có thể cải thiện bằng cách sử dụng post.name
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            post.description?.let {
                Text("Mô tả: $it", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(4.dp))
            }

            post.nutrition?.let {
                Text("Dinh dưỡng: $it", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(4.dp))
            }

            post.benefits?.let {
                Text("Lợi ích: $it", style = MaterialTheme.typography.bodyMedium, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(4.dp))
            }

            post.category?.let {
                Text("Loại: $it", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text("Ngày đăng: ${post.createdAt}", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
    }
}