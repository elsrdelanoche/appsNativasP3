package com.example.practica3

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.example.practica3.databinding.ActivityGalleryBinding
import com.example.practica3.db.AppDatabase

class GalleryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGalleryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalleryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        val mediaItemDao = db.mediaItemDao()

        binding.galleryRecyclerview.layoutManager = GridLayoutManager(this, 3)

        mediaItemDao.getAllMediaItems().observe(this, Observer { mediaItems ->
            binding.galleryRecyclerview.adapter = GalleryAdapter(items = mediaItems, onClick = { mediaItem ->
                val intent = if (mediaItem.isVideo) {
                    Intent(this, AudioPlayerActivity::class.java).apply {
                        putExtra("audio_path", mediaItem.filePath)
                    }
                } else {
                    Intent(this, PhotoViewActivity::class.java).apply {
                        putExtra("photo_uri", mediaItem.filePath)
                    }
                }
                startActivity(intent)
            })
        })
    }
}
