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
import com.example.therapeia.databinding.ActivityAgregarMediPdfBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class Agregar_MediPdf : AppCompatActivity() {
    private lateinit var binding: ActivityAgregarMediPdfBinding
    private lateinit var tipoMediArrayList: ArrayList<ModeloTipoMedicamento>
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var tipoMediArratList: ArrayList<ModeloTipoMedicamento>
    private var pdfUri: Uri?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarMediPdfBinding.inflate(layoutInflater)
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

        binding.TvTipoMedi.setOnClickListener{
            SeleccionarTipoMedi()
        }
        binding.BtnSubirMedi.setOnClickListener {
            validarInformacion()
        }
    }

    private var nombre = ""
    private var descripcion = ""
    private var tipoMedi = ""
    private fun validarInformacion() {
        nombre= binding.EtNombreMedi.text.toString().trim()
        descripcion = binding.EtDescripciNMedi.text.toString().trim()
        tipoMedi = binding.TvTipoMedi.text.toString().trim()

        if (nombre.isEmpty()){
            Toast.makeText(this, "Ingrese el nombre del medicamento", Toast.LENGTH_SHORT).show()
        }
        if (descripcion.isEmpty()){
            Toast.makeText(this, "Ingrese una descripción del medicamento", Toast.LENGTH_SHORT).show()
        }
        if (tipoMedi.isEmpty()){
            Toast.makeText(this, "Seleccione el tipo de medicamento", Toast.LENGTH_SHORT).show()
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
        val ruta_enfermedad = "medicamento/$tiempo"
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
        hashMap["tipo_medi"] = id
        hashMap["url"]= UrlPdfSubido
        hashMap["tiempo"] = tiempo

        val  ref = FirebaseDatabase.getInstance().getReference("medicamento")
        ref.child("$tiempo")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "El contenido se ha subido con éxito", Toast.LENGTH_SHORT).show()
                binding.EtNombreMedi.setText("")
                binding.EtDescripciNMedi.setText("")
                binding.TvTipoMedi.setText("")
                pdfUri = null
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this, "Fallo la subida del archivo debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun CargarTipoEnfer() {
        tipoMediArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("tipo_medicamento").orderByChild("tipo_medi")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tipoMediArrayList.clear()
                for (ds in snapshot.children){
                    val modelo = ds.getValue(ModeloTipoMedicamento::class.java)
                    tipoMediArrayList.add(modelo!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

        })
    }

    private var id = ""
    private var tipo_medi =" "
    private fun  SeleccionarTipoMedi(){
        val tipo_mediArray = arrayOfNulls<String>(tipoMediArrayList.size)
        for (i in tipo_mediArray.indices){
            tipo_mediArray[i]= tipoMediArrayList[i].tipo_medi
        }

        val builder= AlertDialog.Builder(this)
        builder.setTitle("Seleccionar tipo de medicamento")
            .setItems(tipo_mediArray){dialog, which->
                id = tipoMediArrayList[which].id
                tipo_medi = tipoMediArrayList[which].tipo_medi
                binding.TvTipoMedi.text = tipo_medi
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
        ActivityResultCallback<ActivityResult> { resultado->
            if (resultado.resultCode == RESULT_OK){
                pdfUri = resultado.data!!.data
            }else{
                Toast.makeText(this, "Cancelado", Toast.LENGTH_SHORT).show()
            }
        }
    )
}