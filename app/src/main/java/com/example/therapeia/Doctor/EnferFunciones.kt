package com.example.therapeia.Doctor

import android.app.Application
import android.app.ProgressDialog
import android.content.Context
import android.text.format.DateFormat
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.github.barteksc.pdfviewer.PDFView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.util.Calendar
import java.util.Locale

class EnferFunciones : Application(){
    override fun onCreate() {
        super.onCreate()
    }
    companion object{
        fun formatoTiempo (tiempo: Long): String{
            val cal = Calendar.getInstance(Locale.ENGLISH)
            cal.timeInMillis = tiempo
            //dd/mm/yyyy
            return DateFormat.format("dd/mm/yyyy", cal).toString()
        }

        fun CargarTamanioPdf(pdfUrl: String, pdfNombre: String, tamanio: TextView){
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.metadata
                .addOnSuccessListener {metadata->
                    val bytes = metadata.sizeBytes.toDouble()

                    val  KB = bytes/1024
                    val  MB = KB/1024

                    if (MB>1){
                        tamanio.text = "${String.format("%.2f", MB)} MB"
                    }
                    else if (KB>=1){
                        tamanio.text = "${String.format("%.2f", KB)} KB"
                    }
                    else{
                        tamanio.text = "${String.format("%.2f", bytes)} Bytes"
                    }
                }
                .addOnFailureListener {e->

                }
        }

        fun CargarPdfUrl(pdfUrl: String, pdfNombre: String, pdfView: PDFView, progressBar: ProgressBar,
                         paginaTv: TextView?){
            val ref = FirebaseStorage.getInstance().getReferenceFromUrl(pdfUrl)
            ref.getBytes(Constantes.Maximo_bytes_pdf)
                .addOnSuccessListener {bytes->
                    pdfView.fromBytes(bytes)
                        .pages(0)
                        .spacing(0)
                        .swipeHorizontal(false)
                        .enableSwipe(false)
                        .onError{t->
                            progressBar.visibility = View.INVISIBLE
                        }
                        .onPageError { page, pageCount->
                            progressBar.visibility = View.INVISIBLE
                        }
                        .onLoad{Pagina->
                            progressBar.visibility = View.INVISIBLE
                            if (paginaTv != null){
                                paginaTv.text= "$Pagina"
                            }
                        }
                        .load()
                }
                .addOnSuccessListener {

                }
        }

        fun  CargarTipoEnfer(tipoEnferId : String, tipoEnferTv: TextView){
            val ref = FirebaseDatabase.getInstance().getReference("tipo_enfermedad")
            ref.child(tipoEnferId)
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val tipo_enfer = "${snapshot.child("categoria").value}"
                        tipoEnferTv.text= tipo_enfer
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }

        fun  CargarTipoMedi(tipoMediId : String, tipoMediTv: TextView){
            val ref = FirebaseDatabase.getInstance().getReference("tipo_medicamento")
            ref.child(tipoMediId)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val tipo_medi = "${snapshot.child("tipo_medi").value}"
                        tipoMediTv.text= tipo_medi
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }

        fun EliminarEnfer(context: Context, idEnfer: String, urlEnfer: String, nombreEnfer: String){
            val progressDialog= ProgressDialog(context)
            progressDialog.setTitle("Espere por favor")
            progressDialog.setMessage("Eliminando $nombreEnfer")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(urlEnfer)
            storageReference.delete()
                .addOnSuccessListener {
                    val ref = FirebaseDatabase.getInstance().getReference("Enfermedades")
                    ref.child(idEnfer)
                        .removeValue()
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(context, "La enfermedad se ha eliminado correctamente", Toast.LENGTH_SHORT).show()

                        }
                        .addOnFailureListener {e->
                            progressDialog.dismiss()
                            Toast.makeText(context, "Falló la eliminacion debido a ${e.message}", Toast.LENGTH_SHORT).show()

                        }
                }
                .addOnFailureListener {e->
                    progressDialog.dismiss()
                    Toast.makeText(context, "Falló la eliminacion debido a ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }

        fun EliminarMedi(context: Context, idMedi: String, urlMedi: String, nombreMedi: String){
            val progressDialog= ProgressDialog(context)
            progressDialog.setTitle("Espere por favor")
            progressDialog.setMessage("Eliminando $nombreMedi")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()

            val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(urlMedi)
            storageReference.delete()
                .addOnSuccessListener {
                    val ref = FirebaseDatabase.getInstance().getReference("medicamento")
                    ref.child(idMedi)
                        .removeValue()
                        .addOnSuccessListener {
                            progressDialog.dismiss()
                            Toast.makeText(context, "La enfermedad se ha eliminado correctamente", Toast.LENGTH_SHORT).show()

                        }
                        .addOnFailureListener {e->
                            progressDialog.dismiss()
                            Toast.makeText(context, "Falló la eliminacion debido a ${e.message}", Toast.LENGTH_SHORT).show()

                        }
                }
                .addOnFailureListener {e->
                    progressDialog.dismiss()
                    Toast.makeText(context, "Falló la eliminacion debido a ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}