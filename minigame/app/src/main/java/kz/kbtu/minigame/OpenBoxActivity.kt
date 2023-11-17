package kz.kbtu.minigame

import android.content.Intent
import android.os.Bundle
import kz.kbtu.minigame.databinding.ActivityBoxBinding

class OpenBoxActivity: BaseActivity() {
    lateinit var binding: ActivityBoxBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBoxBinding.inflate(layoutInflater).also { setContentView(binding.root) }

        binding.toMainMenuButton.setOnClickListener { onToMainMenuPressed() }
    }

    private fun onToMainMenuPressed(){
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }
}