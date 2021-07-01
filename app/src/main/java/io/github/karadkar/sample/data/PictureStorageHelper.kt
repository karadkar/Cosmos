package io.github.karadkar.sample.data

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.squareup.picasso.Picasso
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class PictureStorageHelper(
    private val applicationContext: Context
) {
    // https://stackoverflow.com/a/59536115/2804351
    fun saveImage(imageUrl: String, name: String): Uri {
        //TODO don't save when already saved
        //TODO write test
        val fos: OutputStream
        val imageUri: Uri
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // FIXME: why it doesn't need storage permission?
            val resolver = applicationContext.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "$name.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)!!
            fos = resolver.openOutputStream(imageUri)!!
        } else {
            val dir = applicationContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
            val image = File(dir, "$name.jpg")
            imageUri = Uri.fromFile(image)
            fos = FileOutputStream(image)
        }
        val bitmap = getBitmap(imageUrl)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos.close()
        return imageUri
    }

    fun getBitmap(imageUrl: String): Bitmap = Picasso.get().load(imageUrl).get()
}