package hr.foi.rampu.eventmanager.helpers

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import hr.foi.rampu.eventmanager.R
import hr.foi.rampu.eventmanager.entity.Event
import hr.foi.rampu.eventmanager.entity.EventType
import hr.foi.rampu.eventmanager.entity.Ticket
import hr.foi.rampu.eventmanager.entity.TicketType
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class NewEventDialogHelper(private val view: View) {

    private val selectedDateTime: Calendar = Calendar.getInstance()

    private val sdfDate = SimpleDateFormat("dd.MM.yyyy.", Locale.ENGLISH)
    private val sdfTime = SimpleDateFormat("HH:mm", Locale.ENGLISH)

    private val spinner = view.findViewById<Spinner>(R.id.spn_new_event_type)
    private val eventTypeSpinner = view.findViewById<Spinner>(R.id.spn_update_event_type)
    private val dateSelection = view.findViewById<EditText>(R.id.et_new_event_date)
    private val timeSelection = view.findViewById<EditText>(R.id.et_new_event_time)
    private val dateSelectionUpdate = view.findViewById<EditText>(R.id.et_update_event_date)
    private val timeSelectionUpdate = view.findViewById<EditText>(R.id.et_update_event_time)

    fun populateSpinner(categories: MutableList<EventType>) {
        val spinnerAdapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_spinner_item,
            categories
        )
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
    }

    fun activateDateTimeListeners() {
        dateSelection.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                DatePickerDialog(
                    view.context,
                    { _, year, monthOfYear, dayOfMonth ->
                        selectedDateTime.set(year, monthOfYear, dayOfMonth)
                        dateSelection.setText(sdfDate.format(selectedDateTime.time).toString())
                    },
                    selectedDateTime.get(Calendar.YEAR),
                    selectedDateTime.get(Calendar.MONTH),
                    selectedDateTime.get(Calendar.DAY_OF_MONTH),
                ).show()
                view.clearFocus()
            }
        }

        timeSelection.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                TimePickerDialog(
                    view.context,
                    { _, hourOfDay, minute ->
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        selectedDateTime.set(Calendar.MINUTE, minute)
                        timeSelection.setText(sdfTime.format(selectedDateTime.time).toString())
                    },
                    selectedDateTime.get(Calendar.HOUR_OF_DAY),
                    selectedDateTime.get(Calendar.MINUTE),
                    true
                ).show()
                view.clearFocus()
            }
        }
    }

    fun activateDateTimeListenersUpdate() {
        dateSelectionUpdate.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                DatePickerDialog(
                    view.context,
                    { _, year, monthOfYear, dayOfMonth ->
                        selectedDateTime.set(year, monthOfYear, dayOfMonth)
                        dateSelectionUpdate.setText(sdfDate.format(selectedDateTime.time).toString())
                    },
                    selectedDateTime.get(Calendar.YEAR),
                    selectedDateTime.get(Calendar.MONTH),
                    selectedDateTime.get(Calendar.DAY_OF_MONTH),
                ).show()
                view.clearFocus()
            }
        }

        timeSelectionUpdate.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                TimePickerDialog(
                    view.context,
                    { _, hourOfDay, minute ->
                        selectedDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        selectedDateTime.set(Calendar.MINUTE, minute)
                        timeSelectionUpdate.setText(sdfTime.format(selectedDateTime.time).toString())
                    },
                    selectedDateTime.get(Calendar.HOUR_OF_DAY),
                    selectedDateTime.get(Calendar.MINUTE),
                    true
                ).show()
                view.clearFocus()
            }
        }
    }

    fun populateEventTypesSpinner(types: MutableList<EventType>) {
        val eventTypes = mutableListOf<Any>()

        types.forEach { eventType ->
            eventTypes.add(eventType)
        }

        val spinnerAdapter = ArrayAdapter(
            view.context,
            android.R.layout.simple_spinner_item,
            eventTypes
        )

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        eventTypeSpinner.adapter = spinnerAdapter
    }

    fun populateDialogWithEvent(event: Event) {
        val etName = view.findViewById<EditText>(R.id.et_update_event_name)
        etName.setText(event.name)

        val etLocation = view.findViewById<EditText>(R.id.et_update_event_location)
        etLocation.setText(event.location)

        val etDescription = view.findViewById<EditText>(R.id.et_update_event_description)
        etDescription.setText(event.eventDescription)

        // Use SimpleDateFormat for formatting the date
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val etEventDate = view.findViewById<EditText>(R.id.et_update_event_date)
        etEventDate.setText(dateFormat.format(event.eventDate))

        // Use SimpleDateFormat for formatting the time
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        val etEventTime = view.findViewById<EditText>(R.id.et_update_event_time)
        etEventTime.setText(timeFormat.format(event.eventDate))

        val eventTypeSpinner = view.findViewById<Spinner>(R.id.spn_update_event_type)
        val categoryAdapter = eventTypeSpinner.adapter as ArrayAdapter<EventType>
        val categoryPosition = categoryAdapter.getPosition(event.eventType)
        eventTypeSpinner.setSelection(categoryPosition)
    }


    fun buildTask(): Event {
        val etName = view.findViewById<EditText>(R.id.et_new_event_name)
        val newTaskName = etName.text.toString()

        val etLocation = view.findViewById<EditText>(R.id.et_new_event_location)
        val newEventLocation = etLocation.text.toString()

        val etDescription = view.findViewById<EditText>(R.id.et_new_event_description)
        val newEventDescription = etDescription.text.toString()

        val spinnerCategory = view.findViewById<Spinner>(R.id.spn_new_event_type)
        val selectedCategory = spinnerCategory.selectedItem as EventType

        return Event(null, newTaskName, newEventLocation, selectedDateTime.time, newEventDescription, selectedCategory,FirebaseAuth.getInstance().uid!!)
    }

}
