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
import com.example.therapeia.Doctor.ListaPdfDoctor
import com.example.therapeia.Doctor.ModeloTipoEnfer
import com.example.therapeia.databinding.ItemCategoriaPacienteBinding

class AdaptadorTipo_enfer : RecyclerView.Adapter<AdaptadorTipo_enfer.HolderTipo_enfer>, Filterable {
        private lateinit var  binding: ItemCategoriaPacienteBinding
        private val m_context: Context
        public var tipoEnferArrayList : ArrayList<ModeloTipoEnfer>
        private var filtroLista: ArrayList<ModeloTipoEnfer>
        private var filtro: Filtro_cate_enfer? = null

        constructor(m_context: Context, tipoEnferArrayList: ArrayList<ModeloTipoEnfer>) {
            this.m_context = m_context
            this.tipoEnferArrayList = tipoEnferArrayList
            this.filtroLista = tipoEnferArrayList
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderTipo_enfer {
            binding = ItemCategoriaPacienteBinding.inflate(LayoutInflater.from(m_context), parent, false)
            return HolderTipo_enfer(binding.root)
        }

        override fun getItemCount(): Int {
            return tipoEnferArrayList.size
        }

        override fun onBindViewHolder(holder: HolderTipo_enfer, position: Int) {
            val modelo = tipoEnferArrayList[position]
            val id = modelo.id
            val categoria = modelo.categoria
            val tiempo = modelo.tiempo
            val uid = modelo.uid

            holder.categoriaTv.text = categoria


            holder.itemView.setOnClickListener {
                val intent = Intent(m_context, ListaPdfPaciente::class.java)
                intent.putExtra("id", id)
                intent.putExtra("nombre", categoria)
                m_context.startActivity(intent)
            }
        }

        inner class HolderTipo_enfer(itemView: View): RecyclerView.ViewHolder(itemView){
            var categoriaTv: TextView = binding.ItemNombreTipoEnfer
        }

   override fun getFilter(): Filter {
              if (filtro == null){
                  filtro = Filtro_cate_enfer(filtroLista, this)
              }
              return filtro as Filtro_cate_enfer
          }

    }