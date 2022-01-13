package com.okmobile.assignmentview.view

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import com.okmobile.assignmentview.R


class AssignmentView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ListView(context, attrs), AbsListView.OnScrollListener {

    private lateinit var mContext : Context
    private lateinit var assignmentAdapter : AssignmentGenericAdapter<String>

    private val TAG = AssignmentView::class.java.simpleName

    private fun Int.toPixel() = (context.resources.displayMetrics.density * this)
    private fun Int.toDP() = this / context.resources.displayMetrics.density


    init {
        mContext = context

    }

    public class AssignmentGenericAdapter<T>(
        private val context: Context,
        private val dataSource : ArrayList<String>
    ) : BaseAdapter() {

        private val inflater: LayoutInflater
                = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return dataSource.size
        }

        override fun getItem(position: Int): String {
            return dataSource[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowItem = inflater.inflate(
                R.layout.layout_assignment_list_item,
                parent,
                false)
            val assignmentImage = rowItem.findViewById<AppCompatImageView>(R.id.assignmentImage)
            return rowItem
        }

    }

    override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {

    }

    override fun onScroll(
        view: AbsListView?,
        firstVisibleItem: Int,
        visibleItemCount: Int,
        totalItemCount: Int
    ) {

    }

}