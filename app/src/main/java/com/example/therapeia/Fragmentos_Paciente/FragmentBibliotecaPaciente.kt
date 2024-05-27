package com.example.therapeia.Fragmentos_Paciente

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.therapeia.Doctor.Visualizar_tipo_enfer
import com.example.therapeia.Doctor.Visualizar_tipo_medi
import com.example.therapeia.Paciente.VisualizarTipoEnferPaci
import com.example.therapeia.Paciente.VisualizarTipoMediPaci
import com.example.therapeia.databinding.FragmentBibliotecaPacienteBinding


class FragmentBibliotecaPaciente : Fragment() {
    private lateinit var binding: FragmentBibliotecaPacienteBinding
    private lateinit var mContext : Context

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBibliotecaPacienteBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.BtnEnfer.setOnClickListener {
            startActivity(Intent(mContext, VisualizarTipoEnferPaci::class.java))
        }

        binding.BtnMedi.setOnClickListener {
            startActivity(Intent(mContext, VisualizarTipoMediPaci::class.java))
        }

    }

}