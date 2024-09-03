package hr.foi.rampu.eventmanager.adapters

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import hr.foi.rampu.eventmanager.R
import hr.foi.rampu.eventmanager.database.eventsDAO
import hr.foi.rampu.eventmanager.database.usersDAO
import hr.foi.rampu.eventmanager.entity.Event
import hr.foi.rampu.eventmanager.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale


class UserEventAdapter(private val usersEventList:MutableList<String?>) : RecyclerView.Adapter<UserEventAdapter.UserEventViewHolder>() {

    inner class UserEventViewHolder(view: View):RecyclerView.ViewHolder(view) {
        private val sdf: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy. HH:mm", Locale.ENGLISH)
        private val eventName: TextView
        private val eventDate: TextView
        private val ivQRCode: ImageView
        private val eventsDAO:eventsDAO
        private val usersDAO: usersDAO
        init {
            eventName = view.findViewById(R.id.tv_event_name)
            eventDate = view.findViewById(R.id.tv_event_date)
            ivQRCode = view.findViewById(R.id.event_imageView)
            val btn=view.findViewById<Button>(R.id.btnSignForEvent)
            btn.visibility=View.INVISIBLE
            eventsDAO=eventsDAO()
            usersDAO = usersDAO()

            // smanji veličinu naziva događaja
            eventName.textSize=18f


        }
       suspend fun bind(eventId: String) {
           var event: Event?
           var user: User?
           try {
               event = eventsDAO.getEventById(eventId)
               user = usersDAO.getCurrentUser()
           }
           catch (e:Exception){
               event=null
               user=null
           }
           var userId = user?.uid

           generateQrCode(eventId + userId)
           eventName.text=event?.name
           eventDate.text = sdf.format(event?.eventDate)
        }
        @SuppressLint("SuspiciousIndentation")
        fun generateQrCode(kod: String){
            val data = kod.trim()
            if (data.isNotEmpty()){
                val writer = QRCodeWriter()
                try {
                    val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512,512)
                    val width = bitMatrix.width
                    val height = bitMatrix.height
                    val bmp = Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565)
                    for(x in 0 until width){
                        for(y in 0 until height){
                            bmp.setPixel(x,y, if(bitMatrix[x,y]) Color.BLACK else Color.WHITE)
                        }
                    }
                    ivQRCode.setImageBitmap(bmp)
                }catch (e: WriterException){
                    e.printStackTrace()
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserEventViewHolder {
        val eventView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.event_list_items, parent, false)
        return UserEventViewHolder(eventView)
    }

    override fun getItemCount(): Int {
       return usersEventList.filter { !it.equals(null) } .size
    }

    override fun onBindViewHolder(holder: UserEventViewHolder, position: Int) {
        CoroutineScope(Dispatchers.Main).launch {
         if(usersEventList[position]!=null)
            holder.bind(usersEventList[position]!!)
        }

    }


}