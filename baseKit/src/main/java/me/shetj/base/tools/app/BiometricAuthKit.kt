package me.shetj.base.tools.app

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.biometric.BiometricPrompt.AuthenticationResult
import androidx.biometric.BiometricPrompt.PromptInfo.Builder
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import me.shetj.base.ktx.logD

import me.shetj.base.R
import me.shetj.base.ktx.logE

/**
 * Biometric authentication kit
 * 生物身份验证，指纹验证
 */
object BiometricAuthKit {

    const val RE_AUTH__TIME = 5 * 1000 * 60

    var isAuth: Boolean = false

    private val processLifecycleOwner = ProcessLifecycleOwner.get()

    private var backTs = 0L

    var isEnable = false

    /**
     * Init
     * 判断生命周期展示
     * 在通过验证的情况下，如果在后台超过5分钟，就重新验证
     */
    fun init(context: FragmentActivity, crypto: BiometricPrompt.CryptoObject? = null) {
        if (isAuth || !checkBiometricCanUsing(context)) return
        processLifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_STOP) {
                    backTs = System.currentTimeMillis()
                } else if (event == Lifecycle.Event.ON_START) {
                    //如果是后台超过5分钟，就重新验证
                    if ((System.currentTimeMillis() - backTs > RE_AUTH__TIME || !isAuth) && isEnable) {
                        startBiometric(context, crypto)
                    }
                }
            }
        })
    }

    /**
     * 直接展示:应用场景点击触发
     */
    suspend fun show(context: FragmentActivity, crypto: BiometricPrompt.CryptoObject? = null) {
        //如果已经验证过，或者不支持生物识别，或者没有开启，就不展示
        if (isAuth || !checkBiometricCanUsing(context) || !isEnable) return
        startBiometric(context, crypto)
    }

    /**
     * 开始身份验证
     */
    private fun startBiometric(context: FragmentActivity, crypto: BiometricPrompt.CryptoObject? = null) {
        val promptInfo = Builder()
            .setTitle(context.getString(R.string.Authentication))
            .setSubtitle(context.getString(R.string.Authentication_Tip))
            .setAllowedAuthenticators(BIOMETRIC_STRONG)
            .setNegativeButtonText("cancel")
            .setConfirmationRequired(false)
            .build()

        //创建BiometricPrompt对象
        val executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(context, executor,
            object : AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    "Authentication error: $errString".logD()
                    isAuth = false
                }

                override fun onAuthenticationSucceeded(
                    result: AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    isAuth = true
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    isAuth = false
                }
            })
        if (crypto != null) {
            biometricPrompt.authenticate(promptInfo, crypto)
            return
        }
        biometricPrompt.authenticate(promptInfo)
    }

    /**
     * 检查生物识别是否可用
     */
    @JvmStatic
    fun checkBiometricCanUsing(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                "App can authenticate using biometrics.".logE()
                true
            }

            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                "No biometric features available on this device.".logE()
                false
            }

            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                "Biometric features are currently unavailable.".logE()
                false
            }

            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                return false
            }

            else -> false
        }
    }

}