package kz.kbtu.olx.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kz.kbtu.olx.adapter.ChatsAdapter
import kz.kbtu.olx.databinding.FragmentChatsBinding
import kz.kbtu.olx.models.Chats

class ChatsFragment : Fragment() {

    private lateinit var binding: FragmentChatsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var chatsArrayList: ArrayList<Chats>
    private lateinit var chatsAdapter: ChatsAdapter
    private lateinit var context: Context

    private var myUid = ""


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentChatsBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        myUid = "${firebaseAuth.uid}"

        loadChats()

        binding.searchEt.addTextChangedListener (object :TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}


            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {


                try {

                    val query = s.toString()

                    chatsAdapter.filter.filter(query)
                } catch (e: Exception) {

                    Log.e(TAG, "onTextChanged: ", e)
                }
            }


            override fun afterTextChanged(s: Editable?) {}
        })

    }


    private fun loadChats(){

        chatsArrayList = ArrayList()

        val ref = FirebaseDatabase.getInstance().getReference("Chats")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                chatsArrayList.clear()

                for (ds in snapshot.children){

                    val chatKey = "${ds.key}"

                    if (chatKey.contains(myUid)){

                        val modelChats = Chats()
                        modelChats.chatKey = chatKey

                        chatsArrayList.add(modelChats)
                    }
                }

                chatsAdapter = ChatsAdapter(firebaseAuth, context, chatsArrayList)
                binding.chatsRl.adapter = chatsAdapter

                sort()
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }


    private fun sort(){

        Handler().postDelayed({

            chatsArrayList.sortWith{ model1: Chats, model2: Chats ->

                model2.timestamp.compareTo(model1.timestamp)
            }

            chatsAdapter.notifyDataSetChanged()
        }, 1000)
    }


    private companion object{

        private const val TAG = "CHATS_FRAGMENT_TAG"
    }
}
