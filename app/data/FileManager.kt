// FileManager.kt
package com.potholereporter.ai.data

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Helper class to manage file operations, specifically saving images to app-specific storage.
 */
class FileManager(private val context: Context) {

    /**
     * Saves an image from a URI (from camera or gallery) to a file in internal storage.
     * @param imageUri The Uri of the image to save.
     * @return The absolute path to the saved image file, or null on failure.
     */
    suspend fun saveImage(imageUri: Uri): String? = withContext(Dispatchers.IO) {
        // Use a timestamp to create a unique filename
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val fileName = "POTHOLE_${timeStamp}.jpg"

        // Get the directory where photos should be stored
        val storageDir = File(context.filesDir, "pothole_photos")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }

        val imageFile = File(storageDir, fileName)

        try {
            // Get bitmap from URI
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)

            // Write the bitmap to the file
            FileOutputStream(imageFile).use { out ->
                // Compress the image before saving (e.g., 90% quality)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
            return@withContext imageFile.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            return@withContext null
        }
    }
}