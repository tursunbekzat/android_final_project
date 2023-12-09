package kz.kbtu.intent_and_intentfilters

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kz.kbtu.intent_and_intentfilters.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startAboutActivityBtn.setOnClickListener{

            Log.d(TAG, "onCreate: startAboutActivityBtn")

            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }
    }


    private companion object{

        private const val TAG = "MAIN_ACTIVITY_TAG"
    }
}