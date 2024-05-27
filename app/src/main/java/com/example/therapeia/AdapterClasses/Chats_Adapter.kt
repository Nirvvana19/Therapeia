package com.example.therapeia.AdapterClasses

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.therapeia.R
import com.example.therapeia.ViewFullImage
import com.example.therapeia.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView

class Chats_Adapter (
    mContext: Context,
    mChatList: List<Chat>,
    imageUrl: String
) : RecyclerView.Adapter<Chats_Adapter.ViewHolder>()
{


    private val mContext: Context
    private val mChatList: List<Chat>
    private val imageUrl: String
    var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser!!

    init {
        this.mChatList = mChatList
        this.mContext = mContext
        this.imageUrl = imageUrl
    }


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {

        return if (position == 1)
        {
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
            ViewHolder(view)
        }else
        {
            val view: View = LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
            ViewHolder(view)
        }
}

    override fun getItemCount(): Int {
        return mChatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val chat: Chat = mChatList[position]

        holder.profile_image?.load(imageUrl)


        //Imagenes
        if (chat.message.equals("Te envio una imagen") && !chat.url.equals("")) {
            //Lado derecho
            if (chat.sender.equals(firebaseUser!!.uid)) {
                holder.show_text_message!!.visibility = View.GONE
                holder.right_image_view!!.visibility = View.VISIBLE
                holder.right_image_view!!.load(chat.url)

                holder.right_image_view!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "Ver Imagen Completa",
                        "Eliminar Imagen",
                        "Cancelar"
                    )

                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("¿Qué quieres hacer?")

                    builder.setItems(options, DialogInterface.OnClickListener{
                        dialog, which ->
                        if (which == 0)
                        {
                            val intent = Intent(mContext, ViewFullImage::class.java)
                            intent.putExtra("url", chat.url)
                            mContext.startActivity(intent)
                        }
                        else if (which == 1)
                        {
                            deleteSentMessage(position)
                        }
                    })
                    builder.show()

                }

            }
            //Lado izquierdo
            else if (!chat.sender.equals(firebaseUser!!.uid)) {
                holder.show_text_message!!.visibility = View.GONE
                holder.left_image_view!!.visibility = View.VISIBLE
                holder.left_image_view!!.load(chat.url)

                holder.left_image_view!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "Ver Imagen Completa",
                        "Cancelar"
                    )

                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("¿Qué quieres hacer?")

                    builder.setItems(options, DialogInterface.OnClickListener { dialog, which ->
                        if (which == 0) {
                            val intent = Intent(mContext, ViewFullImage::class.java)
                            intent.putExtra("url", chat.url)
                            mContext.startActivity(intent)
                        }
                    })
                    builder.show()
                }
            }
        }

        //Mensajes
        else {
            holder.show_text_message!!.text = chat.message

            if (firebaseUser!!.uid == chat.sender)
            {
                holder.show_text_message!!.setOnClickListener {
                    val options = arrayOf<CharSequence>(
                        "Eliminar Mensaje",
                        "Cancelar"
                    )

                    var builder: AlertDialog.Builder = AlertDialog.Builder(holder.itemView.context)
                    builder.setTitle("¿Qué quieres hacer?")

                    builder.setItems(options, DialogInterface.OnClickListener{
                            dialog, which ->
                        if (which == 0)
                        {
                            deleteSentMessage(position)
                        }
                    })
                    builder.show()
                }
            }
        }

        //Visto y enviado
        if (position == mChatList.size-1)
        {
         if (chat.isseen)
         {
             holder.text_seen!!.text = "Visto"


             if (chat.message.equals("Te envio una imagen") && !chat.url.equals(""))
             {
                 val lp: RelativeLayout.LayoutParams? = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                 lp!!.setMargins(0, 245, 10, 0)

                 holder.text_seen!!.layoutParams = lp

             }
         }
         else {
             holder.text_seen!!.text = "Enviado"


             if (chat.message.equals("Te envio una imagen") && !chat.url.equals(""))
             {
                 val lp: RelativeLayout.LayoutParams? = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                 lp!!.setMargins(0, 245, 10, 0)

                 holder.text_seen!!.layoutParams = lp

             }

         }

        }
        else
        {
            holder.text_seen!!.visibility = View.GONE
        }
    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var profile_image: CircleImageView? = null
        var show_text_message: TextView? = null
        var left_image_view: ImageView? = null
        var text_seen: TextView? = null
        var right_image_view: ImageView? = null

        init {
            profile_image = itemView.findViewById(R.id.profile_image)
            show_text_message = itemView.findViewById(R.id.show_text_message)
            left_image_view = itemView.findViewById(R.id.left_image_view)
            text_seen = itemView.findViewById(R.id.text_seen)
            right_image_view = itemView.findViewById(R.id.right_image_view)

        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (mChatList[position].sender.equals(firebaseUser!!.uid))
        {
            1
        }
        else
        {
            0
        }
    }

    private fun deleteSentMessage(position: Int) {
        val messageId = mChatList[position].messageId
        val senderId = mChatList[position].sender

        // Verifica si el usuario actual es el remitente del mensaje
        if (firebaseUser?.uid == senderId) {
            // Si es el remitente, procede con la eliminación del mensaje
            val ref = FirebaseDatabase.getInstance().reference.child("Chats").child(messageId)
            ref.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Eliminación exitosa
                    Toast.makeText(mContext, "Mensaje eliminado", Toast.LENGTH_LONG).show()
                } else {
                    // Error al eliminar
                    Toast.makeText(mContext, "No se pudo eliminar el mensaje", Toast.LENGTH_LONG).show()
                }
            }
        } else {
            // Si no es el remitente, muestra un mensaje de error
            Toast.makeText(mContext, "No tienes permiso para eliminar este mensaje", Toast.LENGTH_LONG).show()
        }
    }

}