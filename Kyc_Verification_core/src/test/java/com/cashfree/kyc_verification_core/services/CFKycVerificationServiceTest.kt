package com.cashfree.kyc_verification_core.services

import android.content.Context
import com.cashfree.pg.base.exception.CFException
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class CFKycVerificationServiceTest {

    @Mock
    private lateinit var mockContext: Context

    private lateinit var service: CFKycVerificationService

    private val validUrl = "https://valid.url"

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        service = CFKycVerificationService.initialize(mockContext, validUrl)
    }


    @Test(expected = CFException::class)
    fun initializeWithEmptyURLThrowsException() {
        CFKycVerificationService.initialize(mockContext, "")
    }

    @Test
    fun initializeWithValidContextAndURL() {
        val newService = CFKycVerificationService.initialize(mockContext, validUrl)
        assert(newService === CFKycVerificationService.getInstance())
    }

    @Test
    fun builderBuildsServiceCorrectly() {
        val builderService = CFKycVerificationService.Builder()
            .setContext(mockContext)
            .setUrl(validUrl)
            .build()
        assert(builderService === CFKycVerificationService.getInstance())
    }

    @Test(expected = IllegalArgumentException::class)
    fun builderWithoutContextThrowsException() {
        CFKycVerificationService.Builder()
            .setUrl(validUrl)
            .build()
    }

    @Test(expected = IllegalArgumentException::class)
    fun builderWithoutURLThrowsException() {
        CFKycVerificationService.Builder()
            .setContext(mockContext)
            .build()
    }
}
