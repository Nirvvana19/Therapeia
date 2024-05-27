package com.example.therapeia.Paciente

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.therapeia.MainActivity
import com.example.therapeia.MainActivityPaciente
import com.example.therapeia.R
import com.example.therapeia.databinding.ActivityLoginDoctorBinding
import com.example.therapeia.databinding.ActivityLoginPacienteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginPaciente : AppCompatActivity() {
    private lateinit var binding: ActivityLoginPacienteBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginPacienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnLoginPaciente.setOnClickListener {
            ValidarInformacion()
        }
    }
    private var email = ""
    private var password = ""

    private fun ValidarInformacion() {
        email = binding.EtEmailPaciente.text.toString().trim()
        password = binding.EtPasswordPaciente.text.toString().trim()

        if (email.isEmpty()){
            binding.EtEmailPaciente.error = "Ingrese su correo"
            binding.EtEmailPaciente.requestFocus()
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.EtEmailPaciente.error = "Correo invalido"
            binding.EtEmailPaciente.requestFocus()
        }
        else if(password.isEmpty()){
            binding.EtPasswordPaciente.error = "Ingrese la contraseña"
            binding.EtPasswordPaciente.requestFocus()
        }
        else{
            LoginPacie()
        }
    }

    private fun LoginPacie() {
        progressDialog.setMessage("Iniciando sesión")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                ValidarPaciente()
            }

            .addOnFailureListener {e->
            progressDialog.dismiss()
            Toast.makeText(applicationContext, "No se pude iniciar sesión debido a ${e.message}",
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun ValidarPaciente() {
        val firebaseUser = firebaseAuth.currentUser
        val references = FirebaseDatabase.getInstance().getReference("Usuarios")
        firebaseUser?.let {
            references.child(it.uid)
                .addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val rol = snapshot.child("rol").value
                        if (rol == "paciente") {
                            progressDialog.dismiss()
                            startActivity(
                                Intent(
                                    this@LoginPaciente,
                                    MainActivityPaciente::class.java
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