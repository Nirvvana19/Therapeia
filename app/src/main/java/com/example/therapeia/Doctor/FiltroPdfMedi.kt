package com.example.therapeia.Doctor

import android.widget.Filter

class FiltroPdfMedi: Filter {

    var filtroList: ArrayList<ModeloMediPdf>
    var adaptadorPdfDoctorMedi: AdaptadorMediPdf

    constructor(filtroList: ArrayList<ModeloMediPdf>, adaptadorPdfDoctor: AdaptadorMediPdf) {
        this.filtroList = filtroList
        this.adaptadorPdfDoctorMedi = adaptadorPdfDoctor
    }

    override fun performFiltering(medicamento: CharSequence?): FilterResults {
        var medicamento: CharSequence? = medicamento
        val resultados = FilterResults()
        if (medicamento != null && medicamento.isNotEmpty()){
            medicamento = medicamento.toString().lowercase()
            val modeloFiltrado: ArrayList<ModeloMediPdf> = ArrayList()
            for (i in filtroList.indices){
                if (filtroList[i].nombre.lowercase().contains(medicamento)){
                    modeloFiltrado.add(filtroList[i])
                }
            }
            resultados.count = modeloFiltrado.size
            resultados.values = modeloFiltrado
        } else{
            resultados.count = filtroList.size
            resultados.values = filtroList
        }
        return resultados
    }

    override fun publishResults(constraint: CharSequence?, resultados: FilterResults?) {
        adaptadorPdfDoctorMedi.pdfArrayList  = resultados?.values as ArrayList<ModeloMediPdf>
        adaptadorPdfDoctorMedi.notifyDataSetChanged()
    }
}