package hr.foi.rampu.eventmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import hr.foi.rampu.eventmanager.R
import hr.foi.rampu.eventmanager.entity.Event
import hr.foi.rampu.eventmanager.entity.Ticket
import java.text.SimpleDateFormat
import java.util.Locale

class TicketAdapter(
    private var ticketList: MutableList<Ticket>,
    private val onItemClick: (Ticket) -> Unit
) : RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    private val db = FirebaseFirestore.getInstance()
    inner class TicketViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val sdf: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.ENGLISH)
        private val ticketEventName: TextView
        private val ticketEventDate: TextView
        private val ticketPrice: TextView
        private val ticketCategory: TextView
        private val qrImage: ImageView

        init {
            ticketEventName = view.findViewById(R.id.tv_ticket_event_name)
            ticketEventDate = view.findViewById(R.id.tv_ticket_event_date)
            ticketPrice = view.findViewById(R.id.tv_price)
            ticketCategory = view.findViewById(R.id.tv_ticket_category)
            qrImage = view.findViewById(R.id.qr_imageView)

            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(ticketList[position])
                }
            }
        }

        fun bind(ticket: Ticket) {
            ticketEventName.text = ticket.eventName
            ticketEventDate.text = sdf.format(ticket.eventDate)
            ticketPrice.text = ticket.price.toString()
            ticketCategory.text = ticket.ticketCategory.toString()

            when (ticket.ticketCategory!!.name) {
                "VIP" -> qrImage.setImageResource(R.drawable.pic_concert)
                "Parter" -> qrImage.setImageResource(R.drawable.pic_show)
                "Tribina" -> qrImage.setImageResource(R.drawable.pic_sport)
                "Djecja" -> qrImage.setImageResource(R.drawable.pic_bussines)
                else -> qrImage.setImageResource(R.drawable.event)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val ticketView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.ticket_list_items, parent, false)
        return TicketViewHolder(ticketView)
    }

    override fun getItemCount() : Int {
        return ticketList.size
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        holder.bind(ticketList[position])
    }

    private fun updateTicketList(newTicketList: MutableList<Ticket>) {
        ticketList = newTicketList
        notifyDataSetChanged()
    }

    fun submitList(newTicketList: MutableList<Ticket>) {
        updateTicketList(newTicketList)
    }
}