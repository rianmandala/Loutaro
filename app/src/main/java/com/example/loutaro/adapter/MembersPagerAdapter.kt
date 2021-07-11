package com.example.loutaro.adapter

import android.content.Context
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.loutaro.R
import com.example.loutaro.ui.members.addMember.AddMemberFragment
import com.example.loutaro.ui.members.listMember.ListMemberFragment
import com.example.loutaro.ui.project.activeProject.ActiveProjectFragment
import com.example.loutaro.ui.project.finishProject.FinishProjectFragment

class MembersPagerAdapter(private val idBoards: String,private val idProject: String,private val mContext: Context, fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(R.string.list_members, R.string.add_members)
    }

    override fun getItem(position: Int): Fragment =
        when (position) {
            0 -> ListMemberFragment.newInstance(idBoards, idProject)
            1 -> AddMemberFragment.newInstance(idBoards, idProject)
            else -> Fragment()
        }

    override fun getPageTitle(position: Int): CharSequence? =
        mContext.resources.getString(TAB_TITLES[position])

    override fun getCount(): Int = 2

}