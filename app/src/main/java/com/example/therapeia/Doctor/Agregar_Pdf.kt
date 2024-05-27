package com.example.therapeia.Doctor

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import com.example.therapeia.databinding.ActivityAgregarPdfBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class Agregar_Pdf : AppCompatActivity() {
    private lateinit var binding: ActivityAgregarPdfBinding
    private lateinit var tipoEnferArrayList: ArrayList<ModeloTipoEnfer>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var tipoEnferArratList: ArrayList<ModeloTipoEnfer>
    private var pdfUri: Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        CargarTipoEnfer()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)


        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding.AdjuntarPdfIb.setOnClickListener{
            ElegirPdf()
        }

        binding.TvTipoEnfermedad.setOnClickListener{
            SeleccionarTipoEnfer()
        }
        binding.BtnSubirEnfermedad.setOnClickListener {
            validarInformacion()
        }
    }

    private var nombre = ""
    private var descripcion = ""
    private var tipoEnfer = ""
    private fun validarInformacion() {
        nombre= binding.EtNombreEnfermedad.text.toString().trim()
        descripcion = binding.EtDescripciNEnfer.text.toString().trim()
        tipoEnfer = binding.TvTipoEnfermedad.text.toString().trim()

        if (nombre.isEmpty()){
            Toast.makeText(this, "Ingrese el nombre de la enfermedad", Toast.LENGTH_SHORT).show()
        }
        if (descripcion.isEmpty()){
            Toast.makeText(this, "Ingrese una descripción de la enfermedad", Toast.LENGTH_SHORT).show()
        }
        if (tipoEnfer.isEmpty()){
            Toast.makeText(this, "Seleccione el tipo de enfermedad", Toast.LENGTH_SHORT).show()
        }else if(pdfUri == null){
            Toast.makeText(this, "Adjunte un pdf", Toast.LENGTH_SHORT).show()
        }
        else{
            subirPdfStore()
        }
    }

    private fun subirPdfStore() {
        progressDialog.setMessage("subiendo pdf")
        progressDialog.show()

        val tiempo= System.currentTimeMillis()
        val ruta_enfermedad = "enfermedad/$tiempo"
        val storageReference = FirebaseStorage.getInstance().getReference(ruta_enfermedad)
        storageReference.putFile(pdfUri!!)

            .addOnSuccessListener {tarea->
                val uriTask: Task<Uri> = tarea.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val UrlPdfSubido = "${uriTask.result}"
                subirPdfBD(UrlPdfSubido, tiempo)

            }
            .addOnFailureListener {e->
                Toast.makeText(this, "Fallo la subida del archivo debido a ${e.message}", Toast.LENGTH_SHORT).show()

            }
    }

    private fun subirPdfBD(UrlPdfSubido: String, tiempo: Long){
        progressDialog.setMessage("subiendo pdf a la BD")
        val uid = firebaseAuth.uid

        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["uid"] = "$uid"
        hashMap["id"] = "$tiempo"
        hashMap["nombre"] = nombre
        hashMap["descripcion"] = descripcion
        hashMap["tipo_enfer"] = id
        hashMap["url"]= UrlPdfSubido
        hashMap["tiempo"] = tiempo

        val  ref = FirebaseDatabase.getInstance().getReference("Enfermedades")
        ref.child("$tiempo")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "El contenido se ha subido con éxito", Toast.LENGTH_SHORT).show()
                binding.EtNombreEnfermedad.setText("")
                binding.EtDescripciNEnfer.setText("")
                binding.TvTipoEnfermedad.setText("")
                pdfUri = null
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this, "Fallo la subida del archivo debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun CargarTipoEnfer() {
        tipoEnferArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("tipo_enfermedad").orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                tipoEnferArrayList.clear()
                for (ds in snapshot.children){
                    val modelo = ds.getValue(ModeloTipoEnfer::class.java)
                    tipoEnferArrayList.add(modelo!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private var id = ""
    private var categoria =" "
    private fun  SeleccionarTipoEnfer(){
        val tipo_enferArray = arrayOfNulls<String>(tipoEnferArrayList.size)
        for (i in tipo_enferArray.indices){
            tipo_enferArray[i]= tipoEnferArrayList[i].categoria
        }

        val builder= AlertDialog.Builder(this)
        builder.setTitle("Seleccionar tipo de enfermedad")
            .setItems(tipo_enferArray){dialog, which->
                id = tipoEnferArrayList[which].id
                categoria = tipoEnferArrayList[which].categoria
                binding.TvTipoEnfermedad.text = categoria
            }
            .show()
    }

    private fun ElegirPdf(){
        val intent = Intent()
        intent.type= "application/pdf"
        intent.action = Intent.ACTION_GET_CONTENT
        pdfActivity.launch(intent)
    }

    val pdfActivity = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback<ActivityResult> {resultado->
            if (resultado.resultCode == RESULT_OK){
                pdfUri = resultado.data!!.data
            }else{
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
            }
        }
    )
}