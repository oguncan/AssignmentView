package com.okmobile.assignmentview.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.okmobile.assignmentview.R
import kotlinx.android.synthetic.main.layout_single_image_item.view.*

import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

import com.okmobile.assignmentview.network.ServicesManager
import com.google.gson.Gson
import okhttp3.OkHttpClient;
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.*
import android.app.Activity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import android.graphics.Rect

import android.view.MotionEvent

import android.view.GestureDetector

import android.database.DataSetObserver
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View.MeasureSpec

import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemLongClickListener

import android.widget.Scroller
import androidx.core.view.GestureDetectorCompat
import kotlinx.coroutines.withContext

import java.util.LinkedList

import java.util.Queue





class AssignmentView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AdapterView<ListAdapter>(context, attrs), GestureDetector.OnGestureListener{

    private lateinit var mContext : Context
    private lateinit var mImageList : List<String>

    private val TAG = AssignmentView::class.java.simpleName

    private fun Int.toPixel() = (context.resources.displayMetrics.density * this)
    private fun Int.toDP() = this / context.resources.displayMetrics.density

    init {
        mContext = context
        initView()
    }

    var mAlwaysOverrideTouch = true
    protected var mAdapter: ListAdapter? = null
    private var mLeftViewIndex = -1
    private var mRightViewIndex = 0
    protected var mCurrentX = 0
    protected var mNextX = 0
    private var mMaxX = Int.MAX_VALUE
    private var mDisplayOffset = 0
    protected var mScroller: Scroller? = null
    private lateinit var mDetector: GestureDetectorCompat
    private val mRemovedViewQueue: Queue<View> = LinkedList()
    private var mOnItemSelected: OnItemSelectedListener? = null
    private var mOnItemClicked: OnItemClickListener? = null
    private var mOnItemLongClicked: OnItemLongClickListener? = null
    private var mDataChanged = false


    @Synchronized
    private fun initView() {
        mLeftViewIndex = -1
        mRightViewIndex = 0
        mDisplayOffset = 0
        mCurrentX = 0
        mNextX = 0
        mMaxX = Int.MAX_VALUE
        mScroller = Scroller(context)
        mDetector = GestureDetectorCompat(context, this)
    }

    override fun setOnItemSelectedListener(
        listener: OnItemSelectedListener?
    ) {
        mOnItemSelected = listener
    }

    override fun setOnItemClickListener(listener: OnItemClickListener?) {
        mOnItemClicked = listener
    }

    override fun setOnItemLongClickListener(
        listener: OnItemLongClickListener?
    ) {
        mOnItemLongClicked = listener
    }

    private val mDataObserver: DataSetObserver = object : DataSetObserver() {
        override fun onChanged() {
            synchronized(this) { mDataChanged = true }
            invalidate()
            requestLayout()
        }

        override fun onInvalidated() {
            reset()
            invalidate()
            requestLayout()
        }
    }

    override fun getAdapter(): ListAdapter? {
        return mAdapter
    }

    override fun getSelectedView(): View? {
        // TODO: implement
        return null
    }

    override fun setAdapter(adapter: ListAdapter?) {
        if (mAdapter != null) {
            mAdapter!!.unregisterDataSetObserver(mDataObserver)
        }
        mAdapter = adapter
        mAdapter!!.registerDataSetObserver(mDataObserver)
        reset()
    }

    @Synchronized
    private fun reset() {
        initView()
        removeAllViewsInLayout()
        requestLayout()
    }

    override fun setSelection(position: Int) {

    }

    private fun addAndMeasureChild(child: View, viewPos: Int) {
        var params = child.layoutParams
        if (params == null) {
            params = LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT
            )
        }
        addViewInLayout(child, viewPos, params, true)
        child.measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST)
        )
    }

    @Synchronized
    override fun onLayout(
        changed: Boolean, left: Int, top: Int,
        right: Int, bottom: Int
    ) {
        super.onLayout(changed, left, top, right, bottom)
        if (mAdapter == null) {
            return
        }
        if (mDataChanged) {
            val oldCurrentX = mCurrentX
            initView()
            removeAllViewsInLayout()
            mNextX = oldCurrentX
            mDataChanged = false
        }
        if (mScroller!!.computeScrollOffset()) {
            val scrollx = mScroller!!.currX
            mNextX = scrollx
        }
        if (mNextX <= 0) {
            mNextX = 0
            mScroller!!.forceFinished(true)
        }
        if (mNextX >= mMaxX) {
            mNextX = mMaxX
            mScroller!!.forceFinished(true)
        }
        val dx = mCurrentX - mNextX
        removeNonVisibleItems(dx)
        fillList(dx)
        positionItems(dx)
        mCurrentX = mNextX
        if (!mScroller!!.isFinished) {
            post { requestLayout() }
        }
    }

    private fun fillList(dx: Int) {
        var edge = 0
        var child = getChildAt(childCount - 1)
        if (child != null) {
            edge = child.right
        }
        fillListRight(edge, dx)
        edge = 0
        child = getChildAt(0)
        if (child != null) {
            edge = child.left
        }
        fillListLeft(edge, dx)
    }

    private fun fillListRight(rightEdge: Int, dx: Int) {
        var rightEdge = rightEdge
        while (rightEdge + dx < width
            && mRightViewIndex < mAdapter!!.count
        ) {
            val child = mAdapter!!.getView(
                mRightViewIndex,
                mRemovedViewQueue.poll(), this
            )
            addAndMeasureChild(child, -1)
            rightEdge += child.measuredWidth
            if (mRightViewIndex == mAdapter!!.count - 1) {
                mMaxX = mCurrentX + rightEdge - width
            }
            if (mMaxX < 0) {
                mMaxX = 0
            }
            mRightViewIndex++
        }
    }

    private fun fillListLeft(leftEdge: Int, dx: Int) {
        var leftEdge = leftEdge
        while (leftEdge + dx > 0 && mLeftViewIndex >= 0) {
            val child = mAdapter!!.getView(
                mLeftViewIndex,
                mRemovedViewQueue.poll(), this
            )
            addAndMeasureChild(child, 0)
            leftEdge -= child.measuredWidth
            mLeftViewIndex--
            mDisplayOffset -= child.measuredWidth
        }
    }

    private fun removeNonVisibleItems(dx: Int) {
        var child = getChildAt(0)
        while (child != null && child.right + dx <= 0) {
            mDisplayOffset += child.measuredWidth
            mRemovedViewQueue.offer(child)
            removeViewInLayout(child)
            mLeftViewIndex++
            child = getChildAt(0)
        }
        child = getChildAt(childCount - 1)
        while (child != null && child.left + dx >= width) {
            mRemovedViewQueue.offer(child)
            removeViewInLayout(child)
            mRightViewIndex--
            child = getChildAt(childCount - 1)
        }
    }

    private fun positionItems(dx: Int) {
        if (childCount > 0) {
            mDisplayOffset += dx
            var left = mDisplayOffset
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                val childWidth = child.measuredWidth
                child.layout(
                    left, 0, left + childWidth,
                    child.measuredHeight
                )
                left += childWidth + child.paddingRight
            }
        }
    }

    @Synchronized
    fun scrollTo(x: Int) {
        mScroller!!.startScroll(mNextX, 0, x - mNextX, 0)
        requestLayout()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)

    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        var handled = super.dispatchTouchEvent(ev)
        handled = handled or mDetector.onTouchEvent(ev)
        return handled
    }

    override fun onFling(
        e1: MotionEvent?, e2: MotionEvent?, velocityX: Float,
        velocityY: Float
    ): Boolean {
        synchronized(this) {
            mScroller!!.fling(
                mNextX, 0,
                (-velocityX).toInt(), 0, 0, mMaxX, 0, 0
            )
        }
        requestLayout()
        return true
    }

    override fun onDown(e: MotionEvent?): Boolean {
        mScroller!!.forceFinished(true)
        return true
    }

    //BURASI

    override fun onShowPress(e: MotionEvent?) {
    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (isEventWithinView(e!!, child)) {
                if (mOnItemClicked != null) {
                    mOnItemClicked!!.onItemClick(
                        this,
                        child, mLeftViewIndex + 1 + i,
                        mAdapter!!.getItemId(mLeftViewIndex + 1 + i)
                    )
                }
                if (mOnItemSelected != null) {
                    mOnItemSelected!!.onItemSelected(
                        this,
                        child, mLeftViewIndex + 1 + i,
                        mAdapter!!.getItemId(mLeftViewIndex + 1 + i)
                    )
                }
                break
            }
        }
        return true
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        synchronized(this) { mNextX += distanceX.toInt() }
        requestLayout()
        return true
    }

    override fun onLongPress(e: MotionEvent?) {
        val childCount = childCount
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (isEventWithinView(e!!, child)) {
                if (mOnItemLongClicked != null) {
                    mOnItemLongClicked!!.onItemLongClick(
                        this, child, mLeftViewIndex
                                + 1 + i,
                        mAdapter!!.getItemId(mLeftViewIndex + 1 + i)
                    )
                }
                break
            }
        }
    }

    private fun isEventWithinView(e: MotionEvent, child: View): Boolean {
        val viewRect = Rect()
        val childPosition = IntArray(2)
        child.getLocationOnScreen(childPosition)
        val left = childPosition[0]
        val right = left + child.width
        val top = childPosition[1]
        val bottom = top + child.height
        viewRect[left, top, right] = bottom
        return viewRect.contains(e.rawX.toInt(), e.rawY.toInt())
    }

    fun setImageList(images : List<String>){
        mImageList = images
    }

    class AssignmentGenericAdapter(
        private val context: Context,
        private val dataSource : List<String>
    ) : BaseAdapter() {

        private val TAG = AssignmentGenericAdapter::class.java.simpleName
        private var mOkhttpClient: OkHttpClient? = null
        private var mGson: Gson? = null

        private val inflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        var startTime = System.currentTimeMillis()
        var elapsedTime = 0L

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): String {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        private fun printError(throwable: Throwable) {
            Log.d(TAG, throwable.toString())
        }

        private fun printImageLoadingTime(position: Int, time : Any){
            Log.d(TAG, "${position+1}. image load time = "+ time.toString())
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowItem = inflater.inflate(
                R.layout.layout_single_image_item,
                parent,
                false)

            mGson = GsonBuilder().setPrettyPrinting().create()
            mOkhttpClient = OkHttpClient()
            (context as Activity).runOnUiThread {
                Glide.with(context)
                    .load(dataSource[position])
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            p0: GlideException?,
                            p1: Any?,
                            p2: Target<Drawable>?,
                            p3: Boolean
                        ): Boolean {
                            rowItem.singleAssignmentImage.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            p0: Drawable?,
                            p1: Any?,
                            p2: Target<Drawable>?,
                            p3: DataSource?,
                            p4: Boolean
                        ): Boolean {
                            elapsedTime = System.currentTimeMillis() - startTime
                            printImageLoadingTime(position, elapsedTime)
                            elapsedTime = 0L
                            return false
                        }
                    }).into(rowItem.singleAssignmentImage)
            }
            GlobalScope.launch(Dispatchers.IO){
                startTime = System.currentTimeMillis()
                val response = ServicesManager.getHttpBinService(mOkhttpClient!!)!!
                    .get()!!.awaitResponse()
                if(response.isSuccessful) {
                }
            }
            return rowItem
        }

    }
}