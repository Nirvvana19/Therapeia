package com.example.therapeia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.therapeia.Doctor.Registrar_doctor
import com.example.therapeia.Paciente.LoginPaciente
import com.example.therapeia.Paciente.RegistroPaciente
import com.example.therapeia.databinding.ActivityElegirRolBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Elegir_rol : AppCompatActivity() {

    private lateinit var binding: ActivityElegirRolBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityElegirRolBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        // Verificar si el usuario está autenticado


        binding.BtnLoginDoctorl.setOnClickListener {
            Toast.makeText(applicationContext, "Rol doctor", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@Elegir_rol, Login_Doctor::class.java))
        }

        binding.BtnLoginPacientel.setOnClickListener {
            Toast.makeText(applicationContext, "Rol paciente", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@Elegir_rol, LoginPaciente::class.java))
        }
    }

}


