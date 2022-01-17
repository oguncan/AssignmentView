package com.okmobile.assignmentview.adapter

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.okmobile.assignmentlistview.R
import com.okmobile.assignmentview.network.ServicesManager
import kotlinx.android.synthetic.main.layout_single_image_item.view.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import retrofit2.awaitResponse
import kotlin.system.measureTimeMillis

class AssignmentAdapter(
    private val context: Context,
    private val dataSource : List<String>
) : BaseAdapter() {

    private val TAG = AssignmentAdapter::class.java.simpleName
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

    private val scope = CoroutineScope(SupervisorJob() + CoroutineName("LoginHelper"))

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowItem = inflater.inflate(
            R.layout.layout_single_image_item,
            parent,
            false)

        mGson = GsonBuilder().setPrettyPrinting().create()
        mOkhttpClient = OkHttpClient()
        startTime = System.currentTimeMillis()
        /**
         * Since no other method could be found, such a method was used.
         * I'm waiting for your feedback if I make a mistake.
         */
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
                        GlobalScope.launch(Dispatchers.IO) {
                            val time = measureTimeMillis {
                                val success = responseSession()
                                elapsedTime = System.currentTimeMillis() - startTime
                                printImageLoadingTime(position, elapsedTime)
                                elapsedTime = 0L
                            }
                        }
                        return false
                    }
                }).into(rowItem.singleAssignmentImage)
        }
        return rowItem
    }

    private suspend fun responseSession() : Boolean {
        val response = ServicesManager.getHttpBinService(mOkhttpClient!!)!!
            .get()!!.awaitResponse()
        return response.isSuccessful
    }

}