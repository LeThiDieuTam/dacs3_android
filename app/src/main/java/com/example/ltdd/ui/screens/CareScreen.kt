package com.example.ltdd.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.ltdd.R
import com.example.ltdd.ui.components.BottomNavigationBar
import kotlin.math.ceil

data class FunctionItem(val title: String, val iconResId: Int, val route: String)
data class ChuyenGiaItem(val name: String, val avatarResId: Int)


val lightPink = Color(0xFFFCE4EC)
val mediumPink = Color(0xFFF48FB1)
val darkPink = Color(0xFFE91E63)

@Composable
fun CareScreen(navController: NavController, userId: String) {

    val chuyenGiaList = listOf(
        ChuyenGiaItem("BS.\nVũ Tề Đăng", R.drawable.doctor_avatar1),
        ChuyenGiaItem("ThS.\nLê Thị Thanh Hương", R.drawable.doctor_avatar2),
        ChuyenGiaItem("Chuyên gia\nTạ Anh Tuấn", R.drawable.doctor_avatar3),
    )

    val functionItems = listOf(
        FunctionItem("Thông Tin\nCon Yêu", R.drawable.ic_calendar1, "child_info"),
        FunctionItem("Con Yêu Theo\nTháng Tuổi", R.drawable.information, "growth"),
        FunctionItem("Cân Nặng &\nChiều Cao", R.drawable.ic_weight1, "weight_height"),
        FunctionItem("Tuần\nKhủng Hoảng", R.drawable.week1, "crisis"),
        FunctionItem("Lưu Giữ\nKhoảnh Khắc", R.drawable.take_photo, "moments"),
        FunctionItem("Tiêm phòng\nMẹ & Bé", R.drawable.vacin, "medical_record")
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController, userId = userId) },
        containerColor = lightPink
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {


            TabRow(
                selectedTabIndex = 0,
                containerColor = Color.White,
                contentColor = darkPink
            ) {
                listOf("Cẩm nang", "Video").forEachIndexed { index, title ->
                    Tab(
                        selected = index == 0,
                        onClick = { /* TODO: Switch tab */ },
                        text = {
                            Text(
                                text = title,
                                color = if (index == 0) darkPink else mediumPink
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Bác sĩ Chuyên gia",
                style = MaterialTheme.typography.headlineSmall.copy(color = darkPink),
                modifier = Modifier.padding(start = 16.dp, top = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                chuyenGiaList.forEach { chuyenGia ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painter = painterResource(id = chuyenGia.avatarResId),
                            contentDescription = chuyenGia.name,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = chuyenGia.name,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = mediumPink,
                                fontWeight = FontWeight.SemiBold
                            ),
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            EqualWidthGrid(columns = 3, spacing = 12.dp, modifier = Modifier.padding(16.dp)) {
                functionItems.forEach { item ->
                    Column(
                        modifier = Modifier
                            .clickable {
                                navController.navigate("${item.route}/$userId")
                            }
                            .padding(vertical = 8.dp, horizontal = 8.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = item.iconResId),
                                contentDescription = item.title,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.title,
                            style = TextStyle(
                                fontSize = 13.sp,
                                color = darkPink,
                                fontWeight = FontWeight.Medium
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EqualWidthGrid(
    columns: Int,
    spacing: Dp = 0.dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        val columnWidth = (constraints.maxWidth - (columns - 1) * spacing.roundToPx()) / columns
        val itemConstraints = constraints.copy(minWidth = columnWidth, maxWidth = columnWidth)
        val placeables = measurables.map { it.measure(itemConstraints) }

        val rows = ceil(placeables.size.toFloat() / columns).toInt()
        val rowHeight = placeables.firstOrNull()?.height ?: 0
        val totalHeight = rows * rowHeight + (rows - 1) * spacing.roundToPx()

        layout(constraints.maxWidth, totalHeight) {
            var x = 0
            var y = 0
            placeables.forEachIndexed { index, placeable ->
                placeable.placeRelative(x, y)
                x += columnWidth + spacing.roundToPx()
                if ((index + 1) % columns == 0) {
                    x = 0
                    y += placeable.height + spacing.roundToPx()
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CareScreenPreview() {
    CareScreen(userId = "1", navController = rememberNavController())
}
