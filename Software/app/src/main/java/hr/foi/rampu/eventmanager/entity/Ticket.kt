package hr.foi.rampu.eventmanager.entity

import java.util.Date

data class Ticket(
    val id: String?,
    val eventName: String?,
    val eventLocation: String?,
    val eventDate: Date?,
    val price: String?, // Change the type to String
    val amount: Int?,
    val ticketCategory: TicketType?
) {
    constructor() : this(null, null, null, null, "0.0", 0, null)
}