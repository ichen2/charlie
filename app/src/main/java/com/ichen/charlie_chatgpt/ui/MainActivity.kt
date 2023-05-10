package com.ichen.charlie_chatgpt.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.ichen.charlie_chatgpt.ui.chat.ChatScreen
import com.ichen.charlie_chatgpt.ui.theme.CharlieTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CharlieTheme {
                ChatScreen()
            }
        }
    }
}
