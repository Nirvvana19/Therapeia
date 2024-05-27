package com.example.therapeia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.therapeia.Doctor.Constantes
import com.example.therapeia.databinding.ActivityLeerEnfermedadBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class Leer_enfermedad : AppCompatActivity() {

    private lateinit var  binding: ActivityLeerEnfermedadBinding
    var id_enfer=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeerEnfermedadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id_enfer = intent.getStringExtra("id_enfer")!!

        binding.icRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        cargarInformacionEnfer()
    }

    private fun cargarInformacionEnfer() {
        val  ref = FirebaseDatabase.getInstance().getReference("Enfermedades")
        ref.child(id_enfer)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pdfUrl = snapshot.child("url").value
                    cargarEnferStorage("$pdfUrl")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
    private fun cargarEnferStorage(pdfUrl: String) {
        val reference = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
        reference.getBytes(Constantes.Maximo_bytes_pdf)
            .addOnSuccessListener { bytes->
                //cargar pdf
                binding.VisualizadorPDF.fromBytes(bytes)
                    .swipeHorizontal(false)
                    .onPageChange{pagina, contadorPagina->
                        val paginaActual = pagina+1
                        binding.TxtLeerEnfer.text = "$paginaActual/$contadorPagina"
                    }
                    .load()
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener {e->
                binding.progressBar.visibility =  View.GONE
            }
    }

}