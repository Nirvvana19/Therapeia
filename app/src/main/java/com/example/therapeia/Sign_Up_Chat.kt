package com.example.therapeia

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Sign_Up_Chat : AppCompatActivity() {

        private lateinit var auth: FirebaseAuth
        private lateinit var databaseReference: DatabaseReference

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_sign_up_chat)

            auth = FirebaseAuth.getInstance()
            val firebaseUser: FirebaseUser? = auth.currentUser

            if (firebaseUser != null) {
                // Si el usuario ya está autenticado, navega a MainActivityC
                startActivity(Intent(this@Sign_Up_Chat, MainActivityC::class.java))
                finish()
            }


        findViewById<Button>(R.id.btnSignUp).setOnClickListener {

            val userName = findViewById<TextView>(R.id.etName).text.toString()
            val email = findViewById<TextView>(R.id.etEmail).text.toString()
            val password = findViewById<TextView>(R.id.etPassword).text.toString()
            val confirmPasword = findViewById<TextView>(R.id.etConfirmPassword).text.toString()


            if (TextUtils.isEmpty(userName)) {
                Toast.makeText(
                    applicationContext,
                    "Se requiere nombre de usuario",
                    Toast.LENGTH_SHORT
                ).show()
            }

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(
                    applicationContext,
                    "Se requiere un correo electronico",
                    Toast.LENGTH_SHORT
                ).show()
            }


            if (TextUtils.isEmpty(password)) {
                Toast.makeText(
                    applicationContext,
                    "Se requiere una contraseña",
                    Toast.LENGTH_SHORT
                ).show()
            }

            if (TextUtils.isEmpty(confirmPasword)) {
                Toast.makeText(
                    applicationContext,
                    "Se requiere confirmar la contraseña",
                    Toast.LENGTH_SHORT
                ).show()
            }

            if (password != (confirmPasword)) {
                Toast.makeText(
                    applicationContext,
                    "La contraseña no es igual",
                    Toast.LENGTH_SHORT
                ).show()
            }

            registerUser(userName, email, password)
        }


        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val intent = Intent(this@Sign_Up_Chat,
                Login_Chat::class.java)
            startActivity(intent)
            finish()
        }

    }




    private fun registerUser(userName: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) {
                if (it.isSuccessful) {
                    val users: FirebaseUser? = auth.currentUser
                    val userId: String = users!!.uid

                    databaseReference =
                        FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap.put("userId", userId)
                    hashMap.put("userName", userName)
                    hashMap.put("profileImage", "https://firebasestorage.googleapis.com/v0/b/therapeia-bd8d4.appspot.com/o/download.png?alt=media&token=977e94bb-4235-448c-b6b7-f97e6c46b9e9")
                    hashMap.put("status", "offline" )
                    hashMap.put("search", userName.toLowerCase())

                    databaseReference.setValue(hashMap).addOnCompleteListener(this) {
                        if (it.isSuccessful) {

                            //Abrir Chat

                            val intent = Intent(this@Sign_Up_Chat, MainActivityC::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }

            .addOnFailureListener(this) { e ->
                Toast.makeText(applicationContext, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }

    }
}
