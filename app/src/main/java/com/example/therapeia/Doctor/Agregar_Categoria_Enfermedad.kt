package com.example.therapeia.Doctor

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.therapeia.MainActivity
import com.example.therapeia.R
import com.example.therapeia.databinding.ActivityAgregarCategoriaEnfermedadBinding
import com.example.therapeia.databinding.FragmentDoctorBibliotecaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Agregar_Categoria_Enfermedad : AppCompatActivity() {

    private lateinit var binding: ActivityAgregarCategoriaEnfermedadBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var  progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarCategoriaEnfermedadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding.AgregarTipoEnfermedadBD.setOnClickListener{
            ValidarDatos()
        }
    }
    private var tipo_enfermedad = ""
    private fun ValidarDatos() {
        tipo_enfermedad = binding.EtTipoEnfermedad.text.toString().trim()
        if (tipo_enfermedad.isEmpty()) {
            Toast.makeText(applicationContext, "Ingrese una categoria", Toast.LENGTH_SHORT).show()
        } else{
            AgregarTipoEnfermedad()
        }
        }

    private fun AgregarTipoEnfermedad() {
        progressDialog.setMessage("Agregando tipo de enfermedad")
        progressDialog.show()

        val tiempo = System.currentTimeMillis()

        val hashMap = HashMap<String, Any>()
        hashMap["id"]= "$tiempo"
        hashMap["categoria"] = tipo_enfermedad
        hashMap["tiempo"] = tiempo
        hashMap["uid"] = "${firebaseAuth.uid}"

        val ref = FirebaseDatabase.getInstance().getReference("tipo_enfermedad")
        ref.child("$tiempo")
            .setValue(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "Se agregó el tipo de enfermedad a la BD", Toast.LENGTH_SHORT).show()
                binding.EtTipoEnfermedad.setText("")
                startActivity(Intent(this@Agregar_Categoria_Enfermedad, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se agregó el tipo de enfermedad debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

}
