package hr.foi.rampu.eventmanager.entity

data class Comment(
    val userUid: String,
    var eventId: String,
    val text: String,
    val value: Long?,
    var id: String?
)
