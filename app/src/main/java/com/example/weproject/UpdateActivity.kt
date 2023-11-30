package com.example.weproject

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class UpdateActivity : AppCompatActivity(), UpdateDialogFragment.UpdateDialogListener {

    // 기존 데이터를 저장할 변수들
    private var title: String = ""
    private var price: String = ""
    private var createdAt: Long = 0 // createdAt 추가
    private var onSale: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        println("여기는 왔어어?")
        // Intent에서 기존 데이터를 받아옴
        title = intent.getStringExtra("title") ?: ""
        price = intent.getStringExtra("price") ?: ""
        createdAt = intent.getLongExtra("createdAt", 0) // createdAt
        onSale = intent.getStringExtra("onSale") ?: ""

        println("Received intent data - title: $title, price: $price, createdAt: $createdAt, onSale: $onSale")

        // 각 TextView에 데이터를 설정
        findViewById<TextView>(R.id.title2TextView).apply {
            text = title
            setOnClickListener {
                // 클릭 이벤트: 다이얼로그를 띄워서 정보 변경
                val dialog = UpdateDialogFragment.newInstance(title, price, onSale,this@UpdateActivity)
                dialog.show(supportFragmentManager, "UpdateDialogFragment")
            }
        }
        findViewById<TextView>(R.id.price2TextView).apply {
            text = price
            setOnClickListener {
                // 클릭 이벤트: 다이얼로그를 띄워서 정보 변경
                val dialog = UpdateDialogFragment.newInstance(title, price, onSale, this@UpdateActivity)
                dialog.show(supportFragmentManager, "UpdateDialogFragment")

            }
        }
        findViewById<TextView>(R.id.onSaleTextView1).apply {
            text = onSale
            setOnClickListener {
                // 클릭 이벤트: 다이얼로그를 띄워서 정보 변경
                val dialog = UpdateDialogFragment.newInstance(title, price, onSale, this@UpdateActivity)
                dialog.show(supportFragmentManager, "UpdateDialogFragment")

            }
        }


        // date, seller 등 다른 데이터는 변경하지 않으므로 따로 처리하지 않음...
    }

    override fun onUpdateClicked(updatedTitle: String, updatedPrice: String, onSale: String) {
        // 이 메서드에서 업데이트 작업을 수행하거나 필요한 로직을 추가하세요.
        updateData(updatedTitle, updatedPrice, onSale)
        println("updateClicked 부분 $updatedTitle $updatedPrice $onSale")
    }

    private fun updateData(updatedTitle: String, updatedPrice: String, onSale: String) {
        // 여기에서 Realtime Database 및 어댑터 업데이트 작업 수행
        println("첫번째") // 여기까지 정상적으로 실행되는지 확인

        // createdAt을 사용하여 데이터 가져오기
        val database = Firebase.database.reference.child("articles")
            .orderByChild("createdAt")
            .equalTo(createdAt.toDouble())

        println("두번째")
        // 업데이트할 데이터를 Map으로 만들어서 업데이트
        val updatedData = mapOf(
            "title" to updatedTitle,
            "price" to updatedPrice,
            "onSale" to onSale
        )

        println("UpdateActivity  updateData: $updatedTitle $updatedPrice $onSale") // 여기까지 정상적으로 실행되는지 확인

        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    // 데이터가 여러 개일 경우 반복문을 통해 업데이트
                    data.ref.updateChildren(updatedData)
                        .addOnSuccessListener {
                            println("성공") // 업데이트 성공
                            // 업데이트 성공한 경우에 추가적인 작업을 진행하면 됩니다.
                            stop()
                        }
                        .addOnFailureListener { e ->
                            println("실패") // 업데이트 실패
                            // 업데이트 실패한 경우에 추가적인 작업을 진행하면 됩니다.
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // 처리
            }
        })
    }
    private fun stop() {
        val alertDialog = AlertDialog.Builder(this)
            .setMessage("데이터를 업로드 중입니다...")
            .setCancelable(false)
            .create()

        alertDialog.show()


        Handler().postDelayed({
            alertDialog.dismiss() // 다이얼로그 닫기
            navigateToHomeFragment() // HomeFragment로 이동
        }, 3000) // 3초 대기
    }
    private fun navigateToHomeFragment() {

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("fragmentToLoad", "homeFragment") // 예시: HomeFragment로 이동
        startActivity(intent)

        // 현재 액티비티를 종료
        finish()
    }
}
