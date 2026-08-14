package com.menuly.app.ui.screens

import android.graphics.Bitmap
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.menuly.app.R
import com.menuly.app.ui.components.GradientButton
import com.menuly.app.ui.theme.AccentOrange
import com.menuly.app.ui.theme.AccentPink
import com.menuly.app.ui.theme.AccentPurple
import com.menuly.app.ui.theme.MenulyBlack
import com.menuly.app.ui.theme.MenulyMuted
import com.menuly.app.ui.theme.MenulyWhite
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Composable
fun ScanScreen(
    onBack: () -> Unit,
    onCaptured: (Bitmap) -> Unit,
    hasCameraPermission: Boolean,
    onRequestPermission: () -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var scanning by remember { mutableStateOf(false) }
    var flashAlpha by remember { mutableStateOf(0f) }

    val holdStill = stringResource(R.string.scan_hold_still)
    val scanError = stringResource(R.string.scan_error)
    val scanDone = stringResource(R.string.scan_done)
    val aimHint = stringResource(R.string.scan_aim_hint)
    var status by remember { mutableStateOf(aimHint) }
    val sweep = remember { Animatable(0f) }

    LaunchedEffect(hasCameraPermission) {
        if (!hasCameraPermission) onRequestPermission()
    }

    fun runScan(capture: ImageCapture) {
        if (scanning) return
        scanning = true
        status = holdStill
        scope.launch {
            try {
                sweep.snapTo(0f)
                flashAlpha = 0f
                val anim = launch {
                    sweep.animateTo(
                        targetValue = 1f,
                        animationSpec = tween(durationMillis = 650, easing = LinearEasing),
                    )
                }
                delay(480)
                val bmp = captureBitmap(capture, context)
                anim.join()
                flashAlpha = 0.92f
                delay(90)
                flashAlpha = 0f
                status = scanDone
                delay(60)
                onCaptured(bmp)
            } catch (e: Exception) {
                status = e.message ?: scanError
                scanning = false
                sweep.snapTo(0f)
                flashAlpha = 0f
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MenulyBlack)
            .statusBarsPadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack, enabled = !scanning) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = MenulyWhite)
            }
            Text(
                stringResource(R.string.scan_title),
                color = MenulyWhite,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.weight(1f),
            )
        }

        BoxWithConstraints(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF111111)),
        ) {
            val frameHeight = maxHeight
            val density = LocalDensity.current

            if (!hasCameraPermission) {
                Column(
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(stringResource(R.string.scan_camera_permission), color = MenulyWhite)
                    Spacer(Modifier.height(16.dp))
                    GradientButton(
                        text = stringResource(R.string.scan_allow_camera),
                        onClick = onRequestPermission,
                    )
                }
            } else {
                AndroidView(
                    factory = { ctx ->
                        PreviewView(ctx).also { previewView ->
                            val providerFuture = ProcessCameraProvider.getInstance(ctx)
                            providerFuture.addListener({
                                val provider = providerFuture.get()
                                val preview = Preview.Builder().build().also {
                                    it.surfaceProvider = previewView.surfaceProvider
                                }
                                val capture = ImageCapture.Builder()
                                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                    .build()
                                imageCapture = capture
                                try {
                                    provider.unbindAll()
                                    provider.bindToLifecycle(
                                        lifecycleOwner,
                                        CameraSelector.DEFAULT_BACK_CAMERA,
                                        preview,
                                        capture,
                                    )
                                } catch (_: Exception) {
                                }
                            }, ContextCompat.getMainExecutor(ctx))
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )

                if (scanning) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colorStops = arrayOf(
                                        0f to Color.Transparent,
                                        (sweep.value - 0.1f).coerceIn(0f, 1f) to Color.Transparent,
                                        sweep.value to AccentPink.copy(alpha = 0.28f),
                                        (sweep.value + 0.1f).coerceIn(0f, 1f) to Color.Transparent,
                                        1f to Color.Transparent,
                                    )
                                )
                            ),
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .offset(
                                y = with(density) {
                                    ((frameHeight.toPx() - 4.dp.toPx()) * sweep.value).toDp()
                                }
                            )
                            .background(
                                Brush.horizontalGradient(
                                    listOf(AccentOrange, AccentPink, AccentPurple)
                                )
                            ),
                    )
                    Text(
                        holdStill,
                        color = MenulyWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .background(Color.Black.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 18.dp, vertical = 10.dp),
                    )
                }

                if (flashAlpha > 0.01f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White.copy(alpha = flashAlpha)),
                    )
                }
            }
        }

        Text(
            status,
            color = MenulyMuted,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        )
        Spacer(Modifier.height(12.dp))

        GradientButton(
            text = if (scanning) {
                stringResource(R.string.scan_scanning)
            } else {
                stringResource(R.string.scan_action)
            },
            onClick = {
                val capture = imageCapture ?: return@GradientButton
                runScan(capture)
            },
            enabled = hasCameraPermission && !scanning && imageCapture != null,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
        )
    }
}

private suspend fun captureBitmap(
    capture: ImageCapture,
    context: android.content.Context,
): Bitmap = suspendCancellableCoroutine { cont ->
    capture.takePicture(
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                try {
                    val bmp = image.toBitmap()
                    image.close()
                    if (cont.isActive) cont.resume(bmp)
                } catch (e: Exception) {
                    image.close()
                    if (cont.isActive) cont.resumeWithException(e)
                }
            }

            override fun onError(exception: ImageCaptureException) {
                if (cont.isActive) cont.resumeWithException(exception)
            }
        },
    )
}
