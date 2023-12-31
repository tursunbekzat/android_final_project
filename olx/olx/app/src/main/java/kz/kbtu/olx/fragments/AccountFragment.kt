package kz.kbtu.olx.fragments


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
import kz.kbtu.olx.ui.account.ChangePasswordActivity
import kz.kbtu.olx.ui.account.DeleteAccountActivity
import kz.kbtu.olx.ui.account.EditProfileActivity


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
        Log.d(TAG, "loadMyInfo: ")
        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child("${firebaseAuth.uid}")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val dob = "${snapshot.child("dob").value}"
                    val email = "${snapshot.child("email").value}"
                    val name = "${snapshot.child("name").value}"
                    val phoneCode = "${snapshot.child("phoneCode").value}"
                    val phoneNumber = "${snapshot.child("phoneNumber").value}"
                    val profileImageUrl = "${snapshot.child("profileImageUrl").value}"
                    var timestamp = "${snapshot.child("timestamp").value}"
                    val userType = "${snapshot.child("userType").value}"

                    val phone = phoneCode + phoneNumber

                    if (timestamp == "null") {
                        timestamp = "0"
                    }

                    val formattedDate = Utils.formatTimestampDate(timestamp.toLong())

                    binding.emailTv.text = email
                    binding.nameTv.text = name
                    binding.dobTv.text = dob
                    binding.phoneTv.text = phone
                    binding.memberSinceTv.text = formattedDate

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

        Log.d(TAG, "verifyAccount: ")

        progressDialog.setMessage("Sending verification instructions to your email...")
        progressDialog.show()

        firebaseAuth.currentUser!!.sendEmailVerification()
            .addOnSuccessListener {

                Log.d(TAG, "verifyAccount: Successfully sent.")
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

        if (dialog != null) {

            dialog.show()
        }
    }

    private companion object{

        private const val TAG = "ACCOUNT_TAG"
    }

}