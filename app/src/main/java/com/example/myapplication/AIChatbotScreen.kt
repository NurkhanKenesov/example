package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val PurplePrimary = Color(0xFF6C63FF)
private val PurpleLight = Color(0xFF8B5CF6)
private val DarkText = Color(0xFF0F0F23)
private val AvatarGrey = Color(0xFFE0E0EB)
private val BubbleBackground = Color(0xFFF5F7FF)
private val InputBackground = Color(0xFFF5F7FF)
private val GreyHint = Color(0xFF757575)
private val Divider = Color(0x14000000)

private val ScreenGradient = Brush.verticalGradient(
    colors = listOf(Color(0xFFF0F2FF), Color(0xFFF5F7FF))
)
private val BotAvatarGradient = Brush.linearGradient(
    colors = listOf(PurplePrimary, PurpleLight)
)

sealed class ChatMessage {
    data class Bot(val paragraphs: List<String>) : ChatMessage()
    data class User(val text: String, val initials: String) : ChatMessage()
}

private val sampleMessages = listOf(
    ChatMessage.Bot(listOf("Привет! Я твой виртуальный тренер. Чем могу помочь?")),
    ChatMessage.User(
        text = "У меня болит колено после вчерашнего бега. Чем заменить приседания сегодня?",
        initials = "AS"
    ),
    ChatMessage.Bot(
        listOf(
            "Рекомендую исключить осевую нагрузку на колени. Давай заменим приседания на «Ягодичный мостик» и «Отведение ноги назад». Я уже обновил твой план тренировки.",
            "Если боль не пройдет через 2 дня, обязательно сообщи преподавателю!"
        )
    )
)

@Composable
fun AIChatbotScreen(onBack: () -> Unit = {}) {
    var inputText by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf(*sampleMessages.toTypedArray()) }
    val listState = rememberLazyListState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenGradient)
    ) {
        ChatNavBar(onBack = onBack)

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(messages) { message ->
                when (message) {
                    is ChatMessage.Bot -> BotMessageRow(message)
                    is ChatMessage.User -> UserMessageRow(message)
                }
            }
        }

        MessageInputBar(
            text = inputText,
            onTextChange = { inputText = it },
            onSend = {
                if (inputText.isNotBlank()) {
                    messages.add(ChatMessage.User(inputText.trim(), "AS"))
                    inputText = ""
                }
            }
        )
    }
}

@Composable
private fun ChatNavBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onBack,
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = "‹ Назад",
                color = PurplePrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "🤖 ИИ-Ассистент",
            color = DarkText,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.weight(1f))
        // Balance spacer to keep title centered
        Spacer(modifier = Modifier.width(72.dp))
    }
}

@Composable
private fun BotMessageRow(message: ChatMessage.Bot) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.Start
    ) {
        BotAvatar()
        Spacer(modifier = Modifier.width(10.dp))
        BotBubble(paragraphs = message.paragraphs)
    }
}

@Composable
private fun UserMessageRow(message: ChatMessage.User) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.End
    ) {
        UserBubble(text = message.text)
        Spacer(modifier = Modifier.width(10.dp))
        UserAvatar(initials = message.initials)
    }
}

@Composable
private fun BotAvatar() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(BotAvatarGradient),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "🤖", fontSize = 18.sp)
    }
}

@Composable
private fun UserAvatar(initials: String) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(AvatarGrey),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun BotBubble(paragraphs: List<String>) {
    Surface(
        shape = RoundedCornerShape(topStart = 4.dp, topEnd = 16.dp, bottomEnd = 16.dp, bottomStart = 16.dp),
        color = Color.White,
        shadowElevation = 2.dp,
        modifier = Modifier.widthIn(max = 240.dp)
    ) {
        Column(
            modifier = Modifier.padding(
                start = 16.dp,
                end = 18.dp,
                top = 11.dp,
                bottom = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(19.dp)
        ) {
            paragraphs.forEach { paragraph ->
                Text(
                    text = paragraph,
                    color = DarkText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 19.6.sp
                )
            }
        }
    }
}

@Composable
private fun UserBubble(text: String) {
    Box(
        modifier = Modifier
            .widthIn(max = 240.dp)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 4.dp, bottomEnd = 16.dp, bottomStart = 16.dp))
            .background(PurplePrimary)
            .padding(start = 16.dp, end = 18.dp, top = 11.dp, bottom = 12.dp)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            lineHeight = 19.6.sp
        )
    }
}

@Composable
private fun MessageInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        TextField(
            value = text,
            onValueChange = onTextChange,
            placeholder = {
                Text(
                    text = "Напишите сообщение...",
                    color = GreyHint,
                    fontSize = 13.sp
                )
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = InputBackground,
                unfocusedContainerColor = InputBackground,
                disabledContainerColor = InputBackground,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            ),
            singleLine = false,
            maxLines = 4
        )

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(PurplePrimary),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onSend, modifier = Modifier.size(40.dp)) {
                Icon(
                    imageVector = Icons.Filled.ArrowUpward,
                    contentDescription = "Отправить",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AIChatbotScreenPreview() {
    AIChatbotScreen()
}
