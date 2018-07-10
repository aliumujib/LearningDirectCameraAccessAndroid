package aliumujib.com.learningtodirectlyusetheandroidcamera

import android.content.Context
import android.hardware.Camera
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.Surface
import android.view.WindowManager

import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

object CameraHelper {
    internal val LOG_TAG = "Camera Direct Access"

    val photoDirectory: File?
        get() {
            var outputDir: File? = null
            val externalStorageState = Environment.getExternalStorageState()
            if (externalStorageState == Environment.MEDIA_MOUNTED) {
                val pictureDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                outputDir = File(pictureDir, "Pluralsight")
                if (!outputDir.exists()) {
                    if (!outputDir.mkdirs()) {
                        val message = "Failed to create directory:" + outputDir.absolutePath
                        Log.e(LOG_TAG, message)
                        outputDir = null
                    }
                }
            }

            return outputDir
        }

    fun getDisplayOrientationForCamera(context: Context, cameraId: Int): Int {
        val DEGREES_IN_CIRCLE = 360
        var temp = 0
        var previewOrientation = 0


        val cameraInfo = Camera.CameraInfo()
        Camera.getCameraInfo(cameraId, cameraInfo)

        val deviceOrientation = getDeviceOrientationDegrees(context)
        when (cameraInfo.facing) {
            Camera.CameraInfo.CAMERA_FACING_BACK -> {
                temp = cameraInfo.orientation - deviceOrientation + DEGREES_IN_CIRCLE
                previewOrientation = temp % DEGREES_IN_CIRCLE
            }
            Camera.CameraInfo.CAMERA_FACING_FRONT -> {
                temp = (cameraInfo.orientation + deviceOrientation) % DEGREES_IN_CIRCLE
                previewOrientation = (DEGREES_IN_CIRCLE - temp) % DEGREES_IN_CIRCLE
            }
        }

        return previewOrientation
    }

    fun getDeviceOrientationDegrees(context: Context): Int {
        var degrees = 0

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val rotation = windowManager.defaultDisplay.rotation

        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        return degrees
    }

    fun generateTimeStampPhotoFile(): File? {
        var photoFile: File? = null
        val outputDir = photoDirectory

        if (outputDir != null) {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val photoFileName = "IMG_$timeStamp.jpg"

            photoFile = File(outputDir, photoFileName)
        }

        return photoFile
    }

    fun generateTimeStampPhotoFileUri(): Uri? {
        var photoFileUri: Uri? = null
        val photoFile = generateTimeStampPhotoFile()

        if (photoFile != null) {
            photoFileUri = Uri.fromFile(photoFile)
        }

        return photoFileUri
    }

}
