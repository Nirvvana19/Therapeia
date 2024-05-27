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
import com.example.therapeia.databinding.ItemCategoriaDoctorBinding
import com.google.firebase.database.FirebaseDatabase

class AdaptadorCategoria_enfer: RecyclerView.Adapter<AdaptadorCategoria_enfer.HolderCategoria_enfer>, Filterable {
    private lateinit var  binding: ItemCategoriaDoctorBinding
    private val m_context: Context
    public var tipoEnferArrayList : ArrayList<ModeloTipoEnfer>
    private var filtroLista: ArrayList<ModeloTipoEnfer>
    private var filtro: Filtro_tipo_enfer? = null

    constructor(m_context: Context, tipoEnferArrayList: ArrayList<ModeloTipoEnfer>) {
        this.m_context = m_context
        this.tipoEnferArrayList = tipoEnferArrayList
        this.filtroLista = tipoEnferArrayList
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderCategoria_enfer {
        binding = ItemCategoriaDoctorBinding.inflate(LayoutInflater.from(m_context), parent, false)
        return HolderCategoria_enfer(binding.root)
    }

    override fun getItemCount(): Int {
        return tipoEnferArrayList.size
    }

    override fun onBindViewHolder(holder: HolderCategoria_enfer, position: Int) {
        val modelo = tipoEnferArrayList[position]
        val id = modelo.id
        val categoria = modelo.categoria
        val tiempo = modelo.tiempo
        val uid = modelo.uid

        holder.categoriaTv.text = categoria

        holder.elimininarcaten.setOnClickListener{
            val builder = AlertDialog.Builder(m_context)
            builder.setTitle("Eliminar tipo de enfermedad")
                .setMessage("¿Estas seguro de eliminar este tipo de enfermedad?")
                .setPositiveButton("Confirmar"){a,d->
                    Toast.makeText(m_context, "Eliminando", Toast.LENGTH_SHORT).show()
                    EliminarTipoEnfer(modelo, holder)
                }
                .setNegativeButton("Cancelar"){a,d->
                    a.dismiss()
                }
            builder.show()
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(m_context, ListaPdfDoctor::class.java)
            intent.putExtra("id", id)
            intent.putExtra("nombre", categoria)
            m_context.startActivity(intent)
        }
    }



    private fun EliminarTipoEnfer(modelo: ModeloTipoEnfer, holder: AdaptadorCategoria_enfer.HolderCategoria_enfer) {
        val id = modelo.id
        val ref = FirebaseDatabase.getInstance().getReference("tipo_enfermedad")
        ref.child(id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(m_context, "La eliminación ha sido exitosa ", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e->
                Toast.makeText(m_context, "No se pudo eliminar debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }

    }

    inner class HolderCategoria_enfer(itemView: View): RecyclerView.ViewHolder(itemView){
        var categoriaTv: TextView = binding.ItemNombreTipoEnfer
        var elimininarcaten : ImageButton = binding.EliminarTipoEnfermedad
    }

    override fun getFilter(): Filter {
        if (filtro == null){
            filtro = Filtro_tipo_enfer(filtroLista, this)
        }
        return filtro as Filtro_tipo_enfer
    }


}