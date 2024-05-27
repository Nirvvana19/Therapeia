package com.example.therapeia

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.therapeia.AdapterClasses.Chats_Adapter
import com.example.therapeia.Fragmentos.APIService
import com.example.therapeia.Notificationsc.Client
import com.example.therapeia.Notificationsc.Data
import com.example.therapeia.Notificationsc.MyResponse
import com.example.therapeia.Notificationsc.Sender
import com.example.therapeia.Notificationsc.Tokens
import com.example.therapeia.model.Users
import com.example.therapeia.model.Chat
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Response

class Message_Chat : AppCompatActivity() {

    var userIdVisit: String = ""
    var firebaseUser: FirebaseUser? = null
    var chatsAdapter: Chats_Adapter? = null
    var mChatList: List<Chat>? = null
    lateinit var recicler_view_chats: RecyclerView
    var reference: DatabaseReference? = null

    var notify = false
    var apiService: APIService? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message_chat)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_message_chat)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            onBackPressedDispatcher.onBackPressed()
            /*val intent = Intent(
                this@Message_Chat,
                Welcome_Activity::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()*/
        }

        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id").toString()
        firebaseUser = FirebaseAuth.getInstance().currentUser


        recicler_view_chats = findViewById(R.id.recicler_view_chats)
        recicler_view_chats.setHasFixedSize(true)

        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recicler_view_chats.layoutManager = linearLayoutManager


        reference = FirebaseDatabase.getInstance().reference
            .child("Usuarios").child(userIdVisit)
        reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {

                val user: Users? = p0.getValue(Users::class.java)

                findViewById<TextView>(R.id.username_mchat).text = user!!.nombres
                findViewById<CircleImageView>(R.id.profile_image_mchat).load(user.imagen)


                retrieveMessages(firebaseUser!!.uid, userIdVisit, user.imagen)

            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


        findViewById<ImageView>(R.id.send_message_btn).setOnClickListener{
            notify = true
            val message = findViewById<EditText>(R.id.text_message).text.toString()
            if (message == "")
            {
                Toast.makeText(this@Message_Chat, "Por favor escribe un mensaje primero...", Toast.LENGTH_LONG).show()

            }
            else
            {
sendMessageToUser(firebaseUser!!.uid, userIdVisit, message)
            }

            findViewById<EditText>(R.id.text_message).setText("")
        }

        findViewById<ImageView>(R.id.attact_image_file).setOnClickListener {
           notify = true
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), 438)
        }

        seenMessage(userIdVisit)
    }

    private fun sendMessageToUser(senderId: String, receiverId: String, message: String) {

        val reference = FirebaseDatabase.getInstance().reference

            // Generar un ID único para el mensaje
            val messageId = "${System.currentTimeMillis()}-${firebaseUser?.uid}"

            // Crear un objeto Chat con el mensaje y el ID generado
            val chat = Chat(sender = firebaseUser?.uid ?: "", message = message, messageId = messageId)

            // Guardar el objeto Chat en la base de datos
            val ref = FirebaseDatabase.getInstance().getReference("Chats").child(messageId)
            ref.setValue(chat)


        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderId
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isseen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageid"] = messageId

        reference.child("Chats")
            .child(messageId!!)
            .setValue(messageHashMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful)
                {
                    val chatsListReference = FirebaseDatabase.getInstance()
                        .reference
                        .child("ChatList")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit)

                    chatsListReference.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {
                            if (!p0.exists()) {
                                chatsListReference.child("id").setValue(userIdVisit)
                            }
                            val chatsListReceiverRef = FirebaseDatabase.getInstance()
                                .reference
                                .child("ChatList")
                                .child(userIdVisit)
                                .child(firebaseUser!!.uid)
                            chatsListReceiverRef.child("id").setValue(firebaseUser!!.uid)

                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })


                }

            }
        val usersReference = FirebaseDatabase.getInstance().reference
            .child("Usuarios").child(firebaseUser!!.uid)

        usersReference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val users = p0.getValue(Users::class.java)
                if (notify)
                {
                    sendNotification(userIdVisit, users!!.nombres, "Te envio una imagen")
                }
                notify = false
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }

    private fun sendNotification(receiverId: String?, userName: String?, message: String) {

        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")

        val query = ref.orderByKey().equalTo(receiverId)

        query.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (datasnapshot in p0.children)
                {
                    val tokens : Tokens? = datasnapshot.getValue(Tokens::class.java)

                    val data = Data(
                        firebaseUser!!.uid,
                        R.mipmap.ic_launcher,
                        "$userName: $message",
                        "Nuevo Mensaje",
                        userIdVisit
                    )

                    val sender = Sender(data!!, FirebaseMessaging.getInstance().token)

                    apiService!!.sendNotification(sender)
                        .enqueue(object : retrofit2.Callback<MyResponse>{


                            override fun onResponse(
                                call: retrofit2.Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
if (response.code() == 200)
{
    if (response.body()!!.succes !== 1)
    {
        Toast.makeText(this@Message_Chat, "Fallo, no paso nada.", Toast.LENGTH_LONG).show()
    }
}
                            }

                            override fun onFailure(call: retrofit2.Call<MyResponse>, t: Throwable) {

                            }
                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode==438 && resultCode == RESULT_OK && data!= null && data!!.data != null)
        {
            val progressBar = ProgressDialog(this)
            progressBar.setMessage("Imagen configurandose, por favor espera...")
            progressBar.show()


            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            var uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "Te envio una imagen"
                    messageHashMap["receiver"] = userIdVisit
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageid"] = messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful)
                            {
//Notificaciones

                                progressBar.dismiss()

                            }
                        }
                }

            }
        }

    }

    private fun retrieveMessages(senderId: String, receiverId: String, receiverImageURL: String) {
        mChatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                // Limpiar la lista de mensajes antes de agregar nuevos
                (mChatList as ArrayList<Chat>).clear()
                for (snapshot in p0.children) {
                    val chat = snapshot.getValue(Chat::class.java)

                    if (chat!!.receiver.equals(senderId) && chat.sender.equals(receiverId)
                        || chat.receiver.equals(receiverId) && chat.sender.equals(senderId)) {
                        (mChatList as ArrayList<Chat>).add(chat)
                    }
                }

                // Crear y configurar el adaptador fuera del bucle de datos
                chatsAdapter = Chats_Adapter(this@Message_Chat, mChatList as ArrayList<Chat>, receiverImageURL)
                recicler_view_chats.adapter = chatsAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar la cancelación de la consulta
                Toast.makeText(this@Message_Chat, "Error al recuperar los mensajes", Toast.LENGTH_SHORT).show()
            }
        })


    val rootView = findViewById<View>(R.id.relative_layout_bottom)
        val recyclerView = findViewById<RecyclerView>(R.id.recicler_view_chats)
        val params = recyclerView.layoutParams as RelativeLayout.LayoutParams

        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.height
            val keypadHeight = screenHeight - rect.bottom
            if (keypadHeight > screenHeight * 0.15) { // Si la altura del teclado es más del 15% de la altura de la pantalla
                // Ocultar el RecyclerView moviéndolo fuera de la pantalla
                params.addRule(RelativeLayout.ABOVE, 0)
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            } else {
                // Mostrar el RecyclerView debajo del EditText
                params.addRule(RelativeLayout.ABOVE, R.id.relative_layout_bottom)
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0)
            }
            recyclerView.layoutParams = params
        }
    }

    var seenListener: ValueEventListener? = null

    private fun seenMessage(userId: String){
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        seenListener = reference!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (datasnapshot in p0.children)
                {
                    val chat = datasnapshot.getValue(Chat::class.java)

                    if (chat!!.receiver.equals(firebaseUser!!.uid) && chat!!.sender.equals(userId))
                    {
                        val hashMap = HashMap<String, Any>()
                        hashMap["isseen"] = true
                        datasnapshot.ref.updateChildren(hashMap)
                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onPause() {
        super.onPause()

        reference!!.removeEventListener(seenListener!!)
    }



}



