package com.example.therapeia.AdapterClasses

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.therapeia.Message_Chat
import com.example.therapeia.R
import com.example.therapeia.model.Users
import com.example.therapeia.model.Chat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView

class User_Adapter_Chat (
    mContext: Context,
    mUsers: List<Users>,
    isChatCheck: Boolean
) : RecyclerView.Adapter<User_Adapter_Chat.ViewHolder?>(){

    private val mContext:Context
    private val mUsers: List<Users>
    private var isChatCheck: Boolean
    var lastMsg: String = ""

    init {
        this.mUsers = mUsers
        this.mContext = mContext
        this.isChatCheck = isChatCheck
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(mContext).inflate(R.layout.user_search_item_layout, viewGroup, false)
        return User_Adapter_Chat.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUsers.size
    }

    override fun onBindViewHolder(holder: ViewHolder, i: Int) {

        val users: Users = mUsers[i]

        holder.userNameTxt.text = users.nombres
        holder.profileImageView.load(users.imagen)

        if (isChatCheck){
            retrieveLastMessage(users.uid, holder.lastMessageTxt)
        }
        else{
            holder.lastMessageTxt.visibility = View.GONE
        }

        if (isChatCheck){
            if (users.status == "En linea"){
                holder.onlineImageView.visibility = View.VISIBLE
                holder.offlineImageView.visibility = View.GONE
            }
            else{
                holder.onlineImageView.visibility = View.GONE
                holder.offlineImageView.visibility = View.VISIBLE
            }
        }
        else{
            holder.onlineImageView.visibility = View.GONE
            holder.offlineImageView.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val options = arrayOf<CharSequence>(
                "Enviar Mensaje",
                "Visitar Perfil"
            )

            val builder: AlertDialog.Builder = AlertDialog.Builder(mContext)
            builder.setTitle("¿Qué quieres hacer?")
            builder.setItems(options, DialogInterface.OnClickListener { dialog, position ->
                if (position == 0)
                {
                    val intent = Intent(mContext, Message_Chat::class.java)
                    intent.putExtra("visit_id", users.uid)
                    mContext.startActivity(intent)
                }
                if (position == 1)
                {

                }
            })
            builder.show()
        }

    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var userNameTxt: TextView
        var profileImageView: CircleImageView
        var onlineImageView: CircleImageView
        var offlineImageView: CircleImageView
        var lastMessageTxt: TextView


        init {
            userNameTxt = itemView.findViewById(R.id.username)
            profileImageView = itemView.findViewById(R.id.profile_image)
            onlineImageView = itemView.findViewById(R.id.image_online)
            offlineImageView = itemView.findViewById(R.id.image_offline)
            lastMessageTxt = itemView.findViewById(R.id.message_last)

        }

    }

    private fun retrieveLastMessage(chatUserId: String?, lastMessageTxt: TextView) {

        lastMsg = "defaultMsg"

        val firebaseUser = FirebaseAuth.getInstance().currentUser

        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                for (datasnapshot in p0.children)
                {
                    val chat: Chat? = datasnapshot.getValue(Chat::class.java)

                    if (firebaseUser!= null && chat!= null)
                    {
                        if (chat.receiver == firebaseUser!!.uid &&
                            chat.sender == chatUserId ||
                            chat.receiver == chatUserId &&
                            chat.sender == firebaseUser!!.uid)

                        {
                            lastMsg = chat.message!!
                        }
                    }
                }
                when(lastMsg){
                    "defaultMsg" -> lastMessageTxt.text = "Ningun mensaje"
                    "Te envio una imagen" -> lastMessageTxt.text = "Imagen enviada"
                    else -> lastMessageTxt.text = lastMsg
                }
                lastMsg = "defaultMsg"
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



}