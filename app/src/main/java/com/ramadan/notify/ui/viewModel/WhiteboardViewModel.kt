@file:Suppress("DEPRECATION")

package com.ramadan.notify.ui.viewModel

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import com.ramadan.notify.utils.DrawView
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


class WhiteboardViewModel : ViewModel() {
    private lateinit var filePath: String
    private var FilePathStrings: Array<String?>? = null
    private var listFile: Array<File>? = null
    var file: File? = null
    private val dirPath = Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_PICTURES
    ).path.toString() + "/Notify"

    val noteListener: NoteListener? = null

    fun clearDrawingNote(whiteboard: DrawView) {
        whiteboard.clear()
    }
//
//    fun saveDrawingNote(fileName: String, context: Context, whiteboard: DrawView) {
//        whiteboard.isDrawingCacheEnabled = true
//        saveImageToExternalStorage(whiteboard.drawingCache, fileName)
//        whiteboard.destroyDrawingCache()
//    }
//
    fun saveImageToExternalStorage(bitmap: Bitmap, fileName: String): Boolean {
        filePath = "$dirPath/$fileName.jpg"
        try {
            val dir = File(dirPath)
            if (!dir.exists())
                dir.mkdirs()
            val file = File(filePath)
            if (file.exists())
                return false
            file.createNewFile()
            val outStream: OutputStream?
            outStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush()
            outStream.close()
        } catch (e: Exception) {
            Log.e("saveToExternalStorage()", e.message!!)
            return false
        }
        return true
    }

    fun loadWhiteboards(): Array<String?>? {
        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            Log.e("Records", "Environment.MEDIA IS MOUNTED")
            noteListener?.onFailure("Failed to load whiteboards")
        } else {
            val dir = File(dirPath)
            if (!dir.exists())
                dir.mkdirs()
            file = File(dirPath)
        }
        if (file!!.isDirectory) {
            listFile = file!!.listFiles()
            listFile!!.sortByDescending { it.lastModified() }
            FilePathStrings = arrayOfNulls(listFile!!.size)
            for (i in listFile!!.indices) {
                FilePathStrings!![i] = listFile!![i].absolutePath
            }
        }
        return FilePathStrings
    }


    private fun hasPermissions(
        context: Context?,
        vararg permissions: String
    ): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(
                        context,
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    return false
                }
            }
        }
        return true
    }


}