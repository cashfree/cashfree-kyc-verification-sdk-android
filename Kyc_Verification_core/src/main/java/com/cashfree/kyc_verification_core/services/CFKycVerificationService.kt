package com.cashfree.kyc_verification_core.services

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.cashfree.kyc_verification_core.ui.KycVerificationActivity
import com.cashfree.kyc_verification_core.utils.Constants
import java.lang.ref.WeakReference


internal interface IVerificationService {
    fun doVerification(context: Context, formId: String)
}


object CFKycVerificationService : IVerificationService {

    private var context: WeakReference<Context>? = null

    override fun doVerification(context: Context, formId: String) {
        this.context = WeakReference<Context>(context)
        startVerification(formId)
    }

    private fun startVerification(formId: String) {
        context.let {
            it?.get()?.let { context ->
                Intent(context, KycVerificationActivity::class.java).apply {
                    putExtras(getVerificationBundle(formId))
                    context.startActivity(this)
                }
            }
        }
    }

    private fun getVerificationBundle(formId: String): Bundle {
        return Bundle().apply {
            putString(Constants.FORM_ID, formId)
        }
    }


}