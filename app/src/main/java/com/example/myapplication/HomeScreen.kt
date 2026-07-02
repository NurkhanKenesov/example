package com.example.myapplication

import android.text.format.DateUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.MockData
import org.koin.androidx.compose.koinViewModel

private val ColorPrimary = Color(0xFF6C63FF)
private val ColorSecondary = Color(0xFF8B5CF6)
private val ColorDark = Color(0xFF0F0F23)
private val ColorTextMuted = Color(0xFF0F0F23).copy(alpha = 0.50f)
private val ColorTextFaint = Color(0xFF0F0F23).copy(alpha = 0.40f)
private val ColorSurface = Color(0xFFF8F9FF)
private val ColorCardBorder = Color(0xFF000000).copy(alpha = 0.06f)
private val ColorPrimaryBorder = Color(0xFF6C63FF).copy(alpha = 0.20f)
private val ColorRed = Color(0xFFF87171)
private val ColorRedBg = Color(0xFFF87171).copy(alpha = 0.07f)
private val ColorRedBorder = Color(0xFFF87171).copy(alpha = 0.20f)
private val ColorGreen = Color(0xFF4ADE80)
private val ColorGreenBg = Color(0xFF4ADE80).copy(alpha = 0.10f)

private val GradientPrimary = Brush.linearGradient(
    colors = listOf(ColorPrimary, ColorSecondary)
)
private val GradientBackground = Brush.verticalGradient(
    colors = listOf(Color(0xFFF0F2FF), Color(0xFFF5F7FF))
)
private val GradientProgressBar = Brush.horizontalGradient(
    colors = listOf(ColorPrimary, ColorSecondary)
)
private val GradientAvatar = Brush.linearGradient(
    colors = listOf(ColorPrimary, ColorSecondary),
    start = Offset(0f, 0f),
    end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
)

@Composable
fun HomeScreen(
    userRole: UserRole = UserRole.Student,
    onNavigateToPlans: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToChatbot: () -> Unit = {},
    onNavigateToStudents: () -> Unit = {},
    onNavigateToLMSAttendance: () -> Unit = {},
    onNavigateToQRScanner: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {},
    onNavigateToModel: () -> Unit = {},
    onNavigateToTeacherQRDisplay: () -> Unit = {},
    viewModel: UserProfileViewModel = koinViewModel(),
    studentsViewModel: StudentsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val studentsState by studentsViewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val healthViewModel: HealthViewModel = viewModel(factory = HealthViewModelFactory(context))
    
    val heartRateState by healthViewModel.healthConnectStatus.collectAsStateWithLifecycle()
    val heartRateValue by healthViewModel.heartRate.collectAsStateWithLifecycle()
    val todaySteps by healthViewModel.todaySteps.collectAsStateWithLifecycle()
    val todayCalories by healthViewModel.todayCalories.collectAsStateWithLifecycle()
    val todayActiveMinutes by healthViewModel.todayActiveMinutes.collectAsStateWithLifecycle()
    val isLoading by healthViewModel.isLoading.collectAsStateWithLifecycle()
    val hasPermission by healthViewModel.hasPermission.collectAsStateWithLifecycle()

    val status = HealthConnectClient.getSdkStatus(context)
    Log.d("HealthConnect", "SDK status: $status")

    val requestPermissions = rememberLauncherForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { grantedPermissions ->
        Log.d("HealthConnect", "Permission result received: $grantedPermissions")
        if (grantedPermissions.containsAll(PERMISSIONS)) {
            healthViewModel.loadHealthData()
        }
    }

    LaunchedEffect(Unit) {
        healthViewModel.refreshAllMetrics()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GradientBackground)
    ) {
        when (val current = state) {
            is ProfileUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ColorPrimary)
                }
            }
            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = current.message,
                        color = ColorRed,
                        fontSize = 16.sp
                    )
                }
            }
            is ProfileUiState.Loaded -> {
                val profile = current.profile
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                        .padding(top = 12.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    WelcomeHeader(
                        profile = profile,
                        onNavigateToProfile = onNavigateToProfile
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    CreditStatusCard()
                    if (userRole == UserRole.Student) {
                        Spacer(modifier = Modifier.height(16.dp))
                        HeartRateCard(
                            state = heartRateState,
                            hasPermission = hasPermission,
                            bpm = heartRateValue,
                            onConnectClick = {
                                context.startActivity(healthViewModel.getInstallIntent())
                            },
                            onRequestPermission = {
                                Log.d("HealthConnect", "Button tapped - launching permission request")
                                Log.d("HealthConnect", "PERMISSIONS set size: ${PERMISSIONS.size}")
                                Log.d("HealthConnect", "PERMISSIONS contents: $PERMISSIONS")
                                Log.d("HealthConnect", "Launcher created: $requestPermissions")
                                Log.d("HealthConnect", "SDK status at launch: ${HealthConnectClient.getSdkStatus(context)}")
                                try {
                                    requestPermissions.launch(PERMISSIONS)
                                    Log.d("HealthConnect", "launch() called successfully")
                                } catch (e: Exception) {
                                    Log.e("HealthConnect", "launch() threw exception", e)
                                }
                            },
                            onRefresh = {
                                Toast.makeText(context, "Обновляем данные...", Toast.LENGTH_SHORT).show()
                                healthViewModel.refreshAllMetrics()
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    SectionLabel("Активность дня")
                    Spacer(modifier = Modifier.height(8.dp))
                    DailyActivityRow(
                        steps = todaySteps,
                        calories = todayCalories,
                        activeMinutes = todayActiveMinutes,
                        isLoading = isLoading
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    NextSessionCard()
                    if (userRole == UserRole.Student) {
                        Spacer(modifier = Modifier.height(10.dp))
                        InjuryWarningCard()
                    }
                    if (userRole == UserRole.Teacher) {
                        Spacer(modifier = Modifier.height(20.dp))
                        SectionLabel("Статистика группы")
                        Spacer(modifier = Modifier.height(12.dp))
                        GroupStatsRow(studentsState = studentsState)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    SectionLabel("Быстрые действия")
                    Spacer(modifier = Modifier.height(12.dp))
                    QuickActionButtons(
                        userRole = userRole,
                        onNavigateToPlans = onNavigateToPlans,
                        onNavigateToChatbot = onNavigateToChatbot,
                        onNavigateToStudents = onNavigateToStudents,
                        onNavigateToLMSAttendance = onNavigateToLMSAttendance,
                        onNavigateToQRScanner = onNavigateToQRScanner,
                        onNavigateToAchievements = onNavigateToAchievements,
                        onNavigateToModel = onNavigateToModel,
                        onNavigateToTeacherQRDisplay = onNavigateToTeacherQRDisplay
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeHeader(
    profile: UserProfile,
    onNavigateToProfile: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = "Добро пожаловать 👋",
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = ColorTextMuted
            )
            Text(
                text = profile.name,
                fontSize = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ColorDark
            )
        }
        AvatarBadge(
            initials = profile.initials,
            modifier = Modifier.clickable { onNavigateToProfile() }
        )
    }
}

@Composable
private fun AvatarBadge(initials: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(GradientAvatar),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            fontSize = 20.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CreditStatusCard() {
    val progress = 0.68f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF6C63FF).copy(alpha = 0.08f),
                        Color.White
                    )
                )
            )
            .border(1.dp, ColorPrimaryBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularScoreIndicator(score = 68, total = 100, progress = progress)
            CreditStatusInfo(remaining = 32, progress = progress)
        }
    }
}

@Composable
private fun CircularScoreIndicator(score: Int, total: Int, progress: Float) {
    Box(
        modifier = Modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 10.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2f
            val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)
            val arcSize = Size(radius * 2f, radius * 2f)

            drawArc(
                color = Color.Black.copy(alpha = 0.10f),
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
            drawArc(
                color = ColorPrimary,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = score.toString(),
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = ColorDark,
                lineHeight = 28.sp
            )
            Text(
                text = "из $total баллов",
                fontSize = 11.sp,
                fontWeight = FontWeight.Normal,
                color = ColorTextFaint,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CreditStatusInfo(remaining: Int, progress: Float) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Статус зачета",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ColorDark
        )
        Text(
            text = "Осталось $remaining балла до автомата",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = ColorTextMuted
        )
        LinearProgressBar(progress = progress)
    }
}

@Composable
private fun LinearProgressBar(progress: Float) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(Color.Black.copy(alpha = 0.08f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(fraction = progress)
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .background(GradientProgressBar)
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(
        text = text.uppercase(),
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = ColorTextFaint,
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun DailyActivityRow(
    steps: Int?,
    calories: Int?,
    activeMinutes: Int?,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MetricCard(
            emoji = "👣", 
            value = if (steps != null && steps > 0) "%,d".format(steps) else if (isLoading) "" else "--", 
            label = "Шагов", 
            modifier = Modifier.weight(1f),
            isLoading = isLoading
        )
        MetricCard(emoji = "🔥", value = if (calories != null) "%,d".format(calories) else "--", label = if (calories == null) "Нет данных с часов" else "Ккал", modifier = Modifier.weight(1f))
        MetricCard(emoji = "⏱️", value = if (activeMinutes != null) activeMinutes.toString() else "--", label = if (activeMinutes == null) "Нет данных с часов" else "Мин", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun MetricCard(emoji: String, value: String, label: String, modifier: Modifier = Modifier, isLoading: Boolean = false) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(ColorSurface)
            .border(1.dp, ColorCardBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(text = emoji, fontSize = 16.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(2.dp))
            if (isLoading) {
                CircularProgressIndicator(
                    color = ColorPrimary,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(
                    text = value,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = ColorDark,
                    textAlign = TextAlign.Center
                )
            }
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                color = ColorTextFaint,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun NextSessionCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ColorGreenBg)
            .border(1.dp, ColorGreen.copy(alpha = 0.30f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(ColorGreen.copy(alpha = 0.20f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📅", fontSize = 18.sp)
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Ближайшее занятие",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorGreen
                )
                Text(
                    text = "Сегодня, 14:30 • Волейбол",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = ColorDark
                )
                Text(
                    text = "Спортивный зал №3",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = ColorTextMuted
                )
            }
        }
    }
}

@Composable
private fun InjuryWarningCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ColorRedBg)
            .border(1.dp, ColorRedBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ColorRed.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "⚠️", fontSize = 16.sp)
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = "Активная травма",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorRed
                )
                Text(
                    text = "knee_sprain (knee) — до 2026-08-01",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = ColorTextMuted
                )
            }
        }
    }
}

@Composable
private fun QuickActionButtons(
    userRole: UserRole,
    onNavigateToPlans: () -> Unit,
    onNavigateToChatbot: () -> Unit,
    onNavigateToStudents: () -> Unit,
    onNavigateToLMSAttendance: () -> Unit,
    onNavigateToQRScanner: () -> Unit,
    onNavigateToAchievements: () -> Unit,
    onNavigateToModel: () -> Unit = {},
    onNavigateToTeacherQRDisplay: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (userRole == UserRole.Teacher) {
            TeacherQuickActions(
                onNavigateToTeacherQRDisplay = onNavigateToTeacherQRDisplay,
                onNavigateToChatbot = onNavigateToChatbot
            )
        } else {
            StudentQuickActions(
                onNavigateToPlans = onNavigateToPlans,
                onNavigateToChatbot = onNavigateToChatbot,
                onNavigateToQRScanner = onNavigateToQRScanner,
                onNavigateToAchievements = onNavigateToAchievements
            )
        }
    }
}

@Composable
private fun StudentQuickActions(
    onNavigateToPlans: () -> Unit,
    onNavigateToChatbot: () -> Unit,
    onNavigateToQRScanner: () -> Unit,
    onNavigateToAchievements: () -> Unit
) {
    QuickActionCard(
        emoji = "🚀",
        title = "Начать тренировку",
        subtitle = "Перейти к текущему плану",
        onClick = onNavigateToPlans
    )
    QuickActionCard(
        emoji = "📷",
        title = "QR-сканер",
        subtitle = "Отметиться на занятии",
        onClick = onNavigateToQRScanner
    )
    QuickActionCard(
        emoji = "🏆",
        title = "Достижения",
        subtitle = "Мои награды и прогресс",
        onClick = onNavigateToAchievements
    )
    QuickActionCard(
        emoji = "🤖",
        title = "ИИ-Ассистент",
        subtitle = "Задать вопрос рекомендациям",
        onClick = onNavigateToChatbot
    )
}

@Composable
private fun QuickActionCard(
    emoji: String,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ColorSurface)
            .border(1.dp, ColorCardBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(ColorPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = emoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorDark
                )
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = ColorTextMuted
                )
            }
            Text(text = "›", fontSize = 20.sp, color = ColorTextMuted)
        }
    }
}

@Composable
private fun TeacherQuickActions(
    onNavigateToTeacherQRDisplay: () -> Unit,
    onNavigateToChatbot: () -> Unit
) {
    QuickActionCard(
        emoji = "🚀",
        title = "Начать занятие",
        subtitle = "Показать QR для отметки посещаемости",
        onClick = onNavigateToTeacherQRDisplay
    )
    QuickActionCard(
        emoji = "🤖",
        title = "ИИ-Ассистент",
        subtitle = "Сгенерировать план тренировок",
        onClick = onNavigateToChatbot
    )
}

@Composable
private fun GroupStatsRow(studentsState: StudentsUiState) {
    val students = (studentsState as? StudentsUiState.Loaded)?.students ?: emptyList()
    val sessionsToday = MockData.attendanceSessions.count { DateUtils.isToday(it.startTime) }
    val alertCount = students.count { it.hasAlert }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MetricCard(emoji = "👥", value = students.size.toString(), label = "Студентов", modifier = Modifier.weight(1f))
        MetricCard(emoji = "📅", value = sessionsToday.toString(), label = "Занятий сегодня", modifier = Modifier.weight(1f))
        MetricCard(emoji = "⚠️", value = alertCount.toString(), label = "Требуют внимания", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun HeartRateCard(
    state: HealthConnectStatus,
    hasPermission: Boolean,
    bpm: Int?,
    onConnectClick: () -> Unit,
    onRequestPermission: () -> Unit,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Пульс",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorDark
                )
                when (state) {
                    HealthConnectStatus.Supported -> {
                        Text(
                            text = if (bpm != null && bpm > 0) "$bpm bpm" else "--",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFE11D48)
                        )
                    }
                    HealthConnectStatus.NotInstalled -> {
                        Text(
                            text = "Health Connect не установлен",
                            fontSize = 13.sp,
                            color = ColorTextMuted
                        )
                    }
                    HealthConnectStatus.UpdateRequired -> {
                        Text(
                            text = "Требуется обновление",
                            fontSize = 13.sp,
                            color = ColorTextMuted
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (state == HealthConnectStatus.Supported && hasPermission) {
                TextButton(
                    onClick = onRefresh,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Обновить", color = ColorPrimary, fontSize = 13.sp)
                }
            }
            if (state == HealthConnectStatus.Supported && !hasPermission) {
                Button(
                    onClick = onRequestPermission,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorPrimary.copy(alpha = 0.1f),
                        contentColor = ColorPrimary
                    )
                ) {
                    Text("Разрешить доступ к здоровью")
                }
            } else if (state != HealthConnectStatus.Supported) {
                Button(
                    onClick = onConnectClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ColorPrimary.copy(alpha = 0.1f),
                        contentColor = ColorPrimary
                    )
                ) {
                    Text("Подключить")
                }
            }
        }
    }
}
