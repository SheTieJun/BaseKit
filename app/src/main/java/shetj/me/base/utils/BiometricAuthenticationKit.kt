package shetj.me.base.utils

import android.content.Context
import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.biometric.BiometricPrompt.AuthenticationResult
import androidx.biometric.BiometricPrompt.PromptInfo.Builder
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import me.shetj.base.ktx.defDataStore
import me.shetj.base.ktx.launch
import me.shetj.base.ktx.logD
import shetj.me.base.R


object BiometricAuthenticationKit {

    var isAuth: Boolean = false


    fun show(context: FragmentActivity,crypto: BiometricPrompt.CryptoObject?=null) {
        if (isAuth) return
        startBiometric(context,crypto)
    }

    private fun startBiometric(context: FragmentActivity, crypto: BiometricPrompt.CryptoObject?=null) {
        val promptInfo = Builder()
            .setTitle(context.getString(R.string.Authentication))
            .setSubtitle(context.getString(R.string.Authentication_Tip))
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .setNegativeButtonText("cancel")
            .setConfirmationRequired(false)
            .build()

        //创建BiometricPrompt对象
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = androidx.biometric.BiometricPrompt(context, executor,
            object : AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    "Authentication error: $errString".logD()
                }

                override fun onAuthenticationSucceeded(
                    result: AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    isAuth = true
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                }

            })
        if (crypto != null){
            biometricPrompt.authenticate(promptInfo,crypto)
            return
        }
        biometricPrompt.authenticate(promptInfo)
    }


    @JvmStatic
    fun checkBiometric(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.")
                true
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Log.e("MY_APP_TAG", "No biometric features available on this device.")
                false
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Log.e("MY_APP_TAG", "Biometric features are currently unavailable.")
                false
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                return false
            }

            else -> false
        }
    }

}