package com.example.practica3

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.lifecycleScope
import com.example.practica3.databinding.ActivityPhotoViewBinding
import com.example.practica3.db.AppDatabase
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.launch
import java.io.File

class PhotoViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPhotoViewBinding
    private lateinit var imageUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imageUri = intent.data!!
        binding.photoView.setImageURI(imageUri)

        binding.shareButton.setOnClickListener { shareImage() }
        binding.editButton.setOnClickListener { editImage() }
        binding.infoButton.setOnClickListener { toggleExifInfo() }
        binding.tagButton.setOnClickListener { showTagDialog() }
    }

    private fun shareImage() {
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/jpeg"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }

    private fun editImage() {
        val destinationUri = Uri.fromFile(File(cacheDir, "IMG_${System.currentTimeMillis()}.jpg"))
        UCrop.of(imageUri, destinationUri)
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(1000, 1000)
            .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            imageUri = resultUri!!
            binding.photoView.setImageURI(resultUri)
        }
    }

    private fun toggleExifInfo() {
        if (binding.exifInfo.visibility == View.GONE) {
            showExifInfo()
        } else {
            binding.exifInfo.visibility = View.GONE
        }
    }

    private fun showExifInfo() {
        try {
            val inputStream = contentResolver.openInputStream(imageUri)
            if (inputStream != null) {
                val exif = ExifInterface(inputStream)
                val exifData = StringBuilder()
                exifData.append("Date: ${exif.getAttribute(ExifInterface.TAG_DATETIME)}\n")
                exifData.append("Image-Width: ${exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH)}\n")
                exifData.append("Image-Length: ${exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH)}\n")
                exifData.append("Camera-Model: ${exif.getAttribute(ExifInterface.TAG_MODEL)}\n")

                val latLong = exif.latLong
                if (latLong != null) {
                    exifData.append("Latitude: ${latLong[0]}\n")
                    exifData.append("Longitude: ${latLong[1]}\n")
                }
                binding.exifInfo.text = exifData.toString()
                binding.exifInfo.visibility = View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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
            val mediaItem = db.mediaItemDao().getMediaItemByUri(imageUri.toString())
            if (mediaItem != null) {
                mediaItem.copy(tags = tags).also { db.mediaItemDao().update(it) }
            }
        }
    }
}