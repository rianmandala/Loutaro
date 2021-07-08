package com.example.loutaro.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.example.loutaro.ui.main.MainActivity
import com.example.loutaro.R
import com.example.loutaro.adapter.OnBoardingViewPagerAdapter
import com.example.loutaro.data.entity.OnBoarding
import com.example.loutaro.databinding.ActivityOnboardingBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.ui.login.LoginActivity
import com.example.loutaro.ui.register.RegisterActivity
import java.util.*

class OnboardingActivity : BaseActivity() {
    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var listDataOnboarding: List<OnBoarding>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listDataOnboarding= listOf(
                OnBoarding(
                        R.drawable.ic_undraw_stand_out, resources.getString(R.string.never_miss_an_opportunity),
                        resources.getString(R.string.find_any_job)),
                OnBoarding(
                        R.drawable.ic_undraw_creative_team,
                        resources.getString(R.string.find_interesting_project),
                        resources.getString(R.string.stand_out_by_replying)),
                OnBoarding(
                        R.drawable.ic_undraw_resume,
                        resources.getString(R.string.global_talent_for_hire),
                        resources.getString(R.string.find_professional_freelancer)),
                OnBoarding(
                        R.drawable.ic_undraw_certification,
                        resources.getString(R.string.post_project_and_get_proposal),
                        resources.getString(R.string.interview_your_favorite_freelancer))
        )
        initTinyDB()
        checkUserLogin()
        setToolbarTitle(getString(R.string.welcome))
        showLoginAndRegisterBtn(false)

        with(binding){
            vpOnboarding.adapter= OnBoardingViewPagerAdapter(listDataOnboarding)
            ViewPager2.ORIENTATION_HORIZONTAL
            vpOnboarding.adapter?.registerAdapterDataObserver(binding.indicator.adapterDataObserver)
            indicator.setViewPager(binding.vpOnboarding)

            btnLogin.setOnClickListener {
                navigateForward(LoginActivity::class.java)
            }

            btnSignUpNow.setOnClickListener {
                navigateForward(RegisterActivity::class.java)
            }

            vpOnboarding.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    btnNextOnboarding.setOnClickListener {
                        if(position==listDataOnboarding.size-2){
                            showLoginAndRegisterBtn(true)
                        }
                        vpOnboarding.run{
                            setCurrentItem(currentItem+1,false)
                        }
                    }
                    btnSkipOnboarding.setOnClickListener {
                        vpOnboarding.setCurrentItem(listDataOnboarding.size-1,false)
                        showLoginAndRegisterBtn(true)
                    }
                    if(position==listDataOnboarding.size-1){
                        showLoginAndRegisterBtn(true)
                    }
                }
            })
        }
    }

    private fun checkUserLogin() {
        Log.d("status_login","${getStatusLoginFromDB()}")
        if(getStatusLoginFromDB()){
            val destinationIntent = Intent(this, MainActivity::class.java)
            destinationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(destinationIntent)
            finish()
        }
    }

    private fun showLoginAndRegisterBtn(state: Boolean) {
        binding.run {
            vpOnboarding.visibility = View.GONE
            vpOnboarding.visibility = View.VISIBLE
            if(state){
                setViewToInvisible(btnNextOnboarding, btnSkipOnboarding)
                setViewToVisible(btnLogin, btnSignUpNow)
            }else{
                setViewToVisible(btnNextOnboarding, btnSkipOnboarding)
                setViewToInvisible(btnLogin, btnSignUpNow)
            }
        }
    }

    override fun getBaseContext(): Context {
        initTinyDB(super.getBaseContext())
        return applyLanguage(super.getBaseContext(), getLanguageCode()?:"en")
    }

    override fun getApplicationContext(): Context {
        val context = super.getApplicationContext()
        initTinyDB(context)
        return applyLanguage(context, getLanguageCode()?:"en")
    }

    override fun attachBaseContext(newBase: Context) {
        initTinyDB(newBase)
        super.attachBaseContext(applyLanguage(newBase, getLanguageCode(newBase)?:"en"))
    }

    private fun applyLanguage(context: Context, language: String): Context {
        val locale = Locale(language)
        val configuration = context.resources.configuration
        val displayMetrics = context.resources.displayMetrics

        Locale.setDefault(locale)

        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            configuration.setLocale(locale)
            context.createConfigurationContext(configuration)
        }else{
            configuration.locale = locale
            context.resources.updateConfiguration(configuration, displayMetrics)
            context
        }
    }
}