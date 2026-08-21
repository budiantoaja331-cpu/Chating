package com.example

import android.content.Context
import android.util.Log
import android.view.SurfaceView
import android.media.AudioManager
import android.media.ToneGenerator
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.BeautyOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class BeautyMode(val title: String) {
    OFF("Normal"),
    NATURAL("Natural"),
    SMOOTH("Mulus"),
    GLAMOUR("Glamour")
}

data class CallState(
    val isJoined: Boolean = false,
    val remoteUid: Int? = null,
    val isMuted: Boolean = false,
    val isVideoEnabled: Boolean = true,
    val beautyMode: BeautyMode = BeautyMode.OFF,
    val connectionStatus: String = "Menghubungkan...",
    val durationSeconds: Long = 0,
    val networkQuality: Int = 0
)

class CallViewModel : ViewModel() {
    private var rtcEngine: RtcEngine? = null
    private var timerJob: Job? = null
    private var toneGenerator: ToneGenerator? = null
    private var isRinging = false
    private var currentChannelId: String = "" 
    
    private val _callState = MutableStateFlow(CallState())
    val callState: StateFlow<CallState> = _callState.asStateFlow()

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            Log.d("CallViewModel", "Joined Channel: $channel, UID: $uid")
            _callState.update { it.copy(isJoined = true) }
            if (_callState.value.remoteUid == null) {
                startRinging()
            }
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            Log.d("CallViewModel", "User Joined: $uid")
            _callState.update { it.copy(remoteUid = uid) }
            stopRinging()
            startTimer()
        }

        override fun onNetworkQuality(uid: Int, txQuality: Int, rxQuality: Int) {
            if (uid == 0) { // 0 means local user
                val worstQuality = if (txQuality == 0) rxQuality else if (rxQuality == 0) txQuality else maxOf(txQuality, rxQuality)
                _callState.update { it.copy(networkQuality = worstQuality) }
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            Log.d("CallViewModel", "User Offline: $uid")
            _callState.update { it.copy(remoteUid = null) }
            stopTimer()
        }
        
        override fun onError(err: Int) {
            Log.e("CallViewModel", "Agora Error: $err")
            // Optional: you can expose this error to the UI state
        }
        
        override fun onConnectionStateChanged(state: Int, reason: Int) {
            val statusStr = when(state) {
                Constants.CONNECTION_STATE_CONNECTING -> "Menghubungkan..."
                Constants.CONNECTION_STATE_CONNECTED -> "Terhubung"
                Constants.CONNECTION_STATE_RECONNECTING -> "Menyambung kembali (Sinyal Lemah)..."
                Constants.CONNECTION_STATE_FAILED -> "Koneksi Gagal"
                Constants.CONNECTION_STATE_DISCONNECTED -> "Terputus"
                else -> "Menunggu..."
            }
            _callState.update { it.copy(connectionStatus = statusStr) }
        }
    }

    fun initAgoraAndJoin(context: Context, channelName: String, isVideo: Boolean) {
        this.currentChannelId = channelName
        try {
            val config = RtcEngineConfig().apply {
                mContext = context.applicationContext
                mAppId = BuildConfig.AGORA_APP_ID
                mEventHandler = mRtcEventHandler
            }
            rtcEngine = RtcEngine.create(config)
            
            if (isVideo) {
                rtcEngine?.enableVideo()
            } else {
                rtcEngine?.disableVideo()
            }

            val options = ChannelMediaOptions().apply {
                clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
                channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            }
            
            // Bergabung ke Channel menggunakan Token Lokal.
            val tokenService = RtcTokenService()
            val token = tokenService.generateToken(channelName, 0)
            rtcEngine?.joinChannel(token, channelName, 0, options)
            
        } catch (e: Exception) {
            Log.e("CallViewModel", "Exception creating RTC engine", e)
        }
    }

    fun setupLocalVideo(surfaceView: SurfaceView) {
        rtcEngine?.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0))
    }

    fun setupRemoteVideo(surfaceView: SurfaceView, uid: Int) {
        rtcEngine?.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, uid))
    }

    fun toggleMute() {
        val muted = !_callState.value.isMuted
        rtcEngine?.muteLocalAudioStream(muted)
        _callState.update { it.copy(isMuted = muted) }
    }

    fun switchCamera() {
        rtcEngine?.switchCamera()
    }

    fun setBeautyMode(mode: BeautyMode) {
        if (mode == BeautyMode.OFF) {
            rtcEngine?.setBeautyEffectOptions(false, BeautyOptions())
        } else {
            val options = BeautyOptions().apply {
                lighteningContrastLevel = BeautyOptions.LIGHTENING_CONTRAST_NORMAL
                when (mode) {
                    BeautyMode.NATURAL -> {
                        lighteningLevel = 0.6f
                        smoothnessLevel = 0.5f
                        rednessLevel = 0.1f
                        sharpnessLevel = 0.3f
                    }
                    BeautyMode.SMOOTH -> {
                        lighteningLevel = 0.7f
                        smoothnessLevel = 0.9f
                        rednessLevel = 0.1f
                        sharpnessLevel = 0.1f
                    }
                    BeautyMode.GLAMOUR -> {
                        lighteningLevel = 0.9f
                        smoothnessLevel = 0.8f
                        rednessLevel = 0.4f
                        sharpnessLevel = 0.6f
                    }
                    else -> {}
                }
            }
            rtcEngine?.setBeautyEffectOptions(true, options)
        }
        _callState.update { it.copy(beautyMode = mode) }
    }

    fun leaveChannel() {
        val duration = _callState.value.durationSeconds
        if (duration > 0 && currentChannelId.isNotEmpty()) {
            updateCallDuration(currentChannelId, duration.toInt())
        }
        
        stopRinging()
        stopTimer()
        rtcEngine?.leaveChannel()
        RtcEngine.destroy()
        rtcEngine = null
        _callState.update { CallState() }
    }
    
    private fun updateCallDuration(channelId: String, duration: Int) {
        val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
        db.collection("call_history")
            .whereEqualTo("channelId", channelId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val docId = snapshot.documents[0].id
                    db.collection("call_history").document(docId).update("durationSeconds", duration)
                }
            }
            .addOnFailureListener { e ->
                Log.e("CallViewModel", "Failed to update call duration", e)
            }
    }

    private fun startRinging() {
        if (!isRinging) {
            try {
                // Gunakan ringback tone standar untuk menandakan panggilan keluar/menunggu
                toneGenerator = ToneGenerator(AudioManager.STREAM_VOICE_CALL, 100)
                toneGenerator?.startTone(ToneGenerator.TONE_SUP_RINGTONE)
                isRinging = true
            } catch (e: Exception) {
                Log.e("CallViewModel", "Failed to start ringtone", e)
            }
        }
    }

    private fun stopRinging() {
        if (isRinging) {
            try {
                toneGenerator?.stopTone()
                toneGenerator?.release()
            } catch (e: Exception) {
                Log.e("CallViewModel", "Failed to stop ringtone", e)
            } finally {
                toneGenerator = null
                isRinging = false
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _callState.update { it.copy(durationSeconds = it.durationSeconds + 1) }
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
        _callState.update { it.copy(durationSeconds = 0) }
    }

    override fun onCleared() {
        super.onCleared()
        leaveChannel()
    }
}
