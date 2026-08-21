package com.example

import io.agora.media.RtcTokenBuilder
import android.util.Log

class RtcTokenService {
    // Gunakan APP ID dan Certificate terbaru yang diberikan oleh pengguna
    private val APP_ID = "085ae7b69ba544a887d74c00b7e9f0d9"
    private val APP_CERTIFICATE = "1a8bb790ab06499a92c1085a07dd23ce"

    fun generateToken(channelName: String, uid: Int): String {
        Log.d("RtcTokenService", "Merakit token lokal untuk channel $channelName")
        val builder = RtcTokenBuilder()
        val timestamp = (System.currentTimeMillis() / 1000).toInt()
        val expirationTimeInSeconds = 3600 // Masa berlaku 1 jam
        val privilegeExpiredTs = timestamp + expirationTimeInSeconds
        
        return builder.buildTokenWithUid(
            APP_ID, 
            APP_CERTIFICATE, 
            channelName, 
            uid, 
            RtcTokenBuilder.Role.Role_Publisher, 
            privilegeExpiredTs
        )
    }
}
