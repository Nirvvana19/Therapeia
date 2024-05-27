package com.example.therapeia

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.therapeia.Doctor.Constantes
import com.example.therapeia.databinding.ActivityLeerMediDocBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class LeerMediDoc : AppCompatActivity() {
    private lateinit var  binding: ActivityLeerMediDocBinding
    var id_medi=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLeerMediDocBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id_medi = intent.getStringExtra("id_medi")!!

        binding.icRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }

        cargarInformacionMedi()
    }

    private fun cargarInformacionMedi() {
        val  ref = FirebaseDatabase.getInstance().getReference("medicamento")
        ref.child(id_medi)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pdfUrl = snapshot.child("url").value
                    cargarMediStorage("$pdfUrl")
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
    private fun cargarMediStorage(pdfUrl: String) {
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