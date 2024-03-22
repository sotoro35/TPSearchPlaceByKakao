package com.hsr2024.tpsearchplacebykakao.fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.hsr2024.tpsearchplacebykakao.R
import com.hsr2024.tpsearchplacebykakao.activities.MainActivity
import com.hsr2024.tpsearchplacebykakao.activities.PlaceDetailActivity
import com.hsr2024.tpsearchplacebykakao.data.Place
import com.hsr2024.tpsearchplacebykakao.databinding.FragmentPlaceListBinding
import com.hsr2024.tpsearchplacebykakao.databinding.FragmentPlaceMapBinding
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.camera.CameraUpdate
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.mapwidget.InfoWindowOptions
import com.kakao.vectormap.mapwidget.component.GuiLayout
import com.kakao.vectormap.mapwidget.component.GuiText
import com.kakao.vectormap.mapwidget.component.Orientation

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

        // 카카오 지도 start
        binding.mapView.start()



    }

    private val mapReadyCallbak: KakaoMapReadyCallback = object : KakaoMapReadyCallback() { // 상속받아 익명클래스로 만듬
        override fun onMapReady(kakaoMap: KakaoMap) { //지도 가져오는게 완료가되면 콜백객체가 발동함..

            // 현재 내 위치를 지도의 중심위치로 설정 LatLng = 위도와 경도를 갖고있는 카카오객체
            val latitude: Double= (activity as MainActivity).myLocation?.latitude ?: 37.5666
            val longitude: Double= (activity as MainActivity).myLocation?.longitude ?: 126.9782
            val myPos: LatLng= LatLng.from(latitude,longitude) //카카오는 new를 안쓰고 .from을 주로쓴다

            // 내 위치로 지도 카메라 이동 (카카오 시스템이 카메라로 찍고 있어서 .. )
            val cameraUpdate: CameraUpdate = CameraUpdateFactory.newCenterPosition(myPos,16) // 지도의 가운데 지점을 내 위치로
            kakaoMap.moveCamera(cameraUpdate)

            // 내 위치 마커(라벨) 추가하기
            val labelOption : LabelOptions= LabelOptions.from(myPos).setStyles(R.drawable.pin_y) //벡터그래픽 이미지는 안됨
            // 라벨이 그려질 레이어 객체 소환
            val labelLayer: LabelLayer = kakaoMap.labelManager!!.layer!!
            // 라벨 레이어에 라벨 추가
            labelLayer.addLabel(labelOption)
            //--------------------------------------------

            // 주변 검색장소들에 마커 추가하기
            val placeList: List<Place>? = (activity as MainActivity).searchPlaceResponse?.documents
            placeList?.forEach{
                //마커(라벨)옵션 객체 생성
                val pos= LatLng.from(it.y.toDouble(), it.x.toDouble())
                val options= LabelOptions.from(pos).setStyles(R.drawable.pin_g).setTexts(it.place_name,"${it.distance}M").setTag(it) // 태그안에 place객체가 들어가도록..
                kakaoMap.labelManager!!.layer!!.addLabel(options)
            }// forEach

            // 라벨 클릭에 반응하기
            kakaoMap.setOnLabelClickListener { kakaoMap, layer, label ->

                label.apply {

                    label.setStyles(R.drawable.pin_g)
                    // 정보창 [ infoWindow ] 보여주기

                    val layout= GuiLayout(Orientation.Vertical)
                    layout.setPadding(16,16,16,16)
                    layout.setBackground(R.drawable.base_msg,true)// Ninepatch: 모서리를 늘릴때 어떻게 할래?

                    this.texts.forEach {  // 여기서 texts = 위에 setTexts 의 값을 갖고 있는거
                        val guiText= GuiText(it)
                        guiText.setTextSize(30)
                        guiText.setTextColor(Color.WHITE)
                        layout.addView(guiText)
                    }

                    // [정보창 info window] 객체 만들기
                    val options: InfoWindowOptions= InfoWindowOptions.from(label.position)
                    options.body= layout
                    options.setBodyOffset(0f, -10f)
                    options.setTag( this.tag ) // 라벨이 갖고 있던 place의 정보를 infowindow가 갖게되는거..

                    kakaoMap.mapWidgetManager!!.infoWindowLayer.removeAll()
                    kakaoMap.mapWidgetManager!!.infoWindowLayer.addInfoWindow(options)

                }// apply..
            }// label click...

            // [정보창 클릭에 반응하기]
            kakaoMap.setOnInfoWindowClickListener { kakaoMap, infoWindow, guiId ->
                //장소에 대한 상세 소개 웹페이지를 보여주는 화면으로 이동
                val intent= Intent(requireContext(),PlaceDetailActivity::class.java)

                // 클릭한 장소에 대한 정보를 json문자열로 변환하여 전달해주기
                val place:Place= infoWindow.tag as Place
                val json:String= Gson().toJson(place)
                intent.putExtra("place",json)

                startActivity(intent)
            }




        }

    }


}