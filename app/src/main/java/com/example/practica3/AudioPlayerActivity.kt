package com.example.practica3

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.lifecycleScope
import com.example.practica3.databinding.ActivityAudioPlayerBinding
import com.example.practica3.db.AppDatabase
import kotlinx.coroutines.launch
import java.io.File

class AudioPlayerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAudioPlayerBinding
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var audioUri: Uri
    private lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAudioPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        audioUri = intent.data!!
        binding.audioTitle.text = File(audioUri.path!!).name

        mediaPlayer = MediaPlayer().apply {
            setDataSource(applicationContext, audioUri)
            prepare()
        }

        handler = Handler(Looper.getMainLooper())

        binding.playPauseButton.setOnClickListener { togglePlayPause() }
        binding.stopButton.setOnClickListener { stopPlayback() }
        binding.renameButton.setOnClickListener { showRenameDialog() }
        binding.deleteButton.setOnClickListener { deleteAudio() }
        binding.shareButton.setOnClickListener { shareAudio() }
        binding.tagButton.setOnClickListener { showTagDialog() }

        initializeSeekBar()
    }

    private fun togglePlayPause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            binding.playPauseButton.text = "Play"
        } else {
            mediaPlayer?.start()
            binding.playPauseButton.text = "Pause"
            updateSeekBar()
        }
    }

    private fun stopPlayback() {
        mediaPlayer?.stop()
        mediaPlayer?.prepare()
        binding.playPauseButton.text = "Play"
        binding.seekBar.progress = 0
    }

    private fun showRenameDialog() {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Rename")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val newName = editText.text.toString()
                renameAudio(newName)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun renameAudio(newName: String) {
        // Logic to rename the file and update the database
    }

    private fun deleteAudio() {
        // Logic to delete the file and update the database
    }

    private fun shareAudio() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, audioUri)
            type = "audio/m4a"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share Audio"))
    }

    private fun showTagDialog() {
        val editText = EditText(this)
        AlertDialog.Builder(this)
            .setTitle("Tags")
            .setMessage("Enter tags, separated by commas")
            .setView(editText)
            .setPositiveButton("Save") { _, _ ->
                val tags = editText.text.toString()
                saveTags(tags)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun saveTags(tags: String) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            val mediaItem = db.mediaItemDao().getMediaItemByUri(audioUri.toString())
            if (mediaItem != null) {
                mediaItem.copy(tags = tags).also { db.mediaItemDao().update(it) }
            }
        }
    }

    private fun initializeSeekBar() {
        binding.seekBar.max = mediaPlayer?.duration ?: 0
        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun updateSeekBar() {
        if (mediaPlayer?.isPlaying == true) {
            binding.seekBar.progress = mediaPlayer?.currentPosition ?: 0
            handler.postDelayed({ updateSeekBar() }, 1000)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}