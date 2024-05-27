package com.example.therapeia.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.therapeia.Elegir_rol
import com.example.therapeia.R

class SevenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_seven, container, false)
        val finish = view!!.findViewById<Button>(R.id.tvFinish)

        finish.setOnClickListener {
            cambiarASignUpActivity()
            onBoardingIsFinished()
        }

        val next = view!!.findViewById<TextView>(R.id.tvNext7)
        val viewPager = activity?.findViewById<ViewPager2>(R.id.view_pager)

        next.setOnClickListener {
            viewPager?.currentItem = 7
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