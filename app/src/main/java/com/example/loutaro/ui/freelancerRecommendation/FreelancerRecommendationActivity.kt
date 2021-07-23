package com.example.loutaro.ui.freelancerRecommendation

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.loutaro.R
import com.example.loutaro.adapter.ListFreelancerRecommendationAdapter
import com.example.loutaro.databinding.ActivityFreelancerRecommendationBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.utils.TFIDF
import com.example.loutaro.viewmodel.ViewModelFactory

class FreelancerRecommendationActivity : BaseActivity() {
    var skillProjectNeeded= mutableListOf<String>()
    lateinit var binding: ActivityFreelancerRecommendationBinding
    private val listFreelancerRecommendationAdapter= ListFreelancerRecommendationAdapter()
    private val freelancerRecommendationViewModel: FreelancerRecommendationViewModel by viewModels {
        ViewModelFactory.getInstance()
    }
    companion object{
        const val EXTRA_SKILL_PROJECT_NEEDED="EXTRA SKILL PROJECT NEEDED"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFreelancerRecommendationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbarTitle(getString(R.string.freelancer_recommendation))
        activatedToolbarBackButton()

        binding.run {
            rvFreelancerRecommendation.layoutManager = LinearLayoutManager(this@FreelancerRecommendationActivity)
            rvFreelancerRecommendation.adapter = listFreelancerRecommendationAdapter
        }

        val request = intent.getStringArrayExtra(EXTRA_SKILL_PROJECT_NEEDED)
        if(request!=null){
            skillProjectNeeded= request.toMutableList()
        }

        freelancerRecommendationViewModel.responseGetAllFreelancer.observe(this){ listFreelancer->
            val freelancerRecommendation = TFIDF().getFreelancerRecommendation(skillProjectNeeded, listFreelancer)
            Log.d("hasil_rekomendasi","${freelancerRecommendation}")
            val resultFreelancerRecommendation = freelancerRecommendation.subList(0,5)
            listFreelancerRecommendationAdapter.submitList(resultFreelancerRecommendation)
            listFreelancerRecommendationAdapter.notifyDataSetChanged()

        }

        freelancerRecommendationViewModel.getAllFreelancer()

        listFreelancerRecommendationAdapter.onContactClickCallback={ typeContact: String, resource: String ->
            when(typeContact){
                getString(R.string.phone) -> {
                    callPhone(resource)
                }
                getString(R.string.email) -> {
                    contactEmail(resource)
                }
                getString(R.string.telegram) -> {
                    contactByTelegram(resource)
                }
            }
        }

    }

    fun callPhone(numberPhone: String){
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$numberPhone"))
        startActivity(intent)
    }

    fun contactEmail(email: String){
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.type = "text/plain"
        intent.data = Uri.parse("mailto:$email")
        startActivity(Intent.createChooser(intent, "Send Email"))
    }

    fun contactByTelegram(username: String){
        try {
            val telegram = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/$username"))
            telegram.setPackage("org.telegram.messenger")
            startActivity(telegram)
        } catch (e: Exception) {
            showSnackbar("Telegram app is not installed")
        }
    }
}