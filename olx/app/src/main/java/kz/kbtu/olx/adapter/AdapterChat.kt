package kz.kbtu.olx.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import kz.kbtu.olx.R
import kz.kbtu.olx.Utils
import kz.kbtu.olx.models.ModelChat

class AdapterChat : RecyclerView.Adapter<AdapterChat.ChatViewHolder>{

    private val context: Context
    private val chatArrayList: ArrayList<ModelChat>
    private val firebaseAuth: FirebaseAuth


    constructor(context: Context, chatArrayList: ArrayList<ModelChat>){

        this.context = context
        this.chatArrayList = chatArrayList

        firebaseAuth = FirebaseAuth.getInstance()
    }

    inner class ChatViewHolder(itemView: View) : ViewHolder(itemView) {

        var messageTv: TextView = itemView.findViewById(R.id.messageTv)
        var timeTv: TextView = itemView.findViewById(R.id.timeTv)
        var messageIv: ShapeableImageView = itemView.findViewById(R.id.messageIv)


    }


    companion object {

        private const val TAG = "ADAPTER_CHAT_TAG"
        private const val MESSAGE_TYPE_LEFT = 0
        private const val MESSAGE_TYPE_RIGHT = 1
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {

        if (viewType == MESSAGE_TYPE_RIGHT) {

            val view = LayoutInflater.from(context).inflate(R.layout.row_chat_right, parent, false)

            return ChatViewHolder(view)
        } else {

            val view = LayoutInflater.from(context).inflate(R.layout.row_chat_left, parent, false)
            return ChatViewHolder(view)
        }
    }


    override fun getItemCount(): Int {

        return chatArrayList.size
    }


    override fun getItemViewType(position: Int): Int {

        return super.getItemViewType(position)

        if (chatArrayList[position].fromUid == firebaseAuth.uid){

            return MESSAGE_TYPE_LEFT
        } else {

            return MESSAGE_TYPE_RIGHT
        }
    }


    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {

        val modelChat = chatArrayList[position]

        val message = modelChat.message
        val messageId = modelChat.messageId
        val messageType = modelChat.messageType
        val timestamp = modelChat.timestamp
        val formattedDate = Utils.formatTimestampDateTime(timestamp)

        holder.timeTv.text = formattedDate

        if (messageType == Utils.MESSAGE_TYPE_TEXT){

            holder.messageTv.visibility = View.VISIBLE
            holder.messageIv.visibility = View.GONE

            holder.messageTv.text = message
        } else {

            holder.messageTv.visibility = View.GONE
            holder.messageIv.visibility = View.VISIBLE

            try {

                Glide.with(context)
                    .load(message)
                    .placeholder(R.drawable.ic_image_gray)
                    .error(R.drawable.ic_broken_image_white)
                    .into(holder.messageIv)
            } catch (e: Exception) {

                Log.e(TAG, "onBindViewHolder: ", e)

            }
        }
    }
}