package com.example.loutaro.ui.country

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.example.loutaro.R
import com.example.loutaro.data.entity.Country
import com.example.loutaro.databinding.ActivitySelectCountryBinding
import com.example.loutaro.ui.register.RegisterActivity
import androidx.appcompat.widget.SearchView.OnQueryTextListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loutaro.adapter.ListCountryAdapter
import com.example.loutaro.ui.baseActivity.BaseActivity
import java.util.*


class SelectCountryActivity : BaseActivity() {
    private lateinit var binding: ActivitySelectCountryBinding
    private var dataCountry = Country.listName
    private lateinit var adapter: ListCountryAdapter
    companion object {
        const val EXTRA_SELECTED_VALUE = "extra_selected_value"
        const val RESULT_CODE = 110
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectCountryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.run{
            title = getString(R.string.select_country)
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
        val countryHasChoosen = intent.getStringExtra(EXTRA_SELECTED_VALUE)
        binding.rvCountry.layoutManager = LinearLayoutManager(this)
        adapter = ListCountryAdapter(countryHasChoosen as String, binding.rgCountry)
        adapter.onItemClick={
            val resultIntent = Intent()
            resultIntent.putExtra(EXTRA_SELECTED_VALUE,it)
            setResult(RESULT_CODE, resultIntent)
            finish()
        }
        binding.rvCountry.adapter = adapter
        binding.svCountry.setOnQueryTextListener(object :
            OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if(query.toString().trim().isNotEmpty()){
                    val dataFilter = dataCountry.filter {
                        it.contains(query.toString(),true)
                    }
                    generateRadioButtonCountry(dataFilter)
                }else{
                    generateRadioButtonCountry(dataCountry)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText.toString().trim().isNotEmpty()){
                    val dataFilter = dataCountry.filter {
                        it.contains(newText.toString(),true)
                    }
                    generateRadioButtonCountry(dataFilter)
                }else{
                    generateRadioButtonCountry(dataCountry)
                }

                return true
            }

        })
        generateRadioButtonCountry(dataCountry)
    }

    private fun generateRadioButtonCountry(country: List<String>){
        adapter.submitList(country)
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}