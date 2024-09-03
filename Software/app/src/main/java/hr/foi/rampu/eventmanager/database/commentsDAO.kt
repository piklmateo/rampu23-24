package hr.foi.rampu.eventmanager.database

import android.util.Log
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import hr.foi.rampu.eventmanager.entity.Comment
import kotlinx.coroutines.tasks.await

class commentsDAO {
    private val db = FirebaseFirestore.getInstance()


    private val commentsCollection: CollectionReference = db.collection("Comments")

    fun getCommentsByEventId(eventId: String, onSuccess: (List<Comment>) -> Unit, onFailure: (Exception) -> Unit) {
        commentsCollection
            .whereEqualTo("eventId", eventId)
            .get()
            .addOnSuccessListener { commentsSnapshot ->
                val comments = mutableListOf<Comment>()

                for (commentDocument in commentsSnapshot.documents) {
                    val userUid = commentDocument.getString("userUid") ?: ""
                    val text = commentDocument.getString("text") ?: ""
                    val value = commentDocument.getLong("value")
                    var id = commentDocument.getString("id")

                    val comment = Comment(userUid, eventId, text, value, id)
                    comments.add(comment)
                }

                onSuccess(comments)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }


    fun saveCommentToFirestore(
        newComment: Comment,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        commentsCollection
            .add(newComment)
            .addOnSuccessListener {
                addPointsToUser(newComment.userUid)
                db.collection("Comments").document(it.id).update("id",it.id)
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun deleteComment(commentId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        commentsCollection
            .whereEqualTo("id", commentId)
            .get()
            .addOnSuccessListener { commentsSnapshot ->
                for (commentDocument in commentsSnapshot.documents) {
                    commentsCollection.document(commentDocument.id)
                        .delete()
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { exception ->
                            onFailure(exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun addPointsToUser(userUid: String) {
        val userReference = db.collection("User").document(userUid)

        userReference.get()
            .addOnSuccessListener { userSnapshot ->
                val currentPoints = userSnapshot.getLong("points") ?: 0

                // Dodajte 10 bodova
                val newPoints = currentPoints + 5

                userReference.update("points", newPoints)
                    .addOnSuccessListener {

                    }
                    .addOnFailureListener { exception ->
                    }
            }
            .addOnFailureListener { exception ->
            }
    }

    fun getAvgGradeForEvent(eventId: String, callback: (Double)->Unit){
        var avgGrade:Double=0.0;

        var grades=db.collection("Comments").whereEqualTo("eventId",eventId).get().addOnSuccessListener { documents ->
            for (document in documents) {
                avgGrade += document.getLong("value")!!.toLong()
            }

            avgGrade /= documents.count()

            callback(avgGrade)
        }
    }

    fun getNoOfPeopleForEvent(eventId: String, callback: (Int)->Unit){


        var grades=db.collection("User").whereArrayContains("events",eventId).get().addOnSuccessListener { documents ->
           callback(documents.count())
        }
    }

}