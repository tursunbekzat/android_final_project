package kz.kbtu.olx.auth

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import kz.kbtu.olx.MainActivity
import kz.kbtu.olx.UI.ForgotPasswordActivity
import kz.kbtu.olx.Utils
import kz.kbtu.olx.databinding.ActivityLoginEmailBinding

class LoginEmailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginEmailBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.toolbarBackBtn.setOnClickListener{
            onBackPressed()
        }

        binding.noAccountTv.setOnClickListener{
            startActivity(Intent(this, RegisterEmailActivity::class.java))
        }

        binding.forgotPasswordTv.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.loginBtn.setOnClickListener{
            validateData()
        }
    }

    private var email = ""
    private var password = ""

    private fun validateData(){
        email = binding.emailEt.text.toString().trim()
        password = binding.passwordEt.text.toString().trim()

        Log.d(TAG, "validateData: email: $email")
        Log.d(TAG, "validateData: password: $password")

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEt.error = "Invalid Email Format"
            binding.emailEt.requestFocus()
        }
        else if (password.isEmpty()) {
            binding.passwordEt.error = "Enter Password"
        }
        else {

            loginUser()
        }

    }

    private fun loginUser(){

        Log.d(TAG, "loginUser: ")
        progressDialog.setMessage("Logging In")
        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.d(TAG, "loginUser: Logged In...")
                startActivity(Intent(this, MainActivity::class.java))
            }
            .addOnFailureListener { e->
                Log.e(TAG, "loginUser: ", e)
                progressDialog.dismiss()

                Utils.toast(this, "Unable to login due to ${e.message}")
            }
    }


    private companion object{
        private const val TAG = "LOGIN_TAG"
    }
}