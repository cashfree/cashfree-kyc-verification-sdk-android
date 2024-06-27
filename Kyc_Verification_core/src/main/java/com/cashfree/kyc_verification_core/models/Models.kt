package com.cashfree.kyc_verification_core.models

data class CFVerificationResponse(
    val status: String,
    val formInt: String
)

data class CFErrorResponse(
    val status: String,
    val message: String,
    val code: String
)