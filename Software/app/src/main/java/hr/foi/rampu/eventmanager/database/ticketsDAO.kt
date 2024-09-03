package hr.foi.rampu.eventmanager.database

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import hr.foi.rampu.eventmanager.entity.Ticket

class ticketsDAO {
    private val db = FirebaseFirestore.getInstance()
    private val ticketsCollection = db.collection("tickets")

    fun addTicket(ticket: Ticket) {
        val newTicketRef = ticketsCollection.document()
        val newTicketId = newTicketRef.id
        newTicketRef.set(mapOf(
            "id" to newTicketId,
            "eventName" to ticket.eventName,
            "eventLocation" to ticket.eventLocation,
            "eventDate" to ticket.eventDate,
            "price" to ticket.price,
            "amount" to ticket.amount,
            "ticketCategory" to ticket.ticketCategory
        ))
    }

    fun getAllTickets(): Task<QuerySnapshot> {
        return ticketsCollection.get()
    }

    fun updateTicket(ticketId: String, updatedTicket: Ticket): Task<Void> {
        val ticketRef = ticketsCollection.document(ticketId)
        val updates = mutableMapOf<String, Any?>()

        if (updatedTicket.price != null) {
            updates["price"] = updatedTicket.price
        }
        if (updatedTicket.amount != null) {
            updates["amount"] = updatedTicket.amount
        }
        if (updatedTicket.ticketCategory != null) {
            updates["ticketCategory"] = mapOf("name" to updatedTicket.ticketCategory.name)
        }

        return ticketRef.update(updates)
            .addOnSuccessListener {
                Log.d("Update:", "Uspješan update")
            }
            .addOnFailureListener { e ->
                Log.e("Update", "Greška prilikom update-a", e)
            }
    }
}