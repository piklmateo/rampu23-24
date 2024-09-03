package hr.foi.rampu.eventmanager.helpers

import hr.foi.rampu.eventmanager.entity.Event
import hr.foi.rampu.eventmanager.entity.EventType
import hr.foi.rampu.eventmanager.entity.TicketType
import java.util.Date

object MockDataLoader {
    /*fun getDemoData(): MutableList<Event> {
        val types = getDemoCategories()

        return mutableListOf(
            Event("1","Forestland", "Cakovec", Date(), "Opis", types[0]),
            Event("1","Ivica i Marica", "Varazdin", Date(), "Opis", types[1]),
            Event("1","Dinamo vs totenham", "Varazdin", Date(), "Opis", types[2]),
            Event("1","Predsjednicki govor", "Cakovec", Date(), "Opis", types[3]),
            Event("1","Predstava", "Varazdin", Date(), "Opis", types[1]),
            Event("1","Poslovni govor", "Varazdin", Date(), "Opis", types[4]),
            Event("1","Tomorrowland", "Belgija", Date(), "Opis", types[0]),
            Event("1","Kazalisna predstava", "Varazdin", Date(), "Opis", types[1])
        )
    }*/

    fun getDemoCategories(): MutableList<EventType> = mutableListOf(
        EventType("Concert"),
        EventType("Show"),
        EventType("Sport"),
        EventType("Politics"),
        EventType("Business")
    )

    fun getTicketCategories(): MutableList<TicketType> = mutableListOf(
        TicketType("VIP"),
        TicketType("Parter"),
        TicketType("Tribina"),
        TicketType("Djecja"),
    )


}