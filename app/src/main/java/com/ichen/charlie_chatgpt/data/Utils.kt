package com.ichen.charlie_chatgpt.data

import java.text.SimpleDateFormat
import java.util.*

fun now(): String {
    val time = Calendar.getInstance().time
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
    return formatter.format(time)
}