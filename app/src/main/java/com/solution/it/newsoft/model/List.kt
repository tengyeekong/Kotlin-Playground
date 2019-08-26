package com.solution.it.newsoft.model

class List {

    var id: String? = null
    var list_name: String? = null
    var distance: String? = null

    constructor(id: String, list_name: String, distance: String) {
        this.id = id
        this.list_name = list_name
        this.distance = distance
    }
}
