package kz.kbtu.minigame.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kz.kbtu.minigame.R
import kz.kbtu.minigame.contract.HasCustomTitle
import kz.kbtu.minigame.contract.navigator
import kz.kbtu.minigame.databinding.FragmentAboutBinding

class AboutFragment : Fragment(), HasCustomTitle {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = FragmentAboutBinding.inflate(inflater, container, false).apply {
        versionNameTextView.text = VERSION_NAME
        versionCodeTextView.text = VERSION_CODE.toString()
        okButton.setOnClickListener { onOkPressed() }
    }.root

    override fun getTitleRes(): Int = R.string.about

    private fun onOkPressed() {
        navigator().goBack()
    }

    companion object{
        const val VERSION_NAME = "1.0"
        const val VERSION_CODE = 1
    }
}