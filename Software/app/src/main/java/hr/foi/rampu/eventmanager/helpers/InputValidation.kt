package hr.foi.rampu.eventmanager.helpers

import android.content.Context
import android.widget.Toast
import hr.foi.rampu.eventmanager.R

class InputValidation {
    fun emptyTextCheck(context: Context, name: String, email: String, phone: String, password: String, pwConfirm: String) : Boolean {
        if(name.isBlank() || email.isBlank() || phone.isBlank() || phone.isBlank() || password.isBlank() || pwConfirm.isBlank()) {
            Toast.makeText(context, context.getString(R.string.emptyTextCheck), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    fun phoneRegexCheck(context: Context, phone: String) : Boolean {
        val regex = Regex("""^(?:\+\d{1,3})?\s?(?:\(\d{1,4}\))?[ -]?\d{1,6}(?:[ -]?\d{1,6})?$""")
        if(regex.matches(phone)) {
            return true
        }
        Toast.makeText(context, context.getString(R.string.wrongPhoneNumberCheck), Toast.LENGTH_SHORT).show()
        return false
    }
    
    fun emailRegexCheck(context: Context, email: String) : Boolean {
        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$")
        if(regex.matches(email)) {
            return true
        }
        Toast.makeText(context, context.getString(R.string.wrongEmailCheck), Toast.LENGTH_SHORT).show()
        return false
    }

    fun passwordConfirmCheck(context: Context, password: String, passwordConfirm: String) : Boolean {
        if(password == passwordConfirm) {
            return true
        }
        Toast.makeText(context, context.getString(R.string.wrongPasswordCheck), Toast.LENGTH_SHORT).show()
        return false
    }
}