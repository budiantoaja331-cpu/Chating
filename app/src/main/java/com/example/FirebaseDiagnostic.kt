package com.example

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.database.FirebaseDatabase

@Composable
fun FirebaseDiagnosticUI() {
    var showDialog by remember { mutableStateOf(false) }
    var diagnosticMessage by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val errors = mutableListOf<String>()
        try {
            val apps = FirebaseApp.getApps(context)
            if (apps.isEmpty()) {
                errors.add("FirebaseApp tidak terinisialisasi. Kemungkinan google-services.json hilang atau tidak terbaca.")
            } else {
                try {
                    FirebaseAuth.getInstance()
                } catch (e: Exception) {
                    errors.add("FirebaseAuth Error: ${e.message}")
                }
                try {
                    FirebaseFirestore.getInstance()
                } catch (e: Exception) {
                    errors.add("FirebaseFirestore Error: ${e.message}")
                }
                try {
                    FirebaseDatabase.getInstance()
                } catch (e: Exception) {
                    errors.add("FirebaseDatabase Error: ${e.message}")
                }
            }
        } catch (e: Exception) {
            errors.add("Fatal Error: ${e.message}")
        }

        if (errors.isNotEmpty()) {
            diagnosticMessage = errors.joinToString("\n\n")
            showDialog = true
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { 
                Text("Peringatan Sistem Firebase", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) 
            },
            text = { 
                Text("Beberapa layanan gagal dimuat:\n\n$diagnosticMessage\n\nPastikan google-services.json ada di folder app/ jika menggunakan Emulator, atau setup GitHub Secrets dengan benar.") 
            },
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }
}
