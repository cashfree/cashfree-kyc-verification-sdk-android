package com.cashfree.kyc_verification_core.models

import junit.framework.TestCase.assertEquals
import org.junit.Test

class CFErrorResponseTest {

    @Test
    fun testCFErrorResponseInitialization() {
        val errorResponse = CFErrorResponse(404, "Not Found")
        assertEquals(404, errorResponse.statusCode)
        assertEquals("Not Found", errorResponse.message)
    }

    @Test
    fun testCFErrorResponseEquality() {
        val errorResponse1 = CFErrorResponse(404, "Not Found")
        val errorResponse2 = CFErrorResponse(404, "Not Found")
        assertEquals(errorResponse1, errorResponse2)
    }

    @Test
    fun testCFErrorResponseNullHandling() {
        val errorResponse = CFErrorResponse(null, "Error message")
        assertEquals(null, errorResponse.statusCode)
    }
}