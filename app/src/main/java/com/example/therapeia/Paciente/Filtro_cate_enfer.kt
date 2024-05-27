package com.example.therapeia.Paciente

import android.widget.Filter
import com.example.therapeia.Doctor.ModeloTipoEnfer

class Filtro_cate_enfer: Filter{

    private var filtroLista: ArrayList<ModeloTipoEnfer>
    private var adaptadorcategoriaEnfer: AdaptadorTipo_enfer

    constructor(filtroLista: ArrayList<ModeloTipoEnfer>,
                adaptadorcategoriaEnfer: AdaptadorTipo_enfer
    ) {
        this.filtroLista = filtroLista
        this.adaptadorcategoriaEnfer = adaptadorcategoriaEnfer
    }

    override fun performFiltering(tipoEnfer: CharSequence?): Filter.FilterResults {
        var tipoEnfer = tipoEnfer
        var resultados = Filter.FilterResults()
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

    override fun publishResults(constraint: CharSequence?, resultados: Filter.FilterResults) {
        adaptadorcategoriaEnfer.tipoEnferArrayList = resultados.values as ArrayList<ModeloTipoEnfer>
        adaptadorcategoriaEnfer.notifyDataSetChanged()
    }

}