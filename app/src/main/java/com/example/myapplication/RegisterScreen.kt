package com.example.myapplication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val RegPurple = Color(0xFF6C63FF)
private val RegViolet = Color(0xFF8B5CF6)
private val RegDarkBlue = Color(0xFF0F0F23)
private val RegDarkBlueSubtle = Color(0x4D0F0F23)
private val RegBackgroundStart = Color(0xFFF0F2FF)
private val RegBackgroundEnd = Color(0xFFF5F7FF)
private val RegToggleBackground = Color(0xFFF0F0F8)
private val RegFieldBackground = Color(0xFFFFFFFF)
private val RegFieldBorder = Color(0x336C63FF)
private val RegGreen = Color(0xFF4ADE80)
private val RegGreenBackground = Color(0x334ADE80)
private val RegRuleInactive = Color(0x4D0F0F23)

enum class UserRole { Student, Teacher }

@Composable
fun RegisterScreen(
    onBack: () -> Unit = {},
    onRegister: (email: String, password: String, studentId: String, role: UserRole) -> Unit = { _, _, _, _ -> }
) {
    var selectedRole by remember { mutableStateOf(UserRole.Student) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }

    val hasMinLength = password.length >= 8
    val hasUpperAndDigit = password.any { it.isUpperCase() } && password.any { it.isDigit() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(RegBackgroundStart, RegBackgroundEnd))
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Navigation bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "‹ Назад",
                    color = RegPurple,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.clickable { onBack() }
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Регистрация",
                        color = RegDarkBlue,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                // Spacer to balance the back button width
                Spacer(modifier = Modifier.width(56.dp))
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                // Page title
                Text(
                    text = "Создайте аккаунт",
                    color = RegDarkBlue,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                // Role toggle
                RoleToggle(
                    selectedRole = selectedRole,
                    onRoleSelected = { selectedRole = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                )

                // Email field
                RegisterFieldLabel(text = "EMAIL")
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = {
                        Text(
                            text = "name@university.edu",
                            color = RegDarkBlueSubtle,
                            fontSize = 15.sp
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = RegFieldBackground,
                        focusedContainerColor = RegFieldBackground,
                        unfocusedBorderColor = RegFieldBorder,
                        focusedBorderColor = RegPurple,
                        cursorColor = RegPurple
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                )

                // Password field
                RegisterFieldLabel(text = "ПАРОЛЬ")
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = {
                        Text(
                            text = "Минимум 8 символов",
                            color = RegDarkBlueSubtle,
                            fontSize = 15.sp
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = RegFieldBackground,
                        focusedContainerColor = RegFieldBackground,
                        unfocusedBorderColor = RegFieldBorder,
                        focusedBorderColor = RegPurple,
                        cursorColor = RegPurple
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )

                // Password validation rules
                PasswordRule(
                    label = "Минимум 8 символов",
                    isMet = hasMinLength,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                PasswordRule(
                    label = "Заглавная буква и цифра",
                    isMet = hasUpperAndDigit,
                    modifier = Modifier.padding(bottom = 14.dp)
                )

                // Student ID field (shown for Student role)
                if (selectedRole == UserRole.Student) {
                    RegisterFieldLabel(text = "STUDENT ID")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = studentId,
                        onValueChange = { studentId = it },
                        placeholder = {
                            Text(
                                text = "Ваш ID в системе",
                                color = RegDarkBlueSubtle,
                                fontSize = 15.sp
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = RegFieldBackground,
                            focusedContainerColor = RegFieldBackground,
                            unfocusedBorderColor = RegFieldBorder,
                            focusedBorderColor = RegPurple,
                            cursorColor = RegPurple
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Register button
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(RegPurple, RegViolet)
                            )
                        )
                        .clickable {
                            onRegister(email, password, studentId, selectedRole)
                        }
                ) {
                    Text(
                        text = "Зарегистрироваться",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun RoleToggle(
    selectedRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(RegToggleBackground, RoundedCornerShape(12.dp))
            .padding(4.dp)
    ) {
        RoleTab(
            label = "🎓 Студент",
            isSelected = selectedRole == UserRole.Student,
            onClick = { onRoleSelected(UserRole.Student) },
            modifier = Modifier.weight(1f)
        )
        RoleTab(
            label = "🧑\u200D🏫 Преподаватель",
            isSelected = selectedRole == UserRole.Teacher,
            onClick = { onRoleSelected(UserRole.Teacher) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun RoleTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundModifier = if (isSelected) {
        modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                Brush.linearGradient(
                    listOf(RegPurple, RegViolet)
                )
            )
    } else {
        modifier.clip(RoundedCornerShape(10.dp))
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = backgroundModifier
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 8.dp)
    ) {
        Text(
            text = label,
            color = if (isSelected) Color.White else RegDarkBlueSubtle,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun PasswordRule(
    label: String,
    isMet: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(if (isMet) RegGreenBackground else Color.Transparent)
                .border(
                    width = 1.dp,
                    color = if (isMet) RegGreen else RegRuleInactive,
                    shape = CircleShape
                )
        ) {
            if (isMet) {
                Text(
                    text = "✓",
                    color = RegGreen,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = if (isMet) RegDarkBlue else RegRuleInactive,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal
        )
    }
}

@Composable
private fun RegisterFieldLabel(text: String) {
    Text(
        text = text,
        color = RegDarkBlueSubtle,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.8.sp
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F7FF)
@Composable
fun RegisterScreenPreview() {
    RegisterScreen()
}
