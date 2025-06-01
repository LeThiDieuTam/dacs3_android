package com.example.ltdd.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ltdd.models.CrisisWeek
import com.example.ltdd.remote.RetrofitClient
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrisisWeekScreen(navController: NavHostController, userId: String) {
    var crisisWeeks by remember { mutableStateOf<List<CrisisWeek>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    val fetchData: suspend () -> Unit = {
        try {
            val data = RetrofitClient.crisisWeekService.getCrisisWeekInfo(userId)
            crisisWeeks = data
            isLoading = false
            errorMessage = null
        } catch (e: Exception) {
            Log.e("CrisisWeekScreen", "Lỗi khi lấy thông tin tuần khủng hoảng: ${e.message}", e)
            errorMessage = "Không thể tải thông tin tuần khủng hoảng. Vui lòng thử lại."
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        fetchData()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tuần Khủng Hoảng", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (errorMessage != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        errorMessage!!,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        // Thử lại lấy dữ liệu
                        isLoading = true
                        errorMessage = null
                        crisisWeeks = emptyList() // Xóa dữ liệu cũ
                        coroutineScope.launch { fetchData() } // Gọi lại fetchData
                    }) {
                        Text("Thử lại")
                    }
                }
            } else if (crisisWeeks.isEmpty()) {
                Text(
                    "Không có thông tin tuần khủng hoảng nào để hiển thị.",
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp), // Khoảng cách xung quanh toàn bộ danh sách
                    verticalArrangement = Arrangement.spacedBy(16.dp) // Khoảng cách giữa các mục
                ) {
                    items(crisisWeeks) { weekInfo ->
                        CrisisWeekCard(weekInfo)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrisisWeekCard(weekInfo: CrisisWeek) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            weekInfo.week?.let {
                Text(
                    text = "Tuần ${it}: ${weekInfo.title ?: "Không có tiêu đề"}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
            } ?: run {
                weekInfo.title?.let {
                    Text(
                        text = it,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            weekInfo.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            weekInfo.createdAt?.let {
                Text(
                    text = "Cập nhật cuối: ${it.substringBefore(" ") ?: "--"}", // Chỉ hiển thị ngày
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CrisisWeekScreenPreview() {
    CrisisWeekScreen(navController = rememberNavController(), userId = "123")
}