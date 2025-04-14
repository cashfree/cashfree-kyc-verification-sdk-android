# Cashfree KYC Verification Android SDK

![Build](https://badgen.net/badge/build/success/blue?icon=github)
![Version](https://badgen.net/badge/version/1.0.0/blue?icon=github)
![Stars](https://badgen.net/badge/stars/100/green?icon=github)

![Sample Banner Image](https://maven.cashfree.com/images/github-header-image.png)

## **Description**

The Cashfree KYC Verification Android SDK enables seamless 1-Click onboarding and KYC verification for Android applications. It allows businesses to quickly verify user identity via a secure and optimized process, reducing drop-offs and ensuring compliance with regulatory standards.

This SDK simplifies the KYC process, allowing you to:
- Trigger verification flows directly within your app.
- Handle permission requests, verification stages, and result callbacks.
- Improve user onboarding experience by reducing manual input and navigation.

👉 For complete integration details, check our [Official Documentation](https://www.cashfree.com/docs/api-reference/vrs/v2/1-click-onboarding/1-click-onboarding-sdk#android-native).

---

## Table of Contents

1. [Getting Started](#getting-started)
2. [Dependencies](#dependencies)
3. [Installation](#installation)
4. [Usage and Documentation](#usage-and-documentation)  
   - [Initialization](#initialization)  
   - [KYC Verification Flow](#kyc-verification-flow)  
5. [Configuration](#configuration)  
6. [Error Handling](#error-handling)  
7. [Getting Help](#getting-help)  
8. [Open Source Licensing and Other Info](#open-source-licensing-and-other-info)

---

## Getting Started

This guide will walk you through integrating the Cashfree KYC SDK into your Android application and triggering the KYC verification process with minimal effort.

---

## Dependencies

- Android Studio Bumblebee or higher
- `minSdkVersion` 21
- Kotlin 1.6+ or Java 8+
- Required permissions:
  - `CAMERA`
  - `RECORD_AUDIO`
  - `ACCESS_FINE_LOCATION`
  - `READ_CALENDAR` *(only if adding calendar events is needed)*

---

## Installation

Add the SDK dependency to your `build.gradle` file:

```groovy
implementation 'com.cashfree.kyc:sdk:<latest-version>'
```

Ensure `mavenCentral()` is added in your project-level `build.gradle`:

```groovy
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

---

## Usage and Documentation

### Initialization

Before using the SDK, initialize it using the following:

```kotlin
CashfreeKycSdk.initialize(
    context = this,
    clientId = "<YOUR_CLIENT_ID>",
    environment = Environment.PRODUCTION // or Environment.SANDBOX
)
```

### KYC Verification Flow

Trigger the KYC process with the access token provided by your backend:

```kotlin
CashfreeKycSdk.startKycVerification(
    activity = this,
    token = "<ACCESS_TOKEN>",
    callback = object : CashfreeKycCallback {
        override fun onSuccess(result: VerificationResult) {
            // Handle success scenario
        }

        override fun onFailure(error: KycError) {
            // Handle failure scenario
            Log.e("KYC", "Verification failed: ${error.message}")
        }
    }
)
```

For more platform-specific usage (iOS/Android), refer to the [official integration docs]([https://www.cashfree.com/docs/api-reference/vrs/v2/1-click-onboarding/1-click-onboarding-sdk#ios-native](https://www.cashfree.com/docs/api-reference/vrs/v2/1-click-onboarding/1-click-onboarding-sdk#android-native)).

---

## Configuration

Add required permissions in your `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.RECORD_AUDIO"/>
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
<uses-permission android:name="android.permission.READ_CALENDAR"/>
```

Also ensure runtime permission handling is done at the app level before triggering the verification flow.

---

## Error Handling

The SDK will return detailed error messages via the `onFailure` callback.

```kotlin
override fun onFailure(error: KycError) {
    Log.e("KYC", "Verification failed: ${error.code} - ${error.message}")
}
```

Implement proper UI feedback to guide the user on retry options or permission issues.

---

## Getting Help

If you have questions, concerns, or bug reports, you can reach out through the following channels:

1. File an issue via this repository's **Issues** section.
2. Email us at [care@cashfree.com](mailto:care@cashfree.com)

---

## Open Source Licensing and Other Info

- [TERMS](TERMS.md)
- [LICENSE](https://github.com/cashfree/nextgen-android/blob/master/LICENSE.md)
- [CODE OF CONDUCT](https://github.com/cashfree/nextgen-android/blob/master/CODE_OF_CONDUCT.md)
- [SECURITY POLICY](https://github.com/cashfree/nextgen-android/blob/master/SECURITY.md)
