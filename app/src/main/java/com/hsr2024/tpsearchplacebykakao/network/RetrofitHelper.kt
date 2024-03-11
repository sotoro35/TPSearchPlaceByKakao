package com.hsr2024.tpsearchplacebykakao.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitHelper {
    companion object{

        fun getRetrofitInstance(baseUrl:String): Retrofit {
            val retrofit= Retrofit.Builder()
                            .baseUrl(baseUrl)
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .addConverterFactory(GsonConverterFactory.create()) //무조건 문자열 -> 지손 으로 받아야함
                            .build()

            return retrofit
        }

    }
}