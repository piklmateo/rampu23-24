package hr.foi.rampu.eventmanager.database

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObject
import hr.foi.rampu.eventmanager.entity.Event
import hr.foi.rampu.eventmanager.entity.Ticket
import kotlinx.coroutines.tasks.await

class eventsDAO {
    private val db = FirebaseFirestore.getInstance()
    private val eventsCollection = db.collection("events")

    fun addEvent(event: Event){

        db.collection("events").add(event).addOnSuccessListener {
            db.collection("events").document(it.id).update("id",it.id)
        }
    }
    fun getAllEvents(): Task<QuerySnapshot> {
        return eventsCollection.get()
    }

    suspend fun getEventById(id:String): Event? {
        return (eventsCollection.document(id).get().await()).toObject(Event::class.java)
    }

    fun updateEvent(eventId: String, updatedEvent: Event): Task<Void> {
        val eventRef = eventsCollection.document(eventId)
        val updates = mutableMapOf<String, Any?>()

        if (updatedEvent.name != null) {
            updates["name"] = updatedEvent.name
        }
        if (updatedEvent.eventDate != null) {
            updates["eventDate"] = updatedEvent.eventDate
        }
        if (updatedEvent.location != null) {
            updates["location"] = updatedEvent.location
        }
        if (updatedEvent.eventDescription != null) {
            updates["eventDescription"] = updatedEvent.eventDescription
        }
        if (updatedEvent.eventType != null) {
            updates["eventType"] = updatedEvent.eventType
        }

        return eventRef.update(updates)
            .addOnSuccessListener {
                Log.d("Update:", "Uspješan update")
            }
            .addOnFailureListener { e ->
                Log.e("Update", "Greška prilikom update-a", e)
            }
    }

   fun getAllLocations(onSuccess: (List<String>) -> Unit, onFailure: (Exception) -> Unit) {
        eventsCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val locationNames = mutableListOf<String>()

                for (document in querySnapshot.documents) {
                    val locationName = document.getString("location")
                    locationName?.let {
                        locationNames.add(it)
                    }
                }

                onSuccess(locationNames)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }

    }
}