package com.example

import android.content.Context
import android.util.Log
import android.view.SurfaceView
import androidx.lifecycle.ViewModel
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class CallState(
    val isJoined: Boolean = false,
    val remoteUid: Int? = null,
    val isMuted: Boolean = false,
    val isVideoEnabled: Boolean = true
)

class CallViewModel : ViewModel() {
    private var rtcEngine: RtcEngine? = null
    
    private val _callState = MutableStateFlow(CallState())
    val callState: StateFlow<CallState> = _callState.asStateFlow()

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            Log.d("CallViewModel", "Joined Channel: $channel, UID: $uid")
            _callState.update { it.copy(isJoined = true) }
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            Log.d("CallViewModel", "User Joined: $uid")
            _callState.update { it.copy(remoteUid = uid) }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            Log.d("CallViewModel", "User Offline: $uid")
            _callState.update { it.copy(remoteUid = null) }
        }
    }

    fun initAgoraAndJoin(context: Context, channelName: String, isVideo: Boolean) {
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
            
            // Bergabung ke Channel. 
            // Catatan: Jika Sertifikat Agora dinyalakan secara ketat (Strict Token),
            // parameter token (null) ini kelak harus diganti dengan Token Generator.
            rtcEngine?.joinChannel(null, channelName, 0, options)
            
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

    fun leaveChannel() {
        rtcEngine?.leaveChannel()
        RtcEngine.destroy()
        rtcEngine = null
        _callState.update { CallState() }
    }

    override fun onCleared() {
        super.onCleared()
        leaveChannel()
    }
}
