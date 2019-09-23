package com.example.aleclock.budgetmanager


import com.example.aleclock.budgetmanager.activity.LoginActivity
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test

class LoginActivityTest {

    @get:Rule
    val mActivityTestRule = ActivityTestRule<LoginActivity>(LoginActivity::class.java)

    @Test
    fun shouldContainUsernameInput() {
        onView(withId(R.id.et_email)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainPasswordInput() {
        onView(withId(R.id.et_password)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainLoginButton() {
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainCreateAccountButton() {
        onView(withId(R.id.btn_register_account)).check(matches(isDisplayed()))
    }

    // TODO controlli sui valori inseriti

    @Test
    fun successfulLoginShouldOpenMainScreen() {
        onView(withId(R.id.et_email)).perform(typeText("clock.alessandro@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("Alessandro1"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
    }

}