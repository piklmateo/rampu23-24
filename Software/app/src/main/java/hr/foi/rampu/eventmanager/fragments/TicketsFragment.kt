package hr.foi.rampu.eventmanager.fragments


import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.integration.android.IntentIntegrator
import hr.foi.rampu.eventmanager.R
import hr.foi.rampu.eventmanager.adapters.TicketAdapter
import hr.foi.rampu.eventmanager.database.eventsDAO
import hr.foi.rampu.eventmanager.database.ticketsDAO
import hr.foi.rampu.eventmanager.entity.Event
import hr.foi.rampu.eventmanager.entity.Ticket
import hr.foi.rampu.eventmanager.helpers.MockDataLoader
import hr.foi.rampu.eventmanager.helpers.NewTicketDialogHelper

class TicketsFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnCreateTicket: FloatingActionButton
    private val eventsDAO = eventsDAO()
    private val ticketsDAO = ticketsDAO()

    private lateinit var  btnScanQr: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_tickets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.rv_tickets_show)

        val ticketsAdapter = TicketAdapter(mutableListOf()) { ticket ->
            showEditTicketDialog(ticket)
        }
        recyclerView.adapter = ticketsAdapter

        recyclerView.layoutManager = LinearLayoutManager(view.context)
        loadTickets()

        btnCreateTicket = view.findViewById(R.id.fab_create_new_ticket)
        btnCreateTicket.setOnClickListener {
            showDialog()
        }

        btnScanQr = view.findViewById(R.id.btnScanQr)
        btnScanQr.setOnClickListener {
            scanQr()
        }
    }

    private fun showDialog() {
        val newTicketDialogView = LayoutInflater
            .from(context)
            .inflate(R.layout.new_ticket_event_dialog, null)

        val dialogHelper = NewTicketDialogHelper(newTicketDialogView)

        AlertDialog.Builder(context)
            .setView(newTicketDialogView)
            .setTitle(getString(R.string.create_a_new_ticket))
            .setPositiveButton(getString(R.string.create_a_new_ticket)) { _, _ ->
                val newTicket = dialogHelper.buildTask()
                ticketsDAO.addTicket(newTicket)
                loadTickets()
            }
            .show()

        eventsDAO.getAllEvents()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val querySnapshot = task.result

                    val eventsList: MutableList<Event> = mutableListOf()
                    for (document in querySnapshot!!) {
                        val event = document.toObject(Event::class.java)
                        eventsList.add(event)
                    }

                    dialogHelper.populateSpinnerEvents(eventsList)
                } else {
                    Log.e("Error: ", "Greska prilikom dohvaćanja evenata")
                }
            }

        dialogHelper.populateSpinnerTickets(MockDataLoader.getTicketCategories())
    }

    private fun showEditTicketDialog(ticket: Ticket) {
        val newTicketDialogView = LayoutInflater
            .from(context)
            .inflate(R.layout.new_update_ticket_dialog, null)

        val dialogHelper = NewTicketDialogHelper(newTicketDialogView)

        dialogHelper.populateSpinnerTickets(MockDataLoader.getTicketCategories())
        dialogHelper.populateSpinnerEvents(mutableListOf())
        dialogHelper.populateDialogWithTicket(ticket)

        AlertDialog.Builder(context)
            .setView(newTicketDialogView)
            .setTitle(getString(R.string.update_ticket))
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                if (ticket.id != null) {
                    val updatedTicket = dialogHelper.buildTask()
                    ticketsDAO.updateTicket(ticket.id!!, updatedTicket)
                    loadTickets()
                } else {
                    Log.e("Error:", "Ticket/Ticket_ID su null")
                }
            }
            .setNegativeButton((getString(R.string.cancel)), null)
            .show()
    }


    private fun loadTickets() {
        ticketsDAO.getAllTickets()
            .addOnSuccessListener { querySnapshot ->
                val tickets = querySnapshot.toObjects(Ticket::class.java)
                val ticketAdapter = recyclerView.adapter as TicketAdapter
                ticketAdapter.submitList(tickets)
            }
            .addOnFailureListener { exception ->
                handleError(exception)
            }
    }

    private fun handleError(exception: Exception) {
        Log.e("EventFragment", "Failed to retrieve events", exception)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show()
            } else {
                val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid
                var eventJurassic = "okwOAvd8K2nC6TiEO1ge"
                if (result.contents == eventJurassic + currentUserId) {
                    Toast.makeText(activity, "Entrance approved!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "Ticket is not valid!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun scanQr(){
        val integrator = IntentIntegrator.forSupportFragment(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        integrator.setPrompt("Scan a QR Code")
        integrator.setCameraId(0)
        integrator.setBeepEnabled(false)
        integrator.setOrientationLocked(false)
        integrator.setBarcodeImageEnabled(true)
        integrator.initiateScan()
    }
}
