package com.siegengel.ping_fct.Model

class Chat {
    private var sender: String? = null
    private var receiver: String? = null
    private var message: String? = null
    private var isseen: Boolean? = false

    constructor()

    constructor(sender: String, receiver: String, message: String, isseen: Boolean) {
        this.sender = sender
        this.receiver = receiver
        this.message = message
        this.isseen = isseen
    }

    // Getters
    fun getSender(): String? {
        return sender
    }

    fun getReceiver(): String? {
        return receiver
    }

    fun getMessage(): String? {
        return message
    }

    // Setters
    fun setSender(sender: String) {
        this.sender = sender
    }

    fun setReceiver(receiver: String) {
        this.receiver = receiver
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun isIsseen(): Boolean? {
        return isseen
    }

    fun setIsseen(isseen: Boolean) {
        this.isseen = isseen
    }
}
