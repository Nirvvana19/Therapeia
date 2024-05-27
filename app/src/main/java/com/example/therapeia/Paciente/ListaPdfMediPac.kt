package com.example.therapeia.Paciente

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.therapeia.Doctor.ModeloMediPdf
import com.example.therapeia.Doctor.ModeloPdf
import com.example.therapeia.R
import com.example.therapeia.databinding.ActivityListaPdfMediPacBinding
import com.example.therapeia.databinding.ActivityListaPdfPacienteBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaPdfMediPac : AppCompatActivity() {
    private lateinit var binding: ActivityListaPdfMediPacBinding
    private var idtipo_medi = ""
    private var nombre_tip = ""
    private lateinit var pdfArrayList: ArrayList<ModeloMediPdf>
    private lateinit var adaptadorPdfPacie: AdaptadorPdfMediPaci

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaPdfMediPacBinding.inflate(layoutInflater)
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
                    adaptadorPdfPacie.filter.filter(enfermedad)
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
                    adaptadorPdfPacie = AdaptadorPdfMediPaci(this@ListaPdfMediPac, pdfArrayList)
                    binding.RvMediDoctor.adapter = adaptadorPdfPacie
                }

                override fun onCancelled(error: DatabaseError) {
                    
                }

            })
    }
    }
