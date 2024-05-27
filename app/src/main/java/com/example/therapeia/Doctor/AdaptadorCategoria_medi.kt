package com.example.therapeia.Doctor

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.therapeia.databinding.ItemTipoMediDoctorBinding
import com.google.firebase.database.FirebaseDatabase

class AdaptadorCategoria_medi: RecyclerView.Adapter<AdaptadorCategoria_medi.HolderCategoria_medi>, Filterable {
        private lateinit var binding: ItemTipoMediDoctorBinding
        private val m_context: Context
        public var tipoMediArrayList : ArrayList<ModeloTipoMedicamento>
        private var filtroLista: ArrayList<ModeloTipoMedicamento>
        private var filtro: Filtro_tipo_medi? = null

    constructor(m_context: Context, tipoMediArrayList: ArrayList<ModeloTipoMedicamento>) {
        this.m_context = m_context
        this.tipoMediArrayList = tipoMediArrayList
        this.filtroLista = tipoMediArrayList

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoria_medi {
        binding = ItemTipoMediDoctorBinding.inflate(LayoutInflater.from(m_context), parent, false)
        return HolderCategoria_medi(binding.root)
    }

    override fun getItemCount(): Int {
        return tipoMediArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategoria_medi, position: Int) {
        val modelo = tipoMediArrayList[position]
        val id = modelo.id
        val categoria = modelo.tipo_medi
        val tiempo = modelo.tiempo
        val uid = modelo.uid

        holder.categoriaTv.text = categoria

        holder.elimininarcaten.setOnClickListener{
            val builder = AlertDialog.Builder(m_context)
            builder.setTitle("Eliminar tipo de medicamento")
                .setMessage("¿Estas seguro de eliminar este tipo de medicamento?")
                .setPositiveButton("Confirmar"){a,d->
                    Toast.makeText(m_context, "Eliminando", Toast.LENGTH_SHORT).show()
                    EliminarTipoMedi(modelo, holder)
                }
                .setNegativeButton("Cancelar"){a,d->
                    a.dismiss()
                }
            builder.show()
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(m_context, ListaPdfMediDoc::class.java)
            intent.putExtra("id", id)
            intent.putExtra("nombre", categoria)
            m_context.startActivity(intent)
        }
    }

    private fun EliminarTipoMedi(modelo: ModeloTipoMedicamento, holder: AdaptadorCategoria_medi.HolderCategoria_medi) {
        val id = modelo.id
        val ref = FirebaseDatabase.getInstance().getReference("tipo_medicamento")
        ref.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(m_context, "La eliminación ha sido exitosa ", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(m_context, "No se pudo eliminar debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    inner class HolderCategoria_medi(itemView: View): RecyclerView.ViewHolder(itemView){
        var categoriaTv: TextView = binding.ItemNombreMediDoctor
        var elimininarcaten : ImageButton = binding.EliminarTipoMedi
    }

    override fun getFilter(): Filter {
        if (filtro == null){
            filtro = Filtro_tipo_medi(filtroLista, this)
        }
        return filtro as Filtro_tipo_medi
    }
}