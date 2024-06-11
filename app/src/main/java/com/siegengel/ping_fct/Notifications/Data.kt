package com.siegengel.ping_fct.Notifications

class Data {
    var user: String? = null
    var icon: Int = 0
    var body: String? = null
    var title: String? = null
    var sented: String? = null

    constructor() {}

    constructor(user: String?, icon: Int, body: String?, title: String?, sented: String?) {
        this.user = user
        this.icon = icon
        this.body = body
        this.title = title
        this.sented = sented
    }
}