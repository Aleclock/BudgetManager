package com.example.aleclock.budgetmanager

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.AttributeSet

class TransitionDetailFragment : Fragment() {

    fun getShowCode() = arguments?.getString(ARG_COURSE_CODE) ?: ""


    override fun onInflate(context: Context?, attrs: AttributeSet, savedInstanceState: Bundle?) {
        super.onInflate(context, attrs, savedInstanceState)

        if (savedInstanceState == null && context != null && context is Activity) {
            //  This can only be called before the fragment has been attached to its activity;
            // that is, you should call it immediately after constructing the fragment.
            this.arguments = context.intent.extras
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val config = resources.configuration
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (activity is DetailActivity) {
                val intent = Intent(activity, MainActivity::class.java).apply {
                    putExtra(MainActivity.ARG_COURSE_SELECTED, getShowCode())
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
                startActivity(intent)
            }
        }
    }

    companion object {

        var ARG_COURSE_CODE = "courseCode"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param courseCode Code of the course to show.
         * @return A new instance of fragment CourseDetailFragment.
         */
        fun newInstance(courseCode: String): TransitionDetailFragment {
            val args = Bundle()
            args.putString(ARG_COURSE_CODE, courseCode)

            return TransitionDetailFragment().apply {
                arguments = args
            }
        }
    }
}