package com.example.therapeia.Doctor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.therapeia.Paciente.RegistroPaciente
import com.example.therapeia.R
import com.example.therapeia.databinding.ActivityRegistrarRolBinding

class RegistrarRol : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarRolBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarRolBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.BtnRolDoctor.setOnClickListener {
            Toast.makeText(applicationContext, "Rol doctor", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@RegistrarRol, Registrar_doctor::class.java))
        }

        binding.BtnRolPaciente.setOnClickListener {
            Toast.makeText(applicationContext, "Rol paciente", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@RegistrarRol, RegistroPaciente::class.java))
        }
    }
}