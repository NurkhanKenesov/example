package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import org.koin.androidx.compose.koinViewModel
import com.example.myapplication.data.Language
import com.example.myapplication.data.LocalPreferencesManager
import com.example.myapplication.data.PreferencesManager
import com.example.myapplication.data.ThemeMode
import com.example.myapplication.navigation.WelcomeRoute
import kotlinx.coroutines.launch

private val ColorPrimary = Color(0xFF6C63FF)
private val ColorPrimaryLight = Color(0x266C63FF)
private val ColorViolet = Color(0xFF8B5CF6)
private val ColorTextDark = Color(0xFF0F0F23)
private val ColorTextMedium = Color(0x660F0F23)
private val ColorTextLight = Color(0x4D0F0F23)
private val ColorGreen = Color(0xFF4ADE80)
private val ColorGreenLight = Color(0x264ADE80)
private val ColorRed = Color(0xFFF87171)
private val ColorSurface = Color(0xFFFFFFFF)
private val ColorBackground = Color(0xFFF0F2FF)
private val ColorBackgroundEnd = Color(0xFFF5F7FF)
private val ColorDivider = Color(0x110F0F23)

@Composable
fun SettingsScreen(
    authViewModel: AuthViewModel,
    navController: NavHostController,
    onBackClick: () -> Unit = {}
) {
    val preferencesManager = LocalPreferencesManager.current
    val profileViewModel: UserProfileViewModel = koinViewModel()
    val profileState by profileViewModel.state.collectAsStateWithLifecycle()
    
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val notificationsEnabled by preferencesManager.notificationsEnabled.collectAsStateWithLifecycle(initialValue = true)
    val workoutReminders by preferencesManager.workoutReminders.collectAsStateWithLifecycle(initialValue = true)
    val weeklyReport by preferencesManager.weeklyReport.collectAsStateWithLifecycle(initialValue = false)
    val currentLanguage by preferencesManager.language.collectAsStateWithLifecycle(initialValue = Language.RU)
    val healthConnected by preferencesManager.healthConnectEnabled.collectAsStateWithLifecycle(initialValue = false)

    val userName by preferencesManager.userName.collectAsStateWithLifecycle(initialValue = "")
    val userEmailPref by preferencesManager.userEmail.collectAsStateWithLifecycle(initialValue = "")
    
    val displayName = (profileState as? ProfileUiState.Loaded)?.profile?.name ?: userName
    val displayEmail = (profileState as? ProfileUiState.Loaded)?.profile?.email ?: userEmailPref

    var editedName by remember(displayName) { mutableStateOf(displayName) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var logoutRequested by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(ColorBackground, ColorBackgroundEnd)
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            SettingsTopBar(onBackClick = onBackClick)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(bottom = 32.dp)
            ) {
                SettingsSectionTitle("ПРОФИЛЬ")
                ProfileSettingsCard(
                    name = editedName,
                    email = displayEmail,
                    onNameChange = { editedName = it },
                    onSave = {
                        scope.launch {
                            preferencesManager.setUserName(editedName)
                            profileViewModel.updateField("name", editedName)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsSectionTitle("УВЕДОМЛЕНИЯ")
                NotificationsSettingsCard(
                    notificationsEnabled = notificationsEnabled,
                    workoutReminders = workoutReminders,
                    weeklyReport = weeklyReport,
                    onNotificationsToggle = {
                        scope.launch { preferencesManager.setNotificationsEnabled(it) }
                    },
                    onWorkoutRemindersToggle = {
                        scope.launch { preferencesManager.setWorkoutReminders(it) }
                    },
                    onWeeklyReportToggle = {
                        scope.launch { preferencesManager.setWeeklyReport(it) }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsSectionTitle("ЗДОРОВЬЕ")
                HealthSettingsCard(
                    isConnected = healthConnected,
                    onConnectClick = {
                        scope.launch {
                            preferencesManager.setHealthConnectEnabled(!healthConnected)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsSectionTitle("ВНЕШНИЙ ВИД")
                ThemeSettingsCard(
                    currentTheme = ThemeMode.SYSTEM,
                    onThemeSelected = {
                        scope.launch { preferencesManager.setThemeMode(it) }
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                SettingsSectionTitle("ЯЗЫК")
                LanguageSettingsCard(
                    currentLanguage = currentLanguage,
                    onLanguageClick = { showLanguageDialog = true }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        authViewModel.resetLogoutState()
                        authViewModel.logout()
                        logoutRequested = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Выйти из аккаунта", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }

    if (showLanguageDialog) {
        LanguageConfirmDialog(
            currentLanguage = currentLanguage,
            onDismiss = { showLanguageDialog = false },
            onLanguageSelected = { lang ->
                scope.launch {
                    preferencesManager.setLanguage(lang)
                }
                showLanguageDialog = false
            }
        )
    }

    LaunchedEffect(logoutRequested) {
        if (logoutRequested) {
            authViewModel.logoutComplete.collect { completed ->
                if (completed) {
                    navController.navigate(WelcomeRoute) { popUpTo(0) { inclusive = true } }
                }
            }
        }
    }
}

@Composable
private fun SettingsTopBar(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Назад",
                tint = ColorPrimary
            )
        }
        Text(
            text = "Настройки",
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorTextDark,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = ColorTextLight,
        letterSpacing = 0.5.sp
    )
}

@Composable
private fun ProfileSettingsCard(
    name: String,
    email: String,
    onNameChange: (String) -> Unit,
    onSave: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Имя пользователя") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = email,
                onValueChange = {},
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                enabled = false
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onSave,
                modifier = Modifier.align(Alignment.End),
                colors = ButtonDefaults.buttonColors(containerColor = ColorPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Сохранить", fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun NotificationsSettingsCard(
    notificationsEnabled: Boolean,
    workoutReminders: Boolean,
    weeklyReport: Boolean,
    onNotificationsToggle: (Boolean) -> Unit,
    onWorkoutRemindersToggle: (Boolean) -> Unit,
    onWeeklyReportToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            SettingsToggleRow(
                label = "Уведомления",
                checked = notificationsEnabled,
                onCheckedChange = onNotificationsToggle
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = ColorDivider
            )
            SettingsToggleRow(
                label = "Напоминания о тренировках",
                checked = workoutReminders,
                onCheckedChange = onWorkoutRemindersToggle,
                enabled = notificationsEnabled
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = ColorDivider
            )
            SettingsToggleRow(
                label = "Еженедельный отчёт",
                checked = weeklyReport,
                onCheckedChange = onWeeklyReportToggle,
                enabled = notificationsEnabled
            )
        }
    }
}

@Composable
private fun HealthSettingsCard(
    isConnected: Boolean,
    onConnectClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onConnectClick() }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Health Connect",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = ColorTextDark
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (isConnected) "Подключено" else "Не подключено",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal,
                    color = if (isConnected) ColorGreen else ColorTextMedium
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isConnected) ColorGreenLight else ColorPrimaryLight
            ) {
                Text(
                    text = if (isConnected) "Отключить" else "Подключить",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isConnected) ColorGreen else ColorPrimary
                )
            }
        }
    }
}

@Composable
private fun ThemeSettingsCard(
    currentTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            ThemeOption(
                label = "Светлая",
                isSelected = currentTheme == ThemeMode.LIGHT,
                onClick = { onThemeSelected(ThemeMode.LIGHT) }
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = ColorDivider
            )
            ThemeOption(
                label = "Тёмная",
                isSelected = currentTheme == ThemeMode.DARK,
                onClick = { onThemeSelected(ThemeMode.DARK) }
            )
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = ColorDivider
            )
            ThemeOption(
                label = "Системная",
                isSelected = currentTheme == ThemeMode.SYSTEM,
                onClick = { onThemeSelected(ThemeMode.SYSTEM) }
            )
        }
    }
}

@Composable
private fun ThemeOption(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = ColorTextDark
        )
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = ColorPrimary)
        )
    }
}

@Composable
private fun LanguageSettingsCard(
    currentLanguage: Language,
    onLanguageClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onLanguageClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ColorSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Язык приложения",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = ColorTextDark
            )
            Text(
                text = currentLanguage.displayName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                color = ColorPrimary
            )
        }
    }
}

@Composable
private fun LanguageConfirmDialog(
    currentLanguage: Language,
    onDismiss: () -> Unit,
    onLanguageSelected: (Language) -> Unit
) {
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Выберите язык",
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column {
                Language.entries.forEach { lang ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedLanguage = lang }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = lang.displayName,
                            fontSize = 16.sp,
                            color = ColorTextDark
                        )
                        RadioButton(
                            selected = selectedLanguage == lang,
                            onClick = { selectedLanguage = lang },
                            colors = RadioButtonDefaults.colors(selectedColor = ColorPrimary)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (selectedLanguage != currentLanguage) {
                        onLanguageSelected(selectedLanguage)
                    } else {
                        onDismiss()
                    }
                }
            ) {
                Text("Перезапустить", color = ColorPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", color = ColorTextMedium)
            }
        }
    )
}

@Composable
private fun SettingsToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = if (enabled) ColorTextDark else ColorTextLight
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            enabled = enabled,
            colors = SwitchDefaults.colors(checkedTrackColor = ColorPrimary)
        )
    }
}