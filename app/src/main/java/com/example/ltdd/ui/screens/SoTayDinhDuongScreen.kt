package com.example.ltdd.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ltdd.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoTayDinhDuongScreen(navController: NavController, userId: String) {
    val suitableFoods = listOf(
        "Bắp cải ", "Đậu nành ", "Hương thảo", "Hoa chuối", "Bông cải xanh"
    )
    val suitableImages = listOf(
        R.drawable.bap_cai, R.drawable.dau_nanh, R.drawable.huong_thao, R.drawable.hoa_chuoi, R.drawable.bong_cai
    )

    val foodGroups = listOf(
        "Thịt", "Thủy hải sản", "Rau củ", "Trái cây", "Ăn vặt",
        "Đồ uống", "Thực phẩm bổ sung", "Hạt - Ngũ cốc", "Thực phẩm từ sữa"
    )
    val foodIcons = listOf(
        R.drawable.ic_thit, R.drawable.ic_thuy_hai_san, R.drawable.ic_rau_cu, R.drawable.ic_trai_cay,
        R.drawable.ic_an_vat, R.drawable.ic_do_uong, R.drawable.ic_bo_sung, R.drawable.ic_hat, R.drawable.ic_sua
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sổ tay dinh dưỡng") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Person, contentDescription = "Profile")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Text("Phù hợp với mẹ", style = MaterialTheme.typography.titleMedium)
                LazyRow(modifier = Modifier.padding(top = 8.dp)) {
                    items(suitableFoods.size) { index ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(end = 16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = suitableImages[index]),
                                contentDescription = suitableFoods[index],
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            Text(
                                suitableFoods[index],
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            item {
                Text("Nhóm thực phẩm", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    userScrollEnabled = false
                ) {
                    items(foodGroups.size) { index ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFFEDEE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = foodIcons[index]),
                                    contentDescription = foodGroups[index],
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                foodGroups[index],
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            item {
                NutritionInfoCards()
            }
        }
    }
}

@Composable
fun NutritionInfoCards() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Card 1
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFD5F4C7)), // Màu xanh nhạt
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(
                    imageVector = Icons.Default.LocalDining,
                    contentDescription = "Icon",
                    tint = Color(0xFF2E7D32), // Màu xanh đậm
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "Truy xuất thành phần dinh dưỡng chính xác",
                        color = Color(0xFF2E7D32),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Babiuni hỗ trợ truy xuất thành phần dinh dưỡng có trong từng loại thực phẩm một cách chính xác và chi tiết.\n\n" +
                                "Mẹ cũng có thể truy xuất nguyên liệu có chứa thành phần dinh dưỡng cần thiết giúp lựa chọn thực phẩm chính xác, hiệu quả, tiết kiệm thời gian và chi phí.",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
        }

        // Card 2
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2C6)), // Màu vàng nhạt
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Icon(
                    imageVector = Icons.Default.MenuBook,
                    contentDescription = "Icon",
                    tint = Color(0xFFB71C1C), // Màu đỏ đậm
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        "Kho tàng kiến thức về dinh dưỡng",
                        color = Color(0xFFB71C1C),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Babiuni cung cấp kho tàng kiến thức về dinh dưỡng bổ ích qua các bài viết phân chia theo chủ đề rõ ràng, gợi ý nội dung phù hợp với từng giai đoạn cho mẹ tham khảo hằng ngày.\n\n" +
                                "Có sẵn các kiến thức bổ ích về dinh dưỡng liên quan tới bệnh lý giúp mẹ bầu cải thiện sức khoẻ, khắc phục tình trạng bệnh.",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSoTayDinhDuongScreen() {
    SoTayDinhDuongScreen(
        navController = rememberNavController(),
        userId = TODO()
    )
}
