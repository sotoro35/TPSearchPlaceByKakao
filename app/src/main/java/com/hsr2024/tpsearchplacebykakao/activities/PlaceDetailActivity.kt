package com.hsr2024.tpsearchplacebykakao.activities

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.Toast
import com.google.gson.Gson
import com.hsr2024.tpsearchplacebykakao.R
import com.hsr2024.tpsearchplacebykakao.data.Place
import com.hsr2024.tpsearchplacebykakao.databinding.ActivityPlaceDetailBinding

class PlaceDetailActivity : AppCompatActivity() {

    private val binding by lazy { ActivityPlaceDetailBinding.inflate(layoutInflater) }

    private var isFavorite= false

    //SQLite Database를 제어하는 객체 참조변수
    private lateinit var db:SQLiteDatabase

    //현재 장소에 대한 정보 객체 참조변수
    private lateinit var place:Place

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 인텐트로부터 데이터 전달받기
        val s:String? = intent.getStringExtra("place")
        s?.also { // let과 같이 it으로 가르키지만... it은 마지막을 리턴, also는 전체를 리턴
            // json --> 객체
            place = Gson().fromJson(it,Place::class.java)

            //웹뷰를 사용할 때 반드시 해야 할 3가지 설정

            //현재 웹뷰안에서 웹문서가 열리도록..
            binding.wv.webViewClient= WebViewClient() // 웹뷰는 크롬브라우저로 열려고함.. 그래서 내가만든 뷰 안에 들어가게 하려고 하는것.
            binding.wv.webChromeClient= WebChromeClient() //웹 문서안에서 다이얼로그나 팝업 같은 것들이 발동할 수 있도록 하는 설정
            binding.wv.settings.javaScriptEnabled= true // 자바스크립을 허용 ( 웹뷰는 기본적으로 보안문제로 JS 동작을 막아놓음 )

            binding.wv.loadUrl(place.place_url)
        }

        // "place.db"라는 이름으로 데이터베이스 파일을 만들거나 열어서 참조하기
        db= openOrCreateDatabase("place", MODE_PRIVATE, null) // 커서팩토리 - 내가 커스텀할때 사용하는거.. 없으면 null

        // "favor"라는 이름의 표(테이블) 만들기 - SQL 쿼리문을 사용하여.. CRUD 작업수행 // PRIMARY KEY 식별자.. 중복되면 안됨
        db.execSQL("CREATE TABLE IF NOT EXISTS favor(id TEXT PRIMARY KEY, place_name TEXT, category_name TEXT, phone TEXT, address_name TEXT, road_address_name TEXT, x TEXT, y TEXT, place_url TEXT, distance TEXT)")


        // 찜 상태 체크하기
        isFavorite= checkFavorite()
        if (isFavorite) binding.fabFavor.setImageResource(R.drawable.baseline_favorite)
        else binding.fabFavor.setImageResource(R.drawable.baseline_favorite_border)

        // 찜 버튼 클릭 처리
        binding.fabFavor.setOnClickListener {
            if (isFavorite){
                // 찜 DB의 데이터를 삭제
                place.apply {
                    db.execSQL("DELETE FROM favor WHERE id=?", arrayOf(id)) //물음표의 갯수가 여러개일수있어서 배열
                }

                Toast.makeText(this, "찜 삭제", Toast.LENGTH_SHORT).show()

            }else{
                // 찜 DB에 데이터를 저장
                place.apply {
                    db.execSQL("INSERT INTO favor VALUES('$id','$place_name','$category_name','$phone','$address_name','$road_address_name','$x','$y','$place_url','$distance')")

                }


                Toast.makeText(this, "찜 저장", Toast.LENGTH_SHORT).show()
            }

            isFavorite= !isFavorite // 불린값을 반대로 바꿀때.. 현재 false라면 true라고 바꾸는것
            if (isFavorite) binding.fabFavor.setImageResource(R.drawable.baseline_favorite)
            else binding.fabFavor.setImageResource(R.drawable.baseline_favorite_border)

        }

    }// onCreate method

    // SQLite Database의 찜 목록에 저장된 장소정보인지 체크하여 결과 여부를 리턴 [ true/ false ]
    private fun checkFavorite():Boolean{

        //SQLite DB 의 "favor" 테이블의 현재장소에 대한 데이터가 있는지 확인
        place.apply {
            val cursor:Cursor= db.rawQuery("SELECT * FROM favor WHERE id=?", arrayOf(id)) //리턴값이 있음... result와 같음.. execSQL은 리턴값이 unit(void처럼 리턴값이 없다는 의미)
            // cusor는 검색조건에 해당하는 데이터를 가져와 만든 가상의 결과표 객체임
            // cursor.count : 총 레코드의 수
            if (cursor.count > 0) return true //리턴값이 위의 찜상태 체크로 되어있음

        }

        return false
    }

    //뒤로가기 버튼처리
    override fun onBackPressed() {
        if (binding.wv.canGoBack()) binding.wv.goBack() //뒤로가는 페이지가 있다면 뒤 페이지로 가라
        else super.onBackPressed() // 뒤로가기 누르면 무조건 액티비티가 꺼짐
    }

}