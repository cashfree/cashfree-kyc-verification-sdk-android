package com.cashfree.kyc_verification_core.utils

import com.cashfree.kyc_verification_core.channel.CFCallbackEvents
import com.cashfree.kyc_verification_core.channel.CFPaymentCallbackEvent
import com.cashfree.kyc_verification_core.models.CFErrorResponse
import com.cashfree.kyc_verification_core.models.CFVerificationResponse
import com.cashfree.subscription.coresdk.channel.CFCallbackEventBus

internal object CFCallbackUtil {

    fun sendVerificationResponse(response: CFVerificationResponse) {
        CFCallbackEventBus.getInstance()?.publishEvent(
            CFPaymentCallbackEvent(CFCallbackEvents.Verify, response = response)
        )
    }

    fun sendErrorResponse(error: CFErrorResponse) {
        CFCallbackEventBus.getInstance()?.publishEvent(
            CFPaymentCallbackEvent(CFCallbackEvents.Error, error = error)
        )
    }
}