package com.hsr2024.tpsearchplacebykakao

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 카카오 SDK 초기화작업
        KakaoSdk.init(this, "586e0c25dc79b7fb8d17c615a96b56c2")
    }
}