package com.example.loutaro.ui.members

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.loutaro.R
import com.example.loutaro.adapter.MembersPagerAdapter
import com.example.loutaro.adapter.SectionPagerAdapter
import com.example.loutaro.databinding.ActivityMembersBinding
import com.example.loutaro.ui.baseActivity.BaseActivity
import com.example.loutaro.ui.boardKanban.BoardKanbanActivity

class MembersActivity : BaseActivity() {
    private lateinit var binding: ActivityMembersBinding
    companion object{
        var EXTRA_ID_BOARDS="EXTRA ID BOARDS"
        var EXTRA_ID_PROJECT="EXTRA ID PROJECT"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMembersBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setToolbarTitle(getString(R.string.members))
        activatedToolbarBackButton()

        supportActionBar?.elevation = 0F;
        val idBoards = intent.getStringExtra(EXTRA_ID_BOARDS).toString()
        val idProject = intent.getStringExtra(EXTRA_ID_PROJECT).toString()

        binding.viewPagerMembers.adapter = MembersPagerAdapter(idBoards,idProject, this, supportFragmentManager)
        binding.tabLayoutMembers.setupWithViewPager(binding.viewPagerMembers)
    }
}