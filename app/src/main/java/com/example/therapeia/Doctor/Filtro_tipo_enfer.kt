package com.example.therapeia.Doctor

import android.widget.Filter


class Filtro_tipo_enfer: Filter{
    private var filtroLista: ArrayList<ModeloTipoEnfer>
    private var adaptadorcategoriaEnfer: AdaptadorCategoria_enfer

    constructor(filtroLista: ArrayList<ModeloTipoEnfer>,
        adaptadorcategoriaEnfer: AdaptadorCategoria_enfer) {
        this.filtroLista = filtroLista
        this.adaptadorcategoriaEnfer = adaptadorcategoriaEnfer
    }

    override fun performFiltering(tipoEnfer: CharSequence?): FilterResults {
        var tipoEnfer = tipoEnfer
        var resultados = FilterResults()
        if (tipoEnfer != null && tipoEnfer.isNotEmpty()){
            tipoEnfer = tipoEnfer.toString().uppercase()
            val modeloFiltrado: ArrayList<ModeloTipoEnfer> = ArrayList()
            for (i in 0  until  filtroLista.size){
                if (filtroLista[i].categoria.uppercase().contains(tipoEnfer)){
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
       adaptadorcategoriaEnfer.tipoEnferArrayList = resultados.values as ArrayList<ModeloTipoEnfer>
        adaptadorcategoriaEnfer.notifyDataSetChanged()
    }


}