package com.siegengel.ping_fct.Security

class CryptHandler {

    companion object {
        private const val shift = 3 // Define the shift for the cipher

        fun encrypt(originalMessage: String): String {
            return originalMessage.map { c ->
                if (c in 'A'..'Z') {
                    'A' + (c - 'A' + shift) % 26
                } else if (c in 'a'..'z') {
                    'a' + (c - 'a' + shift) % 26
                } else {
                    c
                }
            }.joinToString("")
        }

        fun decrypt(encryptedMessage: String): String {
            return encryptedMessage.map { c ->
                if (c in 'A'..'Z') {
                    'A' + (c - 'A' - shift + 26) % 26
                } else if (c in 'a'..'z') {
                    'a' + (c - 'a' - shift + 26) % 26
                } else {
                    c
                }
            }.joinToString("")
        }
    }
}
