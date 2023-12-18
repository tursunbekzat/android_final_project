package kz.kbtu.olx.models

import android.net.Uri

class ImagePicked {

    var id = ""
    var imageUri: Uri? = null
    var imageUrl: String ?= null
    var fromInternet = false


    constructor()


    constructor(id: String, imageUri: Uri?, imageUrl: String?, fromInternet: Boolean) {

        this.id = id
        this.imageUri = imageUri
        this.imageUrl = imageUrl
        this.fromInternet = fromInternet
    }
}