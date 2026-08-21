package com.example

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallMissed
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.Call



import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallHistoryScreen(
    viewModel: CallHistoryViewModel = viewModel(),
    onNavigateToCall: (String, String, Boolean) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUserId = viewModel.currentUserId

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Riwayat Panggilan", fontWeight = FontWeight.Bold) })
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                is CallHistoryUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is CallHistoryUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }
                is CallHistoryUiState.Success -> {
                    if (state.calls.isEmpty()) {
                        Text(
                            "Belum ada riwayat panggilan.",
                            modifier = Modifier.align(Alignment.Center),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.calls, key = { it.id }) { call ->
                                CallRecordItem(
                                    call = call,
                                    currentUserId = currentUserId,
                                    onCallClick = { isVideo ->
                                        val otherId = if (call.callerId == currentUserId) call.receiverId else call.callerId
                                        val otherName = if (call.callerId == currentUserId) call.receiverName else call.callerName
                                        onNavigateToCall(otherId, otherName, isVideo)
                                    }
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
fun CallRecordItem(
    call: CallRecord,
    currentUserId: String,
    onCallClick: (Boolean) -> Unit
) {
    val isCaller = call.callerId == currentUserId
    val otherName = if (isCaller) call.receiverName else call.callerName
    val isMissed = call.status == "missed" || call.status == "rejected"

    val icon = when {
        isMissed && !isCaller -> Icons.AutoMirrored.Filled.CallMissed 
        isMissed && isCaller -> Icons.AutoMirrored.Filled.CallMade 
        isCaller -> Icons.AutoMirrored.Filled.CallMade
        else -> Icons.AutoMirrored.Filled.CallReceived
    }
    
    val iconColor = if (isMissed && !isCaller) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(call.timestamp))
    
    val durationString = if (call.durationSeconds > 0) {
        val mins = call.durationSeconds / 60
        val secs = call.durationSeconds % 60
        if (mins > 0) "${mins}m ${secs}s" else "${secs}s"
    } else {
        if (call.status == "rejected") "Ditolak" else "Tak Terjawab"
    }

    val callTypeIcon = if (call.isVideoCall) Icons.Filled.Videocam else Icons.Filled.Call

    ListItem(
        headlineContent = { 
            Text(otherName, fontWeight = FontWeight.Bold, color = if (isMissed && !isCaller) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
        },
        supportingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = iconColor
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = callTypeIcon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("$dateString • $durationString", style = MaterialTheme.typography.bodySmall)
            }
        },
        leadingContent = {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (otherName.isNotEmpty()) otherName.take(1).uppercase() else "?",
                    color = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        trailingContent = {
            Row {
                IconButton(onClick = { onCallClick(false) }) {
                    Icon(Icons.Filled.Call, contentDescription = "Panggil Ulang Suara", tint = MaterialTheme.colorScheme.primary)
                }
                IconButton(onClick = { onCallClick(true) }) {
                    Icon(Icons.Filled.Videocam, contentDescription = "Panggil Ulang Video", tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    )
}
