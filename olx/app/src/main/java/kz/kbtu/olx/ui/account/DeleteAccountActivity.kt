package kz.kbtu.olx.ui.account

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kz.kbtu.olx.MainActivity
import kz.kbtu.olx.R
import kz.kbtu.olx.Utils
import kz.kbtu.olx.databinding.ActivityDeleteAccountBinding

class DeleteAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeleteAccountBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityDeleteAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth.currentUser!!

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        binding.deleteAccountBtn.setOnClickListener {

            onBackPressed()
        }

        binding.deleteAccountBtn.setOnClickListener {

            deleteAccount()
        }
    }


    private fun deleteAccount() {

        Log.d(TAG, "deleteAccount: ")

        progressDialog.setMessage("Deleting Account...")
        progressDialog.show()

        val myUid = firebaseAuth.uid

        firebaseUser.delete()
            .addOnSuccessListener {

                Log.d(TAG, "deleteAccount: Account Deleted...")
                progressDialog.setMessage("Deleting User Ads")

                val refUserAds = FirebaseDatabase.getInstance().getReference("Ads")
                refUserAds.orderByChild("uid").equalTo(myUid)
                    .addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {

                            for (ds in snapshot.children) {

                                ds.ref.removeValue()
                            }
                            progressDialog.setMessage("Deleting User Data")

                            val refUsers = FirebaseDatabase.getInstance().getReference("Users")
                            refUsers.child(myUid!!).removeValue()
                                .addOnSuccessListener {

                                    Log.d(TAG, "deleteAccount: User Data Deleted")
                                    progressDialog.dismiss()
                                    startMainActivity()
                                }
                                .addOnFailureListener { e->

                                    Log.e(TAG, "deleteAccount: onDataChange: ", e)
                                    progressDialog.dismiss()
                                    Utils.toast(this@DeleteAccountActivity, "Failed deleting user data due to ${e.message}")
                                    startMainActivity()
                                }
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
            }
            .addOnFailureListener { e->

                Log.e(TAG, "deleteAccount: ", e)
                progressDialog.dismiss()
                Utils.toast(this, "Failed deleting account due to ${e.message}")
            }
    }

    private fun startMainActivity() {

        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity()
    }

    override fun onBackPressed() {

        startMainActivity()
    }

    private companion object {

        private const val TAG = "DELETE_ACCOUNT_TAG"
    }

}