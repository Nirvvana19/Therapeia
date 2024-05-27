package com.example.therapeia

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.therapeia.R
import com.example.therapeia.databinding.ActivityLoginDoctorBinding
import com.example.therapeia.databinding.FragmentDoctorCuentaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Login_Doctor : AppCompatActivity() {

    private lateinit var binding: ActivityLoginDoctorBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnLoginDoctor.setOnClickListener {
            ValidarInformacion()
        }
    }
    private var email = ""
    private var password = ""

    private fun ValidarInformacion() {
        email = binding.EtEmailDoctor.text.toString().trim()
        password = binding.EtPasswordDoctor.text.toString().trim()

        if (email.isEmpty()){
            binding.EtEmailDoctor.error = "Ingrese su correo"
            binding.EtEmailDoctor.requestFocus()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmailDoctor.error = "Correo invalido"
            binding.EtEmailDoctor.requestFocus()
        }
        else if(password.isEmpty()){
            binding.EtPasswordDoctor.error = "Ingrese la contraseña"
            binding.EtPasswordDoctor.requestFocus()
        }
        else{
            LoginDoctor()
        }
    }

    private fun LoginDoctor() {
        progressDialog.setMessage("Iniciando sesión")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                ValidarDoctor()

            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se pude iniciar sesión debido a ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }
    }
    private fun ValidarDoctor() {
        val firebaseUser = firebaseAuth.currentUser
        val references = FirebaseDatabase.getInstance().getReference("Usuarios")
        firebaseUser?.let {
            references.child(it.uid)
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val rol = snapshot.child("rol").value
                        if (rol == "doctor") {
                            progressDialog.dismiss()
                            startActivity(
                                Intent(
                                    this@Login_Doctor,
                                    MainActivity::class.java
                                )
                            )
                            finishAffinity()
                        } else{
                            progressDialog.dismiss()
                            Toast.makeText(applicationContext, "Tipo de usuario incorrecto",
                                Toast.LENGTH_SHORT).show()
                        }


                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
        }
    }
}