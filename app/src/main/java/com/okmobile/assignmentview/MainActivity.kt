package com.okmobile.assignmentview

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.okmobile.assignmentview.adapter.AssignmentAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var sampleImageList = listOf(
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image1-500kb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image2-500kb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image3-500kb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image4-500kb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image1-1mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image2-1mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image3-1mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image4-1mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image1-1_5mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image2-1_5mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image3-1_5mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image4-1_5mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image1-2mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image2-2mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image3-2mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image4-2mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image1-3mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image2-3mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image3-3mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image4-3mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image1-5mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image2-5mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image3-5mb.png",
        "https://db62cod6cnasq.cloudfront.net/user-media/0/image4-5mb.png"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        assignmentImageView.setImageList(sampleImageList)
        assignmentImageView.adapter = AssignmentAdapter(this, sampleImageList)
    }
}