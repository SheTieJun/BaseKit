package me.shetj.base.network.https

import java.io.IOException
import java.io.InputStream
import java.security.KeyManagementException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.cert.CertificateException
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import javax.net.ssl.KeyManager
import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager
import timber.log.Timber

/**
 * 描述：Https相关的工具类
 */
object HttpsUtils {

    class SSLParams(
        var sSLSocketFactory: SSLSocketFactory? = null,
        var trustManager: X509TrustManager? = null
    )

    fun getSslSocketFactory(
        bksFile: InputStream?,
        password: String?,
        certificates: Array<InputStream>?
    ): SSLParams {
        val sslParams = SSLParams()
        try {
            val keyManagers = prepareKeyManager(bksFile, password)
            val trustManagers = prepareTrustManager(certificates)
            val sslContext = SSLContext.getInstance("TLS")
            val trustManager: X509TrustManager = if (trustManagers != null) {
                MyTrustManager(chooseTrustManager(trustManagers))
            } else {
                UnSafeTrustManager()
            }
            sslContext.init(keyManagers, arrayOf<TrustManager>(trustManager), null)
            sslParams.sSLSocketFactory = sslContext.socketFactory
            sslParams.trustManager = trustManager
            return sslParams
        } catch (e: NoSuchAlgorithmException) {
            throw AssertionError(e)
        } catch (e: KeyManagementException) {
            throw AssertionError(e)
        } catch (e: KeyStoreException) {
            throw AssertionError(e)
        }
    }

    private fun prepareTrustManager(certificates: Array<InputStream>?): Array<TrustManager>? {
        if (certificates == null || certificates.isEmpty()) return null
        try {
            val certificateFactory = CertificateFactory.getInstance("X.509")
            val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
            keyStore.load(null)
            var index = 0
            for (certificate in certificates) {
                val certificateAlias = Integer.toString(index++)
                keyStore.setCertificateEntry(
                    certificateAlias,
                    certificateFactory.generateCertificate(certificate)
                )
                try {
                    certificate.close()
                } catch (e: IOException) {
                    Timber.e(e)
                }
            }
            val trustManagerFactory =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            trustManagerFactory.init(keyStore)
            return trustManagerFactory.trustManagers
        } catch (e: Exception) {
            Timber.e(e)
        }
        return null
    }

    private fun prepareKeyManager(bksFile: InputStream?, password: String?): Array<KeyManager>? {
        try {
            if (bksFile == null || password == null) return null
            val clientKeyStore = KeyStore.getInstance("BKS")
            clientKeyStore.load(bksFile, password.toCharArray())
            val keyManagerFactory =
                KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
            keyManagerFactory.init(clientKeyStore, password.toCharArray())
            return keyManagerFactory.keyManagers
        } catch (e: Exception) {
            Timber.e(e)
        }
        return null
    }

    private fun chooseTrustManager(trustManagers: Array<TrustManager>): X509TrustManager? {
        for (trustManager in trustManagers) {
            if (trustManager is X509TrustManager) {
                return trustManager
            }
        }
        return null
    }

    private class UnSafeTrustManager : X509TrustManager {
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }

    private class MyTrustManager(private val localTrustManager: X509TrustManager?) : X509TrustManager {
        private var defaultTrustManager: X509TrustManager? = null

        init {
            val var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            var4.init(null as KeyStore?)
            defaultTrustManager = chooseTrustManager(var4.trustManagers)
        }

        @Throws(CertificateException::class)
        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        }

        @Throws(CertificateException::class)
        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
            try {
                defaultTrustManager?.checkServerTrusted(chain, authType)
            } catch (ce: CertificateException) {
                localTrustManager?.checkServerTrusted(chain, authType)
            }
        }

        override fun getAcceptedIssuers(): Array<X509Certificate> {
            return arrayOf()
        }
    }
}
