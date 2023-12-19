package kz.kbtu.olx.domain.models

class ImageSlider {

    var id = ""
    var imageUrl = ""

    constructor()

    constructor(id: String, imageUrl: String) {

        this.id = id
        this.imageUrl = imageUrl
    }
}