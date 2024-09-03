package hr.foi.rampu.eventmanager

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.database.FirebaseDatabase
import hr.foi.rampu.eventmanager.helpers.InputValidation

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val switchRegistration: TextView = findViewById(R.id.tvRegistrationSwitch)
        switchRegistration.setOnClickListener {
            val intent = Intent(this, Registration::class.java)

            startActivity(intent)
        }

        val loginButton: Button = findViewById(R.id.btn_login)
        loginButton.setOnClickListener {
            signIn()
        }
    }

    private fun signIn() {
        val email = findViewById<EditText>(R.id.et_email).text.toString()
        val password = findViewById<EditText>(R.id.et_password).text.toString()

        val validation = InputValidation()
        if(email.isBlank() || password.isBlank()) {
            Toast.makeText(this, getString(R.string.emptyTextCheck), Toast.LENGTH_SHORT).show()
            return
        }
        if(!validation.emailRegexCheck(baseContext, email)) {
            Toast.makeText(this, getString(R.string.wrongEmailCheck), Toast.LENGTH_SHORT).show()
            return
        }

        auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(this, getString(R.string.successfullSignIn), Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this, getString(R.string.failedSignIn), Toast.LENGTH_SHORT).show()
            }
        }
    }
}