package kz.kbtu.olx.presentation.activities.ui.chats

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media
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
import android.Manifest
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.storage.FirebaseStorage
import kz.kbtu.olx.R
import kz.kbtu.olx.Utils
import kz.kbtu.olx.presentation.adapters.ChatAdapter
import kz.kbtu.olx.databinding.ActivityChatBinding
import kz.kbtu.olx.domain.models.Chat
import org.json.JSONObject

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var receiptUid = ""
    private var chatPath = ""
    private var imageUri:Uri? = null
    private var myUid = ""
    private var myName = ""
    private var receiptFcmToken = ""


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()
        receiptUid = intent.getStringExtra("sellerUid")!!
        myUid = firebaseAuth.currentUser!!.uid
        chatPath = Utils.chatPath(receiptUid, myUid)

        loadMyInfo()

        loadReceiptDetails()

        loadMessages()

        binding.toolbarBackBtn.setOnClickListener {

            finish()
        }

        binding.attachFab.setOnClickListener {

            pickImageDialog()
        }

        binding.sendFab.setOnClickListener {

            validateData()
        }
    }

    private fun loadMessages() {

        val messagesArray = ArrayList<Chat>()

        val ref = FirebaseDatabase.getInstance().getReference("Chats")
        ref.child(chatPath)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    messagesArray.clear()

                    for (ds: DataSnapshot in snapshot.children){

                        try {

                            val modelChat = ds.getValue(Chat::class.java)
                            messagesArray.add(modelChat!!)

                        } catch (e: Exception){

                            Log.e(TAG, "onDataChange: ", e)
                        }
                    }

                    val chatAdapter = ChatAdapter(this@ChatActivity, messagesArray)
                    binding.chatRv.adapter = chatAdapter
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }


    private fun loadMyInfo(){

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    myName = "${snapshot.child("name").value}"
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }


    private fun loadReceiptDetails(){

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(receiptUid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    try {

                        var name = "${snapshot.child("name").value}"
                        var imageUrl = "${snapshot.child("profileImageUrl").value}"

                        receiptFcmToken = "${snapshot.child("fcmToken").value}"

                        binding.toolbarTitleTv.text = name

                        try {

                            Glide.with(this@ChatActivity)
                                .load(imageUrl)
                                .placeholder(R.drawable.ic_person_white)
                                .into(binding.toolbarProfileIv)
                        } catch (e: Exception){

                            Log.e(TAG, "onDataChange: ", e)
                        }
                    } catch (e: Exception){

                        Log.e(TAG, "onDataChange: ", e)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })

    }


    private fun pickImageDialog(){

        val popupMenu = PopupMenu(this, binding.attachFab)
        popupMenu.menu.add(Menu.NONE, 1, 1, "CAMERA")
        popupMenu.menu.add(Menu.NONE, 2, 2, "GALLERY")

        popupMenu.show()

        popupMenu.setOnMenuItemClickListener { menuItem ->

            val itemId = menuItem.itemId

            if (itemId == 1) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    requestCameraPermissions.launch(arrayOf(Manifest.permission.CAMERA))
                } else {

                    requestCameraPermissions.launch(arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
            } else if (itemId == 2) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    pickImageGallery()
                } else {

                    requestCameraPermissions.launch(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                }
            }
            true
        }
    }



    private val requestCameraPermissions = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){ result ->

        var areAllGranted = true

        for (isGranted in result.values){
            areAllGranted = isGranted && areAllGranted
        }
        
        if (areAllGranted){

            Log.d(TAG, "requestCameraPermissions: All permissions e.g. Camera and Storage granted")
        } else {

            Log.d(TAG, "requestCameraPermissions: All permissions or Camera and Storage denied")
            Utils.toast(this, "All permissions or Camera and Storage denied")
        }
    }


    private val requestStoragePermissions = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->

        if (isGranted){

            pickImageGallery()
            Log.d(TAG, "requestCameraPermissions: All permissions e.g. Camera and Storage granted")
        } else {

            Utils.toast(this, "Permission denied...")
        }
    }

    private fun pickImageCamera(){

        val contentValues = ContentValues()
        contentValues.put(Media.TITLE, "THE_IMAGE_TITLE")
        contentValues.put(Media.DESCRIPTION, "THE_IMAGE_DESCRIPTION")

        imageUri = contentResolver.insert(Media.EXTERNAL_CONTENT_URI, contentValues)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        cameraActivityResultLauncher.launch(intent)
    }


    private val cameraActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->

        if (result.resultCode == RESULT_OK){

            uploadToFirebaseStorage()
        } else {

            Utils.toast(this, "Canceled...")
        }

    }


    private fun pickImageGallery(){

        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galleryActivityResultLauncher.launch(intent)
    }


    private val galleryActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){ result ->

        if (result.resultCode == RESULT_OK){

            val data = result.data
            imageUri = data!!.data
            uploadToFirebaseStorage()
        } else {

            Utils.toast(this, "Canceled...")
        }
    }


    private fun uploadToFirebaseStorage(){

        progressDialog.setMessage("Uploading image...")
        progressDialog.show()

        val timestamp = Utils.getTimestamp()

        val filePAthAndName = "ChatImages/$timestamp"

        val storageRef = FirebaseStorage.getInstance().getReference(filePAthAndName)
        storageRef.putFile(imageUri!!)
            .addOnProgressListener { snapshot ->

                val progress = 100.0*snapshot.bytesTransferred / snapshot.totalByteCount

                progressDialog.setMessage("Uploading image: Progress ${progress.toUInt()} %")
            }
            .addOnSuccessListener { taskSnapshot ->

                val uriTask = taskSnapshot.storage.downloadUrl

                while (!uriTask.isSuccessful);

                val uploadedImageUrl = uriTask.result.toString()

                if (uriTask.isSuccessful){

                    sendMessage(Utils.MESSAGE_TYPE_IMAGE, uploadedImageUrl, timestamp)
                }
            }
            .addOnFailureListener { e ->

                Log.e(TAG, "uploadToFirebaseStorage: ", e)
                Utils.toast(this, "Failed upload image due to ${e.message}")
            }
    }


    private fun validateData(){

        val message = binding.messageEt.text.toString().trim()
        val timestamp = Utils.getTimestamp()

        if (message.isEmpty()){

            Utils.toast(this, "Enter message to sent...")
        } else {

            sendMessage(Utils.MESSAGE_TYPE_TEXT, message, timestamp)
        }
    }


    private fun sendMessage(messageType: String, message: String, timestamp: Long){

        progressDialog.setMessage("Sending message...")
        progressDialog.show()

        val refChat = FirebaseDatabase.getInstance().getReference("Chats")

        val keyId = "${refChat.push().key}"

        val hashMap = HashMap<String, Any>()

        hashMap["messageId"] = "$keyId"
        hashMap["messageType"] = "$messageType"
        hashMap["message"] = "$message"
        hashMap["fromUid"] = "${myUid}"
        hashMap["toUid"] = "$receiptUid"
        hashMap["timestamp"] = timestamp

        refChat.child(chatPath)
            .child(keyId)
            .setValue(hashMap)
            .addOnSuccessListener {

                progressDialog.dismiss()

                binding.messageEt.setText("")

                if (messageType == Utils.MESSAGE_TYPE_TEXT){

                    prepareNotification(message)
                } else {

                    prepareNotification("Send an attachment")
                }
            }
            .addOnFailureListener { e ->

                Log.e(TAG, "sendMessage: ", e)
                
                progressDialog.dismiss()
                Utils.toast(this, "Failed to send due to ${e.message}")
            }
    }


    private fun prepareNotification(message: String){

        val notificationJo = JSONObject()
        val notificationDataJo = JSONObject()
        val notificationNotificationJo = JSONObject()

        try {

            notificationDataJo.put("notificationType", "${Utils.NOTIFICATION_TYPE_NEW_MESSAGE}")
            notificationDataJo.put("senderUid", "${firebaseAuth.uid}")

            notificationNotificationJo.put("title", "$myName")
            notificationNotificationJo.put("body", "$message")
            notificationNotificationJo.put("sound", "default")

            notificationJo.put("to", "$receiptFcmToken")
            notificationJo.put("notification", notificationNotificationJo)
            notificationJo.put("data", notificationDataJo)
        } catch (e: Exception){

            Log.e(TAG, "prepareNotification: ", e)
        }

        sendFcmNotification(notificationJo)
    }


    private fun sendFcmNotification(notificationJo: JSONObject){

        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            "http://fcm.googleapis.com/fcm/send",
            notificationJo,
            Response.Listener {

                Log.d(TAG, "sendFcmNotification: Notification send $it")
            },
            Response.ErrorListener { e ->

                Log.e(TAG, "sendFcmNotification: ", e)
            }
        ){
            override fun getHeaders(): MutableMap<String, String> {

                val headers = HashMap<String, String>()

                headers["Content-Type"] = "application/json"
                headers["Authorization-Type"] = "key=${Utils.FCM_SERVER_KEY}"

                return headers
            }
        }

        Volley.newRequestQueue(this).add(jsonObjectRequest)
    }

    private companion object {

        private const val TAG = "CHAT_ACTIVITY_TAG"
    }
}