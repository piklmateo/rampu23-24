package hr.foi.rampu.eventmanager.database

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import hr.foi.rampu.eventmanager.entity.Event
import hr.foi.rampu.eventmanager.entity.User
import kotlinx.coroutines.tasks.await


class usersDAO {
    private var db:FirebaseFirestore;
    private lateinit var userMap:HashMap<String,String?>
    private var auth=FirebaseAuth.getInstance()
    private var userId:String
    init {
        db=Firebase.firestore
        userId=FirebaseAuth.getInstance().currentUser!!.uid
    }


    fun AddNewUser(user:User){
        userMap= hashMapOf(
            "FullName" to user.name,
            "Phone" to user.phone,
            "Email" to user.email
        )
        db.collection("User").document(user.uid!!).set(userMap)
            .addOnSuccessListener {
                println("Uspješno")

            }
            .addOnFailureListener{
                println("Neuspješno")

            }
    }

    fun getCurrentUser() : User? {
        val currentUser = auth.currentUser
        return if(currentUser != null){
            User(
                currentUser.uid,
                currentUser.displayName,
                currentUser.email,
                currentUser.phoneNumber
            )
        }else{
            null
        }
    }

    suspend fun GetUserData(callback: (User?) -> Unit) {
        try {
            val userDocument = db.collection("User").document(userId).get().await()
            if (userDocument.exists()) {
                val user = User(
                    userId,
                    userDocument.getString("FullName"),
                    userDocument.getString("Email"),
                    userDocument.getString("Phone"),
                    userDocument.getLong("points") ?: 0
                )
                callback(user)
            } else {
                callback(null)
            }
        } catch (e: Exception) {
            callback(null)
        }
    }


    fun UpdateUserData(user:User,pass:String ,complete:(Boolean)->Unit){

        userMap= hashMapOf(
            "FullName" to user.name,
            "Phone" to user.phone,
            "Email" to user.email,

        )

        db.collection("User").document(user.uid!!).update(userMap as Map<String, Any>)
            .addOnSuccessListener {
                val userInstance=FirebaseAuth.getInstance().currentUser
                userInstance?.let{User->
                    val credentil=EmailAuthProvider.getCredential(User.email!!,pass)
                    Log.d("AUTH","EMAIL AUTH: "+User.email+" "+pass)
                    User.reauthenticate(credentil).addOnSuccessListener {
                        Log.d("AUTH","Success AUTH")
                        val UserAuth = FirebaseAuth.getInstance().currentUser
                        if (UserAuth != null) {
                            UserAuth.updateEmail(user.email!!).addOnCompleteListener{
                                Log.d("AUTH","Success change")
                                complete(true)
                            }
                                .addOnFailureListener {
                                    Log.d("AUTH",it.message.toString())
                                    complete(false)
                                }
                        }

                    }
                        .addOnFailureListener {
                            Log.d("AUTH","Fail AUTH")
                            complete(false)
                        }


                }
            }
            .addOnFailureListener{
                complete(false)


            }

    }


    suspend fun DeleteUser(callback: (Boolean)->Unit){

        val user=Firebase.auth.currentUser!!
        user.delete().addOnCompleteListener {task->
            if(task.isSuccessful)
                db.collection("User").document(userId).delete().addOnCompleteListener{
                    callback(true)
                    Log.d("DeleteUser","obrisan")
                }
                    .addOnFailureListener {
                        callback(false)
                        Log.d("DeleteUser","obrisan u Authentication, ali ne i u Firestore bazi")
                    }


        }
            .addOnFailureListener {
                Log.d("DeleteUser","Nije obrisan")
                callback(false)
            }
            .await()
    }

    suspend fun getFullNameByUid(userUid: String): String {
        return try {
            val userDocument = db.collection("User").document(userUid).get().await()
            val fullName = userDocument.getString("FullName") ?: ""

            fullName
        } catch (e: Exception) {
            ""
        }
    }

    // dohvati sve evente na koje se korisnik prijavio
    // poziva se unutar try{}, u slucaju da ne postoji events lista
    suspend fun userEventList():MutableList<String?>{
        return  try{db.collection("User").document(userId).get().await().get("events") as MutableList<String?>}
        catch (e:Exception){
            mutableListOf<String?>()}
    }

    // dodaje/brise evente u/iz liste spremljenih evenata
    // ako prvi put sprema tada ce unutar try{} izbaciti exception pa ce preci u catch
    // unutar catcha se kreira lista koja sadrzi id eventa (parametar funkcije) i sprema se u bazu
   suspend fun signForEvent(id: String?) {
        val userDocument=db.collection("User").document(userId)
       var userListOfEvents :MutableList<String?>

       try{
            userListOfEvents=userEventList()
           var bodovi:Int=0
           try{
               bodovi = Integer.parseInt(userDocument.get().await().get("points").toString())
           }
           catch(e:Exception){

           }

           try{
               if(userListOfEvents?.firstOrNull() { it==id }==null){
                   userListOfEvents?.add(id)
                   bodovi +=15;
               }

               else {
                   userListOfEvents.remove(id)
                   bodovi -= 5
               }
               userDocument.update(mapOf("events" to userListOfEvents ))
               userDocument.update("points", bodovi);
           }
           catch (e:Exception){
               return
           }
       }
       catch (E:Exception){
           userListOfEvents = mutableListOf(id)
           userDocument.update(mapOf("events" to userListOfEvents ))
           return
       }


    }


}