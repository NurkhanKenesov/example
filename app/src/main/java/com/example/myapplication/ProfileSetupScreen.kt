package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

private val Purple       = Color(0xFF6C63FF)
private val Violet       = Color(0xFF8B5CF6)
private val DarkBlue     = Color(0xFF0F0F23)
private val Subtle       = Color(0x660F0F23)
private val BgStart      = Color(0xFFF0F2FF)
private val BgEnd        = Color(0xFFF5F7FF)
private val FieldBg      = Color(0xFFFFFFFF)
private val FieldBorder  = Color(0x336C63FF)
private val ToggleBg     = Color(0xFFF0F0F8)

@Composable
fun ProfileSetupScreen(
    onSetupComplete: () -> Unit,
    viewModel: UserProfileViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()

    when (val current = state) {
        is ProfileUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Purple)
            }
        }
        is ProfileUiState.Error -> {
            val snackbarHostState = remember { SnackbarHostState() }
            LaunchedEffect(current) {
                snackbarHostState.showSnackbar(current.message)
            }
            Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = current.message,
                        color = Color(0xFFF87171),
                        fontSize = 16.sp
                    )
                }
            }
        }
        is ProfileUiState.Loaded -> {
            val profile = current.profile
            var name by remember { mutableStateOf(profile.name) }
            var age by remember { mutableStateOf(if (profile.age != 18) profile.age.toString() else "") }
            var heightCm by remember { mutableStateOf(if (profile.heightCm != 170) profile.heightCm.toString() else "") }
            var weightKg by remember { mutableStateOf(if (profile.weightKg != 65f) profile.weightKg.toString() else "") }
            var gender by remember { mutableStateOf(profile.gender) }
            var medicalGroup by remember { mutableStateOf(profile.medicalGroup) }
            var groupName by remember { mutableStateOf(profile.groupName) }

            val isFormValid = name.isNotBlank()
                && age.toIntOrNull()?.let { it in 14..80 } == true
                && heightCm.toIntOrNull()?.let { it in 100..250 } == true
                && weightKg.toFloatOrNull()?.let { it in 30f..200f } == true

            Scaffold { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.verticalGradient(listOf(BgStart, BgEnd)))
                        .padding(innerPadding)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp)
                            .padding(top = 32.dp, bottom = 32.dp)
                    ) {
                        Text(
                            text = "Расскажи о себе",
                            color = DarkBlue,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            text = "Данные нужны ИИ-тренеру для персонализации программы",
                            color = Subtle,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 28.dp)
                        )

                        SetupFieldLabel("ИМЯ")
                        Spacer(Modifier.height(6.dp))
                        SetupTextField(
                            value = name,
                            onValueChange = { name = it },
                            placeholder = "Амир Сейткали",
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        SetupFieldLabel("ПОЛ")
                        Spacer(Modifier.height(6.dp))
                        GenderToggle(
                            selected = gender,
                            onSelect = { gender = it },
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                SetupFieldLabel("ВОЗРАСТ")
                                Spacer(Modifier.height(6.dp))
                                SetupTextField(
                                    value = age,
                                    onValueChange = { if (it.length <= 2) age = it },
                                    placeholder = "20",
                                    keyboardType = KeyboardType.Number
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                SetupFieldLabel("РОСТ (см)")
                                Spacer(Modifier.height(6.dp))
                                SetupTextField(
                                    value = heightCm,
                                    onValueChange = { if (it.length <= 3) heightCm = it },
                                    placeholder = "175",
                                    keyboardType = KeyboardType.Number
                                )
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                SetupFieldLabel("ВЕС (кг)")
                                Spacer(Modifier.height(6.dp))
                                SetupTextField(
                                    value = weightKg,
                                    onValueChange = { if (it.length <= 5) weightKg = it },
                                    placeholder = "68",
                                    keyboardType = KeyboardType.Decimal
                                )
                            }
                        }

                        val bmiPreview = remember(heightCm, weightKg) {
                            val h = heightCm.toIntOrNull()
                            val w = weightKg.toFloatOrNull()
                            if (h != null && w != null && h > 0) {
                                val hM = h / 100f
                                w / (hM * hM)
                            } else null
                        }
                        if (bmiPreview != null) {
                            BmiPreviewCard(bmi = bmiPreview, modifier = Modifier.padding(bottom = 16.dp))
                        }

                        SetupFieldLabel("МЕДИЦИНСКАЯ ГРУППА")
                        Spacer(Modifier.height(6.dp))
                        MedicalGroupSelector(
                            selected = medicalGroup,
                            onSelect = { medicalGroup = it },
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        SetupFieldLabel("УЧЕБНАЯ ГРУППА (необязательно)")
                        Spacer(Modifier.height(6.dp))
                        SetupTextField(
                            value = groupName,
                            onValueChange = { groupName = it },
                            placeholder = "ИТ-22-1",
                            modifier = Modifier.padding(bottom = 28.dp)
                        )

                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    if (isFormValid)
                                        Brush.horizontalGradient(listOf(Purple, Violet))
                                    else
                                        Brush.horizontalGradient(listOf(Purple.copy(alpha = 0.4f), Violet.copy(alpha = 0.4f)))
                                )
                                .clickable(enabled = isFormValid && !isSaving) {
                                    val userProfile = UserProfile(
                                        name = name.trim(),
                                        gender = gender,
                                        age = age.toIntOrNull() ?: 18,
                                        heightCm = heightCm.toIntOrNull() ?: 170,
                                        weightKg = weightKg.toFloatOrNull() ?: 65f,
                                        medicalGroup = medicalGroup,
                                        groupName = groupName.trim()
                                    )
                                    viewModel.saveProfile(userProfile, onSuccess = onSetupComplete)
                                }
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = "Сохранить и продолжить →",
                                    color = Color.White,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SetupFieldLabel(text: String) {
    Text(
        text = text,
        color = Subtle,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.8.sp
    )
}

@Composable
private fun SetupTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Subtle, fontSize = 15.sp) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedContainerColor = FieldBg,
            focusedContainerColor   = FieldBg,
            unfocusedBorderColor    = FieldBorder,
            focusedBorderColor      = Purple,
            cursorColor             = Purple
        ),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun GenderToggle(
    selected: Gender,
    onSelect: (Gender) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(ToggleBg, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        Gender.entries.forEach { gender ->
            val isSelected = gender == selected
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected) Brush.linearGradient(listOf(Purple, Violet))
                        else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                    )
                    .clickable { onSelect(gender) }
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    text = if (gender == Gender.MALE) "👨 Мужской" else "👩 Женский",
                    color = if (isSelected) Color.White else Subtle,
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
private fun MedicalGroupSelector(
    selected: MedicalGroup,
    onSelect: (MedicalGroup) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        MedicalGroup.entries.forEach { group ->
            val isSelected = group == selected
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isSelected) Purple.copy(alpha = 0.08f) else FieldBg)
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Purple.copy(alpha = 0.5f) else FieldBorder,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onSelect(group) }
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = { onSelect(group) },
                    colors = RadioButtonDefaults.colors(selectedColor = Purple)
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = group.displayName,
                        color = DarkBlue,
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                    Text(
                        text = when (group) {
                            MedicalGroup.BASIC        -> "Без ограничений по нагрузке"
                            MedicalGroup.PREPARATORY  -> "Ограниченные нагрузки"
                            MedicalGroup.SPECIAL      -> "Строгие медицинские ограничения"
                        },
                        color = Subtle,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun BmiPreviewCard(bmi: Float, modifier: Modifier = Modifier) {
    val label = when {
        bmi < 18.5f -> "Недовес"
        bmi < 25f   -> "Норма ✓"
        bmi < 30f   -> "Избыточный вес"
        else        -> "Ожирение"
    }
    val color = when {
        bmi < 18.5f -> Color(0xFF60A5FA)
        bmi < 25f   -> Color(0xFF4ADE80)
        bmi < 30f   -> Color(0xFFFBBF24)
        else        -> Color(0xFFF87171)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.08f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text("⚖️", fontSize = 20.sp)
        Column {
            Text("Ваш ИМТ", color = Subtle, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = "%.1f — %s".format(bmi, label),
                color = DarkBlue,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F7FF)
@Composable
fun ProfileSetupScreenPreview() {
    ProfileSetupScreen(onSetupComplete = {})
}