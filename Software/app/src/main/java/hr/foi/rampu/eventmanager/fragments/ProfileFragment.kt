package hr.foi.rampu.eventmanager.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.Group
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import hr.foi.rampu.eventmanager.Login
import hr.foi.rampu.eventmanager.R
import hr.foi.rampu.eventmanager.adapters.UserEventAdapter
import hr.foi.rampu.eventmanager.entity.User
import hr.foi.rampu.eventmanager.database.usersDAO
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {
    lateinit var txtFullname:EditText
    lateinit var txtPhone:EditText
    lateinit var txtEmail: EditText
    lateinit var txtPoints: TextView
    lateinit var btnSave:Button
    lateinit var btnLogout:Button
    lateinit var btnDelete:Button
    lateinit var progressBar:Group
    lateinit var groupPhone:Group
    lateinit var groupFullName:Group
    lateinit var groupEmail:Group
    lateinit var btnUserList:FloatingActionButton
    var password:String=""

    lateinit var  ivQRCode: ImageView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prepareUIElements(view)
        lifecycleScope.launch {

            getData()
            showInputElements()
            progressBar.visibility=View.INVISIBLE
        }

        btnSave.setOnClickListener{
            updateUserData()
        }
        btnLogout.setOnClickListener{
            SignOut()
        }
        btnDelete.setOnClickListener {
            DeleteProfile()
        }
        btnUserList.setOnClickListener {
            progressBar.visibility=View.VISIBLE
            ShowList()

        }
    }

    @SuppressLint("SuspiciousIndentation")
    private fun ShowList() {
        val dialog= Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_usereventslist_profile)

        // povecaj dialog
        (dialog.window)?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)

        val recyclerView=dialog.findViewById<RecyclerView>(R.id.rvUserEventList)
        val btn=dialog.findViewById<Button>(R.id.btnCloseDialogProfile)

            lifecycleScope.launch{
                val userEventAdapter=UserEventAdapter(usersDAO().userEventList())
                progressBar.visibility=View.INVISIBLE
                if(userEventAdapter.itemCount==0){
                    dialog.findViewById<TextView>(R.id.txtEmptyListMsg).visibility=View.VISIBLE
                    return@launch
                }
                recyclerView.adapter=userEventAdapter
                recyclerView.layoutManager=LinearLayoutManager(view?.context)


            }

        dialog.show()



        btn.setOnClickListener{
            dialog.dismiss()


        }
    }

    private fun DeleteProfile() {
        val dialog=AlertDialog.Builder(context)
        dialog.setTitle("Jeste li sigurni?")
        dialog.setMessage("Nakon brisanja nema povratka")
        dialog.setPositiveButton("Izbriši",DialogInterface.OnClickListener { _, _ ->
            lifecycleScope.launch {
            usersDAO().DeleteUser { success->
                if(success)
                {
                    val intent = Intent(context, Login::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    activity?.finish()
                }
                else
                {
                    Toast.makeText(activity,"Doslo je do pogreske",Toast.LENGTH_SHORT).show()
                }
            }
            }

        })
        dialog.setNegativeButton("Odustani",DialogInterface.OnClickListener { _, _ ->
           dialog.create().dismiss()
        })
        val alertDialog=dialog.create()
        alertDialog.show()
    }

    private fun SignOut() {
        FirebaseAuth.getInstance().signOut();

        val intent = Intent(context, Login::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        activity?.finish()
    }

    private fun updateUserData() {

        showDialog{pass->
            usersDAO().UpdateUserData(
            User(
                FirebaseAuth.getInstance().currentUser!!.uid,
                txtFullname.text.toString(),
                txtEmail.text.toString(),
                txtPhone.text.toString()
            ),
            pass
        ) { success ->
            if (success) {

                Toast.makeText(activity, "Uspješno spremljeni podaci", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(activity, "Došlo je pogreške", Toast.LENGTH_SHORT).show()
            }
        }
        txtFullname.clearFocus()
        txtPhone.clearFocus()
        txtEmail.clearFocus()
        };
    }

    private fun prepareUIElements(view: View) {
        btnUserList=view.findViewById(R.id.btnShowEventList)
        txtFullname = view.findViewById(R.id.etxtFullNameProfile)
        txtPhone = view.findViewById(R.id.etxtPhoneProfile)
        txtEmail = view.findViewById(R.id.etxtEmailProfile)
        txtPoints = view.findViewById(R.id.txtPoints)
        btnSave = view.findViewById(R.id.btnSaveProfile)
        btnLogout = view.findViewById(R.id.btnLogOutProfile);
        btnDelete=view.findViewById(R.id.btnDeleteProfile)
        progressBar = view.findViewById(R.id.progressBarProfile)
        groupEmail = view.findViewById(R.id.groupEmailProfile)
        groupPhone = view.findViewById(R.id.groupPhoneProfile)
        groupFullName = view.findViewById(R.id.groupFullNameProfile)


        progressBar.visibility=View.VISIBLE
        ivQRCode = view.findViewById(R.id.ivQRCode)
    }

    private fun showInputElements() {
        groupEmail.visibility=View.VISIBLE
        groupFullName.visibility=View.VISIBLE
        groupPhone.visibility=View.VISIBLE
        btnDelete.visibility=View.VISIBLE
        btnLogout.visibility=View.VISIBLE
        btnSave.visibility=View.VISIBLE
        btnUserList.visibility=View.VISIBLE

    }

    private suspend fun getData() {
        usersDAO().GetUserData { user ->
            if (user != null) {
                txtFullname.setText(user.name ?: "")
                txtPhone.setText(user.phone ?: "")
                txtEmail.setText(user.email ?: "")
                txtPoints.text = user.points.toString()
                generateQrCode(user.uid ?: "")
            } else {
                txtFullname.setText("")
                txtPhone.setText("")
                txtEmail.setText("")
                txtPoints.text = "0"
            }
        }
    }



    @SuppressLint("SuspiciousIndentation")
    fun generateQrCode(userUid: String){ //Generiran Qr kod baziran na User ID-u
        val data = userUid.trim()
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

   private fun showDialog(callbacks: (String)->Unit){
        val dialog= Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_text_input)
        dialog.show()
        val btn=dialog.findViewById<Button>(R.id.btnDialogConfirm)
        val text=dialog.findViewById<EditText>(R.id.etxtDialogPassword)
        btn.setOnClickListener{
            dialog.dismiss()
            callbacks(text.text.toString())

        }

    }
}