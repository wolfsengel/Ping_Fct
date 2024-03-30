package com.siegengel.ping_fct

class User {
    private lateinit var id: String
    private var username: String? = null
    private var imageURL: String? = null

    constructor()

    constructor(id: String, username: String, imageURL: String, status: String, search: String) {
        this.id = id
        this.username = username
        this.imageURL = imageURL
    }

    fun getId(): String {
        return id
    }

    fun setId(id: String) {
        this.id = id
    }

    fun getUsername(): String? {
        return username
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun getImageURL(): String? {
        return imageURL
    }

    fun setImageURL(imageURL: String) {
        this.imageURL = imageURL
    }

}
