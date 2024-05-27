package com.example.therapeia.Doctor

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.therapeia.databinding.ItemEnferDoctorBinding
import kotlin.math.log

class AdaptadorPdfDoctor: RecyclerView.Adapter<AdaptadorPdfDoctor.HolderPdfDoctor>, Filterable {

    private lateinit var binding: ItemEnferDoctorBinding
    private var m_context : Context
    public var pdfArrayList : ArrayList<ModeloPdf>
    private var filtroEnfermedad: ArrayList<ModeloPdf>
    private var filtro: FiltroPdfEnfer?=null

    constructor(m_context: Context, pdfArrayList: ArrayList<ModeloPdf>) : super() {
        this.m_context = m_context
        this.pdfArrayList = pdfArrayList
        this.filtroEnfermedad = pdfArrayList
    }


    inner  class HolderPdfDoctor(itemView: View): RecyclerView.ViewHolder(itemView) {
        val VisualizadorPDG = binding.VisualizadorPDF
        val progressBar = binding.progressBar
        val Txt_nombre_enfer_item = binding.TxtNombreEnferItem
        val Txt_descripcion_enfer_item = binding.TxtDescripcionEnferItem
        val Txt_tipo_enfer_doctor = binding.TxtTipoEnferDoctor
        val Txt_tamanio_enfer_doctor = binding.TxtTamanioEnferDoctor
        val Txt_fecha_enfer_doctor = binding.TxtFechaEnferDoctor
        val Ib_mas_opciones = binding.IbMasOpciones
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderPdfDoctor {
        binding = ItemEnferDoctorBinding.inflate(LayoutInflater.from(m_context), parent, false)
        return HolderPdfDoctor(binding.root)
    }

    override fun getItemCount(): Int {
        return pdfArrayList.size
    }

    override fun onBindViewHolder(holder: HolderPdfDoctor, position: Int) {
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


        holder.Ib_mas_opciones.setOnClickListener{
            verOpciones(modelo, holder)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(m_context, DetalleEnfer::class.java)
            intent.putExtra("id_enfer", pdfId)
            m_context.startActivity(intent)
        }
    }

    private fun verOpciones(modelo: ModeloPdf, holder: AdaptadorPdfDoctor.HolderPdfDoctor) {
        val idEnfermedades = modelo.id
        val urlEnfermedad = modelo.url
        val nombre = modelo.nombre

        val  opciones = arrayOf("Actualizar","Eliminar")


        //Alert Dialog
        val builder = AlertDialog.Builder(m_context)
        builder.setTitle("Seleccione una opción")
            .setItems(opciones){dialog, posicion->
                if (posicion == 0){
                    //Actualizar
                    val intent = Intent(m_context, ActualizarEnfer::class.java)
                    intent.putExtra("idEnfer", idEnfermedades)
                    m_context.startActivity(intent)

                } else if(posicion == 1){
                    //Eliminar
                    val opcionesEliminacion = arrayOf("Si", "Cancelar")
                    val builder = AlertDialog.Builder(m_context)
                        builder.setTitle(" ${idEnfermedades}?")
                        .setItems(opcionesEliminacion){dialog, posicion->
                            if (posicion == 0){
                                EnferFunciones.EliminarEnfer(m_context, idEnfermedades, urlEnfermedad, nombre)
                            }else if (posicion == 1){
                                Toast.makeText(m_context, "Se ha cancelado la eliminación", Toast.LENGTH_SHORT).show()
                            }
                        }.show()
                }
            }.show()
    }

    override fun getFilter(): Filter {
        if (filtro == null){
            filtro = FiltroPdfEnfer(filtroEnfermedad, this)
        }
        return filtro as FiltroPdfEnfer
    }

}


