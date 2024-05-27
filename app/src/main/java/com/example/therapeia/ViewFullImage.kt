package com.example.therapeia

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import coil.load

class ViewFullImage : AppCompatActivity() {

    private var image_viewer: ImageView? = null
    private var imageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_full_image)

        imageUrl = intent.getStringExtra("url").toString()
        image_viewer = findViewById(R.id.image_view)

        findViewById<ImageView>(R.id.image_view).load(imageUrl)

    }
}