package shetj.me.base.utils

import android.security.KeyStoreException
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.InvalidAlgorithmParameterException
import java.security.KeyStore
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

/**
 *
 * <b>@author：</b> shetj<br>
 * <b>@createTime：</b> 2024/1/11<br>
 * <b>@email：</b> 375105540@qq.com<br>
 * <b>@describe</b>  <br>
 */
object KeyStoreKit {
    private const val ANDROID_KEY_STORE_PROVIDER = "AndroidKeyStore"
    private const val ANDROID_KEY_STORE_ALIAS = "AES_KEY_BASE_KIT"

    private const val type = "AES/CBC/PKCS7Padding"


//    fun test() {
//            val keyAlias = "my_key_alias"
//
//            // Generate AES key and store it in Android Keystore
//            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
//            val keySpec = KeyGenParameterSpec.Builder(keyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
//                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
//                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
//                .build()
//            keyGenerator.init(keySpec)
//            val key = keyGenerator.generateKey()
//
//            // Encrypt the data
//            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
//            cipher.init(Cipher.ENCRYPT_MODE, key)
//            val encryptedData = cipher.doFinal("Hello, World!".toByteArray())
//            val iv = cipher.parameters.getParameterSpec(IvParameterSpec::class.java).iv
//
//            // Decrypt the data
//            val decryptCipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
//            val ivParamSpec = IvParameterSpec(iv)
//            decryptCipher.init(Cipher.DECRYPT_MODE, key, ivParamSpec)
//            val decryptedData = decryptCipher.doFinal(encryptedData)
//
//            // Print the results
//            println("Original: " + decryptedData.toString(Charsets.UTF_8))
//            println("Encrypted: " + String(encryptedData, Charsets.UTF_8))
//            println("Decrypted: " + decryptedData.toString(Charsets.UTF_8))
//    }

    init {
        kotlin.runCatching {
            createAndStoreSecretKey()
        }.onFailure {
            it.printStackTrace()
        }
    }

    /**
     * Create and store secret key
     * 获取秘钥
     */
    @Throws(
        KeyStoreException::class,
        NoSuchAlgorithmException::class,
        NoSuchProviderException::class,
        InvalidAlgorithmParameterException::class
    )
    private fun createAndStoreSecretKey() {
        val keySpec: KeyGenParameterSpec =    KeyGenParameterSpec.Builder(ANDROID_KEY_STORE_ALIAS, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
            .build()
        val aesKeyGenerator: KeyGenerator =
            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE_PROVIDER)
        aesKeyGenerator.init(keySpec)
        val key: SecretKey = aesKeyGenerator.generateKey()
    }


    /**
     * Encrypt with key store
     * 加密
     */
    fun encryptWithKeyStore(plainText: String): Pair<ByteArray,ByteArray>? {
        return kotlin.runCatching {
            // Initialize KeyStore
            val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEY_STORE_PROVIDER)
            keyStore.load(null)
            // Retrieve the key with alias androidKeyStoreAlias created before
            val keyEntry: KeyStore.SecretKeyEntry =
                keyStore.getEntry(ANDROID_KEY_STORE_ALIAS, null) as? KeyStore.SecretKeyEntry ?: return null
            val key: SecretKey = keyEntry.secretKey
            // Use the secret key at your convenience
            val cipher: Cipher = Cipher.getInstance(type)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val cipherText = cipher.doFinal(plainText.toByteArray())
            val iv = cipher.parameters.getParameterSpec(IvParameterSpec::class.java).iv
            cipherText to iv
        }.onFailure {
            it.printStackTrace()
        }.getOrNull()
    }

    /**
     * Decrypt with key store
     * 解密
     */
    fun decryptWithKeyStore(enInfo:Pair<ByteArray,ByteArray>): String? {
        return kotlin.runCatching {
            // Initialize KeyStore
            val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEY_STORE_PROVIDER)
            keyStore.load(null)
            // Retrieve the key with alias androidKeyStoreAlias created before
            val keyEntry: KeyStore.SecretKeyEntry =
                keyStore.getEntry(ANDROID_KEY_STORE_ALIAS, null) as? KeyStore.SecretKeyEntry ?: return null
            val key: SecretKey = keyEntry.secretKey
            val ivParamSpec = IvParameterSpec(enInfo.second)
            val cipher: Cipher = Cipher.getInstance(type)
            cipher.init(Cipher.DECRYPT_MODE, key, ivParamSpec)
            val decryptedBytes = cipher.doFinal(enInfo.first)
            String(decryptedBytes)
        }.onFailure {
            it.printStackTrace()
        }.getOrNull()

    }
}