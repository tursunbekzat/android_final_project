package kz.kbtu.olx.ui.account

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kz.kbtu.olx.Utils
import kz.kbtu.olx.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChangePasswordBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseUser: FirebaseUser

    private var currentPassword = ""
    private var newPassword = ""
    private var confirmPassword = ""

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.toolbarBackBtn.setOnClickListener {

            onBackPressed()
        }

        binding.changePasswordBtn.setOnClickListener {

            validateData()
        }
    }

    private fun validateData(){

        currentPassword = binding.currentPasswordEt.text.toString().trim()
        newPassword = binding.newPasswordEt.text.toString().trim()
        confirmPassword = binding.confirmPasswordEt.text.toString().trim()

        if (currentPassword.isEmpty()) {

            binding.currentPasswordEt.error = "Enter Current Password"
            binding.currentPasswordEt.requestFocus()
        } else if (newPassword.isEmpty()) {

            binding.newPasswordEt.error = "Enter New Password"
            binding.newPasswordEt.requestFocus()
        } else if (confirmPassword.isEmpty()) {

            binding.confirmPasswordEt.error = "Confirm New Password"
            binding.confirmPasswordEt.requestFocus()
        } else if (newPassword != confirmPassword) {

            binding.confirmPasswordEt.error = "Password does not match"
            binding.confirmPasswordEt.requestFocus()
        } else {

            authenticateUserForPassword()
        }
    }

    private fun authenticateUserForPassword(){

        progressDialog.setMessage("Authentication User")
        progressDialog.show()

        val authCredential = EmailAuthProvider.getCredential(firebaseUser.email.toString(), currentPassword)
        firebaseUser.reauthenticate(authCredential)
            .addOnSuccessListener {

                updatePassword()
            }
            .addOnFailureListener { e ->

                Log.e(TAG, "authenticateUserForPassword: ", e)
                progressDialog.dismiss()
                Utils.toast(this, "Failed to authenticate due to ${e.message}")
            }
    }

    private fun updatePassword() {

        progressDialog.setMessage("Changing password")
        progressDialog.show()

        firebaseUser.updatePassword(newPassword)
            .addOnSuccessListener {

                progressDialog.dismiss()
                Utils.toast(this, "Password updated...")
            }
            .addOnFailureListener { e ->

                Log.e(TAG, "updatePassword: ", e)
                progressDialog.dismiss()
                Utils.toast(this, "Failed to update password due to ${e.message}")
            }
    }


    private companion object {

        private const val TAG = "CHANGE_PASSWORD_TAG"
    }

}