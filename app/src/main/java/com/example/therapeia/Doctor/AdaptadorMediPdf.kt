package com.example.therapeia.Doctor

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.therapeia.databinding.ItemMediDoctorBinding

class AdaptadorMediPdf: RecyclerView.Adapter<AdaptadorMediPdf.HolderMediPdf>, Filterable {

    private lateinit var binding: ItemMediDoctorBinding
    private var m_context : Context
    public var pdfArrayList : ArrayList<ModeloMediPdf>
    private var filtroMedi: ArrayList<ModeloMediPdf>
    private var filtro: FiltroPdfMedi?=null

    constructor(m_context: Context, pdfArrayList: ArrayList<ModeloMediPdf>) : super() {
        this.m_context = m_context
        this.pdfArrayList = pdfArrayList
        this.filtroMedi = pdfArrayList
    }

    inner class HolderMediPdf(itemView: View): RecyclerView.ViewHolder(itemView){
        val VisualizadorPDG = binding.VisualizadorPDF
        val progressBar = binding.progressBar
        val Txt_nombre_medi_item = binding.TxtNombreMedi
        val Txt_descripcion_medi_item = binding.TxtDescripcionMediItem
        val Txt_tipo_medi_doctor = binding.TxtTipoMedi
        val Txt_tamanio_medi_doctor = binding.TxtTamanioMediDoctor
        val Txt_fecha_medi_doctor = binding.TxtFechaMediDoctor
        val Ib_mas_opciones = binding.IbMasOpciones
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderMediPdf {
        binding = ItemMediDoctorBinding.inflate(LayoutInflater.from(m_context), parent, false)
        return HolderMediPdf(binding.root)
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    override fun onBindViewHolder(holder: HolderMediPdf, position: Int) {
        val modelo = pdfArrayList[position]
        val pdfId = modelo.id
        val tipoMedi = modelo.tipo_medi
        val nombreEnfer = modelo.nombre
        val descripcion = modelo.descripcion
        val pdfUrl = modelo.url
        val tiempo = modelo.tiempo

        val formatoTiempo = EnferFunciones.formatoTiempo(tiempo)

        holder.Txt_nombre_medi_item.text = nombreEnfer
        holder.Txt_descripcion_medi_item.text = descripcion
        holder.Txt_fecha_medi_doctor.text = formatoTiempo

        EnferFunciones.CargarTipoMedi(tipoMedi,holder.Txt_tipo_medi_doctor)
        EnferFunciones.CargarPdfUrl(pdfUrl, nombreEnfer, holder.VisualizadorPDG, holder.progressBar, null)
        EnferFunciones.CargarTamanioPdf(pdfUrl, nombreEnfer, holder.Txt_tamanio_medi_doctor)

        holder.Ib_mas_opciones.setOnClickListener{
            verOpciones(modelo, holder)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(m_context, DetalleMedi::class.java)
            intent.putExtra("id_medi", pdfId)
            m_context.startActivity(intent)
        }
    }

    private fun verOpciones(modelo: ModeloMediPdf, holder: AdaptadorMediPdf.HolderMediPdf) {
        val idMedicamentos = modelo.id
        val urlMedicamentos = modelo.url
        val nombre = modelo.nombre

        val  opciones = arrayOf("Actualizar","Eliminar")

        //Alert Dialog
        val  builder = AlertDialog.Builder(m_context)
        builder.setTitle("Seleccione una opción")
            .setItems(opciones){dialog, posicion->
                if (posicion == 0){
                    //Actualizar
                    val intent = Intent(m_context, ActualizarMedi::class.java)
                    intent.putExtra("idMedi", idMedicamentos)
                    m_context.startActivity(intent)
                } else if(posicion== 1){
                    //Eliminar
                    val opcionesEliminacion = arrayOf("Si", "Cancelar")
                    val builder = AlertDialog.Builder(m_context)
                    builder.setTitle(" ${idMedicamentos}?")
                        .setItems(opcionesEliminacion){dialog, posicion->
                            if (posicion == 0){
                                EnferFunciones.EliminarMedi(m_context, idMedicamentos, urlMedicamentos, nombre)
                            }else if (posicion == 1){
                                Toast.makeText(m_context, "Se ha cancelado la eliminación", Toast.LENGTH_SHORT).show()
                            }
                        }.show()
                }
            }.show()
    }

    override fun getFilter(): Filter {
        if (filtro == null){
            filtro = FiltroPdfMedi(filtroMedi, this)
        }
        return filtro as FiltroPdfMedi
    }
}