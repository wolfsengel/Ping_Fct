package com.siegengel.ping_fct

class User {
    private lateinit var id: String
    private lateinit var username: String
    private lateinit var imageURL: String

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

    fun getUsername(): String {
        return username
    }

    fun setUsername(username: String) {
        this.username = username
    }

    fun getImageURL(): String {
        return imageURL
    }

    fun setImageURL(imageURL: String) {
        this.imageURL = imageURL
    }

}
