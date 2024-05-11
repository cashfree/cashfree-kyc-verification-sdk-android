package com.cashfree.kyc_verification

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.cashfree.kyc_verification.databinding.ActivityMainBinding
import com.cashfree.kyc_verification_core.services.CFKycVerificationService

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleClick()
    }

    private fun handleClick() {
        binding.btnVerify.setOnClickListener {
            CFKycVerificationService.doVerification(this,"test")
        }
    }
}