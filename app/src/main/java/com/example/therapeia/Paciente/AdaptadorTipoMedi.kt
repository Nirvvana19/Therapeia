package com.example.therapeia.Paciente

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.therapeia.Doctor.ModeloTipoMedicamento
import com.example.therapeia.databinding.ItemTipoMediPaciBinding

class AdaptadorTipoMedi : RecyclerView.Adapter<AdaptadorTipoMedi.HolderTipoMedi>, Filterable {
    private lateinit var  binding: ItemTipoMediPaciBinding
    private val m_context: Context
    public var tipoMediArrayList : ArrayList<ModeloTipoMedicamento>
    private var filtroLista: ArrayList<ModeloTipoMedicamento>
    private var filtro: Filtro_cate_medi? = null

    constructor(m_context: Context, tipoMediArrayList: ArrayList<ModeloTipoMedicamento>){
        this.m_context = m_context
        this.tipoMediArrayList = tipoMediArrayList
        this.filtroLista = tipoMediArrayList
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HolderTipoMedi {
        binding = ItemTipoMediPaciBinding.inflate(LayoutInflater.from(m_context), parent, false)
        return HolderTipoMedi(binding.root)
    }

    override fun getItemCount(): Int {
        return tipoMediArrayList.size
    }

    override fun onBindViewHolder(holder: HolderTipoMedi, position: Int) {
        val modelo = tipoMediArrayList[position]
        val id = modelo.id
        val categoria = modelo.tipo_medi
        val tiempo = modelo.tiempo
        val uid = modelo.uid

        holder.categoriaTv.text = categoria

        holder.itemView.setOnClickListener {
            val intent = Intent(m_context, ListaPdfMediPac::class.java)
            intent.putExtra("id", id)
            intent.putExtra("nombre", categoria)
            m_context.startActivity(intent)
        }
    }

    inner class HolderTipoMedi(itemView: View): RecyclerView.ViewHolder(itemView){
        var categoriaTv: TextView = binding.ItemNombreTipoMedi
    }

    override fun getFilter(): Filter {
        if (filtro == null){
            filtro = Filtro_cate_medi(filtroLista, this)
        }
        return filtro as Filtro_cate_medi
    }
}