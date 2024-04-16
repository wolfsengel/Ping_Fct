package com.siegengel.ping_fct.RSA

import java.security.spec.PKCS8EncodedKeySpec
import javax.crypto.Cipher
import java.security.spec.X509EncodedKeySpec
import java.io.IOException
import java.security.*
import java.util.*

class RSAHandler {
    var privateKey: PrivateKey
    var publicKey: PublicKey

    companion object {
        // Convert String public key to Key object
        @Throws(GeneralSecurityException::class, IOException::class)
        fun loadPublicKey(stored: String): Key {
            val data: ByteArray = Base64.getDecoder().decode(stored.toByteArray())
            val spec = X509EncodedKeySpec(data)
            val fact = KeyFactory.getInstance("RSA")
            return fact.generatePublic(spec)
        }

        // Encrypt using public key
        @Throws(Exception::class)
        fun encryptMessage(plainText: String, publickey: String): String {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.ENCRYPT_MODE, loadPublicKey(publickey))
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.toByteArray()))
        }

        // Decrypt using private key
        @Throws(Exception::class)
        fun decryptMessage(encryptedText: String?, privatekey: String): String {
            val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
            cipher.init(Cipher.DECRYPT_MODE, loadPrivateKey(privatekey))
            return String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)))
        }

        // Convert String private key to privateKey object
        @Throws(GeneralSecurityException::class)
        fun loadPrivateKey(key64: String): PrivateKey {
            val clear: ByteArray = Base64.getDecoder().decode(key64.toByteArray())
            val keySpec = PKCS8EncodedKeySpec(clear)
            val fact = KeyFactory.getInstance("RSA")
            val priv = fact.generatePrivate(keySpec)
            Arrays.fill(clear, 0.toByte())
            return priv
        }

        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val secretText = "Icono de web global\uD83D\uDE0E Emoji Keyboard Online \uD83D\uDE0D \uD83D\uDE08 \uD83D\uDC7B \uD83D\uDCA9 \uD83D\uDC8B "
            val keyPairGenerator = RSAHandler()

            // Generate private and public key
            val privateKey: String = Base64.getEncoder().encodeToString(keyPairGenerator.privateKey.encoded)
            val publicKey: String = Base64.getEncoder().encodeToString(keyPairGenerator.publicKey.encoded)

            println("Private Key: $privateKey")
            println("Public Key: $publicKey")

            // Encrypt secret text using public key
            val encryptedValue = encryptMessage(secretText, publicKey)
            println("Encrypted Value: $encryptedValue")

            // Decrypt
            val decryptedText = decryptMessage(encryptedValue, privateKey)
            println("Decrypted output: $decryptedText")
        }
    }

    init {
        // Initialize RSA key pair
        val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
        keyPairGenerator.initialize(1024)
        val keyPair = keyPairGenerator.generateKeyPair()
        privateKey = keyPair.private
        publicKey = keyPair.public
    }
}
