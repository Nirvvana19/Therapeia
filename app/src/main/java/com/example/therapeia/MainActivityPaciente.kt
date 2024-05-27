package com.example.therapeia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.therapeia.Fragmentos_Paciente.FragmentBibliotecaPaciente
import com.example.therapeia.Fragmentos_Paciente.FragmentCuentaPaci
import com.example.therapeia.databinding.ActivityMainPacienteBinding
import com.google.firebase.auth.FirebaseAuth

class MainActivityPaciente : AppCompatActivity() {

    private lateinit var binding: ActivityMainPacienteBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainPacienteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        comprobarSesion()
        VerFragmentoBiblioteca()

        binding.BottomNvPaciente.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.Menu_panel -> {
                    VerFragmentoBiblioteca()
                    true
                }

                R.id.Menu_cuenta -> {
                    VerFragmentoCuenta()
                    true

                }
                R.id.Menu_chat -> {
                    VerActivityChat()
                    true

                }
                else->{
                    false
                }
            }
        }
    }
    private fun VerFragmentoCuenta() {
        val nombre_titulo = "Cuenta"
        binding.TituloRLPaciente.text = nombre_titulo

        val fragment = FragmentCuentaPaci()
        val fragmentTransaction =supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FramentsPaciente.id, fragment, "Fragment cuenta")
        fragmentTransaction.commit()
    }

    private fun VerFragmentoBiblioteca() {
        val nombre_titulo = "Biblioteca"
        binding.TituloRLPaciente.text = nombre_titulo

        val fragment = FragmentBibliotecaPaciente()
        val fragmentTransaction =supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FramentsPaciente.id, fragment, "Fragment libreria")
        fragmentTransaction.commit()
    }


    private fun VerActivityChat() {
        val nombre_titulo = "Chat"
        binding.TituloRLPaciente.text = nombre_titulo

        val intent = Intent(this, Welcome_Activity::class.java)
        startActivity(intent)
    }



    private fun comprobarSesion(){
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            startActivity(Intent(this, Elegir_rol::class.java))
            finishAffinity()
        }else{
            Toast.makeText(applicationContext, "Bienvenido(a) ${firebaseUser.email}", Toast.LENGTH_SHORT).show()
        }
    }
}