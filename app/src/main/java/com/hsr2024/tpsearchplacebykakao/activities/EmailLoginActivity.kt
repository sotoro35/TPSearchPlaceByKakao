package com.hsr2024.tpsearchplacebykakao.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hsr2024.tpsearchplacebykakao.G
import com.hsr2024.tpsearchplacebykakao.R
import com.hsr2024.tpsearchplacebykakao.data.UserAccount
import com.hsr2024.tpsearchplacebykakao.databinding.ActivityEmailLoginBinding

class EmailLoginActivity : AppCompatActivity() {

    private val binding by lazy { ActivityEmailLoginBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener { finish() }
        binding.btnSignin.setOnClickListener { clickSignIn() }
    }

    private fun clickSignIn(){

        var email= binding.inputLayoutEmail.editText!!.text.toString()
        var password= binding.inputLayoutPassword.editText!!.text.toString()

        // Firebase Firestore DB에서 이메일 로그인 확인
        val userRef: CollectionReference= Firebase.firestore.collection("emailUsers")

        userRef
            .whereEqualTo("email",email)
            .whereEqualTo("password",password)
            .get().addOnSuccessListener {
                // 이메일,비밀번호 검색 결과가 1개 이상이므로 찾았다는것임. 로그인 성공
                if ( it.documents.size > 0) {

                    // 다른 화면에서도 회원정보를 사용할 수도 있어서 전역변수처럼 G클래스에 저장
                    val id:String = it.documents[0].id // document명을 id로 활용!!
                    G.userAccount= UserAccount(id,email)

                    //로그인 성공했으니.. Main 화면으로 이동
                    val intent= Intent(this, MainActivity::class.java)

                    // 기존 task의 모든 액티비티를 제거하고 새로운 task 시작
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK) // 기존에 있는 것들을 다 없앰
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // 기존에 있던것들은 다 없어지고 새롭게 시작
                    startActivity(intent)

                } else {
                    // 이메일과 비밀번호에 해당하는 document가 없는 것. 로그인 실패.
                    AlertDialog.Builder(this).setMessage("이메일과 비밀번호를 다시 확인해주세요").create().show()
                    binding.inputLayoutEmail.editText!!.requestFocus()
                    binding.inputLayoutEmail.editText!!.selectAll()
                }
            }

    }
}