package kz.kbtu.olx.presentation.activities.auth

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.google.firebase.database.FirebaseDatabase
import kz.kbtu.olx.MainActivity
import kz.kbtu.olx.Utils
import kz.kbtu.olx.databinding.ActivityLoginPhoneBinding
import java.util.concurrent.TimeUnit


class LoginPhoneActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginPhoneBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private var forceRefreshingToken: ForceResendingToken? = null
    private lateinit var mCallbacks: OnVerificationStateChangedCallbacks
    private var mVerificationId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginPhoneBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.phoneInputRl.visibility = View.VISIBLE
        binding.otpInputRl.visibility = View.GONE

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Please wait...")
        progressDialog.setCanceledOnTouchOutside(false)

        firebaseAuth = FirebaseAuth.getInstance()

        phoneLoginCallbacks()

        setOnClickListeners()
    }


    private fun setOnClickListeners(){

        binding.toolbarBackBtn.setOnClickListener {

            onBackPressed()
        }

        binding.sendOtpBtn.setOnClickListener {

            validateData()
        }

        binding.resendOtpTv.setOnClickListener {

            resendVerificationCode(forceRefreshingToken)
        }

        binding.verifyOtpBtn.setOnClickListener {

            val otp = binding.otpEt.text.toString().trim()

            if (otp.isEmpty()){

                binding.otpEt.error = "Enter OTP"
                binding.otpEt.requestFocus()
            } else if (otp.length < 6){

                binding.otpEt.error = "OTP length must be 6 characters long"
                binding.otpEt.requestFocus()
            } else {

                verifyPhoneNumberWithCode(mVerificationId, otp)
            }

        }
    }


    private var phoneCode = ""
    private var phoneNumber = ""
    private var phoneNumberWithCode = ""


    private fun validateData(){

        phoneCode = binding.phoneCodeTil.selectedCountryCodeWithPlus
        phoneNumber = binding.phoneNumberEt.text.toString().trim()
        phoneNumberWithCode = phoneCode + phoneNumber

        if (phoneNumber.isEmpty()){

            binding.phoneNumberEt.error = "Enter Phone Number"
            binding.phoneNumberEt.requestFocus()
        }else{

            startPhoneNumberVerification()
        }
    }

    private fun startPhoneNumberVerification() {

        progressDialog.setMessage("Sending OTP to $phoneNumberWithCode")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumberWithCode)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(mCallbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    private fun phoneLoginCallbacks() {

        mCallbacks = object: OnVerificationStateChangedCallbacks(){
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {

                Log.e(TAG, "onVerificationFailed: ", e)

                progressDialog.dismiss()
                Utils.toast(this@LoginPhoneActivity, "${e.message}")
            }

            override fun onCodeSent(verificationId: String, token: ForceResendingToken) {

                mVerificationId = verificationId
                forceRefreshingToken = token

                progressDialog.dismiss()

                binding.phoneInputRl.visibility = View.GONE
                binding.otpInputRl.visibility = View.VISIBLE

                Utils.toast(this@LoginPhoneActivity, "OTP is sent to your $phoneNumberWithCode")

                binding.loginLabelTv.text = "Please type the verification code sent to $phoneNumberWithCode"
            }

            override fun onCodeAutoRetrievalTimeOut(p0: String) {}
        }
    }

    private fun verifyPhoneNumberWithCode(verificationId: String?, otp: String) {

        progressDialog.setMessage("Verifying OTP")
        progressDialog.show()

        val credential = PhoneAuthProvider.getCredential(verificationId!!, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun resendVerificationCode(token: ForceResendingToken?){

        progressDialog.setMessage("Resending OTP to $phoneNumberWithCode")
        progressDialog.show()

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumberWithCode)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setForceResendingToken(token!!)
            .setCallbacks(mCallbacks)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        progressDialog.setMessage("Logging In")
        progressDialog.show()

        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {authResult ->

                if (authResult.additionalUserInfo!!.isNewUser){

                    updateUserInfoDb()
                } else {

                    startActivity(Intent(this, MainActivity::class.java))
                    finishAffinity()
                }
            }
            .addOnFailureListener {e->

                Log.e(TAG, "signInWithPhoneAuthCredential: ", e)
                progressDialog.dismiss()
                Utils.toast(this, "Failed to login due to ${e.message}")
            }
    }

    private fun updateUserInfoDb() {

        progressDialog.setMessage("Saving User Info")
        progressDialog.show()

        val timestamp = Utils.getTimestamp()
        val registeredUserUid = firebaseAuth.uid

        val hashMap = HashMap<String, Any?>()
        hashMap["name"]=""
        hashMap["phoneCode"]= phoneCode
        hashMap["phoneNumber"]= phoneNumber
        hashMap["profileImageUrl"]=""
        hashMap["dob"]=""
        hashMap["userType"]="Phone"
        hashMap["typingTo"]=""
        hashMap["timestamp"]=timestamp
        hashMap["onlineStatus"]=true
        hashMap["email"]=""
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

                progressDialog.dismiss()
                Log.e(TAG, "updateUserInfoDb: ", e)
                Utils.toast(this, "Failed to save user info due to ${e.message}")
            }
    }

    private companion object{

        private const val TAG = "PHONE_LOGIN_TAG"
    }
}