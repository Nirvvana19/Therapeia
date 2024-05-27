package com.example.therapeia.Fragmentos

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.therapeia.AdapterClasses.User_Adapter_Chat
import com.example.therapeia.R
import com.example.therapeia.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SearchFragment : Fragment() {

    private var userAdapterChat: User_Adapter_Chat? = null
    private var mUsers: MutableList<Users>? = null
    private var reciclerView: RecyclerView? = null
    private var searchEditText: EditText? = null
    //Brindamos un contexto a este fragmento
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
        val view: View = inflater.inflate(R.layout.fragment_search, container, false)

        reciclerView = view.findViewById(R.id.searchList)
        reciclerView!!.setHasFixedSize(true)
        reciclerView!!.layoutManager = LinearLayoutManager(mcontext)
        searchEditText = view.findViewById(R.id.searchUsersET)
        mUsers = mutableListOf()

        retrieveAllUsers()

        searchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(cs: CharSequence?, start: Int, before: Int, count: Int) {
                searchForUsers(cs.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        return view
    }


    private fun retrieveAllUsers() {

        val firebaseUserID = FirebaseAuth.getInstance().currentUser?.uid
        val refUsers = FirebaseDatabase.getInstance().reference.child("Usuarios")

        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers?.clear()
                for (snapshot in dataSnapshot.children) {
                    val user: Users? = snapshot.getValue(Users::class.java)

                    if (user != null && user.uid != firebaseUserID) {
                        mUsers?.add(user)
                    }
                }
                userAdapterChat = User_Adapter_Chat(mcontext!!, mUsers!!, false)
                reciclerView?.adapter = userAdapterChat


            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })
    }

    private fun searchForUsers(str: String) {
        val firebaseUserID = FirebaseAuth.getInstance().currentUser?.uid

        val queryUsers = FirebaseDatabase.getInstance().reference
            .child("Usuarios").orderByChild("search")
            .startAt(str)
            .endAt(str + "\uf8ff")

        queryUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                mUsers?.clear()
                for (snapshot in dataSnapshot.children) {
                    val user: Users? = snapshot.getValue(Users::class.java)
                    if (user != null && user.uid != firebaseUserID) {
                        mUsers?.add(user)
                    }
                }
                userAdapterChat = User_Adapter_Chat(mcontext!!, mUsers!!, false)
                reciclerView?.adapter = userAdapterChat
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }
}

