package com.example.aleclock.budgetmanager

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.example.aleclock.budgetmanager.activity.LoginActivity
import org.junit.*

class MainActivityTest {

    @get:Rule
    val activityTestRule = ActivityTestRule<LoginActivity>(LoginActivity::class.java)

    @After
    fun doLogout() {
        LoginActivity.signOut()
    }

    @Before
    fun doLogin() {
        login()
    }

    private fun login() {
        onView(withId(R.id.et_email)).perform(typeText("piero.bruni@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("Alessandro1"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(4000)
    }

    @Test
    fun shouldContainNavBar() {
        onView(withId(R.id.nav_bar)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainItemTransaction() {
        onView(withId(R.id.transaction)).check(matches(isDisplayed()))
    }
    @Test
    fun shouldContainItemGraph() {
        onView(withId(R.id.graph)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainItemAccount() {
        onView(withId(R.id.account)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainItemSettings() {
        onView(withId(R.id.settings)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainFrameLayout() {
        onView(withId(R.id.frame_layout)).check(matches(isDisplayed()))
    }

    @Test
    fun onItemClickedShouldContainFragmentTransaction() {
        onView(withId(R.id.transaction)).perform(click())
        onView(withId(R.id.fragment_transaction)).check(matches(isDisplayed()))
    }

    @Test
    fun onItemClickedShouldContainFragmentCharts() {
        onView(withId(R.id.graph)).perform(click())
        onView(withId(R.id.fragment_chart)).check(matches(isDisplayed()))
    }

    @Test
    fun onItemClickedShouldContainFragmentAccount() {
        onView(withId(R.id.account)).perform(click())
        onView(withId(R.id.fragment_account_constraint)).check(matches(isDisplayed()))
    }

    @Test
    fun onItemClickedShouldContainFragmentSettings() {
        onView(withId(R.id.settings)).perform(click())
        onView(withId(R.id.fragment_settings)).check(matches(isDisplayed()))
    }



}