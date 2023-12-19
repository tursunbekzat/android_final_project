package kz.kbtu.olx.presentation.activities.ui.sell


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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kz.kbtu.olx.R
import kz.kbtu.olx.Utils
import kz.kbtu.olx.presentation.adapters.ImagePickedAdapter
import kz.kbtu.olx.databinding.ActivityCreateAdBinding
import kz.kbtu.olx.domain.models.ImagePicked


class CreateAdActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateAdBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var imagePickedAdapter: ImagePickedAdapter
    private lateinit var imagePickedArrayList: ArrayList<ImagePicked>


    private var imageUri: Uri? = null
    private var isEditMode = false
    private var adIdForEditing = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCreateAdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        isEditMode = intent.getBooleanExtra("isEditMode", false)

        if (isEditMode) {

            adIdForEditing = intent.getStringExtra("adId") ?: ""

            loadAdDetails()

            binding.toolbarTitleTv.text = "Update Ad"
            binding.postAdBtn.text = "Update Ad"
        } else {

            binding.toolbarTitleTv.text = "Create Ad"
            binding.postAdBtn.text = "Post Ad"
        }

        val adapterCategory = ArrayAdapter(this, R.layout.row_category_act, Utils.categories)
        binding.categoryAct.setAdapter(adapterCategory)

        val adapterCondition = ArrayAdapter(this, R.layout.row_condition_act, Utils.conditions)
        binding.conditionAct.setAdapter(adapterCondition)

        imagePickedArrayList = ArrayList()
        loadImages()

       setOnClickListeners()
    }


    private fun setOnClickListeners(){

        binding.toolbarBackBtn.setOnClickListener {

            onBackPressed()
        }

        binding.toolbarAdBtn.setOnClickListener {

            showImagePickOptions()
        }

        binding.locationAct.setOnClickListener {

            val intent = Intent(this, LocationPickerActivity::class.java)
            locationPickerActivityResultLauncherActivity.launch(intent)
        }

        binding.postAdBtn.setOnClickListener {

            validateData()
        }
    }


    private val locationPickerActivityResultLauncherActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val data = result.data
                if (data != null) {

                    latitude = data.getDoubleExtra("latitude", 0.0)
                    longitude = data.getDoubleExtra("longitude", 0.0)
                    address = data.getStringExtra("address") ?: ""

                    binding.locationAct.setText(address)
                } else {

                    Utils.toast(this, "Cancelled...!")
                }

            }
        }
    private fun loadImages(){

        imagePickedAdapter = ImagePickedAdapter(this, imagePickedArrayList, adIdForEditing)
        binding.imagesRv.adapter = imagePickedAdapter
    }


    private fun showImagePickOptions(){

        val popupMenu = PopupMenu(this, binding.toolbarAdBtn)
        popupMenu.menu.add(Menu.NONE, 1, 1, "Camera")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Gallery")
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener { item ->

            val itemId = item.itemId
            if (itemId == 1){

                if (Build.VERSION.SDK_INT >=  Build.VERSION_CODES.TIRAMISU) {

                    val cameraPermissions = arrayOf(Manifest.permission.CAMERA)
                    requestCameraPermission.launch(cameraPermissions)
                } else {

                    val cameraPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    requestCameraPermission.launch(cameraPermissions)
                }
            } else if (itemId == 2){

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

            if (isGranted) {

                pickImageGallery()
            } else {

                Utils.toast(this, "Storage permission denied...")
            }
    }


    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result->

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

            if (result.resultCode == Activity.RESULT_OK) {

                val timestamp = "${Utils.getTimestamp()}"
                val modelImagePicked = ImagePicked(timestamp, imageUri, null, false)
                imagePickedArrayList.add(modelImagePicked)

                loadImages()
            } else {

                Utils.toast(this, "Canceled...!")
            }

        }

    private fun pickImageGallery() {

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }


    private val galleryActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val data = result.data
                imageUri = data!!.data

                val timestamp = "${Utils.getTimestamp()}"
                val modelImagePicked = ImagePicked(timestamp, imageUri, null, false)

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

            if (isEditMode) {

                updateAd()
            } else {

                postAd()
            }
        }
    }

    private fun postAd(){

        progressDialog.setMessage("Publishing Ad")
        progressDialog.show()

        val timestamp = Utils.getTimestamp()

        val refAds = FirebaseDatabase.getInstance().getReference("Ads")

        val keyId = refAds.push().key

        val hashMap = HashMap<String, Any>()
        hashMap["id"]= "$keyId"
        hashMap["uid"]= "${firebaseAuth.uid}"
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

                uploadImagesStore(keyId)
            }
            .addOnFailureListener { e ->

                Log.e(TAG, "postAd: ", e)
                progressDialog.dismiss()
                Utils.toast(this, "Failed due to ${e.message}")
            }
    }


    private fun updateAd(){

        progressDialog.setMessage("Updating Ad")
        progressDialog.show()

        val hashMap = HashMap<String, Any>()
        hashMap["brand"]= brand
        hashMap["category"]= category
        hashMap["condition"]= condition
        hashMap["address"]= address
        hashMap["price"]= price
        hashMap["title"]= title
        hashMap["description"]= description
        hashMap["latitude"]= latitude
        hashMap["longitude"]= longitude

        val ref = FirebaseDatabase.getInstance().getReference("Ads")
        ref.child(adIdForEditing)
            .updateChildren(hashMap)
            .addOnSuccessListener {

                progressDialog.dismiss()
                uploadImagesStore(adIdForEditing)
                Utils.toast(this, "Successfully Updated")
            }
            .addOnFailureListener { e ->

                Log.e(TAG, "updateAd: ", e)
                progressDialog.dismiss()
                Utils.toast(this, "Failed to update due to ${e.message}")
            }
    }


    private fun uploadImagesStore(adId: String) {


        for (i in imagePickedArrayList.indices) {

            val modelImagePicked = imagePickedArrayList[i]

            if (!modelImagePicked.fromInternet){

                val imageName = modelImagePicked.id
                val filePathAndName = "Ads/$imageName"
                val imageIndexForProgress = i + 1

                val storageReference = FirebaseStorage.getInstance().getReference(filePathAndName)
                storageReference.putFile(modelImagePicked.imageUri!!)
                    .addOnProgressListener { snapshot ->

                        val progress = 100.0 *snapshot.bytesTransferred / snapshot.totalByteCount
                        val message = "Uploading $imageIndexForProgress of ${imagePickedArrayList.size} images ... Progress ${progress.toInt()}"

                        progressDialog.setMessage(message)
                        progressDialog.show()

                    }
                    .addOnSuccessListener { taskSnapshot ->

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
    }


    private fun loadAdDetails(){

        val ref = FirebaseDatabase.getInstance().getReference("Ads")
        ref.child(adIdForEditing)
            .addListenerForSingleValueEvent(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    val brand = "${snapshot.child("brand").value}"
                    val category = "${snapshot.child("category").value}"
                    val condition = "${snapshot.child("condition").value}"
                    val description = "${snapshot.child("description").value}"
                    val price = "${snapshot.child("price").value}"
                    val title = "${snapshot.child("title").value}"
                    val address = "${snapshot.child("address").value}"

                    latitude = (snapshot.child("latitude").value as Double) ?: 0.0
                    longitude = (snapshot.child("longitude").value as Double) ?: 0.0

                    binding.brandEt.setText(brand)
                    binding.categoryAct.setText(category, false)
                    binding.conditionAct.setText(condition, false)
                    binding.locationAct.setText(address)
                    binding.titleEt.setText(title)
                    binding.priceEt.setText(price)
                    binding.descriptionEt.setText(description)

                    var refImages = snapshot.child("Images").ref
                    refImages.addListenerForSingleValueEvent(object : ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {

                            for (ds in snapshot.children){

                                val id = "${ds.child("id").value}"
                                val imageUrl = "${ds.child("imageUrl").value}"

                                val modelImagePicked = ImagePicked(id, null, imageUrl, true)
                                imagePickedArrayList.add(modelImagePicked)
                            }

                            try {

                                loadImages()
                            } catch (e: Exception){

                                Log.e(TAG, "onDataChange: LoadImages Failed due to", e)
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {}
                    })
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }


    private companion object {

        private const val TAG = "CREATE_AD_TAG"
    }
}