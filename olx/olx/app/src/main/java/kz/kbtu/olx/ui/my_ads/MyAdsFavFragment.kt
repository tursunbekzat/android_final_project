package kz.kbtu.olx.ui.my_ads


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
import kz.kbtu.olx.adapter.AdapterAd
import kz.kbtu.olx.databinding.FragmentMyAdsFavBinding
import kz.kbtu.olx.models.ModelAd


class MyAdsFavFragment : Fragment() {

    private lateinit var binding: FragmentMyAdsFavBinding
    private lateinit var mContext: Context
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var adArrayList: ArrayList<ModelAd>
    private lateinit var adapterAd: AdapterAd


    override fun onAttach(context: Context) {

        mContext = context
        super.onAttach(context)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentMyAdsFavBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        loadFavAds()

        binding.searchEt.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                try {

                    val query = s.toString()
                    adapterAd.filter.filter(query)
                } catch (e: Exception) {

                    Log.e(TAG, "onTextChanged: ", e)
                }
            }
            override fun afterTextChanged(s: Editable?) {

            }
        })
    }


    private fun loadFavAds() {

        Log.d(TAG, "loadFavAds: ")

        adArrayList = ArrayList()

        val favRef = FirebaseDatabase.getInstance().getReference("Users")
        favRef.child(firebaseAuth.uid!!).child("Favorites")
            .addValueEventListener(object : ValueEventListener {

                override fun onDataChange(snapshot: DataSnapshot) {

                    adArrayList.clear()

                    for (ds in snapshot.children) {

                        val adId = "${ds.child("adId").value}"
                        Log.d("MY_ADS_FAV_FRAGMENT_TAG", "onDataChange: adId: $adId")

                        val adRef = FirebaseDatabase.getInstance().getReference("Ads")
                        adRef.child(adId)
                            .addListenerForSingleValueEvent(object : ValueEventListener {

                                override fun onDataChange(snapshot: DataSnapshot) {

                                    try {

                                        val modelAd = snapshot.getValue(ModelAd::class.java)
                                        adArrayList.add(modelAd!!)
                                        Log.d("MY_ADS_FAV_FRAGMENT_TAG", "adRef: $adapterAd")
                                    } catch (e: Exception) {

                                        Log.e("MY_ADS_FAV_FRAGMENT_TAG", "onDataChange: ", e)
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {

                                    Log.d(TAG, "onCancelled: adArrayList: $adArrayList")

                                }
                            })
                    }

                    Handler().postDelayed({

                        adapterAd = AdapterAd(mContext, adArrayList)
                        binding.adsRv.adapter = adapterAd
                    }, 500)
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }


    private companion object {

        private const val TAG = "MY_ADS_FAV_FRAGMENT_TAG"
    }
}