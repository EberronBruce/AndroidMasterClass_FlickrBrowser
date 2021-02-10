package com.redravencomputing.flickrbrowser

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso

class PhotoDetailsActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_details)

        activateToolbar(true)

        val photo = intent.getSerializableExtra(PHOTO_TRANSFER) as Photo

        val photo_title = findViewById<TextView>(R.id.photo_title)
        val photo_tags = findViewById<TextView>(R.id.photo_tags)
        val photo_author = findViewById<TextView>(R.id.photo_author)
        val photo_image = findViewById<ImageView>(R.id.photo_image)
        photo_title.text = photo.title
        photo_tags.text = photo.tags
        photo_author.text = photo.author

        Picasso.get().load(photo.link)
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(photo_image)
    }
}