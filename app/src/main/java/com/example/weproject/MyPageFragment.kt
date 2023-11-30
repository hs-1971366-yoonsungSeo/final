package com.example.weproject

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MyPageFragment : Fragment(R.layout.fragment_mypage) {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?


    ): View? {
        val view = inflater.inflate(R.layout.fragment_mypage, container, false)
//println("홈 화면으로 와버렸덩")

        val myList=view.findViewById<Button>(R.id.myList)//내 게시글 버튼 누르면 리스트 출력
        myList.setOnClickListener {
            val intent = Intent(requireContext(), MyListActivity::class.java)
            startActivity(intent)
        }

        val logout = view.findViewById<Button>(R.id.logout)
        logout.setOnClickListener{
            showLogoutConfirmationDialog()
        }

        return view
    }
    private fun showLogoutConfirmationDialog() {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle("로그아웃")
            .setMessage("로그아웃하시겠습니까?(앱이 종료됩니다.)")
            .setPositiveButton("확인") { _, _ ->
                // 사용자가 확인을 선택하면 로그아웃 수행
                Firebase.auth.signOut()
                activity?.finish()
            }
            .setNegativeButton("취소", null)
            .show()
    }

}