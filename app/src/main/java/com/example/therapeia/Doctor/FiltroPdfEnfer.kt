package com.example.therapeia.Doctor

import android.widget.Filter

class FiltroPdfEnfer: Filter {

    var filtroList: ArrayList<ModeloPdf>
    var adaptadorPdfDoctor: AdaptadorPdfDoctor

    constructor(filtroList: ArrayList<ModeloPdf>, adaptadorPdfDoctor: AdaptadorPdfDoctor) {
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                         this.filtroList = filtroList
        this.adaptadorPdfDoctor = adaptadorPdfDoctor
    }

    override fun performFiltering(enfermedad: CharSequence?): FilterResults {
        var enfermedad: CharSequence? = enfermedad
        val resultados = FilterResults()
        if (enfermedad != null && enfermedad.isNotEmpty()){
            enfermedad = enfermedad.toString().lowercase()
            val modeloFiltrado: ArrayList<ModeloPdf> = ArrayList()
            for (i in filtroList.indices){
                if (filtroList[i].nombre.lowercase().contains(enfermedad)){
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

    override fun publishResults(constraint: CharSequence?, resultados: FilterResults) {
        adaptadorPdfDoctor.pdfArrayList = resultados.values as ArrayList<ModeloPdf>
        adaptadorPdfDoctor.notifyDataSetChanged()
    }
}