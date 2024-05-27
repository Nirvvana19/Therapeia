package com.example.therapeia.Paciente

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import com.example.therapeia.Doctor.AdaptadorPdfDoctor
import com.example.therapeia.Doctor.ModeloPdf
import com.example.therapeia.databinding.ActivityListaPdfDoctorBinding
import com.example.therapeia.databinding.ActivityListaPdfPacienteBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ListaPdfPaciente : AppCompatActivity() {
    private lateinit var binding: ActivityListaPdfPacienteBinding
    private var idtipo_enfer = ""
    private var nombre_tip = ""
    private lateinit var pdfArrayList: ArrayList<ModeloPdf>
    private lateinit var adaptadorPdfPacie: AdaptadorPdfPacie
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListaPdfPacienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.IbRegresar.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
        val intent = intent
        idtipo_enfer = intent.getStringExtra("id")!!
        nombre_tip = intent.getStringExtra("nombre")!!

        binding.TxtTipoEnfer.text = nombre_tip


        ListarEnfer()

        binding.EtBuscarEnferPaciente.addTextChangedListener(object : TextWatcher {
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

        val ref = FirebaseDatabase.getInstance().getReference("Enfermedades")
        ref.orderByChild("tipo_enfer").equalTo(idtipo_enfer)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    pdfArrayList.clear()
                    for (ds in snapshot.children){
                        val modelo = ds.getValue(ModeloPdf::class.java)
                        if (modelo != null){
                            pdfArrayList.add(modelo)
                        }
                    }
                    adaptadorPdfPacie = AdaptadorPdfPacie(this@ListaPdfPaciente, pdfArrayList)
                    binding.RvEnferPaciente.adapter = adaptadorPdfPacie
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }

            })
    }
    }
