package com.example.therapeia

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.therapeia.Fragmentos_Doctor.FragmentDoctorBiblio
import com.example.therapeia.Fragmentos_Doctor.Fragment_doctor_cuenta
import com.example.therapeia.alarma.MainActivityAlarma
import com.example.therapeia.databinding.ActivityMainBinding
import com.example.therapeia.ui.main.AlarmsListActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAuth = FirebaseAuth.getInstance()
        ComprobarSesion()
        VerBiblioteca()

        binding.BottomNvDoctor.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.Menu_panel->{
                    VerBiblioteca()
                    true
                }
                R.id.Menu_cuenta->{
                    VerFragmentoCuenta()
                    true

                }
                R.id.Menu_alarma->{
                    VerAlarma()
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

    private fun VerAlarma() {
        val intent = Intent(this, AlarmsListActivity::class.java)
        startActivity(intent)
    }


    private fun VerBiblioteca(){
        val nombre_titulo = "Biblioteca"
        binding.TituloRLDoctor.text = nombre_titulo
        val fragment = FragmentDoctorBiblio()
        val fragmentTransaction =supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FramentsDoctor.id, fragment, "Fragment libreria")
        fragmentTransaction.commit()
    }
    private fun VerFragmentoCuenta(){
        val nombre_titulo = "Mi cuenta"
        binding.TituloRLDoctor.text = nombre_titulo

        val fragment = Fragment_doctor_cuenta()
        val fragmentTransaction =supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(binding.FramentsDoctor.id, fragment, "Fragment mi cuenta")
        fragmentTransaction.commit()
    }

    private fun VerActivityChat() {
        val nombre_titulo = "Chat"
        binding.TituloRLDoctor.text = nombre_titulo

        val intent = Intent(this, MainActivityC::class.java)
        startActivity(intent)
    }

    private fun ComprobarSesion(){
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null){
            startActivity(Intent(this, Elegir_rol::class.java))
        }else{
            /*Toast.makeText(applicationContext, "Bienvenidos ${firebaseUser.email}",
                Toast.LENGTH_SHORT).show()*/
        }
    }
}