package com.okmobile.assignmentview.view

import android.content.Context
import android.view.MotionEvent

import android.view.GestureDetector.SimpleOnGestureListener

import android.view.GestureDetector

import android.util.AttributeSet

import android.view.View
import android.view.View.OnTouchListener

import android.widget.ScrollView
import kotlin.math.abs


class AssignmentScrollView(context: Context?, attrs: AttributeSet?) :
    ScrollView(context, attrs) {
    private val mGestureDetector: GestureDetector
    var mGestureListener: OnTouchListener? = null
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return (super.onInterceptTouchEvent(ev)
                && mGestureDetector.onTouchEvent(ev))
    }

    internal inner class YScrollDetector : SimpleOnGestureListener() {
        override fun onScroll(
            e1: MotionEvent, e2: MotionEvent,
            distanceX: Float, distanceY: Float
        ): Boolean {
            return abs(distanceY) > abs(distanceX)
        }
    }

    init {
        mGestureDetector = GestureDetector(YScrollDetector())
        setFadingEdgeLength(0)
    }
}
