package com.example.ltdd.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.HelpOutline // Đã đổi icon dấu hỏi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.ltdd.R
import com.example.ltdd.models.BirthPlanQuestion
import com.example.ltdd.models.BirthPlanOption
import com.example.ltdd.viewmodel.BirthPlanViewModel
import com.example.ltdd.viewmodel.BirthPlanUiState


@Composable
fun BirthPlanQuestionItem(question: BirthPlanQuestion, onClick: () -> Unit, hasSelections: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = question.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
            if (hasSelections) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Đã chọn",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Circle,
                    contentDescription = "Chưa chọn",
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun BirthPlanOptionsDialog(
    question: BirthPlanQuestion,
    onOptionSelected: (String, Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = question.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                val currentOptions = remember(question) { mutableStateListOf(*question.options.toTypedArray()) }


                question.options.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOptionSelected(option.id, !option.isSelected) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = option.isSelected,
                            onCheckedChange = { isChecked ->
                                onOptionSelected(option.id, isChecked)
                            }
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(option.text, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Đóng")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        onDismiss()
                    }) {
                        Text("Lưu")
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LapTrinhSinhScreen(
    navController: NavHostController,
    userId: String,
    viewModel: BirthPlanViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val questions by viewModel.questionsWithSelections.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var selectedQuestion by remember { mutableStateOf<BirthPlanQuestion?>(null) }

    LaunchedEffect(userId) {
        viewModel.setUserId(userId)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (uiState) {
            is BirthPlanUiState.SaveSuccess -> {
                val message = (uiState as BirthPlanUiState.SaveSuccess).message
                snackbarHostState.showSnackbar(
                    message = message,
                    duration = SnackbarDuration.Short
                )
            }
            is BirthPlanUiState.SaveError -> {
                val message = (uiState as BirthPlanUiState.SaveError).message
                snackbarHostState.showSnackbar(
                    message = "Lỗi: $message",
                    duration = SnackbarDuration.Long
                )
            }
            else -> {  }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KẾ HOẠCH SINH", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Xử lý trợ giúp */ }) {
                        Icon(Icons.Default.HelpOutline, contentDescription = "Trợ giúp")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF66BB6A),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { insets ->
        Column(
            modifier = Modifier
                .padding(insets)
                .fillMaxSize()
                .background(Color(0xFFE8F5E9))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE0F2F1), RoundedCornerShape(12.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.information),
                        contentDescription = "Checklist Icon",
                        modifier = Modifier.size(80.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { /* TODO: Xử lý khi click vào "Kế hoạch sinh của tôi" */ }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Kế hoạch sinh của tôi",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF388E3C)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Tiếp theo",
                        tint = Color(0xFF388E3C)
                    )
                }
                Divider()
            }

            when (uiState) {
                is BirthPlanUiState.Loading -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Đang tải kế hoạch sinh...", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                is BirthPlanUiState.Saving -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Đang lưu lựa chọn...", color = MaterialTheme.colorScheme.primary)
                    }
                }
                is BirthPlanUiState.Success, is BirthPlanUiState.SaveSuccess, is BirthPlanUiState.SaveError -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(questions) { question ->
                            val hasSelections = question.options.any { it.isSelected }
                            BirthPlanQuestionItem(
                                question = question,
                                onClick = {
                                    selectedQuestion = question
                                    showDialog = true
                                },
                                hasSelections = hasSelections
                            )
                        }
                    }
                }
                is BirthPlanUiState.Error -> {
                    val errorMessage = (uiState as BirthPlanUiState.Error).message
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Đã xảy ra lỗi: $errorMessage", color = MaterialTheme.colorScheme.error, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchBirthPlanQuestions() }) {
                            Text("Thử lại")
                        }
                    }
                }
                is BirthPlanUiState.Idle -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Chờ tải kế hoạch sinh...")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.fetchBirthPlanQuestions() }) {
                            Text("Tải dữ liệu")
                        }
                    }
                }
            }

            if (showDialog && selectedQuestion != null) {
                BirthPlanOptionsDialog(
                    question = selectedQuestion!!,
                    onOptionSelected = { optionId, isSelected ->
                        viewModel.updateOptionSelection(selectedQuestion!!.id, optionId, isSelected)
                    },
                    onDismiss = { showDialog = false }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BirthPlanScreenPreview() {
    LapTrinhSinhScreen(navController = rememberNavController(), userId = "test_user")
}