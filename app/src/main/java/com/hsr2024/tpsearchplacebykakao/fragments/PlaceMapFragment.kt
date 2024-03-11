package com.hsr2024.tpsearchplacebykakao.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hsr2024.tpsearchplacebykakao.databinding.FragmentPlaceListBinding
import com.hsr2024.tpsearchplacebykakao.databinding.FragmentPlaceMapBinding

class PlaceMapFragment : Fragment() {

    private val binding by lazy { FragmentPlaceMapBinding.inflate(layoutInflater) }

    override fun onCreateView( //뷰를 만드는곳
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { // :View? 리턴타입
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

}