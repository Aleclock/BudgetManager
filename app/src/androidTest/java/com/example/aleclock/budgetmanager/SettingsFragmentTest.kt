package com.example.aleclock.budgetmanager

import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.example.aleclock.budgetmanager.activity.LoginActivity
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.intent.Intents.intended
import android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent
import android.support.v7.widget.RecyclerView
import com.example.aleclock.budgetmanager.activity.ExpenseCategoryActivity
import org.hamcrest.CoreMatchers.anything
import org.junit.*

class SettingsFragmentTest {

    @get:Rule
    val activityTestRule = ActivityTestRule<LoginActivity>(LoginActivity::class.java)

    private fun login() {
        onView(withId(R.id.et_email)).perform(typeText("piero.bruni@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("Alessandro1"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(2000)
    }

    private fun clickChartItem() {
        onView(withId(R.id.settings)).perform(click())
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
    fun shouldContainSettingsTitle() {
        onView(withId(R.id.settings_title)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainSettingsListView() {
        onView(withId(R.id.setting_listview)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainBtnLogout() {
        onView(withId(R.id.btn_logout)).check(matches(isDisplayed()))
    }

    @Test
    fun onClickExpenseItem() {
        onData(anything()).inAdapterView(withId(R.id.setting_listview)).atPosition(0).perform(click())
        Thread.sleep(500)
        onView(withText(R.string.category_expense)).check(matches(isDisplayed()))
    }

    @Test
    fun onClickIncomeItem() {
        onData(anything()).inAdapterView(withId(R.id.setting_listview)).atPosition(1).perform(click())
        Thread.sleep(500)
        onView(withText(R.string.category_income)).check(matches(isDisplayed()))
    }

    @Test
    fun onClickModelsItem() {
        onData(anything()).inAdapterView(withId(R.id.setting_listview)).atPosition(1).perform(click())
        Thread.sleep(500)
        //onView(withText(R.string.models)).check(matches(isDisplayed()))
    }
}