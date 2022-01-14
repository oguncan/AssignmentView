package com.okmobile.assignmentview.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.hardware.SensorManager.getOrientation
import android.text.Layout
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.okmobile.assignmentview.R
import kotlinx.android.synthetic.main.layout_single_image_item.view.*
import java.util.ArrayList
import android.widget.Toast

import androidx.annotation.NonNull
import androidx.databinding.ObservableArrayMap
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

import com.okmobile.assignmentview.network.ServicesManager
import retrofit2.Call;
import retrofit2.Callback;
import com.okmobile.assignmentview.network.HttpBinService
import com.google.gson.Gson
import okhttp3.OkHttpClient;
import com.google.gson.GsonBuilder
import retrofit2.Response


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

    public class AssignmentGenericAdapter(
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

        private fun testGetRequest() {
            startTime = System.currentTimeMillis()
            val httpBinService: HttpBinService? = ServicesManager.getHttpBinService(mOkhttpClient!!)
            httpBinService!!.get()!!.enqueue(object : Callback<Map<String?, Any?>?> {
                override fun onResponse(
                    call: Call<Map<String?, Any?>?>,
                    response: Response<Map<String?, Any?>?>
                ) {
                }

                override fun onFailure(call: Call<Map<String?, Any?>?>, t: Throwable) {
                    printError(t)
                }
            })
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
            testGetRequest()
            Glide.with(context)
                .load(dataSource[position])
                .placeholder(R.drawable.ic_launcher_foreground)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(p0: GlideException?, p1: Any?, p2: Target<Drawable>?, p3: Boolean): Boolean {
                        return false
                    }
                    override fun onResourceReady(p0: Drawable?, p1: Any?, p2: Target<Drawable>?, p3: DataSource?, p4: Boolean): Boolean {
                        elapsedTime = System.currentTimeMillis() - startTime
                        printImageLoadingTime(position, elapsedTime)
                        elapsedTime = 0L

                        return false
                    }
                }).into(rowItem.singleAssignmentImage)
            return rowItem
        }

    }
}