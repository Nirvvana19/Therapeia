package com.example.therapeia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Login_Chat : AppCompatActivity() {

        private lateinit var auth: FirebaseAuth

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_login_chat)

            auth = FirebaseAuth.getInstance()
            val firebaseUser: FirebaseUser? = auth.currentUser

            if (firebaseUser != null) {
                // Si el usuario ya está autenticado, navega a MainActivityC
                startActivity(Intent(this@Login_Chat, MainActivityC::class.java))
                finish()
            }

            findViewById<Button>(R.id.btnLogin).setOnClickListener {
                val email = findViewById<TextView>(R.id.etEmail).text.toString()
                val password = findViewById<TextView>(R.id.etPassword).text.toString()

                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password)) {
                    Toast.makeText(
                        applicationContext,
                        "email and password are required",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    auth!!.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this) {
                            if (it.isSuccessful) {
                                findViewById<TextView>(R.id.etPassword).setText("")
                                findViewById<TextView>(R.id.etPassword).setText("")
                                val intent = Intent(
                                    this@Login_Chat,
                                    MainActivityC::class.java
                                )
                                startActivity(intent)
                                finish()
                            } else {
                                Toast.makeText(
                                    applicationContext,
                                    "email or password invalid",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            }

            findViewById<TextView>(R.id.btnSignUp).setOnClickListener {
                val intent = Intent(
                    this@Login_Chat,
                    Sign_Up_Chat::class.java
                )
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

            }
        }
    }