package com.cashfree.kyc_verification_core.models

import junit.framework.TestCase.assertEquals
import org.junit.Test

class CFVerificationResponseTest {

    @Test
    fun testCFVerificationResponseInitialization() {
        val response = CFVerificationResponse("success", "form123")
        assertEquals("success", response.status)
        assertEquals("form123", response.form_id)
    }

    @Test
    fun testCFVerificationResponseEquality() {
        val response1 = CFVerificationResponse("success", "form123")
        val response2 = CFVerificationResponse("success", "form123")
        assertEquals(response1, response2)
    }

}