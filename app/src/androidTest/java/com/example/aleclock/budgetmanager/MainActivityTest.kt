package com.example.aleclock.budgetmanager

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.example.aleclock.budgetmanager.activity.LoginActivity
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    // TODO invece di fare il login ogni volta farlo solo nella prima regola, per semplicità

    @get:Rule
    val activityTestRule = ActivityTestRule<LoginActivity>(LoginActivity::class.java)

    private fun login() {
        onView(withId(R.id.et_email)).perform(typeText("piero.bruni@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("Alessandro1"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(1000)
    }

    @Test
    fun loginAndSignOut() {
        login()
        LoginActivity.signOut()
    }

    @Test
    fun shouldContainNavBar() {
        login()
        onView(withId(R.id.nav_bar)).check(matches(isDisplayed()))
        LoginActivity.signOut()
    }

    @Test
    fun sholdContainItemTransaction() {
        login()
        onView(withId(R.id.transaction)).check(matches(isDisplayed()))
        LoginActivity.signOut()
    }

    @Test
    fun sholdContainItemGraph() {
        login()
        onView(withId(R.id.graph)).check(matches(isDisplayed()))
        LoginActivity.signOut()
    }

    @Test
    fun sholdContainItemAccount() {
        login()
        onView(withId(R.id.account)).check(matches(isDisplayed()))
        LoginActivity.signOut()
    }

    @Test
    fun sholdContainItemSettings() {
        login()
        onView(withId(R.id.settings)).check(matches(isDisplayed()))
        LoginActivity.signOut()
    }

    @Test
    fun shouldContainFrameLayout() {
        login()
        onView(withId(R.id.frame_layout)).check(matches(isDisplayed()))
        LoginActivity.signOut()
    }

    @Test
    fun onItemClickedSholdContainFragmentTransaction() {
        login()
        onView(withId(R.id.transaction)).perform(click())
        onView(withId(R.id.fragment_transaction)).check(matches(isDisplayed()))
        LoginActivity.signOut()
    }

    @Test
    fun onItemClickedSholdContainFragmentCharts() {
        login()
        onView(withId(R.id.graph)).perform(click())
        onView(withId(R.id.fragment_chart)).check(matches(isDisplayed()))
        LoginActivity.signOut()
    }

    @Test
    fun onItemClickedSholdContainFragmentAccount() {
        login()
        onView(withId(R.id.account)).perform(click())
        onView(withId(R.id.fragment_account_constraint)).check(matches(isDisplayed()))
        LoginActivity.signOut()
    }

    @Test
    fun onItemClickedSholdContainFragmentSettings() {
        login()
        onView(withId(R.id.settings)).perform(click())
        onView(withId(R.id.fragment_settings)).check(matches(isDisplayed()))
        LoginActivity.signOut()
    }

}