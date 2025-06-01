package com.example.ltdd.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
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

val PrimaryPink = Color(0xFFE91E63)
val LightPinkBackground = Color(0xFFFCE4EC)
val AccentPink = Color(0xFFF48FB1)
val TextPrimary = Color(0xFF424242)
val TextSecondary = Color(0xFF757575)
val CardBackground = Color.White
val GreenAccent = Color(0xFF4CAF50)

data class FunctionItem(val title: String, val iconResId: Int, val route: String)
data class ChuyenGiaItem(val name: String, val avatarResId: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareScreen(navController: NavController, userId: String) {

    val chuyenGiaList = listOf(
        ChuyenGiaItem("BS. Vũ Tề Đăng", R.drawable.doctor_avatar1),
        ChuyenGiaItem("ThS. Lê Thị Thanh Hương", R.drawable.doctor_avatar2),
        ChuyenGiaItem("Chuyên gia Tạ Anh Tuấn", R.drawable.doctor_avatar3),
    )

    val babyCareFunctions = listOf(
        FunctionItem("Thông Tin\nCon Yêu", R.drawable.ic_calendar1, "child_info"),
        FunctionItem("Con Yêu Theo\nTháng Tuổi", R.drawable.information, "growth"),
        FunctionItem("Cân Nặng &\nChiều Cao", R.drawable.ic_weight1, "weight_height"),
        FunctionItem("Tuần khủng\nhoảng", R.drawable.week1, "crisis"),
    )

    val momCareFunctions = listOf(
        FunctionItem("Lập kế\nhoạch sinh", R.drawable.ic_care, "plan"),
        FunctionItem("Bổ sung\nVitamin", R.drawable.vitamin_icon, "vitamin"),
        FunctionItem("Sản phẩm\ntham khảo", R.drawable.product_icon, "moments"),
    )

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController, userId = userId) },
        containerColor = LightPinkBackground
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(LightPinkBackground),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                TopAppBar(
                    title = {
                        Text(
                            "MẸ & BÉ",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    actions = {
                        IconButton(onClick = { }) {
                            Icon(Icons.Default.AccountCircle, contentDescription = "User Profile", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryPink)
                )
            }

            item {
                TabRow(
                    selectedTabIndex = 0,
                    containerColor = Color.White,
                    contentColor = PrimaryPink,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    listOf("Cẩm nang", "Video").forEachIndexed { index, title ->
                        Tab(
                            selected = index == 0,
                            onClick = { },
                            text = {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (index == 0) PrimaryPink else TextSecondary
                                )
                            }
                        )
                    }
                }
            }

            item {
                CardSection(title = "Bác sĩ Chuyên gia") {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(chuyenGiaList) { chuyenGia ->
                            ExpertItem(chuyenGia = chuyenGia)
                        }
                    }
                }
            }

            item {
                CardSection(title = "Chăm sóc bé") {
                    EqualWidthGrid(columns = 3, spacing = 12.dp) {
                        babyCareFunctions.forEach { item ->
                            FunctionCardItem(item = item, navController = navController, userId = userId)
                        }
                    }
                }
            }

            item {
                CardSection(title = "Chăm sóc mẹ") {
                    EqualWidthGrid(columns = 3, spacing = 12.dp) {
                        momCareFunctions.forEach { item ->
                            FunctionCardItem(item = item, navController = navController, userId = userId)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardSection(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(color = PrimaryPink, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            content()
        }
    }
}

@Composable
fun ExpertItem(chuyenGia: ChuyenGiaItem) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(80.dp)) {
        Image(
            painter = painterResource(id = chuyenGia.avatarResId),
            contentDescription = chuyenGia.name,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = chuyenGia.name,
            style = MaterialTheme.typography.bodySmall.copy(
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            ),
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun FunctionCardItem(item: FunctionItem, navController: NavController, userId: String) {
    Column(
        modifier = Modifier
            .clickable { navController.navigate("${item.route}/$userId") }
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(LightPinkBackground)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = item.iconResId),
            contentDescription = item.title,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = item.title,
            style = TextStyle(
                fontSize = 13.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Medium
            ),
            textAlign = TextAlign.Center,
            maxLines = 2,
            lineHeight = 16.sp
        )
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
        val rowHeight = placeables.maxOfOrNull { it.height } ?: 0
        val totalHeight = rows * rowHeight + (rows - 1) * spacing.roundToPx()

        layout(constraints.maxWidth, totalHeight) {
            var y = 0
            placeables.chunked(columns).forEach { row ->
                var x = 0
                row.forEach { placeable ->
                    placeable.placeRelative(x, y)
                    x += columnWidth + spacing.roundToPx()
                }
                y += rowHeight + spacing.roundToPx()
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun CareScreenPreview() {
    CareScreen(userId = "1", navController = rememberNavController())
}