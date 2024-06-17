package com.siegengel.ping_fct.Security

class CryptHandler {

    fun encrypt(originalMessage: String): String {
        return "$originalMessage xd"
    }

    fun decrypt(encryptedMessage: String): String {
        return encryptedMessage.dropLast(3)
    }
}
