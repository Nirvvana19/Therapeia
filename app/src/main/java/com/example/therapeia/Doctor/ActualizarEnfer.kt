package com.example.therapeia.Doctor

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.therapeia.databinding.ActivityActualizarEnferBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ActualizarEnfer : AppCompatActivity() {
    private lateinit var  binding: ActivityActualizarEnferBinding
    private var id_enfer =""
    private lateinit var progressDialog: ProgressDialog
    //nombre
    private lateinit var catNombreArrayList: ArrayList<String>
    //id
    private lateinit var catIdArrayList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActualizarEnferBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id_enfer = intent.getStringExtra("idEnfer")!!
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)

        cargarCategorias()
        cargarInformacion()

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        binding.TvActualizarTipoEnfermedad.setOnClickListener {
            dialogTipoEnfer()
        }
        binding.BtnActualizarEnfermedad.setOnClickListener{
            validarInformacion()
        }


    }

    private fun cargarInformacion() {
        val ref = FirebaseDatabase.getInstance().getReference("Enfermedades")
        ref.child(id_enfer)
            .addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    //Obtener Informacion de la enfermedad seleccionado
                    val nombre = snapshot.child("nombre").value.toString()
                    val descripcion = snapshot.child("descripcion").value.toString()
                    id_seleccionado = snapshot.child("tipo_enfer").value.toString()

                    //seteamos en las vistas
                    binding.EtActualizarNombreEnfermedad.setText(nombre)
                    binding.EtActualizarDescripciNEnfer.setText(descripcion)
                    val refTipoEnfer = FirebaseDatabase.getInstance().getReference("tipo_enfermedad")
                        refTipoEnfer.child(id_seleccionado)
                            .addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    //obtener el tipo de enfermedad
                                    val tipo_enfer = snapshot.child("categoria").value
                                    //seteamos en el text view
                                    binding.TvActualizarTipoEnfermedad.text= tipo_enfer.toString()
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
        nombre = binding.EtActualizarNombreEnfermedad.text.toString().trim()
        descripcion = binding.EtActualizarDescripciNEnfer.text.toString().trim()

        if (nombre.isEmpty()){
            Toast.makeText(this, "Ingrese nombre de la enfermedad", Toast.LENGTH_SHORT ).show()
        }else if (descripcion.isEmpty()){
            Toast.makeText(this, "Ingrese descripción de la enfermedad", Toast.LENGTH_SHORT ).show()
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
        hashMap["tipo_enfer"] = "$id_seleccionado"

        val ref = FirebaseDatabase.getInstance().getReference("Enfermedades")
        ref.child(id_enfer)
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
    private fun dialogTipoEnfer() {
        val tipoEnferArray = arrayOfNulls<String>(catNombreArrayList.size)
        for (i in catNombreArrayList.indices){
            tipoEnferArray[i] = catNombreArrayList[i]
        }

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccione un tipo de enfermedad")
            .setItems(tipoEnferArray) {dialog, posicion->
                id_seleccionado = catIdArrayList[posicion]
                nombre_seleccionado = catNombreArrayList[posicion]

                binding.TvActualizarTipoEnfermedad.text =nombre_seleccionado
            }
            .show()
    }

    private fun cargarCategorias() {
        catNombreArrayList = ArrayList()
        catIdArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("tipo_enfermedad")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                catNombreArrayList.clear()
                catIdArrayList.clear()
                for (ds in snapshot.children){
                    val id = ""+ds.child("id").value
                    val tipo_enfer = ""+ds.child("categoria").value

                    catNombreArrayList.add(tipo_enfer)
                    catIdArrayList.add(id)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}