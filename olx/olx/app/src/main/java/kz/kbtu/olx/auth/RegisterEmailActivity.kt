package kz.kbtu.olx.auth

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kz.kbtu.olx.MainActivity
import kz.kbtu.olx.Utils
import kz.kbtu.olx.databinding.ActivityRegisterEmailBinding

class RegisterEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterEmailBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)


        binding.toolbarBackBtn.setOnClickListener{
            onBackPressed()
        }

        binding.haveAccountTv.setOnClickListener{
            onBackPressed()
        }

        binding.registerBtn.setOnClickListener{
            isValidate()
        }
    }

    private var email = ""
    private var password = ""
    private var confPassword = ""

    private fun isValidate(){
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()
        confPassword = binding.cPasswordEt.text.toString().trim()

        Log.d(TAG, "validateData: email: $email")
        Log.d(TAG, "validateData: password: $password")
        Log.d(TAG, "validateData: confirm password: $confPassword")

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEt.error = "Invalid Email Format"
            binding.emailEt.requestFocus()
        }
        else if (password.isEmpty()) {
            binding.passwordEt.error = "Enter Password"
            binding.passwordEt.requestFocus()
        }
        else if (confPassword.isEmpty()){
            binding.cPasswordEt.error = "Enter Password"
            binding.cPasswordEt.requestFocus()
        }
        else if (password != confPassword){
            binding.cPasswordEt.error = "Password Doesn't Match"
            binding.cPasswordEt.requestFocus()
        }
        else {
            registerUser()
        }

    }

    private fun registerUser(){
        Log.d(TAG, "registerUser: ")

        progressDialog.setMessage("Creating Account")
        progressDialog.show()

//        try {
//
//            val ref = FirebaseDatabase.getInstance().getReference("Users")
//            ref.orderByChild("email").equalTo(email)
//                .addListenerForSingleValueEvent(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        if (snapshot.exists()) {
//                            // User with the given email exists
//                            Log.d(TAG, "onDataChange: User exists")
//                            progressDialog.dismiss()
//                            startActivity(Intent(this@RegisterEmailActivity, MainActivity::class.java))
//                            finishAffinity()
//                        } else {
//                            // User does not exist
//                            Log.d(TAG, "onDataChange: User does not exist")
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//                        // Handle error if needed
//                        Log.e(TAG, "onCancelled: Error reading database", error.toException())
//                    }
//                })
//        } catch (e: Exception){
//
//            Log.e(TAG, "registerUser: ", e)
//        }

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d(TAG, "registerUser: Register Success")
                updateUserInfo()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "registerUser: ", e)
                progressDialog.dismiss()
                Utils.toast(this, "Failed to create account due to ${e.message}")
            }
        }

    private fun updateUserInfo(){
        Log.d(TAG, "updateUserInfo: ")
        progressDialog.setMessage("Saving User Info")

        val timestamp = Utils.getTimestamp()
        val registeredUserEmail = firebaseAuth.currentUser!!.email
        val registeredUserUid = firebaseAuth.uid

        val hashMap = HashMap<String, Any>()
        hashMap["name"]=""
        hashMap["phoneCode"]=""
        hashMap["phoneNumber"]=""
        hashMap["profileImageUrl"]=""
        hashMap["dob"]=""
        hashMap["userType"]="Email"
        hashMap["typingTo"]=""
        hashMap["timestamp"]=timestamp
        hashMap["onlineStatus"]=true
        hashMap["email"]="$registeredUserEmail"
        hashMap["uid"]="$registeredUserUid"

        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(registeredUserUid!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                Log.d(TAG, "updateUserInfo: User Registered...")
                progressDialog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "updateUserInfo: ", e)
                progressDialog.dismiss()
                Utils.toast(this, "Failed to save user info due to ${e.message}")
            }
    }

    private companion object{
        private const val TAG = "REGISTER_TAG"
    }
}