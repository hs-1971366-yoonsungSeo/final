package com.example.weproject

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ToggleButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment

class UpdateDialogFragment : DialogFragment() {

    private lateinit var listener: UpdateDialogListener
    private lateinit var updatedTitleEditText: EditText
    private lateinit var updatedPriceEditText: EditText
    private lateinit var toggleButton: ToggleButton

    // 콜백 인터페이스 정의
    interface UpdateDialogListener {
        fun onUpdateClicked(updatedTitle: String, updatedPrice: String, onSale: String)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is UpdateDialogListener) {
            listener = context
        } else {
            throw ClassCastException("$context must implement UpdateDialogListener")
        }
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // 직접 다이얼로그의 레이아웃을 inflate
            val inflater = LayoutInflater.from(it)
            val view = inflater.inflate(R.layout.dialog_update, null)

            // 기존 데이터를 받아와서 다이얼로그에 표시
            toggleButton = view.findViewById(R.id.toggleButton)
            updatedTitleEditText = view.findViewById(R.id.updatedTitleEditText)
            updatedPriceEditText = view.findViewById(R.id.updatedPriceEditText)

            // 전달된 인수를 기반으로 텍스트 필드에 값을 설정
            val currentTitle = arguments?.getString("currentTitle", "")
            val currentPrice = arguments?.getString("currentPrice", "")


            updatedTitleEditText.setText(currentTitle)
            updatedPriceEditText.setText(currentPrice)


            val builder = AlertDialog.Builder(it)
            builder.setView(view)
                .setPositiveButton("Update") { _, _ ->
                    // 사용자가 "Update" 버튼을 눌렀을 때
                    val updatedTitle = updatedTitleEditText.text.toString()
                    val updatedPrice = updatedPriceEditText.text.toString()
                    val onSale = if (toggleButton.isChecked) "판매중" else "판매완료"
                    // 리스너를 통해 업데이트 클릭 이벤트 전달
                    listener.onUpdateClicked(updatedTitle, updatedPrice, onSale)
                }
                .setNegativeButton("Cancel") { _, _ ->
                    // 사용자가 "Cancel" 버튼을 눌렀을 때
                    dialog?.cancel()
                }

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }


    companion object {
        fun newInstance(currentTitle: String, currentPrice: String, currentOnSale: String, listener: UpdateDialogListener): UpdateDialogFragment {
            return UpdateDialogFragment().apply {
                arguments = Bundle().apply {
                    putString("currentTitle", currentTitle)
                    putString("currentPrice", currentPrice)
                    putString("currentOnSale", currentOnSale)
                }

                setListener(listener)
            }
        }


        private fun UpdateDialogFragment.setListener(listener: UpdateDialogListener) {
            this.listener = listener
        }
    }
}
