package kz.kbtu.olx.presentation.activities.ui.account

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth
import kz.kbtu.olx.Utils
import kz.kbtu.olx.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog

    private var email = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)


        binding.toolbarBackBtn.setOnClickListener {

            onBackPressed()
        }

        binding.submitBtn.setOnClickListener {

            validateData()
        }
    }


    private fun validateData(){

        email = binding.emailEt.text.toString().trim()

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){

            binding.emailEt.error = "Invalid Email Format"
            binding.emailEt.requestFocus()
        } else {

            sendPasswordRecoveryInstructions()
        }
    }


    private fun sendPasswordRecoveryInstructions() {

        progressDialog.setMessage("Sending password reset instructions to $email")
        progressDialog.show()

        firebaseAuth.sendPasswordResetEmail(email)
            .addOnSuccessListener {

                progressDialog.dismiss()
                Utils.toast(this, "Instructions to reset password send to $email")
            }
            .addOnFailureListener { e ->

                Log.e(TAG, "sendPasswordRecoveryInstructions: ", e)
                progressDialog.dismiss()
                Utils.toast(this, "Failed resending password due to ${e.message}")
            }
    }


    private companion object{

        private const val TAG = "FORGOT_PASSWORD_TAG"
    }
}
