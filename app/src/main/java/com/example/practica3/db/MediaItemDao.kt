package com.example.practica3.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface MediaItemDao {

    @Insert
    suspend fun insert(mediaItem: MediaItem)

    @Update
    suspend fun update(mediaItem: MediaItem)

    @Query("SELECT * FROM media_items ORDER BY date DESC")
    fun getAllMediaItems(): LiveData<List<MediaItem>>

    @Query("SELECT * FROM media_items WHERE uri = :uri LIMIT 1")
    suspend fun getMediaItemByUri(uri: String): MediaItem?
}