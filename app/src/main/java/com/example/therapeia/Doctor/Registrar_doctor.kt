package com.example.therapeia.Doctor

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.therapeia.MainActivity
import com.example.therapeia.databinding.ActivityRegistrarDoctorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class Registrar_doctor : AppCompatActivity() {

    private lateinit var binding: ActivityRegistrarDoctorBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrarDoctorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnRegistrarDoctor.setOnClickListener {
            ValidarInformacion()
        }

    }

    var nombres = ""
    var email = ""
    var password = ""
    var r_password = ""
    var num_documento =""

    private fun ValidarInformacion() {
        nombres = binding.EtNombreDoctor.text.toString().trim()
        email = binding.EtEmailDoctor.text.toString().trim()
        num_documento = binding.EtDocumentoDoctor.text.toString().trim()
        password = binding.EtPasswordDoctor.text.toString().trim()
        r_password = binding.EtRPasswordDoctor.text.toString().trim()

        if (nombres.isEmpty()) {
            binding.EtNombreDoctor.error = "Ingrese nombre"
            binding.EtNombreDoctor.requestFocus()
        } else if (email.isEmpty()) {
            binding.EtEmailDoctor.error = "Ingrese email"
            binding.EtEmailDoctor.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.EtEmailDoctor.error = "Email no es valido"
            binding.EtEmailDoctor.requestFocus()
        } else if (num_documento.isEmpty()){
            binding.EtDocumentoDoctor.error ="Ingrese el numero de documento"
            binding.EtDocumentoDoctor.requestFocus()
        } else if (password.isEmpty()) {
            binding.EtPasswordDoctor.error = "Ingrese contraseña"
            binding.EtPasswordDoctor.requestFocus()
        } else if (password.length < 6) {
            binding.EtPasswordDoctor.error = "La contraseña debe tener mas 6 caracteres"
        } else if (r_password.isEmpty()) {
            binding.EtRPasswordDoctor.error = "Repita su contraseña"
            binding.EtRPasswordDoctor.requestFocus()
        } else if (password != r_password) {
            binding.EtRPasswordDoctor.error = "Las contraseñas no coinciden"
            binding.EtRPasswordDoctor.requestFocus()
        } else {
            CrearCuentaDoctor(email, password)
        }
    }

    private fun CrearCuentaDoctor(email: String, password: String) {
        progressDialog.setMessage("Creando cuenta")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                AgregarInfoBD()
            }
            .addOnFailureListener { e ->
                progressDialog.dismiss()
                Toast.makeText(
                    applicationContext,
                    "Ha fallado la creación de la cuenta debido a ${e.message}",
                    Toast.LENGTH_SHORT
                )
            }
    }

    private fun AgregarInfoBD() {
        progressDialog.setMessage("Guardando información...")
        val tiempo = System.currentTimeMillis()
        val uid = firebaseAuth.uid

        val datos_doctor: HashMap<String, Any?> = HashMap()
        datos_doctor["uid"] = uid
        datos_doctor["nombres"] = nombres
        datos_doctor["email"] = email
        datos_doctor["numero_documento"] = num_documento
        datos_doctor["info_user"] = ""
        datos_doctor["rol"] = "doctor"
        datos_doctor["tiempo_registro"] = tiempo
        datos_doctor["imagen"] = "https://firebasestorage.googleapis.com/v0/b/therapeia-bd8d4.appspot.com/o/download.png?alt=media&token=977e94bb-4235-448c-b6b7-f97e6c46b9e9"
        datos_doctor["status"] = "offline"
        datos_doctor["search"] = nombres.toLowerCase()

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uid!!)
            .setValue(datos_doctor)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "La cuenta se ha creado con exito", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se pudo guardar la información debido a ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }

    }


