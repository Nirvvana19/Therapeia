package com.example.therapeia.Paciente

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.therapeia.Doctor.AdaptadorCategoria_enfer
import com.example.therapeia.Doctor.ModeloTipoEnfer
import com.example.therapeia.databinding.FragmentDoctorBibliotecaBinding
import com.example.therapeia.databinding.FragmentEnfermedadesBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VisualizarTipoEnferPaci: AppCompatActivity() {
    private lateinit var binding: FragmentEnfermedadesBinding
    private lateinit var tipoEnferArrayList : ArrayList<ModeloTipoEnfer>
    private lateinit var adaptadorTipoEnfer : AdaptadorTipo_enfer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentEnfermedadesBinding.inflate(layoutInflater)
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
                adaptadorTipoEnfer = AdaptadorTipo_enfer(this@VisualizarTipoEnferPaci, tipoEnferArrayList)
                binding.tipoEnfermedadesRv.adapter = adaptadorTipoEnfer
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}