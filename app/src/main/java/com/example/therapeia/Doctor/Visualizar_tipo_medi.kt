package com.example.therapeia.Doctor

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.example.therapeia.databinding.FragmentDoctorMedicamentosBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Visualizar_tipo_medi: AppCompatActivity() {
    private lateinit var binding: FragmentDoctorMedicamentosBinding
    private lateinit var tipoMediArrayList : ArrayList<ModeloTipoMedicamento>
    private lateinit var adaptadorTipoMedi : AdaptadorCategoria_medi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentDoctorMedicamentosBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ListarTipoMedi()

        binding.BuscarTipoMedicamen.addTextChangedListener(object : TextWatcher {
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

        binding.BtnAgregarCategoriaMedi.setOnClickListener{
            startActivity(Intent(this@Visualizar_tipo_medi, Agregar_Categoria_Medicamentos::class.java))
        }

        binding.AgregarPdf.setOnClickListener{
            startActivity(Intent(this@Visualizar_tipo_medi, Agregar_MediPdf::class.java))
        }

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
                adaptadorTipoMedi = AdaptadorCategoria_medi(this@Visualizar_tipo_medi, tipoMediArrayList)
                binding.tipoMedicamentosRv.adapter = adaptadorTipoMedi
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }
}