package hr.foi.rampu.eventmanager.entity

data class TicketType(val name: String) {
    override fun toString(): String {
        return name
    }
    constructor() : this("")
}