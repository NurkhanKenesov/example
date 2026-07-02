package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import org.koin.androidx.compose.koinViewModel
import java.util.concurrent.Executors

@Composable
fun QRScannerScreen(
    navController: NavHostController,
    onCheckInSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    val attendanceViewModel: AttendanceViewModel = koinViewModel()

    var hasCameraPermission by remember { mutableStateOf(false) }
    var scannedResult by remember { mutableStateOf<String?>(null) }
    var isScanning by remember { mutableStateOf(true) }
    var checkInMessage by remember { mutableStateOf<String?>(null) }
    var isCheckInSuccess by remember { mutableStateOf<Boolean?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) 
            == PackageManager.PERMISSION_GRANTED) {
            hasCameraPermission = true
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Кнопка назад
        Box(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            androidx.compose.material3.TextButton(onClick = { navController.popBackStack() }) {
                androidx.compose.material3.Text(
                    text = "‹ Назад",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        if (hasCameraPermission) {
            AndroidView(
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()

                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        // ML Kit Barcode Scanner
                        val options = BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                            .build()
                        val barcodeScanner = BarcodeScanning.getClient(options)

                        val imageAnalysis = ImageAnalysis.Builder()
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .build()

                        imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                            if (!isScanning) {
                                imageProxy.close()
                                return@setAnalyzer
                            }

                            // Используем ML Kit Analyzer (рекомендуемый способ)
                            // Для простоты используем direct ML Kit call на ImageProxy
                            // Можно улучшить через MlKitAnalyzer из camerax-mlkit

                            val image = imageProxy.image
                            if (image == null) {
                                imageProxy.close()
                                return@setAnalyzer
                            }
                            barcodeScanner.process(image, imageProxy.imageInfo.rotationDegrees)
                                .addOnSuccessListener { barcodes ->
                                    for (barcode in barcodes) {
                                        val rawValue = barcode.rawValue
                                        if (rawValue != null && isScanning) {
                                            isScanning = false
                                            scannedResult = rawValue
                                            CoroutineScope(Dispatchers.Main).launch {
                                                attendanceViewModel.checkIn(rawValue).fold(
                                                    onSuccess = { record ->
                                                        checkInMessage = "✅ Вы отмечены как присутствующий"
                                                        isCheckInSuccess = true
                                                        onCheckInSuccess()
                                                    },
                                                    onFailure = { error ->
                                                        val errorMsg = when (error.message) {
                                                            "Not logged in" -> "Ошибка: пользователь не авторизован"
                                                            "Session not found" -> "Сессия не найдена"
                                                            else -> "Ошибка отметки: ${error.message}"
                                                        }
                                                        checkInMessage = errorMsg
                                                        isCheckInSuccess = false
                                                    }
                                                )
                                            }
                                            break
                                        }
                                    }
                                }
                                .addOnCompleteListener {
                                    imageProxy.close()
                                }
                        }

                        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageAnalysis
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                },
                modifier = Modifier.fillMaxSize()
            )

            // Оверлей с инструкциями
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = "Наведите камеру на QR-код",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Требуется разрешение на камеру")
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Разрешить доступ к камере")
                }
            }
        }

        // Показ результата отметки
        checkInMessage?.let { message ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                androidx.compose.material3.Card(
                    modifier = Modifier.padding(16.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = if (isCheckInSuccess == true) Color(0xFFDCFCE7) else Color(0xFFFEF2F2)
                    )
                ) {
                    Text(
                        text = message,
                        modifier = Modifier.padding(24.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (isCheckInSuccess == true) Color(0xFF166534) else Color(0xFF991B1B)
                    )
                }
            }
        }
    }

    // Очистка
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}
