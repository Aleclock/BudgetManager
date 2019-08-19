package com.example.aleclock.budgetmanager

import android.support.v4.app.FragmentPagerAdapter
import android.content.Context;
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager

class ViewPagerAdapter(private val myContext: Context, fm: FragmentManager, internal var totalTabs: Int) : FragmentPagerAdapter(fm) {

    // this is for fragment tabs
    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> { return ExpenseFragment() }
            1 -> { return IncomeFragment() }
            else -> return ExpenseFragment()
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return totalTabs
    }
}
