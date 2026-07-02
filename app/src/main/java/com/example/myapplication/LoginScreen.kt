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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

sealed interface AuthUiState {
    object Idle : AuthUiState
    object Loading : AuthUiState
    data class Success(val message: String = "") : AuthUiState
    data class Error(val message: String) : AuthUiState
}

private val Purple = Color(0xFF6C63FF)
private val DarkBlue = Color(0xFF0F0F23)
private val DarkBlueSubtle = Color(0x660F0F23)
private val Amber = Color(0xFFFBBF24)
private val BackgroundStart = Color(0xFFF0F2FF)
private val BackgroundEnd = Color(0xFFF5F7FF)
private val FieldBackground = Color(0xFFFFFFFF)
private val FieldBorder = Color(0x336C63FF)
private val InfoCardBackground = Color(0xFFFFFFFF)

@Composable
fun LoginScreen(
    uiState: AuthUiState = AuthUiState.Idle,
    onLoginClick: (email: String, password: String) -> Unit = { _, _ -> },
    onLoginSuccess: () -> Unit = {},
    onErrorDismiss: () -> Unit = {},
    onBackClick: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    
    val isEmailValid = email.contains("@")
    val isPasswordValid = password.length >= 6
    val isLoading = uiState is AuthUiState.Loading
    val errorMessage = (uiState as? AuthUiState.Error)?.message
    val isFormValid = isEmailValid && isPasswordValid && !isLoading
    
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            onLoginSuccess()
        }
    }
    
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            snackbarHostState.showSnackbar(
                message = errorMessage,
                duration = SnackbarDuration.Short
            )
            onErrorDismiss()
        }
    }
    
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFFF87171),
                    contentColor = Color.White
                )
            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(BackgroundStart, BackgroundEnd)
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Back navigation
                Text(
                    text = "‹ Назад",
                    color = Purple,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                        .clickable { onBackClick() }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    // Title
                    Text(
                        text = "Вход в аккаунт",
                        color = DarkBlue,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    // Subtitle
                    Text(
                        text = "Введите ваш университетский email",
                        color = DarkBlueSubtle,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier.padding(bottom = 36.dp)
                    )

                    // Email field
                    FieldLabel(text = "EMAIL")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        placeholder = {
                            Text(
                                text = "student1@smartpe.edu",
                                color = DarkBlueSubtle,
                                fontSize = 15.sp
                            )
                        },
                        leadingIcon = {
                            Text(text = "✉", fontSize = 16.sp, color = DarkBlueSubtle)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = FieldBackground,
                            focusedContainerColor = FieldBackground,
                            unfocusedBorderColor = FieldBorder,
                            focusedBorderColor = Purple,
                            cursorColor = Purple
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = if (email.isNotEmpty() && !isEmailValid) 4.dp else 16.dp)
                            .testTag("email_field")
                    )
                    if (email.isNotEmpty() && !isEmailValid) {
                        Text(
                            text = "Неверный формат email",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(bottom = 16.dp)
                                .testTag("email_error_text")
                        )
                    }

                    // Password field
                    FieldLabel(text = "ПАРОЛЬ")
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = {
                            Text(
                                text = "••••••••",
                                color = DarkBlueSubtle,
                                fontSize = 15.sp
                            )
                        },
                        leadingIcon = {
                            Text(text = "🔒", fontSize = 15.sp)
                        },
                        trailingIcon = {
                            Text(
                                text = if (passwordVisible) "🙈" else "👁",
                                fontSize = 16.sp,
                                modifier = Modifier.clickable {
                                    passwordVisible = !passwordVisible
                                }
                            )
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None
                        else
                            PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = FieldBackground,
                            focusedContainerColor = FieldBackground,
                            unfocusedBorderColor = FieldBorder,
                            focusedBorderColor = Purple,
                            cursorColor = Purple
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = if (password.isNotEmpty() && !isPasswordValid) 4.dp else 24.dp)
                            .testTag("password_field")
                    )
                    if (password.isNotEmpty() && !isPasswordValid) {
                        Text(
                            text = "Пароль должен быть не менее 6 символов",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier
                                .padding(bottom = 24.dp)
                                .testTag("password_error_text")
                        )
                    }

                    // Login button
                    Button(
                        onClick = {
                            onLoginClick(email, password)
                        },
                        enabled = isFormValid,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Purple,
                            disabledContainerColor = Purple.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("login_button")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier
                                    .size(24.dp)
                                    .testTag("loading_indicator"),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Войти",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Forgot password
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        Text(
                            text = buildAnnotatedString {
                                withStyle(SpanStyle(color = DarkBlueSubtle, fontSize = 14.sp)) {
                                    append("Забыли пароль? ")
                                }
                                withStyle(SpanStyle(color = Purple, fontSize = 14.sp, fontWeight = FontWeight.Medium)) {
                                    append("Восстановить")
                                }
                            },
                            modifier = Modifier.clickable { onForgotPassword() }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Role info card
                RoleInfoCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                )
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = DarkBlueSubtle,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.8.sp
    )
}

@Composable
private fun RoleInfoCard(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(InfoCardBackground, RoundedCornerShape(16.dp))
            .border(1.dp, FieldBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 10.dp)
        ) {
            Text(text = "ℹ", fontSize = 14.sp, color = Purple)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Роли в системе",
                color = DarkBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Student role
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Purple, fontWeight = FontWeight.Bold, fontSize = 13.sp)) {
                    append("Студент")
                }
                withStyle(SpanStyle(color = DarkBlueSubtle, fontSize = 13.sp)) {
                    append(" — просмотр плана, отметка выполнения")
                }
            },
            modifier = Modifier.padding(bottom = 6.dp)
        )

        // Teacher role
        Text(
            text = buildAnnotatedString {
                withStyle(SpanStyle(color = Amber, fontWeight = FontWeight.Bold, fontSize = 13.sp)) {
                    append("Преподаватель")
                }
                withStyle(SpanStyle(color = DarkBlueSubtle, fontSize = 13.sp)) {
                    append(" — управление и корректировка")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LoginScreen()
}