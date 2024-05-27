package com.example.therapeia.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.therapeia.Elegir_rol
import com.example.therapeia.R


class EightFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_eight, container, false)
        val finish = view!!.findViewById<Button>(R.id.tvFinish)

        finish.setOnClickListener {
            cambiarASignUpActivity()
            onBoardingIsFinished()
        }

    return view
}

private fun cambiarASignUpActivity() {
    // Crear una intención para cambiar a SignUpActivity
    val intent = Intent(activity, Elegir_rol::class.java)
    startActivity(intent)
}

    private fun onBoardingIsFinished(){

        val sharedPreferences = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("finished",true)
        editor.apply()
    }


}