package hr.foi.rampu.eventmanager.entity

data class EventType(val name: String) {
    override fun toString(): String {
        return name
    }
    constructor() : this("")
}

