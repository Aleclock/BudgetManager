package com.example.aleclock.budgetmanager

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.example.aleclock.budgetmanager.activity.LoginActivity
import org.junit.Rule
import org.junit.Test

class TransactionFragmentTest {

    @get:Rule
    val activityTestRule = ActivityTestRule<LoginActivity>(LoginActivity::class.java)

    private fun login() {
        LoginActivity.signOut()
        onView(withId(R.id.et_email)).perform(typeText("piero.bruni@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("Alessandro1"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        //Thread.sleep(2000)
    }

    private fun clickTransactionItem() {
        login()
        onView(withId(R.id.transaction)).perform(click())
    }

    @Test
    fun onItemClickedSholdContainFragmentTransaction() {
        clickTransactionItem()
        LoginActivity.signOut()
    }
}