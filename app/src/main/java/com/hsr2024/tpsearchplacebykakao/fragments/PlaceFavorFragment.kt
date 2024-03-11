package com.hsr2024.tpsearchplacebykakao.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.common.net.HttpHeaders.FROM
import com.hsr2024.tpsearchplacebykakao.adapter.PlaceListRecyclerAdapter
import com.hsr2024.tpsearchplacebykakao.data.Place
import com.hsr2024.tpsearchplacebykakao.databinding.ActivityMainBinding
import com.hsr2024.tpsearchplacebykakao.databinding.FragmentPlaceFavorBinding
import com.hsr2024.tpsearchplacebykakao.databinding.FragmentPlaceListBinding

class PlaceFavorFragment : Fragment() {

    lateinit var binding:FragmentPlaceFavorBinding

    override fun onCreateView( //뷰를 만드는곳
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { // :View? 리턴타입
        binding = FragmentPlaceFavorBinding.inflate(layoutInflater,container,false)
        // container를 주면 사이즈를 미리 만들어 놓는다.
        // private val binding by lazy { FragmentPlaceFavorBinding.inflate(layoutInflater) }
        // 멤버로 위에 만들어두면 container를 못쓴다. 그래서 컴퓨터가 화면을 2번 수정하게 된다. 

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { //처음 만들어졌을때 1번만 호출
        super.onViewCreated(view, savedInstanceState)

        //loadData()
    }

    override fun onResume() { // 다른게 가렸다가 오면 다시 호출..
        super.onResume()

        loadData()
    }

    private fun loadData(){
        //SQLite DB... [place.db]파일 안에.. "favor"테이블에 저장된 장소정보들을 읽어오기
        // activity를 직접 부르는것보다  requireContext()를 쓰는걸 권장
        val db= requireContext().openOrCreateDatabase("place", Activity.MODE_PRIVATE,null) // requireContext() 널이면 실행하지 않는 기능..


        val cursor= db.rawQuery("SELECT * FROM favor", null) // where가 전부라면 안쓰고 null을 주면 된다
        //레코드의 개수만큼 반복하면서 값들을 읽어오기
        cursor?.apply {

            moveToFirst()

            val placeList:MutableList<Place> = mutableListOf()

            for (i in 0 until cursor.count){
                val id:String = this.getString(0)
                val place_name= getString(1)
                val category_name = getString(2)
                val phone= getString(3)
                val address_name= getString(4)
                val road_address_name= getString(5)
                val x= getString(6)
                val y= getString(7)
                val place_url= getString(8)
                val distance:String= getString(9)

                val place:Place= Place(id, place_name, category_name, phone, address_name, road_address_name, x, y, place_url, distance)
                placeList.add(place)

                moveToNext()

            }//for..

            // 리스트 데이터를 리사이클러뷰에 아이템뷰로 보이도록 아답터 설정
            binding.recyclerView.adapter= PlaceListRecyclerAdapter(requireContext(),placeList)



        }//apply..
    }//loadData...

}