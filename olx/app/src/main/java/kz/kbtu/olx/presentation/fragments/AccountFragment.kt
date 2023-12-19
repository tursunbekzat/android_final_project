package kz.kbtu.olx.presentation.fragments


import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kz.kbtu.olx.MainActivity
import kz.kbtu.olx.R
import kz.kbtu.olx.Utils
import kz.kbtu.olx.databinding.AvatarBinding
import kz.kbtu.olx.databinding.FragmentAccountBinding
import kz.kbtu.olx.presentation.activities.ui.account.ChangePasswordActivity
import kz.kbtu.olx.presentation.activities.ui.account.DeleteAccountActivity
import kz.kbtu.olx.presentation.activities.ui.account.EditProfileActivity


class AccountFragment : Fragment() {

    private lateinit var binding: FragmentAccountBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var mContext: Context
    private lateinit var progressDialog: ProgressDialog


    override fun onAttach(context: Context) {

        mContext = context
        super.onAttach(context)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentAccountBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        progressDialog = ProgressDialog(mContext)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        loadMyInfo()

        setOnClickListeners()
    }


    private fun setOnClickListeners(){

        binding.logoutCv.setOnClickListener {

            firebaseAuth.signOut()
            startActivity(Intent(mContext, MainActivity::class.java))
            activity?.finishAffinity()
        }

        binding.editProfileCv.setOnClickListener {

            startActivity(Intent(mContext, EditProfileActivity::class.java))
        }

        binding.changePasswordCv.setOnClickListener {

            startActivity(Intent(mContext, ChangePasswordActivity::class.java))
        }

        binding.verifyAccountCv.setOnClickListener {

            verifyAccount()
        }

        binding.deleteAccountCv.setOnClickListener {
            startActivity(Intent(mContext, DeleteAccountActivity::class.java))
        }

        binding.profileIv.setOnClickListener {

            showProfileImage()
        }
    }


    private fun loadMyInfo() {

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val phoneCode = "${snapshot.child("phoneCode").value}"
                    val phoneNumber = "${snapshot.child("phoneNumber").value}"
                    val profileImageUrl = "${snapshot.child("profileImageUrl").value}"
                    var timestamp = "${snapshot.child("timestamp").value}"
                    val userType = "${snapshot.child("userType").value}"

                    val phone = phoneCode + phoneNumber

                    if (timestamp == "null") {
                        timestamp = "0"
                    }

                    binding.emailTv.text = "${snapshot.child("email").value}"
                    binding.nameTv.text = "${snapshot.child("name").value}"
                    binding.dobTv.text = "${snapshot.child("dob").value}"
                    binding.phoneTv.text = phone
                    binding.memberSinceTv.text = Utils.formatTimestampDate(timestamp.toLong())

                    if (userType == "Email"){

                        val isVerified = firebaseAuth.currentUser!!.isEmailVerified

                        if (isVerified){

                            binding.verifyAccountCv.visibility = View.GONE
                            binding.verificationTv.text = "Verified"
                        } else {

                            binding.verifyAccountCv.visibility = View.VISIBLE
                            binding.verificationTv.text = "Not Verified"
                        }
                    } else {

                        binding.verifyAccountCv.visibility = View.GONE
                        binding.verificationTv.text = "Verified"
                    }

                    try {

                        Glide.with(mContext)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_person_white)
                            .into(binding.profileIv)
                    } catch (e: Exception) {

                        Log.e(TAG, "onDataChange: ", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }


    private fun verifyAccount() {

        progressDialog.setMessage("Sending verification instructions to your email...")
        progressDialog.show()

        firebaseAuth.currentUser!!.sendEmailVerification()
            .addOnSuccessListener {

                progressDialog.dismiss()
                Utils.toast(mContext, "Verification instructions sent to your email...")
            }
            .addOnFailureListener { e->

                Log.e(TAG, "verifyAccount: ", e)
                progressDialog.dismiss()
                Utils.toast(mContext, "Failed to send verification instructions due to ${e.message}")
            }
    }


    private fun showProfileImage() {

        val dialogBinding = AvatarBinding.inflate(layoutInflater)

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val profileImageUrl = "${snapshot.child("profileImageUrl").value}"

                    try {

                        Glide.with(mContext)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_person_white)
                            .into(dialogBinding.profileIv)
                    } catch (e: Exception) {

                        Log.e(TAG, "onDataChange: ", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })


        // Create the AlertDialog
        val dialog = AlertDialog.Builder(mContext)

        dialog.setView(dialogBinding.root)

        dialog.create()

        if (dialog != null) dialog.show()
    }


    private companion object{

        private const val TAG = "ACCOUNT_TAG"
    }

}