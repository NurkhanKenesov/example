package com.example.myapplication

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val ColorPrimary = Color(0xFF6C63FF)
private val ColorDark = Color(0xFF0F0F23)
private val ColorGreen = Color(0xFF4ADE80)
private val ColorTextMuted = Color(0x80_0F0F23)
private val ColorCardBg = Color(0xFFFFFFFF)

@Composable
fun TeacherQRDisplayScreen(
    onBackClick: () -> Unit = {}
) {
    val attendanceViewModel: AttendanceViewModel = koinViewModel()
    val uiState by attendanceViewModel.uiState.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFFF0F2FF), Color(0xFFF5F7FF))))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            androidx.compose.material3.TextButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.Start)
            ) {
                androidx.compose.material3.Text(
                    text = "‹ Назад",
                    color = ColorPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            when (uiState) {
                is AttendanceUiState.Loading -> {
                    CircularProgressIndicator(color = ColorPrimary)
                }
                is AttendanceUiState.Loaded -> {
                    val loaded = uiState as AttendanceUiState.Loaded
                    val session = attendanceViewModel.getCurrentActiveSession()
                    
                    if (session != null) {
                        Text(
                            text = session.subject,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorDark
                        )
                        Text(
                            text = "${formatSessionTime(session.startTime)} • ${session.location}",
                            fontSize = 16.sp,
                            color = ColorTextMuted
                        )
                        
                        QRCodeDisplay(
                            qrCode = session.qrCode,
                            modifier = Modifier.size(240.dp)
                        )
                        
                        val checkedIn = attendanceViewModel.getCheckedInCountForSession(session.id)
                        val total = loaded.totalStudents
                        
                        Text(
                            text = "Отмечено: $checkedIn из $total",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorDark
                        )
                    } else {
                        Text(
                            text = "Нет активного занятия",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColorTextMuted
                        )
                        Text(
                            text = "Создайте занятие в расписании",
                            fontSize = 16.sp,
                            color = ColorTextMuted
                        )
                    }
                }
                is AttendanceUiState.Error -> {
                    Text(
                        text = "Ошибка загрузки",
                        color = Color.Red,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun QRCodeDisplay(
    qrCode: String,
    modifier: Modifier = Modifier
) {
    val bitMatrix = remember(qrCode) {
        generateQRCode(qrCode, 240)
    }
    
    Canvas(
        modifier = modifier
            .border(2.dp, ColorPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .background(ColorCardBg, RoundedCornerShape(16.dp))
    ) {
        val size = this.size
        val blockSize = size.width / bitMatrix.width
        
        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height) {
                if (bitMatrix[x, y]) {
                    drawRect(
                        color = ColorDark,
                        topLeft = androidx.compose.ui.geometry.Offset(x * blockSize, y * blockSize),
                        size = androidx.compose.ui.geometry.Size(blockSize, blockSize)
                    )
                }
            }
        }
    }
}

private fun generateQRCode(text: String, size: Int): BitMatrix {
    val hints = mapOf(com.google.zxing.EncodeHintType.MARGIN to 0)
    return com.google.zxing.qrcode.QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, size, size, hints)
}

private fun formatSessionTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}