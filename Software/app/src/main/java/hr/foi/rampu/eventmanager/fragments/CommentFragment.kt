package hr.foi.rampu.eventmanager.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.eventmanager.R
import hr.foi.rampu.eventmanager.adapters.CommentAdapter
import hr.foi.rampu.eventmanager.database.commentsDAO
import hr.foi.rampu.eventmanager.database.eventsDAO
import hr.foi.rampu.eventmanager.database.usersDAO
import hr.foi.rampu.eventmanager.entity.Comment
import hr.foi.rampu.eventmanager.entity.Event
import hr.foi.rampu.eventmanager.entity.EventType
import hr.foi.rampu.eventmanager.entity.User
import hr.foi.rampu.eventmanager.helpers.MockDataLoader
import hr.foi.rampu.eventmanager.helpers.NewEventDialogHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class CommentFragment : Fragment() {
    private lateinit var selectedEvent: Event
    private lateinit var currentUser: User
    private val commentsDAO = commentsDAO()
    private lateinit var recyclerView: RecyclerView
    private lateinit var commentAdapter: CommentAdapter

    private fun closeKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        currentUser = usersDAO().getCurrentUser() ?: User("", "", "", "")
        return inflater.inflate(R.layout.fragment_comment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        selectedEvent = requireArguments().getSerializable("selectedEvent") as Event

        val commentEditText: EditText = view.findViewById(R.id.editTextComment)
        val ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        val submitButton: Button = view.findViewById(R.id.buttonSubmit)
        val updateButton: Button = view.findViewById(R.id.btnUpdateEvent)
        recyclerView = view.findViewById(R.id.recyclerViewComments)

        recyclerView.layoutManager = LinearLayoutManager(context)

        commentAdapter = CommentAdapter(mutableListOf()) { commentId ->
            deleteComment(commentId)
        }


        loadCommentsAndRatings()


        submitButton.setOnClickListener {
            val commentText = commentEditText.text.toString()
            val ratingValue = ratingBar.rating.toInt()

            if (commentText.isNotEmpty() && ratingValue > 0) {
                val newComment = Comment(
                    userUid = currentUser.uid!!,
                    eventId = selectedEvent.id.toString(),
                    text = commentText,
                    value = ratingValue.toLong(),
                    id = "0"
                )

                commentsDAO.saveCommentToFirestore(newComment,
                    {
                        loadCommentsAndRatings()
                        closeKeyboard()
                    },
                    { exception ->
                        Toast.makeText(
                            requireContext(),
                            "Greška pri spremanju komentara.",
                            Toast.LENGTH_SHORT
                        ).show()
                    })

            } else {
                Toast.makeText(requireContext(), "Unesite komentar i ocjenu.", Toast.LENGTH_SHORT).show()
            }
        }
        updateButton.setOnClickListener {
            showUpdateEventDialog()
        }


    }

    private fun loadCommentsAndRatings() {
        val commentsList = mutableListOf<Comment>()


        commentsDAO.getCommentsByEventId(
            selectedEvent.id.toString(),
            { comments ->
                commentsList.addAll(comments)


                commentAdapter = CommentAdapter(commentsList) {commentId -> deleteComment(commentId)}
                recyclerView.adapter = commentAdapter

                val numericRatings = comments.map { it.value }
                val ratingsTextView: TextView? = view?.findViewById(R.id.textViewRating)
                ratingsTextView?.text = "Ocjene: ${numericRatings.joinToString(", ")}"
                ratingsTextView?.visibility = View.VISIBLE
            },
            { exception ->
                Toast.makeText(
                    requireContext(),
                    "Greška pri dohvaćanju komentara i ocjena.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    private fun deleteComment(commentId: String) {
        commentsDAO.deleteComment(commentId,
            {
                loadCommentsAndRatings()
                Toast.makeText(requireContext(), "Komentar uspešno obrisan", Toast.LENGTH_SHORT).show()
            },
            { exception ->
                Toast.makeText(requireContext(), "Greška pri brisanju komentara.", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun showUpdateEventDialog() {
        val dialogView = layoutInflater.inflate(R.layout.new_update_event_dialog, null)
        val dialogHelper = NewEventDialogHelper(dialogView)

        dialogHelper.populateEventTypesSpinner(MockDataLoader.getDemoCategories())
        dialogHelper.activateDateTimeListenersUpdate()
        dialogHelper.populateDialogWithEvent(selectedEvent)

        val alertDialog = AlertDialog.Builder(requireContext()).apply {
            setView(dialogView)
            setPositiveButton("Update") { _, _ ->
                selectedEvent.id?.let { eventId ->
                    val newName = dialogView.findViewById<EditText>(R.id.et_update_event_name).text.toString()
                    val newLocation = dialogView.findViewById<EditText>(R.id.et_update_event_location).text.toString()
                    val newDescription = dialogView.findViewById<EditText>(R.id.et_update_event_description).text.toString()
                    val newType = dialogView.findViewById<Spinner>(R.id.spn_update_event_type).selectedItem as EventType
                    val newDate = parseDate(dialogView.findViewById<EditText>(R.id.et_update_event_date).text.toString())
                    val newTime = parseTime(dialogView.findViewById<EditText>(R.id.et_update_event_time).text.toString())

                    val newDateTime = combineDateAndTime(newDate, newTime)
                    val updatedEvent = Event(eventId, newName, newLocation, newDateTime, newDescription, newType,"")

                    eventsDAO().updateEvent(eventId, updatedEvent).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(requireContext(), "Event uspješno ažuriran", Toast.LENGTH_SHORT).show()
                            eventsDAO().getAllEvents()
                        } else {
                            Toast.makeText(requireContext(), "Greška prilikom ažuriranja", Toast.LENGTH_SHORT).show()
                        }
                    }
                } ?: Toast.makeText(requireContext(), "Nepostojeći event", Toast.LENGTH_SHORT).show()
            }
            setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
        }.create()

        alertDialog.show()
    }

    private fun parseDate(dateStr: String): Date {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        return dateFormat.parse(dateStr) ?: Date()
    }

    private fun parseTime(timeStr: String): Date {
        val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return timeFormat.parse(timeStr) ?: Date()
    }

    private fun combineDateAndTime(date: Date, time: Date): Date {
        val calendarDate = Calendar.getInstance()
        calendarDate.time = date

        val calendarTime = Calendar.getInstance()
        calendarTime.time = time

        val year = calendarDate.get(Calendar.YEAR)
        val month = calendarDate.get(Calendar.MONTH)
        val day = calendarDate.get(Calendar.DAY_OF_MONTH)

        val hour = calendarTime.get(Calendar.HOUR_OF_DAY)
        val minute = calendarTime.get(Calendar.MINUTE)

        val combinedCalendar = Calendar.getInstance()
        combinedCalendar.set(year, month, day, hour, minute)

        return combinedCalendar.time
    }
}
