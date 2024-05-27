package com.example.therapeia.Fragmentos

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.therapeia.Notificationsc.Tokens
import com.example.therapeia.AdapterClasses.User_Adapter_Chat
import com.example.therapeia.R
import com.example.therapeia.model.ChatList
import com.example.therapeia.model.Users
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging


class ChatsFragment : Fragment() {

    private var userAdapterChat: User_Adapter_Chat? = null
    private var mUsers: List<Users>? = null
    private var usersChatList: List<ChatList>? = null
    lateinit var recicler_view_chatList: RecyclerView
    private var firebaseUser: FirebaseUser? = null
    private lateinit var mcontext: Context

    override fun onAttach(context: Context) {
        mcontext = context
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_chats, container, false)


        recicler_view_chatList = view.findViewById(R.id.recicler_view_chatlist)
        recicler_view_chatList.setHasFixedSize(true)
        recicler_view_chatList.layoutManager = LinearLayoutManager(mcontext)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (usersChatList as ArrayList).clear()

                for (datasnapshot in p0.children)
                {
                    val chatList = datasnapshot.getValue(ChatList::class.java)

                    (usersChatList as ArrayList).add(chatList!!)
                }

                retrieveChatList()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        updateToken(FirebaseMessaging.getInstance().token)

        return view


    }

    private fun updateToken(token: Task<String>) {

        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1  = Tokens(token.toString())
        ref.child(firebaseUser!!.uid).setValue(token1)

    }

    private fun retrieveChatList(){

        mUsers = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Usuarios")
        ref!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                (mUsers as ArrayList).clear()

                for (datasnapshot in p0.children)
                {
                    val users = datasnapshot.getValue(Users::class.java)

                    for (eachList in usersChatList!!)
                    {
                        if (users!!.uid.equals(eachList.id))
                        {
                            (mUsers as ArrayList).add(users!!)
                        }
                    }
                }
                userAdapterChat = User_Adapter_Chat(mcontext!!, (mUsers as ArrayList<Users>), true)
                recicler_view_chatList.adapter = userAdapterChat
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}


