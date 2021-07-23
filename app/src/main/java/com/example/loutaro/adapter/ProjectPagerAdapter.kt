package com.example.loutaro.adapter

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.loutaro.R
import com.example.loutaro.ui.project.activeProject.ActiveProjectFragment
import com.example.loutaro.ui.project.finishProject.FinishProjectFragment
import com.example.loutaro.ui.project.pendingProject.PendingProjectFragment

class ProjectPagerAdapter(private val mContext: Context, fm: FragmentManager, val isBusinessMan: Boolean) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        @StringRes
        private val TAB_TITLES_FREELANCER = intArrayOf(R.string.active_tab_title, R.string.finish_tab_title)
        private val TAB_TITLES_BUSINESSMAN = intArrayOf(R.string.pending_tab_title, R.string.active_tab_title, R.string.finish_tab_title)
    }

    override fun getItem(position: Int): Fragment {
        return if(isBusinessMan){
            when (position) {
                0 -> PendingProjectFragment()
                1 -> ActiveProjectFragment()
                2 -> FinishProjectFragment()
                else -> Fragment()
            }
        }else{
            when (position) {
                0 -> ActiveProjectFragment()
                1 -> FinishProjectFragment()
                else -> Fragment()
            }
        }

    }


    override fun getPageTitle(position: Int): CharSequence? {
        return if(isBusinessMan) mContext.resources.getString(TAB_TITLES_BUSINESSMAN[position])
        else mContext.resources.getString(TAB_TITLES_FREELANCER[position])
    }

    override fun getCount(): Int {
        return if(isBusinessMan) 3
        else 2
    }

}