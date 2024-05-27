package com.example.therapeia.Fragmentos_Doctor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.therapeia.Doctor.EditarPerfil
import com.example.therapeia.Doctor.EnferFunciones
import com.example.therapeia.Doctor.RegistrarRol
import com.example.therapeia.Elegir_rol
import com.example.therapeia.R
import com.example.therapeia.databinding.ActivityRegistrarDoctorBinding
import com.example.therapeia.databinding.FragmentDoctorCuentaBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Fragment_doctor_cuenta : Fragment() {

    private lateinit var binding: FragmentDoctorCuentaBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mcontext: Context
    override fun onAttach(context: Context) {
        mcontext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding= FragmentDoctorCuentaBinding.inflate(layoutInflater, container, false)
       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        firebaseAuth= FirebaseAuth.getInstance()

        cargarInformacion()

        binding.EditarPerfilDoctor.setOnClickListener {
            startActivity(Intent(mcontext, EditarPerfil::class.java))
        }

        binding.CerrarSesionDoctor.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(context, Elegir_rol::class.java))
            activity?.finishAffinity()
        }

        binding.AgregarUser.setOnClickListener {
            startActivity(Intent(mcontext, RegistrarRol::class.java))
        }
    }

    private fun cargarInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("Usuarios")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Obtener los datos del usuario actual
                    val nombre = "${snapshot.child("nombres").value}"
                    val email = "${snapshot.child("email").value}"
                    var t_registro = "${snapshot.child("tiempo_registro").value}"
                    val rol = "${snapshot.child("rol").value}"
                    if(t_registro== null){
                        t_registro = "0"
                    }
                    //convertir fecha
                    val formato_fecha = EnferFunciones.formatoTiempo(t_registro.toLong())

                    //Setear la informacion
                    binding.TxtNombresDoctor.text = nombre
                    binding.TxtEmailDoctor.text = email
                    binding.TxtTiempoRegistroDoctor.text = formato_fecha
                    binding.TxtRolDoctor.text = rol
                }


                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}
