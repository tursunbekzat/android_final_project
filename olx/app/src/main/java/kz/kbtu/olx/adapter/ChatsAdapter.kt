package kz.kbtu.olx.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kz.kbtu.olx.R
import kz.kbtu.olx.Utils
import kz.kbtu.olx.databinding.RowChatsBinding
import kz.kbtu.olx.models.Chats
import kz.kbtu.olx.ui.chats.FilterChats
import kz.kbtu.olx.ui.chats.ChatActivity

class ChatsAdapter : Adapter<ChatsAdapter.ChatsViewHolder>, Filterable{

    private lateinit var binding: RowChatsBinding

    private var firebaseAuth: FirebaseAuth
    private var context: Context
    private var filterList: ArrayList<Chats>
    private var filter: FilterChats? = null
    private var myUid = ""
    var chatsArrayList: ArrayList<Chats>


    constructor(firebaseAuth: FirebaseAuth, context: Context, chatsArrayList: ArrayList<Chats>) {

        this.firebaseAuth = firebaseAuth
        this.context = context
        this.chatsArrayList = chatsArrayList
        this.filterList = chatsArrayList

        myUid = "${firebaseAuth.uid}"
    }


    inner class ChatsViewHolder(itemView: View) : ViewHolder(itemView) {

        var profileIv = binding.profileIv
        var lastMessageTv = binding.lastMessageTv
        var nameTv = binding.nameTv
        var dateTimeTv = binding.dateTimeTv
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsViewHolder {

        binding = RowChatsBinding.inflate(LayoutInflater.from(context), parent, false)

        return ChatsViewHolder(binding.root)
    }


    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {

        val modelChats = chatsArrayList[position]

        loadLastMessage(modelChats, holder)

        holder.itemView.setOnClickListener {

            val receiptUid = modelChats.receiptUid

            if (receiptUid != null) {
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("sellerUid", receiptUid)
                context.startActivity(intent)
            }
        }
    }

    private fun loadLastMessage(modelChats: Chats, holder: ChatsAdapter.ChatsViewHolder) {

        val chatKey = modelChats.chatKey

        val ref = FirebaseDatabase.getInstance().getReference("Chats")
        ref.child(chatKey).limitToLast(1)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (ds in snapshot.children){

                        val fromUid = "${ds.child("fromUid").value}"
                        val toUid = "${ds.child("toUid").value}"
                        val message = "${ds.child("message").value}"
                        val messageId = "${ds.child("messageId").value}"
                        val messageType = "${ds.child("messageType").value}"
                        val timestamp = ds.child("timestamp").value as Long ?: 0
                        val formattedDate = Utils.formatTimestampDateTime(timestamp)

                        modelChats.message = message
                        modelChats.messageId = messageId
                        modelChats.messageType = messageType
                        modelChats.timestamp = timestamp
                        modelChats.fromUid = fromUid
                        modelChats.toUid = toUid

                        holder.dateTimeTv.text = "$formattedDate"

                        if (messageType == Utils.MESSAGE_TYPE_TEXT){

                            holder.lastMessageTv.text = message
                        } else {

                            holder.lastMessageTv.text = "Sends Attachment"
                        }
                    }

                    loadRecepitUserInfo(modelChats, holder)
                }
                override fun onCancelled(error: DatabaseError) {}
            })

    }

    private fun loadRecepitUserInfo(modelChats: Chats, holder: ChatsAdapter.ChatsViewHolder) {

        val fromUid = modelChats.fromUid
        val toUid = modelChats.toUid
        var receiptUid = ""

        if (fromUid == myUid){

            receiptUid = toUid
        } else {

            receiptUid = fromUid
        }

        modelChats.receiptUid = receiptUid

        val ref = FirebaseDatabase.getInstance().getReference("Users")
        ref.child(receiptUid)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    val name = "${snapshot.child("name").value}"
                    val profileImageUrl = "${snapshot.child("profileImageUrl").value}"

                    modelChats.name = name
                    modelChats.profileImageUrl = profileImageUrl

                    holder.nameTv.text = name

                    try {

                        Glide.with(context)
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_person_white)
                            .into(binding.profileIv)
                    } catch (e: Exception) {

                        Log.e("ADAPTER_CHATS_TAG", "onDataChange: ", e)
                    }
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }


    override fun getItemCount(): Int {

        return chatsArrayList.size
    }


    override fun getFilter(): Filter {

        if (filter == null) filter = FilterChats(this, filterList)

        return filter!!
    }
}