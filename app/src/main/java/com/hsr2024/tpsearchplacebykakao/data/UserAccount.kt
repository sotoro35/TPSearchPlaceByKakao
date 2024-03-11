package com.hsr2024.tpsearchplacebykakao.data

import com.google.gson.annotations.SerializedName

data class UserAccount(var id:String, var email:String)

data class UserAccountNaver(var response:UserAccountN)

data class UserAccountN(var id:String, var email:String)
