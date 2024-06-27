package com.cashfree.kyc_verification

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.cashfree.kyc_verification.databinding.ActivityMainBinding
import com.cashfree.kyc_verification_core.models.CFErrorResponse
import com.cashfree.kyc_verification_core.models.CFVerificationResponse
import com.cashfree.kyc_verification_core.services.CFKycVerificationService
import com.cashfree.subscription.coresdk.channel.CFVerificationCallback

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var verificationService: CFKycVerificationService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeKYCSdk()
        addVerificationCall()
        handleClick()

    }

    private fun initializeKYCSdk() {
        try {
            verificationService = CFKycVerificationService.Builder()
                .setContext(this)
                .setUrl("https://www.google.com/")
                .build()
        } catch (e: Exception) {
            Toast.makeText(this, "error: ${e?.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleClick() {
        binding.btnVerify.setOnClickListener {
            try {
                verificationService?.doVerification()

            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "error: ${e?.message}", Toast.LENGTH_SHORT).show()
                Log.d("test error response", e.toString())
                false
            }

        }
    }

    private fun addVerificationCall() {
        verificationService.setCheckoutCallback(object : CFVerificationCallback {
            override fun onVerificationResponse(response: CFVerificationResponse) {

                Toast.makeText(baseContext, "status -> ${response.status}", Toast.LENGTH_LONG)
                    .show()
            }

            override fun onVerificationCancelled(error: CFErrorResponse) {
                Toast.makeText(baseContext, "Error -> ${error.message}", Toast.LENGTH_LONG).show()
            }

        })
    }
}
