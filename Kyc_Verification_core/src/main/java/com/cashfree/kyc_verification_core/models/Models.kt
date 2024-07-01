package com.cashfree.kyc_verification_core.models

data class CFVerificationResponse(
    val status: String,
    val form_id: String
)

data class CFErrorResponse(
    val statusCode: Int?=null,
    val message: String
)