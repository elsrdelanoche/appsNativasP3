package com.example.practica3.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "media_items")
data class MediaItem(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uri: String,
    val isVideo: Boolean,
    val date: Long,
    val duration: Long? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val album: String? = null,
    val tags: String? = null
)