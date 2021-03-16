package com.example.myapplication

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.phoneauthfirebase.databinding.ActivityMainBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
   private lateinit var  mAuth : FirebaseAuth
   private  var verificationID : String = ""

    private lateinit var binding: ActivityMainBinding;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAuth= FirebaseAuth.getInstance()
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.otp.visibility=GONE

        binding.getOtp.setOnClickListener { getOtp()
        binding.otp.visibility= VISIBLE
        }

        setContentView(binding.root)
    }

    private fun getOtp() {
    val number = binding.phone.text.toString()
        val options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber("+91"+number)
                .setTimeout(60L,TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallBack)
                .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private  val mCallBack:PhoneAuthProvider.OnVerificationStateChangedCallbacks = object :PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
        override fun onVerificationCompleted(p0: PhoneAuthCredential) {
            val code = p0.smsCode
            if(code!= null){
                binding.otp.setText(code)
                verifyCode(code)
            }
        }

        override fun onVerificationFailed(p0: FirebaseException) {
          Toast.makeText(this@MainActivity,p0.message,Toast.LENGTH_LONG).show()
        }

        override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
            super.onCodeSent(p0, p1)
            verificationID = p0;
        }
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationID,code)
        signInWithCredential(credential)

    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener{ task ->
            if(task.isSuccessful){
                Toast.makeText(this@MainActivity,"otp received and correct",Toast.LENGTH_LONG).show()
            }else
            {
                Toast.makeText(this@MainActivity,task.exception!!.message,Toast.LENGTH_LONG).show()
            }
        }

    }


}