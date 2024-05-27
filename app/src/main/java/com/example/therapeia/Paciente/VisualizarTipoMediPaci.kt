package com.example.therapeia.Paciente

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.therapeia.Doctor.AdaptadorCategoria_medi
import com.example.therapeia.Doctor.ModeloTipoEnfer
import com.example.therapeia.Doctor.ModeloTipoMedicamento
import com.example.therapeia.databinding.ActivityTipoMediBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class VisualizarTipoMediPaci: AppCompatActivity() {
    private lateinit var binding: ActivityTipoMediBinding
    private lateinit var tipoMediArrayList : ArrayList<ModeloTipoMedicamento>
    private lateinit var adaptadorTipoMedi : AdaptadorTipoMedi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTipoMediBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ListarTipoMedi()

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
                    adaptadorTipoMedi.filter.filter(categoria)
                } catch (e: Exception) {

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

    }

    private fun ListarTipoMedi() {
        tipoMediArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("tipo_medicamento").orderByChild("tipo_medi")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                tipoMediArrayList.clear()
                for (ds in snapshot.children){
                    val modelo = ds.getValue(ModeloTipoMedicamento::class.java)
                    tipoMediArrayList.add(modelo!!)
                }
                adaptadorTipoMedi = AdaptadorTipoMedi(this@VisualizarTipoMediPaci, tipoMediArrayList)
                binding.tipoEnfermedadesRv.adapter = adaptadorTipoMedi
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}