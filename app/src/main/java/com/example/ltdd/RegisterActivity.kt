package com.example.ltdd


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.ltdd.ui.screens.RegisterScreen
import com.example.ltdd.ui.theme.LTDDTheme

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LTDDTheme {
                RegisterScreen(
                    onRegisterSuccess = {
                        // Quay lại màn hình đăng nhập sau khi đăng ký thành công
                        finish()
                    }
                )
            }
        }
    }
}
