package com.example.myapplication.data.models

sealed class ChatMessage {
    data class Bot(val paragraphs: List<String>) : ChatMessage()
    data class User(val text: String, val initials: String) : ChatMessage()
}
