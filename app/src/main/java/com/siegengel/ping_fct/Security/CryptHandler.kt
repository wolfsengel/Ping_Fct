package com.siegengel.ping_fct.Security
class CryptHandler {

    fun encrypt(originalMessage: String): String {
        return originalMessage.toByteArray().joinToString("")
    }

    fun decrypt(encryptedMessage: String): String {
        return encryptedMessage.chunked(2).joinToString("") { it.toInt().toChar().toString() }
    }
}
