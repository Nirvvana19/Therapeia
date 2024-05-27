package com.example.therapeia.Doctor

import android.widget.Filter


class Filtro_tipo_medi: Filter{
    private var filtroLista: ArrayList<ModeloTipoMedicamento>
    private var adaptadorcategoriaMedi: AdaptadorCategoria_medi

    constructor(filtroLista: ArrayList<ModeloTipoMedicamento>,
                adaptadorcategoriaMedi: AdaptadorCategoria_medi) {
        this.filtroLista = filtroLista
        this.adaptadorcategoriaMedi = adaptadorcategoriaMedi
    }

    override fun performFiltering(tipoMedi: CharSequence?): FilterResults {
        var tipoMedi = tipoMedi
        var resultados = FilterResults()
        if (tipoMedi != null && tipoMedi.isNotEmpty()){
            tipoMedi = tipoMedi.toString().uppercase()
            val modeloFiltrado: ArrayList<ModeloTipoMedicamento> = ArrayList()
            for (i in 0  until  filtroLista.size){
                if (filtroLista[i].tipo_medi.uppercase().contains(tipoMedi)){
                    modeloFiltrado.add(filtroLista[i])
                }
                resultados.count = modeloFiltrado.size
                resultados.values = modeloFiltrado
            }
        }
        else{
            resultados.count = filtroLista.size
            resultados.values = filtroLista
        }
        return resultados
    }

    override fun publishResults(constraint: CharSequence?, resultados: FilterResults) {
        adaptadorcategoriaMedi.tipoMediArrayList = resultados.values as ArrayList<ModeloTipoMedicamento>
        adaptadorcategoriaMedi.notifyDataSetChanged()
    }

}