package com.example.therapeia.onboardign

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat.finishAffinity
import androidx.navigation.fragment.findNavController
import com.example.therapeia.Elegir_rol
import com.example.therapeia.MainActivity
import com.example.therapeia.MainActivityPaciente
import com.example.therapeia.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SplashFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        auth = FirebaseAuth.getInstance()

        // Inflate the layout for this fragment
        Handler(Looper.getMainLooper()).postDelayed({
            if (onBoardingIsFinished()) {
                cambiarASignUpActivity()
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_onBoardingFragment)
            }

        }, 3000)


        val view = inflater.inflate(R.layout.fragment_splash, container, false)

        val animTop = AnimationUtils.loadAnimation(view.context, R.anim.from_top)
        val animBottom = AnimationUtils.loadAnimation(view.context, R.anim.from_bottom)
        val animleft = AnimationUtils.loadAnimation(view.context, R.anim.from_le)
        val animright = AnimationUtils.loadAnimation(view.context, R.anim.from_ri)


        val tvSplash = view.findViewById<TextView>(R.id.tv_splash)
        val imgSplash = view.findViewById<ImageView>(R.id.imageView)
        val im_iz = view.findViewById<ImageView>(R.id.im_iz)
        val im_ri = view.findViewById<ImageView>(R.id.im_ri)


        tvSplash.animation = animBottom
        imgSplash.animation = animTop
        im_iz.animation = animleft
        im_ri.animation = animright


        return view
    }


    private fun cambiarASignUpActivity() {
        // Crear una intención para cambiar a SignUpActivity

        val currentUser = auth.currentUser
        if (currentUser == null) {
            // Si el usuario está autenticado, redirigirlo a MainActivityC
            startActivity(Intent(activity, Elegir_rol::class.java))

        }else{
            //Con esto podemos manejar las sesiones de los usuarios
            val reference= FirebaseDatabase.getInstance().getReference("Usuarios")
            reference.child(currentUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val rol = snapshot.child("rol").value
                        if (rol == "doctor") {
                            startActivity(
                                Intent(
                                    activity,
                                    MainActivity::class.java
                                )
                            )

                        } else if (rol == "paciente"){
                            startActivity(
                                Intent(
                                    activity,
                                    MainActivityPaciente::class.java
                                )
                            )
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                })
        }

    }

    private fun onBoardingIsFinished(): Boolean {

        val sharedPreferences =
            requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("finished", false)


    }



}


