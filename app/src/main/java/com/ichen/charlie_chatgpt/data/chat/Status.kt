package com.ichen.charlie_chatgpt.data.chat

sealed class Status<out T> {
    object NotStarted: Status<Nothing>()
    object Loading: Status<Nothing>()
    data class Error(val e: Throwable): Status<Nothing>()
    data class Loaded<out R>(val data: R): Status<R>()
}