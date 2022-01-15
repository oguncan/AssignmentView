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


class AssignmentView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ListView(context, attrs){

    private lateinit var mContext : Context
    private lateinit var mImageList : List<String>

    private val TAG = AssignmentView::class.java.simpleName

    private fun Int.toPixel() = (context.resources.displayMetrics.density * this)
    private fun Int.toDP() = this / context.resources.displayMetrics.density


    init {
        mContext = context
        this.layoutMode = LinearLayoutManager.HORIZONTAL
    }


    fun setImageList(images : List<String>){
        mImageList = images
        this.adapter = AssignmentGenericAdapter(context, mImageList)
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
            Log.d(TAG, "${position}. image load time = "+ time.toString())
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val rowItem = inflater.inflate(
                R.layout.layout_single_image_item,
                parent,
                false)

            mGson = GsonBuilder().setPrettyPrinting().create()
            mOkhttpClient = OkHttpClient()
            GlobalScope.launch(Dispatchers.IO){
                startTime = System.currentTimeMillis()
                val response = ServicesManager.getHttpBinService(mOkhttpClient!!)!!
                    .get()!!.awaitResponse()
                if(response.isSuccessful) {
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
                }
            }
            return rowItem
        }

    }
}