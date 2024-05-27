package com.example.therapeia.Notificationsc

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseInstanceId: FirebaseMessagingService() {


    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        firebaseUser?.uid?.let { userId ->
            updateToken(userId, token)
        }
    }

    private fun updateToken(refreshToken: String, token: String) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val tokenObj = Tokens(refreshToken)
        firebaseUser?.uid?.let {
            ref.child(it).setValue(tokenObj)
                .addOnSuccessListener {
                    Log.d(TAG, "Token actualizado exitosamente en la base de datos")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error al actualizar el token en la base de datos: $e")
                }
        }
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // Aquí puedes manejar los mensajes entrantes si lo necesitas
    }

    companion object {
        private const val TAG = "MyFirebaseMsgService"
    }
}