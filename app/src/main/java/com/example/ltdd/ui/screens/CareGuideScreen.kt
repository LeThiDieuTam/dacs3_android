@file:Suppress("UnusedImport")

package com.example.ltdd.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


data class Guide(val title: String, val content: String)

val growthGuides = listOf(
    Guide(
        "Cân nặng & chiều cao sơ sinh (0-1 tháng)",
        """
        ● Chuẩn WHO
          • Bé trai 3 – 4.4 kg  • 48 – 52 cm
          • Bé gái 2.9 – 4.2 kg • 47 – 51 cm
        
        ● Theo dõi
          • Tăng ≥150 g/tuần – dài thêm ≈3 cm.
        
        ● Mẹo
          • Cho bú sớm trong 1 giờ đầu.
          • Da-kề-da giúp ổn định nhịp tim & đường huyết.
        """.trimIndent()
    ),
    Guide(
        "Giai đoạn 2-6 tháng",
        """
        ● Chuẩn WHO (ví dụ 3 tháng)
          • Trai: 5.1 – 7.9 kg • 57 – 65 cm
          • Gái: 4.6 – 7.4 kg • 56 – 64 cm
        
        ● Theo dõi: 1 lần/tháng – tăng 600-800 g/tháng.
        ● Mẹo: Bú mẹ hoàn toàn; mẹ bổ sung DHA, vitamin D; tummy-time 3-4 lần/ngày.
        """.trimIndent()
    ),
    Guide(
        "Giai đoạn 6-12 tháng",
        """
        ● Mốc 9 tháng
          • Trai 7.1 – 10.9 kg • 67 – 75 cm
          • Gái 6.5 – 10.3 kg • 65 – 73 cm
        
        ● Theo dõi: cân mỗi 2 tuần khi mới ăn dặm (≥400 g/tháng).
        ● Mẹo: Ăn dặm giàu sắt/kẽm, thêm 5 ml dầu/bữa, giữ 500 ml sữa mẹ/ngày.
        """.trimIndent()
    ),
    Guide(
        "Trẻ 1-3 tuổi",
        """
        ● Công thức nhanh
          • Cân nặng ≈ Tuổi×2 + 8 (kg)
          • Chiều cao ≈ Tuổi×5 + 80 (cm)
        
        ● Theo dõi: mỗi 3 tháng; dùng biểu đồ Z-score.
        ● Mẹo: Vận động ≥60 phút/ngày; khẩu phần đủ 4 nhóm; 400-500 ml sữa/ngày.
        """.trimIndent()
    ),
    Guide(
        "Trẻ 3-5 tuổi (mẫu giáo)",
        """
        ● Chuẩn WHO (5 tuổi)
          • Trai 16 – 21 kg • 105 – 116 cm
          • Gái 15 – 20 kg • 104 – 115 cm
        
        ● BMI 14 – 17 là bình thường.
        ● Mẹo:
          1. Ăn sáng ≥25 % năng lượng.
          2. Đường <25 g & nước ngọt <100 ml/ngày.
          3. Ngủ 10-13 giờ; thể dục: bơi, xe đạp, nhảy lò cò.
        """.trimIndent()
    )
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareGuideScreen(
    navController: NavHostController,
    userId: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cẩm nang chăm sóc") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ExpandLess, // Bạn có thể thay bằng Icons.Default.ArrowBack nếu muốn
                            contentDescription = "Quay lại"
                        )
                    }
                }
            )
        }
    ) { insets ->
        LazyColumn(
            modifier = Modifier
                .padding(insets)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(growthGuides) { guide ->
                GuideCard(guide)
            }
        }
    }
}


@Composable
private fun GuideCard(guide: Guide) {
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
                    guide.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null
                )
            }

            if (expanded) {
                Spacer(Modifier.height(8.dp))
                Text(
                    guide.content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CareGuideScreenPreview() {
    CareGuideScreen(
        navController = rememberNavController(),
        userId = "123"
    )
}
