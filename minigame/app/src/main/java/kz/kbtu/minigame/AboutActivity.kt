package kz.kbtu.minigame

import android.os.Bundle
import kz.kbtu.minigame.databinding.ActivityAboutBinding

class AboutActivity: BaseActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding){
            versionNameTextView.text = versionNameTextView.text.toString() + "  " + VERSION_NAME
            versionCodeTextView.text = versionCodeTextView.text.toString() + "  " + VERSION_CODE.toString()
            okButton.setOnClickListener{ onOkPressed() }
        }
    }
    private fun onOkPressed(){
        finish()
    }

    companion object{
        const val VERSION_NAME = "1.0"
        const val VERSION_CODE = 1
    }
}