package com.example.aleclock.budgetmanager

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import com.example.aleclock.budgetmanager.activity.LoginActivity
import com.example.aleclock.budgetmanager.fragment.TransactionsFragment
import org.junit.*

class TransactionFragmentTest {

    @get:Rule
    val activityTestRule = ActivityTestRule<LoginActivity>(LoginActivity::class.java)

    private fun login() {
        onView(withId(R.id.et_email)).perform(typeText("piero.bruni@gmail.com"), closeSoftKeyboard())
        onView(withId(R.id.et_password)).perform(typeText("Alessandro1"), closeSoftKeyboard())
        onView(withId(R.id.btn_login)).perform(click())
        Thread.sleep(4000)
    }

    @After
    fun doLogout() {
        LoginActivity.signOut()
    }

    @Before
    fun doLogin() {
        login()
        clickTransactionItem()
    }

    private fun clickTransactionItem() {
        onView(withId(R.id.transaction)).perform(click())
    }

    @Test
    fun shouldContainModelsBtn() {
        onView(withId(R.id.btn_models)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainSetDateBtn() {
        onView(withId(R.id.btn_set_date)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainRecyclerViewTransaction() {
        onView(withId(R.id.recycler_view_transaction)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldContainAddTransactionBtn() {
        onView(withId(R.id.btn_add_transaction)).check(matches(isDisplayed()))
    }

    @Test
    fun onClickModelsShowPopup() {
        onView(withId(R.id.btn_models)).perform(click())
        onView(withText(R.string.models)).check(matches(isDisplayed()))
    }

    @Test
    fun onClickNewTransactionShowDialog() {
        onView(withId(R.id.btn_add_transaction)).perform(click())
        onView(withText(R.string.addTransaction)).check(matches(isDisplayed()))
    }

    @Test
    fun getCorrectMonth() {
        val date = "20190829"
        val transactionFragment = TransactionsFragment()
        val expectedDate = "201908"

        Assert.assertEquals(expectedDate, transactionFragment.getMonth(date))
    }

    @Test
    fun accountListNameNotNull() {
        val transactionFragment = TransactionsFragment()
        Assert.assertNotEquals(null, transactionFragment.accountListName)
    }

    @Test
    fun accountListIdNotNull() {
        val transactionFragment = TransactionsFragment()
        Assert.assertNotEquals(null, transactionFragment.accountListId)
    }

    @Test
    fun modelsListNotNull() {
        val transactionFragment = TransactionsFragment()
        Assert.assertNotEquals(null, transactionFragment.modelsList)
    }



}