package com.example.therapeia.Doctor

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.therapeia.databinding.FragmentDoctorBibliotecaBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Visualizar_tipo_enfer : AppCompatActivity() {
    private lateinit var binding: FragmentDoctorBibliotecaBinding
    private lateinit var tipoEnferArrayList : ArrayList<ModeloTipoEnfer>
    private lateinit var adaptadorTipoEnfer : AdaptadorCategoria_enfer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentDoctorBibliotecaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ListarTipoEnfer()

        binding.BuscarTipoEnfer.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(
                categoria: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                try {
                    adaptadorTipoEnfer.filter.filter(categoria)
                } catch (e: Exception) {

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.BtnAgregarCategoria.setOnClickListener{
            startActivity(Intent(this@Visualizar_tipo_enfer, Agregar_Categoria_Enfermedad::class.java))
        }

        binding.AgregarPdf.setOnClickListener{
            startActivity(Intent(this@Visualizar_tipo_enfer, Agregar_Pdf::class.java))
    }

}

    private fun ListarTipoEnfer() {
        tipoEnferArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("tipo_enfermedad").orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tipoEnferArrayList.clear()
                for (ds in snapshot.children){
                    val modelo = ds.getValue(ModeloTipoEnfer::class.java)
                    tipoEnferArrayList.add(modelo!!)
                }
                adaptadorTipoEnfer = AdaptadorCategoria_enfer(this@Visualizar_tipo_enfer, tipoEnferArrayList)
                binding.tipoEnfermedadesRv.adapter = adaptadorTipoEnfer
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}