package hr.foi.rampu.eventmanager.adapters

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import hr.foi.rampu.eventmanager.R
import hr.foi.rampu.eventmanager.database.commentsDAO
import hr.foi.rampu.eventmanager.database.usersDAO
import hr.foi.rampu.eventmanager.entity.Event
import hr.foi.rampu.eventmanager.fragments.EventNotificationReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EventAdapter(private var context: Context,  private var eventList: MutableList<Event>,private var userEventList:MutableList<String?>) :
    RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    private var itemClickListener: ((Event) -> Unit)? = null

    private var alarmIntent: PendingIntent? = null
    inner class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val sdf: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.ENGLISH)
        private val eventName: TextView
        private val eventDate: TextView
        private val eventImage: ImageView
        private val buttonSign:Button
        private val usersDAO = usersDAO()
        private var context:Context?=null
        private val buttonGenerateReport:Button
        init {
            eventName = view.findViewById(R.id.tv_event_name)
            eventDate = view.findViewById(R.id.tv_event_date)
            eventImage = view.findViewById(R.id.event_imageView)
            buttonSign=view.findViewById(R.id.btnSignForEvent)
            buttonGenerateReport=view.findViewById(R.id.btnGenerateReport)
            try{
                context=view.context
            }
            catch (e:Exception){

            }
        }


        @SuppressLint("ResourceAsColor", "SuspiciousIndentation")
        fun bind(event: Event) {
            try{
                eventName.text = event.name
                eventDate.text = sdf.format(event.eventDate)

                if(Date().compareTo(event.eventDate) == 1)
                {
                  buttonSign.visibility=View.GONE
                    if(event.creatorId == FirebaseAuth.getInstance().uid){
                        buttonGenerateReport.visibility=View.VISIBLE
                    }
                    else{
                        buttonGenerateReport.visibility=View.GONE

                    }

                }
                else
                {
                    buttonSign.visibility=View.VISIBLE
                    buttonGenerateReport.visibility=View.GONE

                }


                if(userEventList.firstOrNull(){it==event.id}==null ) {
                    buttonSign.text= context?.getString(R.string.signForEventText)
                    buttonSign.setBackgroundColor(ContextCompat.getColor(context!!,R.color.btnSignForEvent))
                } else{
                    buttonSign.text= context?.getString(R.string.signoutFromEventText)
                    buttonSign.setBackgroundColor(ContextCompat.getColor(context!!,R.color.btnSignOutFromEvent))
                }
            }
            catch (e:Exception){

            }
            when (event.eventType!!.name) {
                "Concert" -> eventImage.setImageResource(R.drawable.pic_concert)
                "Show" -> eventImage.setImageResource(R.drawable.pic_show)
                "Sport" -> eventImage.setImageResource(R.drawable.pic_sport)
                "Business" -> eventImage.setImageResource(R.drawable.pic_bussines)
                "Politics" -> eventImage.setImageResource(R.drawable.pic_politics)
                else -> eventImage.setImageResource(R.drawable.event)
            }
            itemView.setOnClickListener{
                itemClickListener?.invoke(event)
            }
            buttonSign.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    usersDAO.signForEvent(event.id)
                    userEventList=usersDAO.userEventList()
                    if(buttonSign.text!=context?.getString(R.string.signForEventText)){
                        buttonSign.text= context?.getString(R.string.signForEventText)
                        buttonSign.setBackgroundColor(ContextCompat.getColor(context!!,R.color.btnSignForEvent))

                        alarmIntent?.let { intent ->
                            val alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                            alarmManager.cancel(intent)
                        }

                    } else{
                        buttonSign.text=context?.getString(R.string.signoutFromEventText)
                        buttonSign.setBackgroundColor(ContextCompat.getColor(context!!,R.color.btnSignOutFromEvent))
                        createNotificationChannel()
                        scheduleNotification(event)
                    }
                }

            }

            buttonGenerateReport.setOnClickListener{


                    val dialog= Dialog(context!!)
                    dialog.setContentView(R.layout.dialog_stats)
                    dialog.show()

                val txtDate=dialog.findViewById<TextView>(R.id.txtDate)
                val txtName=dialog.findViewById<TextView>(R.id.txtName)
                val txtPlace=dialog.findViewById<TextView>(R.id.txtPlace)
                val txtPeopleNo=dialog.findViewById<TextView>(R.id.txtPeopleNo)
                val txtAvgGrad=dialog.findViewById<TextView>(R.id.txtAvgGrade)

                    txtPlace.text = "Lokacija: " + event.location
                    txtName.text = "Naziv: " + event.name
                    txtDate.text = "Datum: " + sdf.format(event.eventDate).toString()
                    txtPeopleNo.text = "Broj ljudi ";
                    commentsDAO().getAvgGradeForEvent(event.id!!) { it ->

                        txtAvgGrad.text ="Prosječna ocjena: "+ if (it.toString() !== "NaN") it.toString() else "Nema ocjena"
                    }
                    commentsDAO().getNoOfPeopleForEvent(event.id) { it ->

                    txtPeopleNo.text ="Broj prijavljenih ljudi: "+it.toString()
                }

                    val btn = dialog.findViewById<Button>(R.id.btnCloseStats)
                    btn.setOnClickListener {
                        dialog.dismiss()

                    }





            }
        }
    }


    private fun generatePDF() {


    }

    private fun createNotificationChannel() {
        val CHANNEL_ID = "qr_notification_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Obavijest eventa"
            val descriptionText = "Obavijest o sutrašnjem prijavljenom eventu"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun scheduleNotification(event: Event) {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis

        val eventDate = event.eventDate
        val eventName = event.name

        val alarmTime = event.eventDate.time - 86400000

        if (eventDate.time > currentTime) {
            val intent = Intent(context, EventNotificationReceiver::class.java)
            intent.putExtra("EVENT_DATE", SimpleDateFormat("dd.MM HH:mm").format(eventDate))
            intent.putExtra("EVENT_NAME", eventName)
            alarmIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            )

            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarmTime, alarmIntent!!)
        } else {
            alarmIntent?.let { intent ->
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.cancel(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val eventView = LayoutInflater
            .from(parent.context)
            .inflate(hr.foi.rampu.eventmanager.R.layout.event_list_items, parent, false)
        return EventViewHolder(eventView)
    }

    override fun getItemCount() : Int {
        return eventList.size
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        holder.bind(eventList[position])
    }

    fun updateEventList(newEventList: MutableList<Event>) {
        eventList = newEventList
        notifyDataSetChanged()
    }

    fun submitList(newEventList: MutableList<Event>) {
        updateEventList(newEventList)
    }

    fun setOnItemClickListener(listener: (Event) -> Unit) {
        itemClickListener = listener
    }


}
