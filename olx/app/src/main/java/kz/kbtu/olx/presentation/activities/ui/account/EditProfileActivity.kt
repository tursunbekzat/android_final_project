package kz.kbtu.olx.presentation.activities.ui.account


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
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import kz.kbtu.olx.R
import kz.kbtu.olx.Utils
import kz.kbtu.olx.databinding.ActivityProfileEditBinding


class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileEditBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var myUserType = ""
    private var imageUri: Uri? = null
    private var name = ""
    private var dob = ""
    private var email = ""
    private var phoneCode = ""
    private var phoneNumber = ""


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        loadMyInfo()

        setOnClickListener()
    }


    private fun setOnClickListener(){

        binding.toolbarBackBtn.setOnClickListener {

            onBackPressed()
        }

        binding.profileImagePickFab.setOnClickListener {

            imagePickDialog()
        }

        binding.updateBtn.setOnClickListener {

            validateData()
        }
    }

    private fun validateData(){

        name = binding.nameEt.text.toString().trim()
        dob = binding.dobEt.text.toString().trim()
        email = binding.emailEt.text.toString().trim()
        phoneNumber = binding.phoneNumberEt.text.toString().trim()
        phoneCode = binding.phoneCodeTil.selectedCountryCodeWithPlus

        if (imageUri == null) {

            updateProfileDb(null)
        } else {

            uploadProfileImageStorage()
        }
    }


    private fun updateProfileDb(uploadedImageUrl: String?) {

        progressDialog.setMessage("Updating user info")
        progressDialog.show()

        val hashMap = HashMap<String, Any>()
        hashMap["name"]= name
        hashMap["dob"]= dob

        if (uploadedImageUrl != null) {

            hashMap["profileImageUrl"]= uploadedImageUrl
        }

        if (myUserType.equals("Phone", true)) {

            hashMap["email"]= email
        } else if (myUserType.equals("Email", true) || myUserType.equals("Google", true)){

            hashMap["phoneCode"]= phoneCode
            hashMap["phoneNumber"]= phoneNumber
        }

        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(firebaseAuth.uid!!)
            .updateChildren(hashMap)
            .addOnSuccessListener {

                progressDialog.dismiss()
                Utils.toast(this, "Updated...")
                imageUri = null
            }
            .addOnFailureListener { e ->

                Log.e(TAG, "updateUserInfo: ", e)
                progressDialog.dismiss()
                Utils.toast(this, "Failed to update user info due to ${e.message}")
            }
    }


    private fun uploadProfileImageStorage() {

        progressDialog.setMessage("Uploading user profile image")
        progressDialog.show()

        val filePathAndName = "UserProfile/profile_${firebaseAuth.uid}"

        val ref = FirebaseStorage.getInstance().reference.child(filePathAndName)
        ref.putFile(imageUri!!)
            .addOnProgressListener { snapshot ->

                val progress = 100.0*snapshot.bytesTransferred / snapshot.totalByteCount
                progressDialog.setMessage("Uploading profile image. \nProgress: ${progress.toInt()}")
            }
            .addOnSuccessListener { taskSnapshot ->

                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isSuccessful);
                val uploadedImageUrl = uriTask.result.toString()
                if (uriTask.isSuccessful) {

                    updateProfileDb(uploadedImageUrl)
                }
                progressDialog.dismiss()
            }
            .addOnFailureListener {e ->

                Log.e(TAG, "uploadProfileImageStorage: ", e )
                progressDialog.dismiss()
                Utils.toast(this, "Failed upload due to ${e.message}")
            }
    }


    private fun loadMyInfo() {

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dob = "${snapshot.child("dob").value}"
                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val phoneCode = "${snapshot.child("phoneCode").value}"
                    val phoneNumber = "${snapshot.child("phoneNumber").value}"
                    val profileImageUrl = "${snapshot.child("profileImageUrl").value}"
                    var timestamp = "${snapshot.child("timestamp").value}"
                    myUserType = "${snapshot.child("userType").value}"

                    val phone = phoneCode + phoneNumber

                    if (myUserType.equals("Email", true) || myUserType.equals("Google", true)){

                        binding.emailTil.isEnabled = false
                        binding.emailEt.isEnabled = false
                    } else {

                        binding.phoneNumberTil.isEnabled = false
                        binding.phoneNumberEt.isEnabled = false
                        binding.phoneCodeTil.isEnabled = false
                    }

                    binding.emailEt.setText(email)
                    binding.dobEt.setText(dob)
                    binding.nameEt.setText(name)
                    binding.phoneNumberEt.setText(phoneNumber)

                    try {

                        val phoneCodeInt = phoneCode.replace("+", "").toInt()
                        binding.phoneCodeTil.setCountryForPhoneCode(phoneCodeInt)
                    } catch (e: Exception) {

                        Log.e(TAG, "onDataChange: ", e)
                    }

                    try {

                        Glide.with(this@EditProfileActivity)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_person_white)
                            .into(binding.profileIv)
                    } catch (e: Exception) {

                        Log.e(TAG, "onDataChange: ", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }


    private fun imagePickDialog(){

        val popupMenu = PopupMenu(this, binding.profileImagePickFab)

        popupMenu.menu.add(Menu.NONE, 1, 1, "Camera")
        popupMenu.menu.add(Menu.NONE, 2, 2, "Gallery")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener {item->

            val itemId = item.itemId

            if (itemId == 1){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    requestCameraPermissions.launch(arrayOf(Manifest.permission.CAMERA))
                } else {

                    requestCameraPermissions.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }

            } else if (itemId == 2){

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    pickImageGallery()
                } else {

                    requestStoragePermissions.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }
            return@setOnMenuItemClickListener true
        }
    }


    private val requestCameraPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ result ->

            var areAllGranted = true
            for (isGranted in result.values) {

                areAllGranted = areAllGranted && isGranted
            }
            if (areAllGranted) {

                pickImageCamera()
            } else {

                Utils.toast(this, "Camera or Storage or both permissions denied")
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

                try {

                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.ic_person_white)
                        .into(binding.profileIv)
                } catch (e: Exception) {

                    Log.e(TAG, "cameraActivityResultLauncher: ", e)
                }
            } else {

                Utils.toast(this, "Canceled...!")
            }

        }


    private val requestStoragePermissions =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted ->

            if (isGranted){

                pickImageGallery()
            } else {

                Utils.toast(this, "Storage permissions denied...")
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

                try {

                    Glide.with(this)
                        .load(imageUri)
                        .placeholder(R.drawable.ic_person_white)
                        .into(binding.profileIv)
                } catch (e: Exception) {

                    Log.e(TAG, "galleryActivityResultLauncher: ", e)
                }

            } else {

                Utils.toast(this, "Canceled...!")
            }

        }


    private companion object{

        private const val TAG = "PROFILE_EDIT_TAG"
    }
}