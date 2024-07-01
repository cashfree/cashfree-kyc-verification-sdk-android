package com.cashfree.kyc_verification_core.channel

import com.cashfree.kyc_verification_core.models.CFErrorResponse
import com.cashfree.kyc_verification_core.models.CFVerificationResponse


internal sealed class CFCallbackEvents {
    object Verify : CFCallbackEvents()
    object Error : CFCallbackEvents()
}

internal data class CFPaymentCallbackEvent(
    val eventId: CFCallbackEvents,
    val response: CFVerificationResponse? = null,
    val error: CFErrorResponse? = null
)