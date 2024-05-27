package com.example.therapeia.Doctor

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.therapeia.R
import com.example.therapeia.databinding.ActivityActualizarEnferBinding
import com.example.therapeia.databinding.ActivityActualizarMediBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ActualizarMedi : AppCompatActivity() {
    private lateinit var  binding: ActivityActualizarMediBinding
    private var id_medi =""
    private lateinit var progressDialog: ProgressDialog
    //nombre
    private lateinit var catNombreArrayList: ArrayList<String>
    //id
    private lateinit var catIdArrayList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizarMediBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id_medi = intent.getStringExtra("idMedi")!!
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        cargarCategorias()
        cargarInformacion()

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding.TvActualizarTipoMedi.setOnClickListener {
            dialogTipoMedi()
        }
        binding.BtnActualizarMedi.setOnClickListener{
            validarInformacion()
        }


    }

    private fun cargarInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("medicamento")
        ref.child(id_medi)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Obtener Informacion de la enfermedad seleccionado
                    val nombre = snapshot.child("nombre").value.toString()
                    val descripcion = snapshot.child("descripcion").value.toString()
                    id_seleccionado = snapshot.child("tipo_medi").value.toString()

                    //seteamos en las vistas
                    binding.EtActualizarNombreMedi.setText(nombre)
                    binding.EtActualizarDescripciNMedi.setText(descripcion)
                    val refTipoMedi = FirebaseDatabase.getInstance().getReference("tipo_medicamento")
                    refTipoMedi.child(id_seleccionado)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                //obtener el tipo de enfermedad
                                val tipo_medi = snapshot.child("tipo_medi").value
                                //seteamos en el text view
                                binding.TvActualizarTipoMedi.text= tipo_medi.toString()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }

    private var nombre = ""
    private var descripcion = ""
    private fun validarInformacion() {
        //obtener los datos ingresados
        nombre = binding.EtActualizarNombreMedi.text.toString().trim()
        descripcion = binding.EtActualizarDescripciNMedi.text.toString().trim()

        if (nombre.isEmpty()){
            Toast.makeText(this, "Ingrese nombre de la medicamento", Toast.LENGTH_SHORT ).show()
        }else if (descripcion.isEmpty()){
            Toast.makeText(this, "Ingrese descripción de la medicamento", Toast.LENGTH_SHORT ).show()
        }else if (id_seleccionado.isEmpty()){
            Toast.makeText(this, "Seleccione una categoria", Toast.LENGTH_SHORT ).show()
        }else{
            actualizarInformacion()
        }
    }

    private fun actualizarInformacion() {
        progressDialog.setMessage("Actualizar información")
        progressDialog.show()
        val hashMap = HashMap<String, Any>()
        hashMap["nombre"] = "$nombre"
        hashMap["descripcion"] = "$descripcion"
        hashMap["tipo_medi"] = "$id_seleccionado"

        val ref = FirebaseDatabase.getInstance().getReference("medicamento")
        ref.child(id_medi)
            .updateChildren(hashMap)
            .addOnSuccessListener {
                progressDialog.dismiss()
                Toast.makeText(this, "La actualización fue exitosa", Toast.LENGTH_SHORT ).show()
            }
            .addOnFailureListener {e->
                progressDialog.dismiss()
                Toast.makeText(this, "La actualización falló debido a ${e.message}", Toast.LENGTH_SHORT ).show()
            }
    }

    private var id_seleccionado = ""
    private var nombre_seleccionado = ""
    private fun dialogTipoMedi() {
        val tipoMediArray = arrayOfNulls<String>(catNombreArrayList.size)
        for (i in catNombreArrayList.indices){
            tipoMediArray[i] = catNombreArrayList[i]
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccione un tipo de medicamento")
            .setItems(tipoMediArray) {dialog, posicion->
                id_seleccionado = catIdArrayList[posicion]
                nombre_seleccionado = catNombreArrayList[posicion]

                binding.TvActualizarTipoMedi.text =nombre_seleccionado
            }
            .show()
    }

    private fun cargarCategorias() {
        catNombreArrayList = ArrayList()
        catIdArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("tipo_medicamento")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                catNombreArrayList.clear()
                catIdArrayList.clear()
                for (ds in snapshot.children){
                    val id = ""+ds.child("id").value
                    val tipo_medi = ""+ds.child("tipo_medi").value

                    catNombreArrayList.add(tipo_medi)
                    catIdArrayList.add(id)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}