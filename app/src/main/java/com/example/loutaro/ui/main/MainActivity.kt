package com.example.loutaro.ui.main

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.loutaro.R
import com.example.loutaro.databinding.ActivityMainBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.ui.home.HomeFragment
import com.example.loutaro.ui.profile.ProfileFragment
import com.example.loutaro.ui.project.ProjectFragment
import com.example.loutaro.ui.saved.SavedFragment
import com.example.loutaro.ui.search.SearchFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private val fragment1 = HomeFragment()
    private val fragment2 = ProjectFragment()
    private val fragment3 = SearchFragment()
    private val fragment4 = SavedFragment()
    private val fragment5 = ProfileFragment()
    private var active: Any = fragment1
    private val fm = supportFragmentManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        fm.beginTransaction().add(R.id.nav_host_fragment, fragment5, "5").hide(fragment5).commit()
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment4, "4").hide(fragment4).commit()
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment3, "3").hide(fragment3).commit()
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment2, "2").hide(fragment2).commit()
        fm.beginTransaction().add(R.id.nav_host_fragment, fragment1, "1").commit()

        supportActionBar?.elevation = 0f
        supportActionBar?.title = "Loutaro"
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_tab -> {
                    supportActionBar?.title = "Loutaro"
                    fm.beginTransaction().hide(active as Fragment).show(fragment1).commit()
                    active = fragment1
                    return@OnNavigationItemSelectedListener true
                }
                R.id.project_tab -> {
                    supportActionBar?.title = getString(R.string.project_tab)
                    fm.beginTransaction().hide(active as Fragment).show(fragment2).commit()
                    active = fragment2
                    return@OnNavigationItemSelectedListener true
                }
                R.id.search_tab -> {
                    supportActionBar?.title = getString(R.string.search_tab)
                    fm.beginTransaction().hide(active as Fragment).show(fragment3).commit()
                    active = fragment3
                    return@OnNavigationItemSelectedListener true
                }
                R.id.saved_tab -> {
                    supportActionBar?.title = getString(R.string.favorite_tab)
                    fm.beginTransaction().hide(active as Fragment).show(fragment4).commit()
                    active = fragment4
                    return@OnNavigationItemSelectedListener true
                }
                R.id.profile_tab -> {
                    supportActionBar?.title = getString(R.string.profile_tab)
                    fm.beginTransaction().hide(active as Fragment).show(fragment5).commit()
                    active = fragment5
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
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