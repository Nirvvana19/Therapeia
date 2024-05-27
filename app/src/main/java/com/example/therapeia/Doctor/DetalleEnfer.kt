package com.example.therapeia.Doctor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.therapeia.Leer_enfermedad
import com.example.therapeia.databinding.ActivityDetalleEnferBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetalleEnfer : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleEnferBinding
    private var id_enfer= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleEnferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id_enfer= intent.getStringExtra("id_enfer")!!

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding.BtnLeerEnfermedad.setOnClickListener {
            val intent = Intent(this@DetalleEnfer, Leer_enfermedad::class.java)
            intent.putExtra("id_enfer", id_enfer)
            startActivity(intent)
        }

        cargarDetalleEnfer()
    }

    private fun cargarDetalleEnfer() {
        val ref = FirebaseDatabase.getInstance().getReference("Enfermedades")
        ref.child(id_enfer)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //obtener informacion de la enfermedad
                    val tipo_enfer = "${snapshot.child("tipo_enfer").value}"
                    val nombre = "${snapshot.child("nombre").value}"
                    val descripcion = "${snapshot.child("descripcion").value}"
                    val tiempo = "${snapshot.child("tiempo").value}"
                    val url = "${snapshot.child("url").value}"

                    //cambiamos el formato del tiempo
                    val fecha = EnferFunciones.formatoTiempo(tiempo.toLong())

                    //cargamos los tipos de enfermedades
                    EnferFunciones.CargarTipoEnfer(tipo_enfer, binding.tipoEnferD)

                    //cargamos la miniatura del pdf
                    EnferFunciones.CargarPdfUrl("$url", "$nombre", binding.VisualizadorPDF, binding.progressBar, binding.hojas)

                    //cargar tamaño
                    EnferFunciones.CargarTamanioPdf("$url", "$nombre", binding.tamanio)

                    //Seteamos información restante
                    binding.nombreEnferD.text = nombre
                    binding.descripcion.text = descripcion
                    binding.fecha.text = fecha
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}