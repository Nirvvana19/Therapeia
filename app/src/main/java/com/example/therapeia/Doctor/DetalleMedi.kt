package com.example.therapeia.Doctor

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.therapeia.LeerMediDoc
import com.example.therapeia.databinding.ActivityDetalleMediBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DetalleMedi : AppCompatActivity() {
    private lateinit var binding: ActivityDetalleMediBinding
    private var id_medi= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetalleMediBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id_medi= intent.getStringExtra("id_medi")!!

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding.BtnLeerMedi.setOnClickListener {
            val intent = Intent(this@DetalleMedi, LeerMediDoc::class.java)
            intent.putExtra("id_medi", id_medi)
            startActivity(intent)
        }

        cargarDetalleMedi()
    }

    private fun cargarDetalleMedi() {
        val ref = FirebaseDatabase.getInstance().getReference("medicamento")
        ref.child(id_medi)
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //obtener informacion del medicamento
                    val tipo_medi = "${snapshot.child("tipo_medi").value}"
                    val nombre = "${snapshot.child("nombre").value}"
                    val descripcion = "${snapshot.child("descripcion").value}"
                    val tiempo = "${snapshot.child("tiempo").value}"
                    val url = "${snapshot.child("url").value}"

                    //cambiamos el formato del tiempo
                    val fecha = EnferFunciones.formatoTiempo(tiempo.toLong())

                    //cargamos los tipos de medicamentos
                    EnferFunciones.CargarTipoMedi(tipo_medi, binding.tipoMediD)

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