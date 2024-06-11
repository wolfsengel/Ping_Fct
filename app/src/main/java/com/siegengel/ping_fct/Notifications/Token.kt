package com.siegengel.ping_fct.Notifications

class Token {
    private var token: String = ""

    constructor() {}

    constructor(token: String) {
        this.token = token
    }

    fun getToken(): String {
        return token
    }

    fun setToken(token: String) {
        this.token = token
    }
}