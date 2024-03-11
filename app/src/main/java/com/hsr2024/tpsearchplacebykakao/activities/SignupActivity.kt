package com.hsr2024.tpsearchplacebykakao.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.hsr2024.tpsearchplacebykakao.R
import com.hsr2024.tpsearchplacebykakao.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private val binding by lazy { ActivitySignupBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //툴바의 업버튼 클릭시 돌아가기
        binding.toolbar.setNavigationOnClickListener { finish() }

        binding.btnSignup.setOnClickListener { clickSignup() }

    }

    private fun clickSignup(){
        // Firebase Firestore DB에 사용자 정보 저장하기

        var email= binding.inputLayoutEmail.editText!!.text.toString()
        var password= binding.inputLayoutPassword.editText!!.text.toString()
        var passwordConfirm= binding.inputLayoutPasswordConfirm.editText!!.text.toString()

        //유효성 검사 - 패스워드와 패스워드확인이 맞는지 검사
        if (password != passwordConfirm){
            AlertDialog.Builder(this).setMessage("패스워드가 다릅니다. 다시 확인바랍니다.").create().show()
            binding.inputLayoutPasswordConfirm.editText!!.selectAll() // selectAll 써있는 글씨를 드래그로 다 선택되어있음
            return
        }

        // Firebase Firestore DB에 저장하기 - 프로젝트 연동

        //"emailUsers" 이름의 Collection(테이블 명) 참조객체부터 소환
        val userRef: CollectionReference= Firebase.firestore.collection("emailUsers")


        // 중복된 이메일은 저장되면 안되기에..
        userRef.whereEqualTo("email",email).get().addOnSuccessListener {
            // 혹시 같은 email값을 가진 도큐먼트가 여러개 일수도 있어서..
            if (it.documents.size>0){ // 개수가 0개 이상이면.. 같은 email이 있다는 것임.
                AlertDialog.Builder(this).setMessage("중복된 이메일이 있습니다. 다시 확인하여 입력해주세요.").create().show()
                binding.inputLayoutEmail.editText!!.requestFocus() // 다시입력해야하니 포커스 자동이동
                binding.inputLayoutEmail.editText!!.selectAll() // 선택상태로 바뀜... 쓴것이 드래그되어있음

            }else{// 중복된 이메일이 없다면 저장..

                // 저장할 값(이메일, 비밀번호)을 MutableMap으로 묶어주기----------------------
                val user: MutableMap<String,String> = mutableMapOf()
                user["email"] = email //["email"] 식별자가 됨
                user["password"] = password

                userRef.document(email).set(user).addOnSuccessListener {
                    AlertDialog.Builder(this)
                        .setMessage("축하합니다.\n회원가입이 완료되었습니다.")
                        .setPositiveButton("확인",{ p0,p1 -> finish()})
                        .create().show()
                }//--------------------------------------------------------------------
            }
        }


    }
}