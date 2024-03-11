package com.hsr2024.tpsearchplacebykakao.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hsr2024.tpsearchplacebykakao.activities.MainActivity
import com.hsr2024.tpsearchplacebykakao.adapter.PlaceListRecyclerAdapter
import com.hsr2024.tpsearchplacebykakao.data.Place
import com.hsr2024.tpsearchplacebykakao.databinding.FragmentPlaceListBinding

class PlaceListFragment : Fragment() {

    private val binding by lazy { FragmentPlaceListBinding.inflate(layoutInflater) }

    override fun onCreateView( //뷰를 만드는곳 // 이때 메인에 프래그먼트가 붙는작업이 된다. 프래그먼트의 값이 null이면 액티비티에 붙기전. 그래서 이 메소드 호출이 불가능
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { // :View? 리턴타입
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //리사이클러뷰에 MainActivity가 가지고 있는 대량의 장소정보들을 보여지도록...
        //메인 액티비티를 불러오기
        val ma:MainActivity = activity as MainActivity //형변환을 안해주면 본인이 메인인지 모른다

        ma.searchPlaceResponse?: return //아직 서버로딩이 완료되지 않았을수도 있으니..

        binding.recyclerView.adapter = PlaceListRecyclerAdapter(requireContext(),ma.searchPlaceResponse!!.documents) //액티비티안에있는 context를 뽑아오는거



    }

}