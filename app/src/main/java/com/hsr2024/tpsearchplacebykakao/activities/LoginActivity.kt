package com.hsr2024.tpsearchplacebykakao.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.hsr2024.tpsearchplacebykakao.G
import com.hsr2024.tpsearchplacebykakao.data.UserAccount
import com.hsr2024.tpsearchplacebykakao.data.UserAccountN
import com.hsr2024.tpsearchplacebykakao.data.UserAccountNaver
import com.hsr2024.tpsearchplacebykakao.databinding.ActivityLoginBinding
import com.hsr2024.tpsearchplacebykakao.network.RetrofitHelper
import com.hsr2024.tpsearchplacebykakao.network.RetrofitService
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.navercorp.nid.NaverIdLoginSDK
import com.navercorp.nid.oauth.OAuthLoginCallback
import okhttp3.internal.wait
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }


    var accountNaver:UserAccountNaver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //둘러보기 글씨 클릭으로 로그인없이 Main화면으로 이동
        binding.tvGo.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }

        //회원가입 버튼 클릭
        binding.tvSignup.setOnClickListener { startActivity(Intent(this,SignupActivity::class.java)) }

        //이메일 로그인 버튼 클릭
        binding.layoutEmailLogin.setOnClickListener { startActivity(Intent(this,EmailLoginActivity::class.java)) }

        //간편 로그인 버튼 클릭
        binding.btnLoginKakao.setOnClickListener { clickKaKao() }
        binding.btnLoginGoogle.setOnClickListener { clickGoogle() }
        binding.btnLoginNaver.setOnClickListener { clickNaver() }
    }


    private fun clickKaKao(){

        //두개의 로그인 요청 콜백 함수
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null){
                Toast.makeText(this, "카카오 로그인 실패", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this, "카카오 로그인 성공", Toast.LENGTH_SHORT).show()

                // 사용자 정보 요청
                UserApiClient.instance.me { user, error ->
                    if (user != null){
                        val id:String= user.id.toString()
                        val nickname:String= user.kakaoAccount?.profile?.nickname ?: ""

                        Toast.makeText(this, "$id\n$nickname", Toast.LENGTH_SHORT).show()
                        G.userAccount= UserAccount(id, nickname)

                        // 로그인 되었으니
                        startActivity(Intent(this,MainActivity::class.java))
                        finish()
                    }
                }
            }
        }


        // 카카오톡이 사용가능하면 이를 이용하여 로그인하고 없으면 카카오계정으로 로그인하기
        if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
            UserApiClient.instance.loginWithKakaoTalk(this, callback= callback)
        }else{
            UserApiClient.instance.loginWithKakaoAccount(this, callback= callback)
        }

    }

    private fun clickGoogle(){
        //Toast.makeText(this, "구글로그인", Toast.LENGTH_SHORT).show()

        // 로그인 옵션객체 생성 - Builder - 이메일 요청
        val signInOptions: GoogleSignInOptions= GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build()

        // 구글 로그인을 하는 화면 액티비티를 실행하는 Intent 객체로 로그인 구현 (play-services-auth 라이브러리 추가)
        val intent:Intent= GoogleSignIn.getClient(this, signInOptions).signInIntent // 로그인 인텐트를 달라는 의미
        // 구글 로그인화면 결과를 받아올수있게 대행사로 ... 그냥 startActivity(intent)를하면 결과를 받지 못함
        resultLauncher.launch(intent)
    }

    // 구글 로그인화면 결과를 받아주는 대행사 등록
    val resultLauncher= registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        // 로그인 결과를 가져온 인텐트 소환
        val intent:Intent?= it.data
        // 인텐트로 부터 구글 계정 정보를 가져오는 작업자 객체를 소환..
        val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(intent)

        // 작업자로부터 계정 받기
        val account: GoogleSignInAccount= task.result
        val id:String= account.id.toString()
        val email:String= account.email ?: ""

        Toast.makeText(this, "$id\n$email", Toast.LENGTH_SHORT).show()
        G.userAccount= UserAccount(id,email)

        // main 화면으로 이동
        startActivity(Intent(this,MainActivity::class.java))
    }



    private fun clickNaver(){
        //Toast.makeText(this, "네이버로그인", Toast.LENGTH_SHORT).show()

        // 네아로SDK 초기화
        NaverIdLoginSDK.initialize(this,"TpN23HeB2gGqdO5dEEzL","AayZfeJPNm","서치플라이")

        // 로그인 요청
        NaverIdLoginSDK.authenticate(this, object : OAuthLoginCallback{
            override fun onError(errorCode: Int, message: String) {
                Toast.makeText(this@LoginActivity, "$message", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(httpStatus: Int, message: String) {
                Toast.makeText(this@LoginActivity, "$message", Toast.LENGTH_SHORT).show()
            }



            override fun onSuccess() {
                Toast.makeText(this@LoginActivity, "로그인 성공", Toast.LENGTH_SHORT).show()

                //사용자 정보를 받아오기.. -- REST API로 받아야 함..
                //로그인에 성공하면 REST API로 요청할 수 있는 토큰(token)을 발급받음. 토큰이라는건 인증서같은 키임.. 네이버는 일회용임..그래서 이용시마다 발급받아야함
                val accessToken:String? = NaverIdLoginSDK.getAccessToken()

                //retrofit 작업을 통해 사용자 정보 가져오기
                val retrofit= RetrofitHelper.getRetrofitInstance("https://openapi.naver.com")
                val retrofitService= retrofit.create(RetrofitService::class.java)
                val call= retrofitService.getNinUserInfo("Bearer ${accessToken}")
                call.enqueue(object : Callback<UserAccountNaver>{ // json이 잘 오는지 확인하기 위해서 우선 String받고 이후, json을 객체로 넣을 데이터클래스를 만들어서 제네릭을 바꾼다
                    override fun onResponse(
                        call: Call<UserAccountNaver>,
                        response: Response<UserAccountNaver>
                    ) {
                        accountNaver= response.body()

                        if (accountNaver != null ){
                            val userAccount:UserAccountN = accountNaver!!.response
                            AlertDialog.Builder(this@LoginActivity).setMessage("${userAccount?.id}\n${userAccount?.email}").create().show()
                            G.userAccount?.id = userAccount!!.id
                            G.userAccount?.email= userAccount!!.email

                            startActivity(Intent(this@LoginActivity,MainActivity::class.java))
                            finish()

                        }else Toast.makeText(this@LoginActivity, "로그인에 실패했습니다", Toast.LENGTH_SHORT).show()

                    }

                    override fun onFailure(call: Call<UserAccountNaver>, t: Throwable) {
                        AlertDialog.Builder(this@LoginActivity).setMessage(t.message).create().show()
                    }
                })

            }

        })
    }
}

    //override fun onResponse(call: Call<String>, response: Response<String>) {
//                        val s= response.body()
//                        //AlertDialog.Builder(this@LoginActivity).setMessage(s).create().show()
//
//                        val gson= Gson()
//                        val userAccountNaver = gson.fromJson(s,UserAccountNaver::class.java)
//                        AlertDialog.Builder(this@LoginActivity).setMessage("$s \n\n ${userAccountNaver.id}").create().show()
//
//
//
//
//                        //startActivity(Intent(this@LoginActivity,MainActivity::class.java))
//                        //finish()
//                    }
//
//                    override fun onFailure(call: Call<String>, t: Throwable) {
//                        Toast.makeText(this@LoginActivity, "${t.message}", Toast.LENGTH_SHORT).show()
//                    }
//
//                }