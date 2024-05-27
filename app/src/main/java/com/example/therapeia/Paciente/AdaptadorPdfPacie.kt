package com.example.therapeia.Paciente


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.example.therapeia.Doctor.DetalleEnfer
import com.example.therapeia.Doctor.EnferFunciones
import com.example.therapeia.Doctor.FiltroPdfEnfer
import com.example.therapeia.Doctor.ModeloPdf
import com.example.therapeia.databinding.ItemEnferDoctorBinding
import com.example.therapeia.databinding.ItemEnferPacienteBinding

class AdaptadorPdfPacie : RecyclerView.Adapter<AdaptadorPdfPacie.HolderPdfPaciente>, Filterable {

    private lateinit var binding: ItemEnferPacienteBinding
    private var m_context : Context
    public var pdfArrayList : ArrayList<ModeloPdf>
    private var filtroEnfermedad: ArrayList<ModeloPdf>
    private var filtro: FiltroPdfEnfer?=null

    constructor(m_context: Context, pdfArrayList: ArrayList<ModeloPdf>) : super() {
        this.m_context = m_context
        this.pdfArrayList = pdfArrayList
        this.filtroEnfermedad = pdfArrayList
    }


    inner  class HolderPdfPaciente(itemView: View): RecyclerView.ViewHolder(itemView) {
        val VisualizadorPDG = binding.VisualizadorPDF
        val progressBar = binding.progressBar
        val Txt_nombre_enfer_item = binding.TxtNombreEnferItem
        val Txt_descripcion_enfer_item = binding.TxtDescripcionEnferItem
        val Txt_tipo_enfer_doctor = binding.TxtTipoEnferDoctor
        val Txt_tamanio_enfer_doctor = binding.TxtTamanioEnferDoctor
        val Txt_fecha_enfer_doctor = binding.TxtFechaEnferDoctor
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfPaciente {
        binding = ItemEnferPacienteBinding.inflate(LayoutInflater.from(m_context), parent, false)
        return HolderPdfPaciente(binding.root)
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    override fun onBindViewHolder(holder: HolderPdfPaciente, position: Int) {
        val modelo = pdfArrayList[position]
        val pdfId = modelo.id
        val tipoEnfer = modelo.tipo_enfer
        val nombreEnfer = modelo.nombre
        val descripcion = modelo.descripcion
        val pdfUrl = modelo.url
        val tiempo = modelo.tiempo

        val formatoTiempo = EnferFunciones.formatoTiempo(tiempo)

        holder.Txt_nombre_enfer_item.text = nombreEnfer
        holder.Txt_descripcion_enfer_item.text = descripcion
        holder.Txt_fecha_enfer_doctor.text = formatoTiempo

        EnferFunciones.CargarTipoEnfer(tipoEnfer,holder.Txt_tipo_enfer_doctor)
        EnferFunciones.CargarPdfUrl(pdfUrl, nombreEnfer, holder.VisualizadorPDG, holder.progressBar, null)
        EnferFunciones.CargarTamanioPdf(pdfUrl, nombreEnfer, holder.Txt_tamanio_enfer_doctor)




        holder.itemView.setOnClickListener {
            val intent = Intent(m_context, DetalleEnfer::class.java)
            intent.putExtra("id_enfer", pdfId)
            m_context.startActivity(intent)
        }
    }

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }


    /*override fun getFilter(): Filter {
        if (filtro == null){
            filtro = FiltroPdfEnfer(filtroEnfermedad, this)
        }
        return filtro as FiltroPdfEnfer
    }*/
}
