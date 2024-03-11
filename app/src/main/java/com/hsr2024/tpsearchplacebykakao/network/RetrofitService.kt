package com.hsr2024.tpsearchplacebykakao.network

import com.hsr2024.tpsearchplacebykakao.data.KakaoSearchPlaceResponse
import com.hsr2024.tpsearchplacebykakao.data.UserAccountNaver
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query

interface RetrofitService {

    // 카카오 로컬 검색 api 요청해주는 코드 만들어줘. 우선 응답 type: String
    @Headers("Authorization: KakaoAK 873ff9df496d53dc12acbdfa350141ce")
    @GET("/v2/local/search/keyword.json")
    fun searchPlaceToString(@Query("query") query:String,@Query("x") longitude:String,@Query("y") latitude:String) : Call<String>


    // 카카오 로컬 검색 api 요청해주는 코드 만들어줘. 우선 응답 type: KakaoSearchPlaceResponse
    @Headers("Authorization: KakaoAK 873ff9df496d53dc12acbdfa350141ce")
    @GET("/v2/local/search/keyword.json?sort=distance")
    fun searchPlace(@Query("query") query:String,@Query("x") longitude:String,@Query("y") latitude:String) : Call<KakaoSearchPlaceResponse>


    // 네아로 회원정보 프로필 api 요청코드 만들어줘
    @GET("/v1/nid/me")
    fun getNinUserInfo(@Header("Authorization") authorization:String ): Call<UserAccountNaver> //@Header("Authorization") 이것에 대한 값은 authorization:String 여기에 넣어둠
}