package com.example.aleclock.budgetmanager

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.example.aleclock.budgetmanager.activity.LoginActivity
import com.example.aleclock.budgetmanager.fragment.GraphFragment
import org.junit.*

class ChartFragmentTest {

    @get:Rule
    val activityTestRule = ActivityTestRule<LoginActivity>(LoginActivity::class.java)

    private fun login() {
        onView(withId(R.id.et_email)).perform(typeText("piero.bruni@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("Alessandro1"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(4000)
    }

    private fun clickChartItem() {
        onView(withId(R.id.graph)).perform(click())
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
    fun shouldContainChartTitle() {
        onView(withId(R.id.chartTitle)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainChartPeriod() {
        onView(withId(R.id.chartPeriod)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainChartDateBtn() {
        onView(withId(R.id.btn_chart_date)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainFitlerBtn() {
        onView(withId(R.id.btn_filter)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainTabLayout() {
        onView(withId(R.id.tab_layout_type)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainPieChart() {
        onView(withId(R.id.pieChart)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainNegativePositiveChart() {
        onView(withId(R.id.negative_positive_chart)).check(matches(isDisplayed()))
    }

    @Test
    fun onClickSelectPeriod() {
        onView(withId(R.id.btn_filter)).perform(click())
        onView(withText(R.string.select_period)).check(matches(isDisplayed()))
    }

    @Test
    fun getCorrectMonth() {
        val graphFragment = GraphFragment()
        val date = "20190922"
        val expectedDate = 201909

        Assert.assertEquals(expectedDate, graphFragment.getMonth(date))
    }

    @Test
    fun setCorrectYear() {
        val graphFragment = GraphFragment()
        val date = "20200811"
        val expectedDate = "2020"

        Assert.assertEquals(expectedDate, graphFragment.getYear(date))
    }

}