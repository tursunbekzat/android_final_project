package kz.kbtu.olx.domain.models

class Chat {

    var messageId: String = ""
    var message: String = ""
    var messageType: String = ""
    var fromUid: String = ""
    var toUid: String = ""
    var timestamp: Long = 0


    constructor()


    constructor(
        messageId: String,
        message: String,
        messageType: String,
        fromUid: String,
        toUid: String,
        timestamp: Long
    ) {
        this.messageId = messageId
        this.message = message
        this.messageType = messageType
        this.fromUid = fromUid
        this.toUid = toUid
        this.timestamp = timestamp
    }


}