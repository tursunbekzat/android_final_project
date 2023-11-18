package kz.kbtu.olx.UI

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kz.kbtu.olx.R
import kz.kbtu.olx.databinding.ActivityLoginOptionsBinding

class LoginOptionsActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginOptionsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.closeBtn.setOnClickListener{
            onBackPressed()
        }

    }
}