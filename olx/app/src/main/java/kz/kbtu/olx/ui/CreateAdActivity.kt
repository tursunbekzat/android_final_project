package kz.kbtu.olx.ui


import android.Manifest
import android.app.Activity
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kz.kbtu.olx.R
import kz.kbtu.olx.Utils
import kz.kbtu.olx.adapter.AdapterImagePicked
import kz.kbtu.olx.databinding.ActivityCreateAdBinding


class CreateAdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAdBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth


    private var imageUri: Uri? = null
    private lateinit var imagePickedArrayList: ArrayList<ModelImagePicked>
    private lateinit var adapterImagePicked: AdapterImagePicked


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCreateAdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        val adapterCategory = ArrayAdapter(this, R.layout.row_category_act, Utils.categories)
        binding.categoryAct.setAdapter(adapterCategory)

        val adapterCondition = ArrayAdapter(this, R.layout.row_condition_act, Utils.conditions)
        binding.conditionAct.setAdapter(adapterCondition)

        imagePickedArrayList = ArrayList()
        loadImages()

        binding.toolbarBackBtn.setOnClickListener {

            onBackPressed()
        }

        binding.toolbarAdBtn.setOnClickListener {

            showImagePickOptions()
        }

        binding.postAdBtn.setOnClickListener {

            validateData()
        }
    }


    private fun loadImages(){

        Log.d(TAG, "loadImages: ")

        adapterImagePicked = AdapterImagePicked(this, imagePickedArrayList)
        binding.imagesRv.adapter = adapterImagePicked
    }


    private fun showImagePickOptions(){

        Log.d(TAG, "showImagePickOptions: ")

        val popupMenu = PopupMenu(this, binding.toolbarAdBtn)
        popupMenu.menu.add(Menu.NONE, 1, 1, "Camera")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Galery")
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { item ->

            val itemId = item.itemId
            if (itemId == 1){

                Log.d(TAG, "imagePickDialog: Camera Clicked, check if camera permission(s) granted or not")
                if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.TIRAMISU) {

                    val cameraPermissions = arrayOf(Manifest.permission.CAMERA)
                    requestCameraPermission.launch(cameraPermissions)
                } else {

                    val cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestCameraPermission.launch(cameraPermissions)
                }
            } else if (itemId == 2){

                Log.d(TAG, "imagePickDialog: Gallery Clicked, check if storage permission(s) granted or not")

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    pickImageGallery()
                } else {

                    val storagePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE
                    requestStoragePermission.launch(storagePermission)
                }
            }
            true
        }
    }


    private val requestStoragePermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted->

            Log.d(TAG, "requestStorePermission: isGranted: $isGranted")

            if (isGranted) {

                pickImageGallery()
            } else {

                Utils.toast(this, "Storage permission denied...")
            }
    }


    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result->

            Log.d(TAG, "requestStorePermission: result: $result")

            var areAllGranted = true

            for (isGranted in result.values) {

                areAllGranted = areAllGranted && isGranted
            }

            if (areAllGranted) {

                pickImageCamera()
            } else {

                Utils.toast(this, "Camera or Storage or both permissions denied...")
            }
    }


    private fun pickImageCamera() {
        Log.d(TAG, "pickImageCamera: ")

        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_image_title")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp_image_description")

        imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }


    private val cameraActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->

            Log.d(TAG, "cameraActivityResultLauncher: ")



            if (result.resultCode == Activity.RESULT_OK) {

                Log.d(TAG, "cameraActivityResultLauncher: Image Captured: imageUri: $imageUri")

                val timestamp = "${Utils.getTimstamp()}"
                val modelImagePicked = ModelImagePicked(timestamp, imageUri, null, false)
                imagePickedArrayList.add(modelImagePicked)

                loadImages()
            } else {
                Utils.toast(this, "Canceled...!")
            }

        }

    private fun pickImageGallery() {
        Log.d(TAG, "pickImageGallery: ")

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }


    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val data = result.data
                imageUri = data!!.data

                val timestamp = "${Utils.getTimstamp()}"
                val modelImagePicked = ModelImagePicked(timestamp, imageUri, null, false)

                imagePickedArrayList.add(modelImagePicked)

                loadImages()
            } else {

                Utils.toast(this, "Canceled...!")
            }

        }


    private var brand = ""
    private var category = ""
    private var condition = ""
    private var address = ""
    private var title = ""
    private var price = ""
    private var description = ""
    private var latitude = 0.0
    private var longitude = 0.0


    private fun validateData(){

        Log.d(TAG, "validateData: ")

        brand = binding.brandEt.text.toString().trim()
        category = binding.categoryAct.text.toString().trim()
        condition = binding.conditionAct.text.toString().trim()
        address = binding.locationAct.text.toString().trim()
        title = binding.titleEt.text.toString().trim()
        description = binding.descriptionEt.text.toString().trim()
        price = binding.priceEt.text.toString().trim()

        if (brand.isEmpty()) {

            binding.brandEt.error = "Enter Brand"
            binding.brandEt.requestFocus()
        } else if (category.isEmpty()){

            binding.categoryAct.error = "Choose Category"
            binding.categoryAct.requestFocus()
        } else if (condition.isEmpty()){

            binding.conditionAct.error = "Choose Condition"
            binding.conditionAct.requestFocus()
        } else if (title.isEmpty()){

            binding.titleEt.error = "Enter Title"
            binding.titleEt.requestFocus()
        } else if (description.isEmpty()){

            binding.descriptionEt.error = "Enter Description"
            binding.descriptionEt.requestFocus()
        } else {

            postAd()
        }
    }

    private fun postAd(){

        Log.d(TAG, "postAd: ")

        progressDialog.setMessage("Publishing Ad")
        progressDialog.show()

        val timestamp = Utils.getTimstamp()

        val refAds = FirebaseDatabase.getInstance().getReference("Ads")

        val keyId = refAds.push().key

        val hashMap = HashMap<String, Any>()
        hashMap["id"]= "$keyId"
        hashMap["uid"]= "$firebaseAuth.uid"
        hashMap["brand"]= brand
        hashMap["category"]= category
        hashMap["condition"]= condition
        hashMap["address"]= address
        hashMap["price"]= price
        hashMap["title"]= title
        hashMap["description"]= description
        hashMap["status"]= Utils.ADD_STATUS_AVAILABLE
        hashMap["timestamp"]= timestamp
        hashMap["latitude"]= latitude
        hashMap["longitude"]= longitude

        refAds.child(keyId!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {

                Log.d(TAG, "postAd: Publishing...")
                uploadImagesStore(keyId)
            }
            .addOnFailureListener { e ->

                Log.e(TAG, "postAd: ", e)
                progressDialog.dismiss()
                Utils.toast(this, "Failed due to ${e.message}")
            }
    }


    private fun uploadImagesStore(adId: String) {


        for (i in imagePickedArrayList.indices) {

            val modelImagePicked = imagePickedArrayList[i]
            val imageName = modelImagePicked.id
            val filePathAndName = "Ads/$imageName"
            val imageIndexForProgress = i + 1

            val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
            storageReference.putFile(modelImagePicked.imageUri!!)
                .addOnProgressListener { snapshot ->

                    val progress = 100.0 *snapshot.bytesTransferred / snapshot.totalByteCount
                    Log.d(TAG, "uploadImagesStore: progress: $progress")
                    val message = "Uploading $imageIndexForProgress of ${imagePickedArrayList.size} images ... Progress ${progress.toInt()}"
                    Log.d(TAG, "uploadImagesStore: message: $message")

                    progressDialog.setMessage(message)
                    progressDialog.show()

                }
                .addOnSuccessListener { taskSnapshot ->

                    Log.d(TAG, "uploadImagesStore: Uploading image...")

                    val uriTask = taskSnapshot.storage.downloadUrl

                    while (!uriTask.isSuccessful);
                    val uploadedImageUrl = uriTask.result

                    if (uriTask.isSuccessful) {

                        val hashMap = HashMap<String, Any>()
                        hashMap["id"]= modelImagePicked.id
                        hashMap["imageUrl"]= "$uploadedImageUrl"

                        val ref = FirebaseDatabase.getInstance().getReference("Ads")
                        ref.child(adId).child("Images")
                            .child(imageName)
                            .updateChildren(hashMap)
                    }
                    progressDialog.dismiss()
                }
                .addOnFailureListener { e ->

                    Log.e(TAG, "uploadImagesStore: ", e)
                    progressDialog.dismiss()
                }
        }
    }

    private companion object {

        private const val TAG = "CREATE_AD_TAG"
    }
}