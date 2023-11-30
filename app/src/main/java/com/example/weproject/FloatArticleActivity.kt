package com.example.weproject

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageException
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.File

class FloatArticleActivity : AppCompatActivity() {
    lateinit var storage: FirebaseStorage
    private val imageView by lazy { findViewById<ImageView>(R.id.photoImageView) }

    private var selectedUri: Uri? = null


    companion object {
        const val REQUEST_CODE = 1
        const val UPLOAD_FOLDER = "upload_images/"
    }

    private val collection =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // 안드로이드 버전 10이상인 경우
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL) // 10이상 버전에 따른 URI 가져오는 방식 다름
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI // 10이하 버전에 따른 URI 가져오는 방식 다름
        }

    private fun hasPermission(permission: String) =
        checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)

        //Firebase.auth.currentUser ?: finish() //Firebase 인증 사용자만 사용자 인증 x 시 현재 액티비티 종료

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) // 안드로이드 버전에 따른 권한 요청
            requestSinglePermission(Manifest.permission.READ_EXTERNAL_STORAGE)  // 12이하
        else
            requestSinglePermission(Manifest.permission.READ_MEDIA_IMAGES) // 12 이상


        storage = Firebase.storage
        val storageRef = storage.reference
        val imageRef1 = storageRef.child("carrot2.png")

        displayImageRef(imageRef1, imageView)


        findViewById<Button>(R.id.imageAddButton)?.setOnClickListener {
            listPhotosDialog()
        }
        findViewById<Button>(R.id.submitButton)?.setOnClickListener {
            registerArticle()
        }


    }

    private fun requestSinglePermission(permission: String) {
        if (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) // 권한 체크한다
            return

        val requestPermLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                if (it == false) { // permission is not granted!
                    AlertDialog.Builder(this).apply {
                        setTitle("Warning")
                        setMessage("permission required!")
                    }.show()
                }
            }
        if (shouldShowRequestPermissionRationale(permission)) { // 권한이 부여되지 않았다면 교육용 UI 띄운다
            // you should explain the reason why this app needs the permission.
            AlertDialog.Builder(this).apply {
                setTitle("Reason")
                setMessage("permission required!")
                setPositiveButton("Allow") { _, _ -> requestPermLauncher.launch(permission) }
                setNegativeButton("Deny") { _, _ -> }
            }.show()
        } else {
            // should be called in onCreate()
            requestPermLauncher.launch(permission)
        }
    }


    private fun listPhotosDialog() {
        storage.reference.child(UPLOAD_FOLDER).listAll()
            .addOnSuccessListener { result ->
                val itemsString = mutableListOf<String>()
                for (i in result.items) {
                    itemsString.add(i.name)
                }
                AlertDialog.Builder(this)
                    .setTitle("Uploaded Photos")
                    .setItems(itemsString.toTypedArray()) { _, position ->
                        val selectedImageName = itemsString[position]
                        val imageRef = storage.reference.child("${UPLOAD_FOLDER}$selectedImageName")

                        // 사용자가 선택한 이미지의 URI를 selectedUri에 저장
                        imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                            println("여기에요 " + imageUrl.toString())
                            println("여기에요2 " + selectedUri.toString())
                            displayImageRef(imageRef, imageView)
                        }
                    }.show()
            }.addOnFailureListener {
                // 오류 처리
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "이미지 목록을 불러오는데 실패했습니다.",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
    }

    private fun displayImageRef(imageRef: StorageReference?, view: ImageView) {
        imageRef?.downloadUrl?.addOnSuccessListener { imageUrl ->
            selectedUri = imageUrl // 다운로드한 이미지 URL을 selectedUri에 저장
            println("여기에요3 " + selectedUri.toString())
            Glide.with(this).load(selectedUri).into(view) // Glide를 사용하여 이미지뷰에 이미지 로드
        }?.addOnFailureListener {
            // Failed to download the image
        }
    }
    private fun registerArticle() {
        val titleEditText = findViewById<EditText>(R.id.titleEditText)
        val priceEditText = findViewById<EditText>(R.id.priceEditText)
        val contentEditText = findViewById<EditText>(R.id.contentEditText)

        val title = titleEditText.text.toString()
        val price = priceEditText.text.toString()
        val content = contentEditText.text.toString()
        println("여기에요4 " + selectedUri.toString())

        if (title.isEmpty() || price.isEmpty() || content.isEmpty()) {
            Snackbar.make(titleEditText, "제목 및 가격 및 내용 정보를 입력해주세요.", Snackbar.LENGTH_SHORT).show()
            return
        }

        if (selectedUri == null) {
            uploadPhoto(selectedUri!!, FirebaseAuth.getInstance().currentUser?.email ?: "")
        } else {
            uploadArticleToDatabase(title, price, content,"")
        }
    }



//    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
//        val fileName = "${System.currentTimeMillis()}.png"
//        println("안녕하세요")
//
//        val storageRef = storage.reference
//        val imageRef = storageRef.child("download").child(System.currentTimeMillis().toString()+".png")
//        println("1" + imageRef.toString())
//        println("2: ${uri}")
//        imageRef.putFile(uri)
//            .addOnProgressListener {
//                val progress = (100.0 * it.bytesTransferred) / it.totalByteCount
//                println("Upload is $progress% done")
//            }
//            .addOnSuccessListener { taskSnapshot ->
//                // 이미지 업로드가 성공한 경우
//                taskSnapshot.storage.downloadUrl
//                    .addOnSuccessListener { imageUrl ->
//                        println("Image URL: $imageUrl")
//                        successHandler(imageUrl.toString())
//                    }
//                    .addOnFailureListener { exception ->
//                        // 이미지 URL을 가져오는 과정에서 오류가 발생한 경우
//                        errorHandler()
//                        println("Image URL retrieval error: ${exception.message}")
//                    }
//            }
//            .addOnFailureListener { exception ->
//                // 이미지 업로드가 실패한 경우
//                errorHandler()
//                println("Image upload error: ${exception.message}")
//                if (exception is StorageException) {
//                    println("Error code: ${exception.errorCode}")
//                }
//            }
//   }

    private fun uploadPhoto(uri: Uri, userId: String) {
        val storageRef = storage.reference
        val imageRef = storageRef.child("upload_images/$userId/${System.currentTimeMillis()}.png")

        val progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Uploading...")
        progressDialog.show()

        val uploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            // 이미지 업로드가 성공한 경우
            imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                // 이미지 URL을 성공적으로 가져온 경우
                val rootNode = FirebaseDatabase.getInstance()
                val reference = rootNode.getReference("CustomerDP")
                val helper = ArticleModel(imageUrl.toString(), userId)

                reference.child(userId).setValue(helper)
                progressDialog.dismiss()
            }.addOnFailureListener { exception ->
                // 이미지 URL을 가져오는 과정에서 오류가 발생한 경우
                progressDialog.dismiss()
                println("Image URL retrieval error: ${exception.message}")
            }
        }.addOnFailureListener { exception ->
            // 이미지 업로드가 실패한 경우
            progressDialog.dismiss()
            println("Image upload error: ${exception.message}")
            if (exception is StorageException) {
                println("Error code: ${exception.errorCode}")
                println("Error https: ${exception.httpResultCode}")
            }
        }.addOnProgressListener { taskSnapshot ->
            // 업로드 진행 상황을 표시할 수 있습니다.
            val progress = (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
            println("Upload is $progress% done")
        }
    }





//    private fun registerArticle() {
//        val titleEditText = findViewById<EditText>(R.id.titleEditText)
//        val priceEditText = findViewById<EditText>(R.id.priceEditText)
//
//        val title = titleEditText.text.toString()
//        val price = priceEditText.text.toString()
//
//        if (title.isEmpty() || price.isEmpty()) {
//            Snackbar.make(titleEditText, "제목 및 가격 정보를 입력해주세요.", Snackbar.LENGTH_SHORT).show()
//            return
//        }
//        println("selectedUri:" + selectedUri.toString() )
//        if (selectedUri != null) {
//            uploadPhoto(selectedUri!!,
//                successHandler = { uri->
//                    println("1111")
//                    uploadArticleToDatabase(title, price, uri)
//                },
//                errorHandler = {
//                    Snackbar.make(titleEditText, "이미지 업로드 중 오류가 발생했습니다", Snackbar.LENGTH_SHORT)
//                        .show()
//                }
//            )
//            Snackbar.make(titleEditText, "이미지 정보를 입력해주세요.", Snackbar.LENGTH_SHORT).show()
//            return
//        } else {
//            uploadArticleToDatabase(title, price, "")
//        }
//    }


    private fun uploadArticleToDatabase(title: String, price: String, content: String, imageName: String) {
        // ArticleModel 객체를 생성합니다.
        val currentUser = FirebaseAuth.getInstance().currentUser?.email


        val article = ArticleModel(
            sellerId = currentUser?:"", // 판매자 ID를 설정하세요 (Firebase Auth UID와 연결할 수 있습니다).
            title = title,
            content = content,
            createdAt = System.currentTimeMillis(), // 현재 시간을 사용하여 생성 시간을 설정합니다.
            price = price,
            imageURL = imageName,
            onSale = "판매중",
            visible = false,
        )

        // Firebase Realtime Database에 연결합니다.
        val articleDB = Firebase.database.reference.child("articles")

        // articles 노드에 새로운 글을 추가합니다.
        val newArticleRef = articleDB.push()
        newArticleRef.setValue(article)
            .addOnSuccessListener {
                // 업로드 성공 시 성공 메시지를 표시하거나 다른 화면으로 이동할 수 있습니다.
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                println("글이 성공")
            }
            .addOnFailureListener {
                // 업로드 실패 시 실패 메시지를 표시하거나 오류 처리를 수행할 수 있습니다.
                Snackbar.make(imageView, "글 등록 중 오류가 발생했습니다", Snackbar.LENGTH_SHORT).show()
                println("글이 실")
            }
    }



}
