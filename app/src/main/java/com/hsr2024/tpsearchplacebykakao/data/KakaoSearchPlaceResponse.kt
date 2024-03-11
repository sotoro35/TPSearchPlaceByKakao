package com.hsr2024.tpsearchplacebykakao.data

data class KakaoSearchPlaceResponse(var meta:PlaceMeta, var documents:List<Place>)

data class PlaceMeta(var total_count:Int, var pageable_count:Int, var is_end:Boolean)

data class Place( //한계정에 포함되어있는 내용
    var id:String,
    var place_name:String,
    var category_name:String,
    var phone:String,
    var address_name:String,
    var road_address_name:String,
    var x:String,
    var y:String,
    var place_url:String,
    var distance:String
)
