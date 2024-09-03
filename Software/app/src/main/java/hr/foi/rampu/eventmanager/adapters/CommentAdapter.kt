package hr.foi.rampu.eventmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.foi.rampu.eventmanager.R
import hr.foi.rampu.eventmanager.database.usersDAO
import hr.foi.rampu.eventmanager.entity.Comment

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CommentAdapter(private val comments: MutableList<Comment>, private val onDeleteComment: (String) -> Unit) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.bind(comment)

        holder.itemView.findViewById<Button>(R.id.buttonDeleteComment).setOnClickListener {
            comment.id?.let { id -> onDeleteComment(id) }
        }
    }

    override fun getItemCount(): Int = comments.size

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTextView: TextView = itemView.findViewById(R.id.textViewUsername)
        private val commentTextView: TextView = itemView.findViewById(R.id.textViewComment)
        private val ratingTextView: TextView = itemView.findViewById(R.id.textViewRating)

        fun bind(comment: Comment) {
            CoroutineScope(Dispatchers.Main).launch {
                val userFullName = usersDAO().getFullNameByUid(comment.userUid)
                usernameTextView.text = userFullName
            }
            commentTextView.text = comment.text
            ratingTextView.text = "Ocjena: ${comment.value}"
        }
    }
}