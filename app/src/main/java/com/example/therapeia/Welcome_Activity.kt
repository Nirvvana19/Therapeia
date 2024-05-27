package com.example.therapeia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class Welcome_Activity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        auth = FirebaseAuth.getInstance()

        // Verificar si el usuario está autenticado
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Si el usuario está autenticado, redirigirlo a MainActivityC
            val intent = Intent(this@Welcome_Activity, MainActivityC::class.java)
            startActivity(intent)
            finish()
        }

        // Configurar el listener para el botón de ingresar al chat
        findViewById<TextView>(R.id.btnentrar).setOnClickListener {
            val intent = Intent(this@Welcome_Activity, Sign_Up_Chat::class.java)
            startActivity(intent)
            finish()
        }
    }
}