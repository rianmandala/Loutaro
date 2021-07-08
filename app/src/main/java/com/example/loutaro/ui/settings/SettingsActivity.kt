package com.example.loutaro.ui.settings

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import com.example.loutaro.R
import com.example.loutaro.databinding.ActivitySettingsBinding
import com.example.loutaro.databinding.BottomSheetChangeLanguageBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.ui.main.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*

class SettingsActivity : BaseActivity() {
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbarTitle(getString(R.string.setting))
        activatedToolbarBackButton()

        initTinyDB(this@SettingsActivity)
        binding.run{
            parentLayoutSettingLogout.setOnClickListener {
                logOut()
            }

            parentLayoutSettingChangeLanguage.setOnClickListener {
                // on below line we are creating a new bottom sheet dialog.
                val dialog = BottomSheetDialog(this@SettingsActivity)

                // on below line we are inflating a layout file which we have created.
                val view = BottomSheetChangeLanguageBinding.inflate(layoutInflater)

                // on below line we are adding on click listener
                // for our dismissing the dialog button.
                view.btnSaveChangeLanguage.setOnClickListener {
                    when(view.rgChangeLanguage.checkedRadioButtonId){
                        R.id.rb_indonesia->{
                            setLanguageCode("in")
                            reloadActivity()
                        }
                        R.id.rb_english->{
                            setLanguageCode("en")
                            reloadActivity()
                        }
                    }
                    dialog.dismiss()
                }

                val languageCode = getLanguageCode()?:"en"
                Log.d("hasil_getLanguage", languageCode)
                if(languageCode=="en") view.rbEnglish.isChecked= true
                else if(languageCode=="in") view.rbIndonesia.isChecked=true

                // on below line we are setting
                // content view to our view.
                dialog.setContentView(view.root)

                // on below line we are calling
                // a show method to display a dialog.
                dialog.show()

//                val locale = Locale("in")
//                Locale.setDefault(locale)
//                val resources: Resources = this@SettingsActivity.getResources()
//                val config: Configuration = resources.getConfiguration()
//                config.setLocale(locale)
//                resources.updateConfiguration(config, resources.getDisplayMetrics())
            }

            parentLayoutSettingAbout.setOnClickListener {
                showSnackbar("Coming soon")
            }

        }
    }

    private fun reloadActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
        }
        startActivity(intent)
        finish()
    }
}