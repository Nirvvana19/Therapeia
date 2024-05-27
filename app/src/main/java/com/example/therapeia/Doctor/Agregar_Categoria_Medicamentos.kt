package com.example.therapeia.Doctor

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.therapeia.MainActivity
import com.example.therapeia.databinding.ActivityAgregarCategoriaMediBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Agregar_Categoria_Medicamentos: AppCompatActivity() {
    private lateinit var binding: ActivityAgregarCategoriaMediBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var  progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarCategoriaMediBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding.AgregarTipoMediBD.setOnClickListener{
            ValidarDatos()
        }
    }
    private var tipo_medicamento = ""
    private fun ValidarDatos() {
        tipo_medicamento = binding.EtTipoMedi.text.toString().trim()
        if (tipo_medicamento.isEmpty()) {
            Toast.makeText(applicationContext, "Ingrese una categoria", Toast.LENGTH_SHORT).show()
        } else{
            AgregarTipoMedicamento()
        }
    }

    private fun AgregarTipoMedicamento() {
        progressDialog.setMessage("Agregando tipo de Medicamento")
        progressDialog.show()

        val tiempo = System.currentTimeMillis()

        val hashMap = HashMap<String, Any>()
        hashMap["id"]= "$tiempo"
        hashMap["tipo_medi"] = tipo_medicamento
        hashMap["tiempo"] = tiempo
        hashMap["uid"] = "${firebaseAuth.uid}"

        val ref = FirebaseDatabase.getInstance().getReference("tipo_medicamento")
        ref.child("$tiempo")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Se agregó el tipo de medicamento a la BD", Toast.LENGTH_SHORT).show()
                binding.EtTipoMedi.setText("")
                startActivity(Intent(this@Agregar_Categoria_Medicamentos, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se agregó el tipo de medicamento debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}