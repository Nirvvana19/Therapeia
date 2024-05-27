package com.example.therapeia.Doctor

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.therapeia.R
import com.example.therapeia.databinding.ActivityListaPdfDoctorBinding
import com.example.therapeia.databinding.ActivityListaPdfMediDocBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaPdfMediDoc : AppCompatActivity() {
    private lateinit var binding: ActivityListaPdfMediDocBinding
    private var idtipo_medi = ""
    private var nombre_tip = ""
    private lateinit var pdfArrayList: ArrayList<ModeloMediPdf>
    private lateinit var adaptadorPdfDoctor: AdaptadorMediPdf

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaPdfMediDocBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        val intent = intent
        idtipo_medi = intent.getStringExtra("id")!!
        nombre_tip = intent.getStringExtra("nombre")!!

        binding.TxtTipoEnfer.text = nombre_tip

        ListarEnfer()

        binding.EtBuscarMediDoctor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(enfermedad: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    adaptadorPdfDoctor.filter.filter(enfermedad)
                }catch (e:Exception){

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    private fun ListarEnfer() {
        pdfArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("medicamento")
        ref.orderByChild("tipo_medi").equalTo(idtipo_medi)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children){
                        val modelo = ds.getValue(ModeloMediPdf::class.java)
                        if (modelo != null){
                            pdfArrayList.add(modelo)
                        }
                    }
                    adaptadorPdfDoctor = AdaptadorMediPdf(this@ListaPdfMediDoc, pdfArrayList)
                    binding.RvMediDoctor.adapter = adaptadorPdfDoctor
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
}