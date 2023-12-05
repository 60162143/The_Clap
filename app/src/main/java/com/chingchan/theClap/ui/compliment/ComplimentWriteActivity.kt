package com.chingchan.theClap.ui.compliment

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.chingchan.theClap.databinding.ActiCompWriteBinding
import com.chingchan.theClap.globalFunction.GlobalFunction
import com.chingchan.theClap.ui.compliment.data.CompCatData
import com.chingchan.theClap.ui.compliment.data.UploadImageType
import com.chingchan.theClap.ui.compliment.data.UploadImageURI
import com.chingchan.theClap.ui.toast.customToast
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ComplimentWriteActivity : AppCompatActivity(), UploadImageDialogInterface {
    private lateinit var binding: ActiCompWriteBinding
    private var compCatData: ArrayList<CompCatData>? = null
    private var curCategoryPosition = 0
    private val uploadImageDialog: CompWriteUploadImageDialog by lazy { CompWriteUploadImageDialog(this, this) }
    private val uploadImageAdapter: UploadImageRecyclerAdapter by lazy { UploadImageRecyclerAdapter() }
    private var uploadImageURIList: ArrayList<UploadImageURI>? = ArrayList()

    private lateinit var writeType: String  // 지인 칭찬 : friend, 셀프 칭찬 : self


    // 가져온 사진 보여주기
    private val pickImageLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data

                if (data?.clipData != null) {
                    val count = data.clipData!!.itemCount

                    for (i in 0 until count) {
                        if(uploadImageURIList?.size!! > 4){
                            customToast.showCustomToast("한번에 최대 5장까지 업로드 가능합니다", this@ComplimentWriteActivity)
                            break
                        }
                        val imageUri = data.clipData!!.getItemAt(i).uri
                        uploadImageURIList?.add(UploadImageURI(imageUri))
                    }

                    binding.rvImageCount.text = "${uploadImageURIList?.size!!}/5"   // 이미지 Count SET

                    uploadImageAdapter.submitList(uploadImageURIList?.toMutableList())

                } else {
                    data?.data?.let { uri ->
                        val imageUri: Uri? = data.data

                        if (imageUri != null) {
                            uploadImageURIList?.add(UploadImageURI(imageUri))

                            binding.rvImageCount.text = "${uploadImageURIList?.size!!}/5"   // 이미지 Count SET

                            uploadImageAdapter.submitList(uploadImageURIList?.toMutableList())
                        }
                    }
                }
            }else{
                customToast.showCustomToast("이미지 불러오기를 실패했습니다.", this)
            }
        }


    var pictureUri: Uri? = null // 찍은 사진 Uri
    // 찍은 사진 보여주기
    private val getTakePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if(it) {
            pictureUri?.let {
                // differ의 현재 리스트를 받아와서 newList에 넣기
                val newList = uploadImageAdapter.currentList.toMutableList()

                newList.add(UploadImageURI(pictureUri!!))

                // adapter의 differ.submitList()로 newList 제출
                // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                uploadImageAdapter.submitList(newList.toMutableList())

                uploadImageURIList?.add(UploadImageURI(pictureUri!!))   // 기존 데이터 Update

                binding.rvImageCount.text = "${uploadImageURIList?.size!!}/5"   // 이미지 Count SET
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActiCompWriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        compCatData = GlobalFunction.getSerializableExtra(intent, "compCatData", ArrayList<CompCatData>()::class.java)
        writeType = intent?.getStringExtra("writeType") ?: ""

        with(binding){

            // 셀프 칭찬하기의 경우 카테고리 설정 불가
            if(writeType == "self"){
                rvCat.visibility = View.GONE
            }

            // 업로드 이미지 리사이클러뷰 클릭 리스너
            uploadImageAdapter.onImageDeleteClickListener = object :
                UploadImageRecyclerAdapter.OnImageDeleteClickListener {
                override fun onImageDeleteClick(position: Int) {
                    // differ의 현재 리스트를 받아와서 newList에 넣기
                    val newList = uploadImageAdapter.currentList.toMutableList()

                    newList.removeAt(position)

                    uploadImageURIList?.removeAt(position)  // 기존 데이터 삭제

                    // adapter의 differ.submitList()로 newList 제출
                    // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                    uploadImageAdapter.submitList(newList.toMutableList())

                    binding.rvImageCount.text = "${uploadImageURIList?.size!!}/5"   // 이미지 Count SET
                }
            }

            // 업로드 이미지 리사이클러뷰 어뎁터
            rvImage.adapter = uploadImageAdapter

            // 뒤로 가기 버튼 클릭 리스너
            btnBack.setOnClickListener(View.OnClickListener {
                finish()
            })

            // 이미지 추가 버튼 클릭 리스너
            btnImageAdd.setOnClickListener{
                if(uploadImageURIList!!.size < 5){
                    uploadImageDialog.show()
                }else{
                    customToast.showCustomToast("한번에 최대 5장까지 업로드 가능합니다", this@ComplimentWriteActivity)
                }

            }

            // 제목 입력 텍스트 변화 확인 리스너
            editTitle.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    btnConfirm.isEnabled = getEditTextIsEmpty() // 제목과 칭찬할 내용 모두 입력시에만 확인 버튼 활성화
                }
            })

            // 칭찬할 내용 입력 텍스트 변화 확인 리스너
            editTitle.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    btnConfirm.isEnabled = getEditTextIsEmpty() // 제목과 칭찬할 내용 모두 입력시에만 확인 버튼 활성화
                }
            })
        }
    }

    override fun onResume() {
        super.onResume()

        val catRecyclerAdapter = CompWriteCatRecyclerAdapter()
        binding.rvCat.adapter = catRecyclerAdapter

        // 카테고리 '전체' 선택 디폴트
        compCatData?.get(0)?.isSelect = true

        // DiffUtil 적용 후 데이터 추가
        catRecyclerAdapter.differ.submitList(compCatData)

        catRecyclerAdapter.catClickListener = object : CompWriteCatRecyclerAdapter.OnCatClickListener{
            override fun onCatClick(position: Int) {
                // differ의 현재 리스트를 받아와서 newList에 넣기
                // differUtil을 사용할 때 중요한 점은 참조되는 객체의 주소가 변경되어야 반영됨
                val newList = catRecyclerAdapter.differ.currentList.toMutableList()

                // 이전 선택되어 있는 카테고리
                // 객체의 주소를 바꾸기 위해 copy 사용
                val newPreviousData = newList[curCategoryPosition].copy(isSelect = false)

                // 새로 선택된 카테고리
                // 객체의 주소를 바꾸기 위해 copy 사용
                val newPostData = newList[position].copy(isSelect = true)

                // 기존 데이터 값 갱신
                compCatData?.get(curCategoryPosition)?.isSelect = false
                compCatData?.get(position)?.isSelect = true

                // 새로운 객체로 저장
                newList[curCategoryPosition] = newPreviousData
                newList[position] = newPostData

                curCategoryPosition = position  // 현재 선택되어 있는 카테고리의 index Update

                // adapter의 differ.submitList()로 newList 제출
                // submitList()로 제출하면 기존에 있는 oldList와 새로 들어온 newList를 비교하여 UI 반영
                catRecyclerAdapter.differ.submitList(newList.toMutableList())
            }
        }
    }

    // 화면 터치시 키보드 내리기
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val imm: InputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        return super.dispatchTouchEvent(ev)
    }

    // API 33 이후 getSerializable()가 deprecated 되어서 만든 확장함수
    private fun <T: Serializable> Intent.intentSerializable(key: String, clazz: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.getSerializableExtra(key, clazz)
        } else {
            this.getSerializableExtra(key) as T?
        }
    }

    // 이미지 업로드 다이얼로그 사진보관함 버튼 클릭
    override fun onStorageBtnClicked() {
        checkPermission(UploadImageType.STORAGE.name, UploadImageType.STORAGE.code)
    }

    // 이미지 업로드 다이얼로그 사진찍기 버튼 클릭
    override fun onCameraBtnClicked() {
        checkPermission(UploadImageType.CAMERA.name, UploadImageType.CAMERA.code)
    }

    // 이미지 업로드 다이얼로그 취소 버튼 클릭
    override fun onCancelBtnClicked() {
        uploadImageDialog.dismiss()
    }

    // 권한 확인
    private fun checkPermission(type: String, permissionRequestCode: Int) {
        val permission = mutableMapOf<String, String>()

        if(type == UploadImageType.CAMERA.name){
            // 카메라 권한
            permission["camera"] = Manifest.permission.CAMERA
//            permissionRequestCode =
        }else if(type == UploadImageType.STORAGE.name){
            // 저장소 권한
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){      // api 33 이상
                permission["storageMediaImage"] = Manifest.permission.READ_MEDIA_IMAGES
            }else{// api 29 이하
                permission["storageRead"] = Manifest.permission.READ_EXTERNAL_STORAGE
            }

            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){      // api 28이하, 이후로는 체크 X
                permission["storageWrite"] =  Manifest.permission.WRITE_EXTERNAL_STORAGE
            }
        }

        // 현재 권한 상태 검사
        val denied = permission.count { ContextCompat.checkSelfPermission(this, it.value)  == PackageManager.PERMISSION_DENIED }

        // 동의하지 않은 권한 있을 경우
        if(denied > 0) {
            requestPermissions(permission.values.toTypedArray(), permissionRequestCode)
        }else{
            if(permissionRequestCode == UploadImageType.STORAGE.code){
                openGallery()
            }else if(permissionRequestCode == UploadImageType.CAMERA.code){
                // 1. TakePicture
                pictureUri = createImageFile()
                getTakePicture.launch(pictureUri)   // Require Uri
            }

            uploadImageDialog.dismiss()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //            1. 권한 확인이 다 끝난 후 동의하지 않은것이 있다면 종료
        val count = grantResults.count { it == PackageManager.PERMISSION_DENIED } // 동의 안한 권한의 개수

        if(requestCode == UploadImageType.STORAGE.code) {
            if(count != 0) {
                customToast.showCustomToast("서비스의 필요한 권한입니다. 권한에 동의해주세요.", this)
            }else{
                openGallery()
            }
        }else if(requestCode == UploadImageType.CAMERA.code){
            if(count != 0) {
                customToast.showCustomToast("서비스의 필요한 권한입니다. 권한에 동의해주세요.", this)
            }else{
                // 1. TakePicture
                pictureUri = createImageFile()
                getTakePicture.launch(pictureUri)   // Require Uri
            }
        }

        uploadImageDialog.dismiss()
    }

    // 갤러리 화면 Intent
    private fun openGallery() {
        val gallery = Intent(Intent.ACTION_PICK)
        gallery.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)

        pickImageLauncher.launch(gallery)

    }

    // 카메라에서 찍은 사진 저장 방식 지정
    private fun createImageFile(): Uri? {
        val now = SimpleDateFormat("yyMMdd_HHmmss").format(Date())
        val content = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "img_${now + getRandomString(10)}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpg")
        }
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, content)
    }

    // 임의의 문자열 생성
    private fun getRandomString(length: Int) : String {
        val charset = "ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz0123456789"
        return (1..length)
            .map { charset.random() }
            .joinToString("")
    }

    private fun getEditTextIsEmpty(): Boolean {
        return binding.editTitle.text.toString().trim().isNotEmpty() && binding.editCont.text.toString().trim().isNotEmpty()
    }
}