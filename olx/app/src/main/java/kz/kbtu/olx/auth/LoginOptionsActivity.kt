package kz.kbtu.olx.auth

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kz.kbtu.olx.MainActivity
import kz.kbtu.olx.Utils
import kz.kbtu.olx.databinding.ActivityLoginOptionsBinding

class LoginOptionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginOptionsBinding
    private lateinit var progressDIalog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mGoogleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressDIalog = ProgressDialog(this)
        progressDIalog.setTitle("Please wait...")
        progressDIalog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("1037131843142-6c7nnaps8lll649aj4n71sq7t0m8v8mc.apps.googleusercontent.com")
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        setOnClickListeners()
    }


    private fun setOnClickListeners(){

        binding.closeBtn.setOnClickListener{
            onBackPressed()
        }

        binding.loginEmailBtn.setOnClickListener{
            startActivity(Intent(this, LoginEmailActivity::class.java))
        }

        binding.loginGoogleBtn.setOnClickListener {
            beginGoogleLogin()
        }

        binding.loginPhoneBtn.setOnClickListener {
            startActivity(Intent(this, LoginPhoneActivity::class.java))
        }
    }


    private fun beginGoogleLogin(){

        val googleSignInIntent = mGoogleSignInClient.signInIntent
        googleSignInARL.launch(googleSignInIntent)
    }


    private val googleSignInARL = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()){ result ->

            if (result.resultCode == Activity.RESULT_OK) {

                val data = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)

                try {

                    val account = task.getResult(ApiException::class.java)
                    firebaseAuthWithGoogleAccount(account?.idToken)
                }
                catch (e: ApiException){

                    Log.e(TAG, "googleSignInARL: ", e)
                    Utils.toast(this, "${e.message}")
                }
            }
            else {

                Utils.toast(this, "Cancelled...!")
            }
        }

    private fun firebaseAuthWithGoogleAccount(idToken: String?) {

        if (idToken.isNullOrEmpty()) {

            Utils.toast(this, "ID Token is null or empty")

            return
        }

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {authResult->
                if (authResult.additionalUserInfo!!.isNewUser){

                    updateUserInfoDb()
                }
                else {

                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener { e->

                Log.e(TAG, "firebaseAuthWithGoogleAccount: ", e)
                Utils.toast(this, "${e.message}")
            }
    }

    private fun updateUserInfoDb(){

        progressDIalog.setMessage("Saving User Info...")
        progressDIalog.show()

        val timestamp = Utils.getTimestamp()
        val registeredUserEmail = firebaseAuth.currentUser?.email
        val registeredUserUid = firebaseAuth.uid
        val name = firebaseAuth.currentUser?.displayName

        val hashMap = HashMap<String, Any?>()
        hashMap["name"]="$name"
        hashMap["phoneCode"]=""
        hashMap["phoneNumber"]=""
        hashMap["profileImageUrl"]=""
        hashMap["dob"]=""
        hashMap["userType"]="Google"
        hashMap["typingTo"]=""
        hashMap["timestamp"]=timestamp
        hashMap["onlineStatus"]=true
        hashMap["email"]="$registeredUserEmail"
        hashMap["uid"]="$registeredUserUid"

        val reference = FirebaseDatabase.getInstance().getReference("Users")
        reference.child(registeredUserUid!!)
            .setValue(hashMap)
            .addOnSuccessListener {

                progressDIalog.dismiss()
                startActivity(Intent(this, MainActivity::class.java))
                finishAffinity()
            }
            .addOnFailureListener { e ->

                progressDIalog.dismiss()
                Log.e(TAG, "updateUserInfoDb: ", e)
                Utils.toast(this, "Failed to save user info due to ${e.message}")
            }
    }

    private companion object{

        private const val TAG = "LOGIN_OPTIONS_TAG"
    }
}