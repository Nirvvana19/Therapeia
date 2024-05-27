package com.example.therapeia.Paciente

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import com.example.therapeia.MainActivity
import com.example.therapeia.MainActivityPaciente
import com.example.therapeia.databinding.ActivityRegistroPacienteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegistroPaciente : AppCompatActivity() {
    private lateinit var binding: ActivityRegistroPacienteBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroPacienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.IbRegresar.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.BtnRegistrarPaciente.setOnClickListener {
            ValidarInformacion()
        }

    }

    var nombres = ""
    var email = ""
    var password = ""
    var r_password = ""

    private fun ValidarInformacion() {
        nombres = binding.EtNombrePaciente.text.toString().trim()
        email = binding.EtEmailPaciente.text.toString().trim()
        password = binding.EtPasswordPaciente.text.toString().trim()
        r_password = binding.EtRPasswordPaciente.text.toString().trim()

        if (nombres.isEmpty()) {
            binding.EtNombrePaciente.error = "Ingrese nombre"
            binding.EtNombrePaciente.requestFocus()
        } else if (email.isEmpty()) {
            binding.EtEmailPaciente.error = "Ingrese email"
            binding.EtEmailPaciente.requestFocus()
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.EtEmailPaciente.error = "Email no es valido"
            binding.EtEmailPaciente.requestFocus()
        } else if (password.isEmpty()) {
            binding.EtPasswordPaciente.error = "Ingrese contraseña"
            binding.EtPasswordPaciente.requestFocus()
        } else if (password.length < 6) {
            binding.EtPasswordPaciente.error = "La contraseña debe tener mas 6 caracteres"
        } else if (r_password.isEmpty()) {
            binding.EtRPasswordPaciente.error = "Repita su contraseña"
            binding.EtRPasswordPaciente.requestFocus()
        } else if (password != r_password) {
            binding.EtRPasswordPaciente.error = "Las contraseñas no coinciden"
            binding.EtRPasswordPaciente.requestFocus()
        } else {
            CrearCuentaPaciente(email, password)
        }
    }

    private fun CrearCuentaPaciente(email: String, password: String) {
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

        val datos_paciente: HashMap<String, Any?> = HashMap()
        datos_paciente["uid"] = uid
        datos_paciente["nombres"] = nombres
        datos_paciente["email"] = email
        datos_paciente["numero_documento"] = ""
        datos_paciente["info_user"] = ""
        datos_paciente["rol"] = "paciente"
        datos_paciente["tiempo_registro"] = tiempo
        datos_paciente["imagen"] = ""
        datos_paciente["status"] = "offline"
        datos_paciente["search"] = nombres.toLowerCase()

        val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
        reference.child(uid!!)
            .setValue(datos_paciente)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "La cuenta se ha creado con exito", Toast.LENGTH_SHORT)
                    .show()
                startActivity(Intent(this, MainActivityPaciente::class.java))
                finishAffinity()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(applicationContext, "No se pudo guardar la información debido a ${e.message}", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}
