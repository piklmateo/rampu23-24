package hr.foi.rampu.eventmanager.fragments

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import hr.foi.rampu.eventmanager.R
import hr.foi.rampu.eventmanager.adapters.EventAdapter
import hr.foi.rampu.eventmanager.database.eventsDAO
import hr.foi.rampu.eventmanager.database.usersDAO
import hr.foi.rampu.eventmanager.entity.Event
import hr.foi.rampu.eventmanager.helpers.MockDataLoader
import hr.foi.rampu.eventmanager.helpers.NewEventDialogHelper
import kotlinx.coroutines.launch

class EventFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var btnCreateTask: FloatingActionButton
    private lateinit var btnShowReset: Button
    private lateinit var btnShowConcert: Button
    private lateinit var btnShowSport: Button
    private lateinit var btnShowShow: Button
    private lateinit var btnShowPolitics: Button
    private lateinit var btnShowBusiness: Button
    private val eventsDAO = eventsDAO()
    private val usersDAO=usersDAO()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView = view.findViewById(R.id.rv_event_show)

        // za asinkrone funkcije (userEventList()) koristi lifecycle.launch
        lifecycleScope.launch {
            //inicijalizraj listu koja ce sadrzavati sve evente na koje se trenutni korisnik prijavio
            val userEventList:MutableList<String?> =
                try{
                    //probaj dohvatiti listu evenata
                    usersDAO.userEventList()
                }
                catch (e:Exception){
                    // inicijalizraj praznu listu
                     mutableListOf()
                }

            val eventAdapter= EventAdapter(requireContext(), mutableListOf(),userEventList)
            recyclerView.adapter = eventAdapter



        recyclerView.layoutManager = LinearLayoutManager(view.context)
        loadEvents()

        btnCreateTask = view.findViewById(R.id.fab_create_new_event)
        btnCreateTask.setOnClickListener {
            showDialog()
        }

        btnShowReset = view.findViewById(R.id.btnShowReset)
        btnShowReset.setOnClickListener {
            loadEvents()
        }

        btnShowConcert = view.findViewById(R.id.btnShowConcert)
        btnShowSport = view.findViewById(R.id.btnShowSport)
        btnShowShow = view.findViewById(R.id.btnShowShow)
        btnShowPolitics = view.findViewById(R.id.btnShowPolitics)
        btnShowBusiness = view.findViewById(R.id.btnShowBusiness)

        //objedinjuje duplikacije
        fun filterEventsByType(eventType: String) {
            loadEventsAgain { originalEvents ->
                val filteredEvents = if (eventType == "All") {
                    originalEvents.toMutableList()
                } else {
                    originalEvents.filter { event ->
                        event.eventType?.name == eventType
                    }.toMutableList()
                }
                eventAdapter.updateEventList(filteredEvents)
            }
        }

        btnShowShow.setOnClickListener { filterEventsByType("Show") }
        btnShowSport.setOnClickListener { filterEventsByType("Sport") }
        btnShowConcert.setOnClickListener { filterEventsByType("Concert") }
        btnShowPolitics.setOnClickListener { filterEventsByType("Politics") }
        btnShowBusiness.setOnClickListener { filterEventsByType("Business") }

        // Dodaj za otvaranje CommentFragment kada se događaj klikne
            eventAdapter.setOnItemClickListener { selectedEvent ->
                showCommentFragment(selectedEvent)
            }
        }
    }

    private fun showDialog() {
        val newTaskDialogView = LayoutInflater
            .from(context)
            .inflate(R.layout.new_event_dialog, null)

        val dialogHelper = NewEventDialogHelper(newTaskDialogView)

        AlertDialog.Builder(context)
            .setView(newTaskDialogView)
            .setTitle(getString(R.string.create_a_new_event))
            .setPositiveButton(getString(R.string.create_a_new_event)) { _, _ ->
                val newEvent = dialogHelper.buildTask()
                eventsDAO.addEvent(newEvent)
                try{
                loadEvents()
                }
                catch (e:Exception){
                    Log.d("LoadEvent exception",e.message.toString())
                }
            }
            .show()
        dialogHelper.populateSpinner(MockDataLoader.getDemoCategories())
        dialogHelper.activateDateTimeListeners()
    }

    private fun loadEventsAgain(onSuccess: (List<Event>) -> Unit) {
        eventsDAO.getAllEvents()
            .addOnSuccessListener { querySnapshot ->
                val events = querySnapshot.toObjects(Event::class.java)
                val eventAdapter = recyclerView.adapter as EventAdapter
                eventAdapter.submitList(events)

                onSuccess(events)
            }
            .addOnFailureListener { exception ->
                handleError(exception)
            }
    }

    private fun loadEvents() {
        eventsDAO.getAllEvents()
            .addOnSuccessListener { querySnapshot ->
                val events = querySnapshot.toObjects(Event::class.java)
                val eventAdapter = recyclerView.adapter as EventAdapter
                eventAdapter.submitList(events)
            }
            .addOnFailureListener { exception ->
                handleError(exception)
            }
    }

    private fun handleError(exception: Exception) {
        Log.e("EventFragment", "Failed to retrieve events", exception)
    }


    private fun showCommentFragment(selectedEvent: Event) {
        val commentFragment = CommentFragment()
        val bundle = Bundle()
        bundle.putSerializable("selectedEvent", selectedEvent)
        commentFragment.arguments = bundle

        val transaction = requireActivity().supportFragmentManager.beginTransaction()

        transaction.replace(R.id.parentLayout, commentFragment)

        transaction.addToBackStack(null)
        transaction.commit()

    }
}
