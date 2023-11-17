package ua.cn.stu.navigation

import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.LifecycleOwner
import kz.kbtu.minigame.fragments.*
import kz.kbtu.minigame.contract.*
import kz.kbtu.minigame.databinding.ActivityMainBinding
import kz.kbtu.minigame.model.Options
import kz.kbtu.minigame.R


class MainActivity : AppCompatActivity(), Navigator {

    private lateinit var binding: ActivityMainBinding

    private val currentFragment: Fragment
        get() = supportFragmentManager.findFragmentById(R.id.fragmentContainer)!!

    private val fragmentListener = object : FragmentManager.FragmentLifecycleCallbacks() {
        override fun onFragmentViewCreated(fm: FragmentManager, f: Fragment, v: View, savedInstanceState: Bundle?) {
            super.onFragmentViewCreated(fm, f, v, savedInstanceState)
            updateUi()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also { setContentView(it.root) }
        setSupportActionBar(binding.toolbar)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, MenuFragment())
                .commit()
        }

        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentListener, false)
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // we've called setSupportActionBar in onCreate,
        // that's why we need to override this method too
        updateUi()
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun showBoxSelectionScreen(options: Options) {
        launchFragment(BoxSelectionFragment.newInstance(options))
    }

    override fun showOptionsScreen(options: Options) {
        launchFragment(OptionsFragment.newInstance(options))
    }

    override fun showCongratulationsScreen() {
        launchFragment(BoxFragment())
    }

    override fun showAboutScreen() {
        launchFragment(AboutFragment())
    }

    override fun goBack() {
        onBackPressed()
    }

    override fun goToMenu() {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    override fun <T : Parcelable> publishResult(result: T) {
        supportFragmentManager.setFragmentResult(result.javaClass.name, bundleOf(KEY_RESULT to result))
    }

    override fun <T : Parcelable> listenResult(clazz: Class<T>, owner: LifecycleOwner, listener: ResultListener<T>) {
        supportFragmentManager.setFragmentResultListener(clazz.name, owner, FragmentResultListener { key, bundle ->
            listener.invoke(bundle.getParcelable(KEY_RESULT)!!)
        })
    }

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in,
                R.anim.fade_out,
                R.anim.fade_in,
                R.anim.slide_out
            )
            .addToBackStack(null)
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun updateUi() {
        val fragment = currentFragment

        if (fragment is HasCustomTitle) {
            binding.toolbar.title = getString(fragment.getTitleRes())
        } else {
            binding.toolbar.title = getString(R.string.fragment_navigation_example)
        }

        if (supportFragmentManager.backStackEntryCount > 0) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        } else {
            supportActionBar?.setDisplayHomeAsUpEnabled(false)
            supportActionBar?.setDisplayShowHomeEnabled(false)
        }

        if (fragment is HasCustomAction) {
            createCustomToolbarAction(fragment.getCustomAction())
        } else {
            binding.toolbar.menu.clear()
        }
    }

    private fun createCustomToolbarAction(action: CustomAction) {
        binding.toolbar.menu.clear() // clearing old action if it exists before assigning a new one

        val iconDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(this, action.iconRes)!!)
        iconDrawable.setTint(Color.WHITE)

        val menuItem = binding.toolbar.menu.add(action.textRes)
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        menuItem.icon = iconDrawable
        menuItem.setOnMenuItemClickListener {
            action.onCustomAction.run()
            return@setOnMenuItemClickListener true
        }
    }

    companion object {
        @JvmStatic private val KEY_RESULT = "RESULT"
    }
}


//package kz.kbtu.minigame
//
//import android.content.Intent
//import android.os.Bundle
//import kz.kbtu.minigame.OptionsActivity.Companion.EXTRA_OPTIONS
//import kz.kbtu.minigame.databinding.ActivityMainBinding
//import kz.kbtu.minigame.model.Options
//import java.lang.IllegalArgumentException
//
//class MainActivity : BaseActivity() {
//
//    private lateinit var binding: ActivityMainBinding
//    private lateinit var options: Options
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        with(binding){
//            openBoxButton.setOnClickListener { openBox() }
//            optionsButton.setOnClickListener { setOptions() }
//            aboutButton.setOnClickListener { aboutGame() }
//            exitButton.setOnClickListener { closeActivity() }
//        }
//
//        options = savedInstanceState?.getParcelable(KEY_OPTIONS) ?: Options.DEFAULT
//    }
//
//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putParcelable(KEY_OPTIONS, options)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == OPTIONS_REQUEST_CODE && resultCode == RESULT_OK){
//            options = data?.getParcelableExtra(OptionsActivity.EXTRA_OPTIONS) ?:
//                    throw IllegalArgumentException("Can't get the updated data from options activity")
//        }
//    }
//
//    private fun openBox(){
//        val intent = Intent(this, OpenBoxActivity::class.java)
//        startActivity(intent)
//    }
//
//    private fun setOptions(){
//        val intent = Intent(this, OptionsActivity::class.java)
//        intent.putExtra(EXTRA_OPTIONS, options)
//        startActivityForResult(intent, OPTIONS_REQUEST_CODE)
//    }
//
//    private fun aboutGame(){
//        val intent  = Intent(this, AboutActivity::class.java)
//        startActivity(intent)
//    }
//
//    private fun closeActivity(){ finish() }
//
//
//    companion object{
//        const val KEY_OPTIONS = "OPTIONS"
//        const val OPTIONS_REQUEST_CODE = 1
//    }
//}