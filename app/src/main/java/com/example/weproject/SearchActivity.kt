package com.example.weproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SearchActivity : AppCompatActivity() {
    private var adapter: ArticleAdapter? = null
    private val db: FirebaseFirestore = Firebase.firestore
    private val itemsCollectionRef = db.collection("items")
    private var snapshotListener: ListenerRegistration? = null
    private val progressWait by lazy {findViewById<ProgressBar>(R.id.progressWait)}
    /*private val checkAutoID by lazy { findViewById<CheckBox>(R.id.checkAutoID) }
    private val editID by lazy { findViewById<EditText>(R.id.editID) }
    private val recyclerViewItems by lazy { findViewById<RecyclerView>(R.id.recyclerViewItems) }
    private val textSnapshotListener by lazy { findViewById<TextView>(R.id.textSnapshotListener) }
    private val editItemName by lazy { findViewById<EditText>(R.id.editItemName)}
    private val editPrice by lazy {findViewById<EditText>(R.id.editPrice)}*/



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        val search=findViewById<Button>(R.id.searchbutton)

        search.setOnClickListener{
            querySearch()//이름으로 검색하는 함수임이걸 새로운 엑티비티에 나오도록 해야함
        }


    }
    private fun querySearch() {
        val intent = Intent(this, SearchResult::class.java)
        val searchQuery = findViewById<EditText>(R.id.editText).text.toString()
        intent.putExtra("searchQuery", searchQuery)
        startActivity(intent)
    }
}