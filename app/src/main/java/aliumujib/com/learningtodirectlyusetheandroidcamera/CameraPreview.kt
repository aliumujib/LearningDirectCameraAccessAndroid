package aliumujib.com.learningtodirectlyusetheandroidcamera

import android.content.Context
import android.hardware.Camera
import android.util.AttributeSet
import android.view.Surface
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.WindowManager


class CameraPreview : SurfaceView, SurfaceHolder.Callback {

    val TAG = CameraPreview::class.java.canonicalName

    private var camera: Camera? = null
    private var surfaceHolder: SurfaceHolder? = null

    override fun surfaceChanged(p0: SurfaceHolder?, p1: Int, p2: Int, p3: Int) {
        stopPreview()
        startPreview()
    }

    override fun surfaceDestroyed(p0: SurfaceHolder?) {
        stopPreview()
    }

    override fun surfaceCreated(p0: SurfaceHolder?) {
        startPreview()
    }

    fun startCamera(camera: Camera?, cameraId: Int) {
        this.camera = camera

        this.camera!!.stopPreview()
        var previewOrientation = getCameraPreviewOrientation(cameraId)

        try {
            this.camera!!.setDisplayOrientation(previewOrientation)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        this.surfaceHolder = holder
        surfaceHolder?.addCallback(this)

        //SHOW PREVIEW
        startPreview()
    }

    private fun startPreview() {
        try {
            if (this.camera != null && surfaceHolder!!.surface != null) {
                this.camera!!.setPreviewDisplay(surfaceHolder)
                this.camera!!.startPreview()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopPreview() {
        try {
            camera?.stopPreview()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun releaseCamera() {
        stopPreview()
        camera?.release()
        camera = null
    }

    private fun getCameraPreviewOrientation(cameraID: Int): Int {
        var temp = 0
        val DEGREES_IN_A_CIRCLE = 360
        var previewOrientation = 0

        val cameraInfo = Camera.CameraInfo()
        Camera.getCameraInfo(cameraID, cameraInfo)

        val deviceOrientation = getDeviceOrientation()

        when (cameraInfo.facing) {
            Camera.CameraInfo.CAMERA_FACING_FRONT -> {
                temp = cameraInfo.orientation - deviceOrientation + DEGREES_IN_A_CIRCLE
                previewOrientation = temp % DEGREES_IN_A_CIRCLE
            }

            Camera.CameraInfo.CAMERA_FACING_BACK -> {
                temp = (cameraInfo.orientation + deviceOrientation) % DEGREES_IN_A_CIRCLE
                previewOrientation = (DEGREES_IN_A_CIRCLE - temp) * DEGREES_IN_A_CIRCLE
            }

        }

        return previewOrientation
    }

    private fun getDeviceOrientation(): Int {
        val windowManager: WindowManager = context!!.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val rotation = windowManager.defaultDisplay.rotation

        var degrees = 0

        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        return degrees
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

}