package com.example.aleclock.budgetmanager


import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.example.aleclock.budgetmanager.activity.LoginActivity
import org.junit.Rule
import org.junit.Test


class LoginActivityTest {

    @get:Rule
    val activityTestRule = ActivityTestRule<LoginActivity>(LoginActivity::class.java)

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

    @Test
    fun emailPasswordEmpty() {
        onView(withId(R.id.et_email)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        onView(withText(R.string.error_fill_fields)).check(matches(isDisplayed()))
    }

    @Test
    fun emailFillPasswordEmpty() {
        onView(withId(R.id.et_email)).perform(typeText("piero.bruni@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        onView(withText(R.string.error_fill_fields)).check(matches(isDisplayed()))
    }

    @Test
    fun emailEmptyPasswordFill() {
        onView(withId(R.id.et_email)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("Alessandro1"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        onView(withText(R.string.error_fill_fields)).check(matches(isDisplayed()))
        onView(withId(R.id.login_title)).check(matches(isDisplayed()))
    }

    @Test
    fun emailPasswordWrong() {
        onView(withId(R.id.et_email)).perform(typeText("aaaa"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("aaa"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        onView(withId(R.id.login_title)).check(matches(isDisplayed()))
    }

    @Test
    fun emailCorrectPasswordWrong() {
        onView(withId(R.id.et_email)).perform(typeText("piero.bruni@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("aaa"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        onView(withId(R.id.login_title)).check(matches(isDisplayed()))
    }

    @Test
    fun emailWrongPasswordCorrect() {
        onView(withId(R.id.et_email)).perform(typeText("aaa"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("Alessandro1"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        onView(withId(R.id.login_title)).check(matches(isDisplayed()))
    }

    @Test
    fun createAccountClick() {
        onView(withId(R.id.btn_register_account)).perform(click())
        onView(withId(R.id.registration_title)).check(matches(isDisplayed()))
    }

    @Test
    fun successfulLoginShouldOpenMainScreen() {
        onView(withId(R.id.et_email)).perform(typeText("piero.bruni@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("Alessandro1"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(5000)
        LoginActivity.signOut()
    }

}