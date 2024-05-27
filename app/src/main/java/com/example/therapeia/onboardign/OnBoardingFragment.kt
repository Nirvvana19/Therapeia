package com.example.therapeia.onboardign

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.example.therapeia.R
import com.example.therapeia.screens.EightFragment
import com.example.therapeia.screens.FirstFragment
import com.example.therapeia.screens.FiveFragment
import com.example.therapeia.screens.FourFragment
import com.example.therapeia.screens.SecondFragment
import com.example.therapeia.screens.SevenFragment
import com.example.therapeia.screens.SixFragment
import com.example.therapeia.screens.ThirdFragment
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator

class OnBoardingFragment : Fragment() {

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_on_boarding, container, false)

        val fragmentList = arrayListOf<Fragment>(
            FirstFragment(),
            SecondFragment(),
            ThirdFragment(),
            FourFragment(),
            FiveFragment(),
            SixFragment(),
            SevenFragment(),
            EightFragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        val viewPager = view!!.findViewById<ViewPager2>(R.id.view_pager)

        viewPager.adapter = adapter
        val indicator = view!!.findViewById<DotsIndicator>(R.id.dots_indicator)

        indicator.attachTo(viewPager)

        return view

    }

}