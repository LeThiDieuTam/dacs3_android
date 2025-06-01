package com.example.ltdd.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ltdd.models.Vitamin
import com.example.ltdd.viewmodel.VitaminUiState
import com.example.ltdd.viewmodel.VitaminViewModel

@Composable
private fun VitaminCard(vitamin: Vitamin) {
    var expanded by remember { mutableStateOf(false) }

    ElevatedCard(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    vitamin.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Thu gọn" else "Mở rộng"
                )
            }

            if (expanded) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Liều lượng: ${vitamin.dosage}\nCông dụng: ${vitamin.benefits}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoSungVitaminScreen(
    navController: NavHostController,
    userId: String,
    viewModel: VitaminViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bổ Sung Vitamin") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                }
            )
        }
    ) { insets ->
        Column(
            modifier = Modifier
                .padding(insets)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (uiState) {
                is VitaminUiState.Loading -> {
                    CircularProgressIndicator()
                    Text("Đang tải dữ liệu vitamin...")
                }
                is VitaminUiState.Success -> {
                    val vitamins = (uiState as VitaminUiState.Success).vitamins
                    if (vitamins.isEmpty()) {
                        Text("Không có dữ liệu vitamin nào.")
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(vitamins) { vitamin ->
                                VitaminCard(vitamin)
                            }
                        }
                    }
                }
                is VitaminUiState.Error -> {
                    val errorMessage = (uiState as VitaminUiState.Error).message
                    Text("Đã xảy ra lỗi: $errorMessage", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.fetchVitamins() }) {
                        Text("Thử lại")
                    }
                }
                is VitaminUiState.Idle -> {
                    Text("Chờ tải dữ liệu vitamin...")
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.fetchVitamins() }) {
                        Text("Tải dữ liệu")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BoSungVitaminScreenPreview() {
    BoSungVitaminScreen(
        navController = rememberNavController(),
        userId = "preview_user_id"
    )
}