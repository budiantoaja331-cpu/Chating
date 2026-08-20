package com.example

import android.Manifest
import android.view.SurfaceView
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CallScreen(
    channelName: String,
    isVideoCall: Boolean,
    onNavigateBack: () -> Unit,
    viewModel: CallViewModel = viewModel()
) {
    val context = LocalContext.current
    val callState by viewModel.callState.collectAsState()
    
    val permissions = if (isVideoCall) {
        listOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    } else {
        listOf(Manifest.permission.RECORD_AUDIO)
    }
    
    val permissionState = rememberMultiplePermissionsState(permissions)

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }

    LaunchedEffect(permissionState.allPermissionsGranted) {
        if (permissionState.allPermissionsGranted) {
            viewModel.initAgoraAndJoin(context, channelName, isVideoCall)
        }
    }

    BackHandler {
        viewModel.leaveChannel()
        onNavigateBack()
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (permissionState.allPermissionsGranted) {
            // Video Layar Penuh (Teman / Remote)
            if (isVideoCall && callState.remoteUid != null) {
                AndroidView(
                    factory = { ctx ->
                        SurfaceView(ctx).apply {
                            viewModel.setupRemoteVideo(this, callState.remoteUid!!)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else if (!isVideoCall || callState.remoteUid == null) {
                // Tampilan saat menunggu atau Panggilan Suara saja
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Filled.AccountCircle, 
                        contentDescription = null, 
                        modifier = Modifier.size(120.dp),
                        tint = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (callState.remoteUid == null) "Menunggu teman masuk..." else "Panggilan Suara Berlangsung",
                        color = Color.White
                    )
                }
            }

            // Video Melayang (Diri Sendiri / Local PIP)
            if (isVideoCall) {
                Box(
                    modifier = Modifier
                        .padding(top = 48.dp, end = 16.dp)
                        .size(100.dp, 150.dp)
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.DarkGray)
                ) {
                    AndroidView(
                        factory = { ctx ->
                            SurfaceView(ctx).apply {
                                setZOrderMediaOverlay(true) // Memastikan video kita berada di atas video teman
                                viewModel.setupLocalVideo(this)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Deretan Tombol Kendali (Bawah)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 48.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tombol Mute (Bisukan)
                IconButton(
                    onClick = { viewModel.toggleMute() },
                    modifier = Modifier
                        .size(56.dp)
                        .background(if (callState.isMuted) Color.White else Color.DarkGray, CircleShape)
                ) {
                    Icon(
                        imageVector = if (callState.isMuted) Icons.Filled.MicOff else Icons.Filled.Mic,
                        contentDescription = "Mute",
                        tint = if (callState.isMuted) Color.Black else Color.White
                    )
                }

                // Tombol Akhiri Panggilan (Tutup Telepon)
                IconButton(
                    onClick = {
                        viewModel.leaveChannel()
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color.Red, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.CallEnd,
                        contentDescription = "Akhiri Panggilan",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Tombol Putar Kamera (Switch Camera)
                if (isVideoCall) {
                    IconButton(
                        onClick = { viewModel.switchCamera() },
                        modifier = Modifier
                            .size(56.dp)
                            .background(Color.DarkGray, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FlipCameraAndroid,
                            contentDescription = "Ganti Kamera",
                            tint = Color.White
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(56.dp))
                }
            }
        } else {
            // Tampilan jika izin ditolak
            Text(
                "Membutuhkan izin Kamera dan Mikrofon untuk menelpon.",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}
