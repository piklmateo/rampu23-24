package hr.foi.rampu.eventmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import hr.foi.rampu.eventmanager.entity.User
import hr.foi.rampu.eventmanager.helpers.InputValidation
import hr.foi.rampu.eventmanager.database.usersDAO

class Registration : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        val loginSwitch: TextView = findViewById(R.id.tvLoginSwitch)
        loginSwitch.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }

        val signUpBtn: Button = findViewById(R.id.btnRegister)
        signUpBtn.setOnClickListener {
            createAccount()
        }
    }

    private fun createAccount() {
        val name = findViewById<EditText>(R.id.et_full_name).text.toString()
        val email = findViewById<EditText>(R.id.et_email).text.toString()
        val phone = findViewById<EditText>(R.id.et_phone).text.toString()
        val password = findViewById<EditText>(R.id.et_password).text.toString()
        val pwConfirm = findViewById<EditText>(R.id.et_pwConfirm).text.toString()
        //val points = findViewById<EditText>(R.id.txtPoints).text.toString().toInt()

        val textValidation = InputValidation()
        if(!textValidation.emptyTextCheck(baseContext, name, email, phone, password, pwConfirm)) return
        if(!textValidation.emailRegexCheck(baseContext, email)) return
        if(!textValidation.phoneRegexCheck(baseContext, phone)) return
        if(!textValidation.passwordConfirmCheck(baseContext, password, pwConfirm)) return

        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful) {

                val user=User(it.getResult().user!!.uid,name,email,phone, 0)
                usersDAO().AddNewUser(user)

                Toast.makeText(this, getString(R.string.successfullSignUp), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, Login::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this, getString(R.string.failedSignUp), Toast.LENGTH_SHORT).show()
            }
        }
    }
}
