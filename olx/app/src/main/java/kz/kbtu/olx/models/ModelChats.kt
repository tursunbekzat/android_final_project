package kz.kbtu.olx.models

class ModelChats {

    var name: String = ""
    var profileImageUrl: String = ""
    var message: String = ""
    var date: String = ""
    var chatKey: String = ""
    var receiptUid: String = ""
    var toUid: String = ""
    var messageId: String = ""
    var messageType: String = ""
    var fromUid: String = ""
    var timestamp: Long = 0


    constructor()


    constructor(

        name: String,
        profileImageUrl: String,
        message: String,
        date: String,
        chatKey: String,
        receiptUid: String,
        toUid: String,
        messageId: String,
        messageType: String,
        fromUid: String,
        timestamp: Long
    ) {

        this.name = name
        this.profileImageUrl = profileImageUrl
        this.message = message
        this.date = date
        this.chatKey = chatKey
        this.receiptUid = receiptUid
        this.toUid = toUid
        this.messageId = messageId
        this.messageType = messageType
        this.fromUid = fromUid
        this.timestamp = timestamp
    }
}