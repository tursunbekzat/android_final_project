package kz.kbtu.minigame

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kz.kbtu.minigame.databinding.ActivityOptionsBinding
import kz.kbtu.minigame.model.Options
import java.lang.IllegalArgumentException
import java.security.AllPermission

class OptionsActivity:BaseActivity() {

    private lateinit var binding: ActivityOptionsBinding
    private lateinit var options: Options
    private lateinit var boxCountItems: List<BoxCountItem>
    private lateinit var adapter: ArrayAdapter<BoxCountItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        options = savedInstanceState?.getParcelable<Options>(KEY_OPTIONS) ?:
            intent.getParcelableExtra(EXTRA_OPTIONS) ?:
                throw IllegalArgumentException("You need to specify EXTRA_OPTIONS argument to launch this activivty")

        setUpSpinner()
        setUpCheckBox()
        updateUI()

        binding.cancelButton.setOnClickListener{ exitActivity() }
        binding.confirmButton.setOnClickListener{ setOptions() }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(KEY_OPTIONS, options)
    }

    private fun setUpSpinner(){
        boxCountItems = (1..6).map { BoxCountItem(it, "{it} boxes") }
        adapter = ArrayAdapter(
            this,
            R.layout.item_spinner,
            boxCountItems
        )
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_1)

        binding.boxCountSpinner.adapter = adapter
        binding.boxCountSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                val count = boxCountItems[position].count
                options = options.copy(boxCount = count)
            }
        }
    }

    private fun setUpCheckBox(){
        binding.enableTimerCheckBox.setOnClickListener{
            options = options.copy(isTimerEnabled = binding.enableTimerCheckBox.isChecked)
        }
    }

    private fun updateUI(){
        binding.enableTimerCheckBox.isChecked = options.isTimerEnabled
        val currentIndex = boxCountItems.indexOfFirst { it.count == options.boxCount }
        binding.boxCountSpinner.setSelection(currentIndex)
    }

    private fun exitActivity(){
        finish()
    }

    private fun setOptions(){
        val intent = Intent()
        intent.putExtra(EXTRA_OPTIONS, options)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object{
        const val EXTRA_OPTIONS = "EXTRA_OPTIONS"
        const val KEY_OPTIONS = "KEY_OPTIONS"
    }

    class BoxCountItem(val count: Int, private val optionTitle: String){ override fun toString(): String { return optionTitle } }
}