package com.example.aleclock.budgetmanager

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.example.aleclock.budgetmanager.activity.LoginActivity
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.v7.widget.RecyclerView
import org.junit.*

class AccountFragmentTest {

    @get:Rule
    val activityTestRule = ActivityTestRule<LoginActivity>(LoginActivity::class.java)

    private fun login() {
        onView(withId(R.id.et_email)).perform(typeText("piero.bruni@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("Alessandro1"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(4000)
    }

    private fun clickChartItem() {
        onView(withId(R.id.account)).perform(click())
    }

    @After
    fun doLogout() {
        LoginActivity.signOut()
    }

    @Before
    fun doLogin() {
        login()
        clickChartItem()
    }

    @Test
    fun shouldContainShowDialogBtn() {
        onView(withId(R.id.btn_show_dialog)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainRecyclerView() {
        Thread.sleep(500)
        onView(withId(R.id.recycler_view_account)).check(matches(isDisplayed()))
    }

    @Test
    fun onClickAddAccount() {
        onView(withId(R.id.btn_show_dialog)).perform(click())
        onView(withText(R.string.addAccount)).check(matches(isDisplayed()))
    }

    @Test
    fun onClickCreateAccountEmptyField() {
        onView(withId(R.id.btn_show_dialog)).perform(click())
        onView(withText(R.string.addAccount)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_create_account)).perform(click())
        onView(withText(R.string.addAccount)).check(matches(isDisplayed()))
    }

    @Test
    fun longClickItem() {
        Thread.sleep(500)
        onView(withId(R.id.recycler_view_account)).perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, longClick()))
        onView(withId(R.id.btn_back_selected)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_remove_account)).check(matches(isDisplayed()))
    }
}