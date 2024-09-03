package hr.foi.rampu.eventmanager.helpers

import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Spinner
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.QuerySnapshot
import hr.foi.rampu.eventmanager.R
import hr.foi.rampu.eventmanager.entity.Event
import hr.foi.rampu.eventmanager.entity.Ticket
import hr.foi.rampu.eventmanager.entity.TicketType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class NewTicketDialogHelper(private val view: View) {

    private val ticketSpinner = view.findViewById<Spinner>(R.id.spn_ticket_category)
    private val eventSpinner = view.findViewById<Spinner>(R.id.spn_event_name)

    fun populateSpinnerTickets(categories: MutableList<TicketType>) {
        val spinnerAdapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_spinner_item,
            categories
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        ticketSpinner.adapter = spinnerAdapter
    }

    fun populateSpinnerEvents(events: MutableList<Event>) {
        val eventItems = mutableListOf<Any>()

        events.forEach { event ->
            eventItems.add(event)
        }

        val spinnerAdapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_spinner_item,
            eventItems
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        eventSpinner.adapter = spinnerAdapter
    }

    fun populateDialogWithTicket(ticket: Ticket) {
        val etPrice = view.findViewById<EditText>(R.id.et_price)
        etPrice.setText(ticket.price.toString())

        val etAmount = view.findViewById<EditText>(R.id.et_amount)
        etAmount.setText(ticket.amount.toString())

        val ticketSpinner = view.findViewById<Spinner>(R.id.spn_ticket_category)
        val categoryAdapter = ticketSpinner.adapter as ArrayAdapter<TicketType>
        val categoryPosition = categoryAdapter.getPosition(ticket.ticketCategory)
        ticketSpinner.setSelection(categoryPosition)
    }

    fun buildTask(): Ticket {
        val etPrice = view.findViewById<EditText>(R.id.et_price)
        val newPrice = etPrice.text.toString()

        val etAmount = view.findViewById<EditText>(R.id.et_amount)
        val newAmount = etAmount.text.toString()
        val amountInt = newAmount.toIntOrNull()

        val spinnerTicketCategory = view.findViewById<Spinner>(R.id.spn_ticket_category)
        val selectedTicketCategory = spinnerTicketCategory.selectedItem as TicketType

        val spinnerEvent = view.findViewById<Spinner>(R.id.spn_event_name)
        val selectedEventItem = spinnerEvent.selectedItem

        val eventName: String
        val location: String?
        val date: Date?

        when (selectedEventItem) {
            is Event -> {
                eventName = selectedEventItem.name
                location = selectedEventItem.location
                date = selectedEventItem.eventDate
            }
            is String -> {
                eventName = selectedEventItem
                location = null
                date = null
            }
            else -> {
                eventName = ""
                location = null
                date = null
            }
        }
        return Ticket(null, eventName, location, date, newPrice, amountInt, selectedTicketCategory)
    }
}
