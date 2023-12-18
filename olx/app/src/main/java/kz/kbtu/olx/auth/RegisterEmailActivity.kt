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

        setOnClickListeners()
    }


    private fun setOnClickListeners(){

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

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {

                updateUserInfo()
            }
            .addOnFailureListener { e ->

                Log.e(TAG, "registerUser: ", e)
                progressDialog.dismiss()
                Utils.toast(this, "Failed to create account due to ${e.message}")
            }
        }


    private fun updateUserInfo(){

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