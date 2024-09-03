package hr.foi.rampu.eventmanager.entity

import java.io.Serializable
import java.util.Date
data class Event(
    val id: String?,
    val name: String,
    val location: String,
    val eventDate: Date,
    val eventDescription: String,
    val eventType: EventType?,
    val creatorId:String,
    val comments: List<Comment> = emptyList(),
) : Serializable{


    constructor() : this(null, "", "", Date(), "", null,"")

    override fun toString(): String {
        return name
    }
}

