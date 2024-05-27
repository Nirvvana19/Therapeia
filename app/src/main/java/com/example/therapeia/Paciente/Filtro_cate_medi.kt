package com.example.therapeia.Paciente

import android.widget.Filter
import com.example.therapeia.Doctor.ModeloTipoEnfer
import com.example.therapeia.Doctor.ModeloTipoMedicamento

class Filtro_cate_medi: Filter {
    private var filtroLista: ArrayList<ModeloTipoMedicamento>
    private var adaptadorcategoriaMedi: AdaptadorTipoMedi

    constructor(filtroLista: ArrayList<ModeloTipoMedicamento>,
                adaptadorcategoriamedi: AdaptadorTipoMedi
    ) {
        this.filtroLista = filtroLista
        this.adaptadorcategoriaMedi = adaptadorcategoriamedi
    }

    override fun performFiltering(tipoMedi: CharSequence?): Filter.FilterResults {
        var tipoMedi = tipoMedi
        var resultados = Filter.FilterResults()
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

    override fun publishResults(constraint: CharSequence?, resultados: Filter.FilterResults) {
        adaptadorcategoriaMedi.tipoMediArrayList = resultados.values as ArrayList<ModeloTipoMedicamento>
        adaptadorcategoriaMedi.notifyDataSetChanged()
    }
}