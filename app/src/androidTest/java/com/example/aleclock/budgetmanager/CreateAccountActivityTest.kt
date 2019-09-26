package com.example.aleclock.budgetmanager

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.text.TextUtils.concat
import com.example.aleclock.budgetmanager.activity.LoginActivity
import org.junit.Rule
import org.junit.Test
import java.util.*

class CreateAccountActivityTest {

    @get:Rule
    var mActivityTestRule = ActivityTestRule<LoginActivity>(LoginActivity::class.java)

    private fun createAccountClick() {
        onView(withId(R.id.btn_register_account)).perform(click())
    }

    @Test
    fun shouldContainFirstNameInput() {
        createAccountClick()
        onView(withId(R.id.et_first_name)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainLastNameInput() {
        createAccountClick()
        onView(withId(R.id.et_last_name)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainPasswordInput() {
        createAccountClick()
        onView(withId(R.id.et_password)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainEmailInput() {
        createAccountClick()
        onView(withId(R.id.et_email)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainRegisterButton() {
        createAccountClick()
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()))
    }

    @Test
    fun fieldsEmpty() {
        createAccountClick()
        onView(withId(R.id.et_first_name)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.et_last_name)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.et_email)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.btn_register)).perform(click())
        onView(withText(R.string.error_fill_fields)).check(matches(isDisplayed()))
    }

    @Test
    fun firstNameEmpty() {
        createAccountClick()
        onView(withId(R.id.et_first_name)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.et_last_name)).perform(typeText("Rossi"), closeSoftKeyboard())
        onView(withId(R.id.et_email)).perform(typeText("marco.rossi@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("MarcoRossi1 "), closeSoftKeyboard())
        onView(withId(R.id.btn_register)).perform(click())
        onView(withText(R.string.error_fill_fields)).check(matches(isDisplayed()))
    }

    @Test
    fun lastNameEmpty() {
        createAccountClick()
        onView(withId(R.id.et_first_name)).perform(typeText("Marco"), closeSoftKeyboard())
        onView(withId(R.id.et_last_name)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.et_email)).perform(typeText("marco.rossi@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("MarcoRossi1"), closeSoftKeyboard())
        onView(withId(R.id.btn_register)).perform(click())
        onView(withText(R.string.error_fill_fields)).check(matches(isDisplayed()))
    }

    @Test
    fun emailEmpty() {
        createAccountClick()
        onView(withId(R.id.et_first_name)).perform(typeText("Marco"), closeSoftKeyboard())
        onView(withId(R.id.et_last_name)).perform(typeText("Rossi"), closeSoftKeyboard())
        onView(withId(R.id.et_email)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("MarcoRossi1"), closeSoftKeyboard())
        onView(withId(R.id.btn_register)).perform(click())
        onView(withText(R.string.error_fill_fields)).check(matches(isDisplayed()))
    }

    @Test
    fun passwordEmpty() {
        createAccountClick()
        onView(withId(R.id.et_first_name)).perform(typeText("Marco"), closeSoftKeyboard())
        onView(withId(R.id.et_last_name)).perform(typeText("Rossi"), closeSoftKeyboard())
        onView(withId(R.id.et_email)).perform(typeText("marco.rossi@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.btn_register)).perform(click())
        onView(withText(R.string.error_fill_fields)).check(matches(isDisplayed()))
    }

    @Test
    fun wrongEmail() {
        createAccountClick()
        onView(withId(R.id.et_first_name)).perform(typeText("Marco"), closeSoftKeyboard())
        onView(withId(R.id.et_last_name)).perform(typeText("Rossi"), closeSoftKeyboard())
        onView(withId(R.id.et_email)).perform(typeText("aaaaaa"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("MarcoRossi1"), closeSoftKeyboard())
        onView(withId(R.id.btn_register)).perform(click())
        onView(withId(R.id.registration_title)).check(matches(isDisplayed()))
    }

    @Test
    fun successfulCreateAccount() {
        createAccountClick()

        val email = concat("marco.rossi",randomInt(),"@gmail.com").toString()
        onView(withId(R.id.et_first_name)).perform(typeText("Marco"), closeSoftKeyboard())
        onView(withId(R.id.et_last_name)).perform(typeText("Rossi"), closeSoftKeyboard())
        onView(withId(R.id.et_email)).perform(typeText(email), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("MarcoRossi1"), closeSoftKeyboard())
        onView(withId(R.id.btn_register)).perform(click())

        Thread.sleep(5000)
        LoginActivity.signOut()
    }

    private fun randomInt(): String {
        return Random().nextInt(100000).toString()
    }
}