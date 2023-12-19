package kz.kbtu.olx.presentation.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import kz.kbtu.olx.databinding.FragmentMyAdsBinding
import kz.kbtu.olx.presentation.activities.ui.my_ads.MyAdsFragment
import kz.kbtu.olx.presentation.activities.ui.my_ads.FavAdsFragment

class MyAdsFragment : Fragment() {

    private lateinit var binding: FragmentMyAdsBinding
    private lateinit var mContext: Context
    private lateinit var myTabsViewPagerAdapter: MyTabsViewPagerAdapter

    override fun onAttach(context: Context) {

        mContext = context
        super.onAttach(context)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentMyAdsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Ads"))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Favorites"))

        val fragmentManager = childFragmentManager
        myTabsViewPagerAdapter = MyTabsViewPagerAdapter(fragmentManager, lifecycle)
        binding.viewPager.adapter = myTabsViewPagerAdapter

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {

                binding.viewPager.currentItem = tab.position
            }
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }
        })

        binding.viewPager.registerOnPageChangeCallback(object : OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {

                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position))
            }
        })
    }

    class MyTabsViewPagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fragmentManager, lifecycle) {

        override fun createFragment(position: Int): Fragment {

            if (position == 0) return MyAdsFragment()
            else return FavAdsFragment()
        }

        override fun getItemCount(): Int {

            return 2
        }
    }
}