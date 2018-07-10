package aliumujib.com.learningtodirectlyusetheandroidcamera

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import android.support.v4.app.Fragment
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.*
import kotlinx.android.synthetic.main.fragment_camera.*
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream


@Suppress("DEPRECATION")
/**
 * A fragment to pratice using this shit
 *
 * I know these classes are deprecated, but I will learn to use them and then move to camera2
 */
class CameraActivityFragment() : Fragment() {

    var TAG = javaClass.canonicalName
    var hasFrontCamera = false
    var hasAnyCamera = false

    var INVALID_CAMERA_ID = -1
    var frontCameraId = INVALID_CAMERA_ID
    var backCameraId = INVALID_CAMERA_ID

    var selectedCameraId = INVALID_CAMERA_ID
    var selectedCamera: Camera? = null
    lateinit var menu: Menu
    val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 30

    val SAVE_INSTANCE_CAM_ID = "SAVE_INSTANCE_CAM_ID"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermissions()
        }

        val packageManager: PackageManager = activity!!.packageManager
        hasAnyCamera = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
        hasFrontCamera = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)

        if (!hasAnyCamera) {
            showCameraUnavailableDialog()
        }

        snap_photo.setOnClickListener {
            takePhoto()
        }

        setHasOptionsMenu(true)
    }

    private fun takePhoto() {
        selectedCamera!!.takePicture(null, null, object : Camera.PictureCallback {
            override fun onPictureTaken(p0: ByteArray?, p1: Camera?) {

                val file = CameraHelper.generateTimeStampPhotoFile()

                try {
                    var outputStream: OutputStream = BufferedOutputStream(FileOutputStream(file))
                    outputStream.write(p0)
                    outputStream.flush()
                    outputStream.close()

                } catch (e: Exception) {
                    e.printStackTrace()
                }


                activity!!.sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED,
                        Uri.parse("file:///${Environment.getExternalStorageDirectory()}")))

                selectedCamera!!.startPreview()
            }
        })
    }

    private fun requestCameraPermissions() {
        ActivityCompat.requestPermissions(activity!!,
                arrayOf(Manifest.permission.CAMERA),
                MY_PERMISSIONS_REQUEST_READ_CONTACTS)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_camera, menu)
        super.onCreateOptionsMenu(menu, inflater)
        this.menu = menu
        enableCamera(menu, hasAnyCamera)
        enableFrontCamera(menu, hasFrontCamera)
    }

    private fun enableFrontCamera(menu: Menu, enable: Boolean) {
        menu.findItem(R.id.action_front_camera).isEnabled = false
    }

    private fun enableCamera(menu: Menu, enable: Boolean) {
        menu.findItem(R.id.action_front_camera).isEnabled = enable
        menu.findItem(R.id.action_back_camera).isEnabled = enable
        menu.findItem(R.id.close_camera).isEnabled = enable
    }

    override fun onResume() {
        super.onResume()

        openSelectedCamera()
    }

    override fun onPause() {
        super.onPause()
        releaseSelectedCamera()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_front_camera -> {
                openFrontCamera()
                return true
            }
            R.id.action_back_camera -> {
                openBackCamera()
                return true
            }
            R.id.close_camera -> {
                closeCamera()
                return true
            }
        }

        return false
    }

    private fun showCameraUnavailableDialog() {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle("No Camera Available")
        alertDialogBuilder.setMessage("This device doesn't have a camera attached")
        alertDialogBuilder.show()
    }

    private fun openBackCamera() {
        selectedCameraId = getBackFacingCameraID()
        openSelectedCamera()
    }

    private fun openFrontCamera() {
        selectedCameraId = getFrontFacingCameraID()
        openSelectedCamera()
    }

    private fun closeCamera() {
        releaseSelectedCamera()
    }

    private fun getFrontFacingCameraID(): Int {
        if (frontCameraId == INVALID_CAMERA_ID)
            frontCameraId = getCameraFacingId(Camera.CameraInfo.CAMERA_FACING_FRONT)

        return frontCameraId
    }


    private fun getBackFacingCameraID(): Int {
        if (backCameraId == INVALID_CAMERA_ID)
            backCameraId = getCameraFacingId(Camera.CameraInfo.CAMERA_FACING_BACK)

        return backCameraId
    }

    private fun getCameraFacingId(cameraFacing: Int): Int {
        var cameraInfo = Camera.CameraInfo()
        var cameraId = INVALID_CAMERA_ID

        var cameraCount = Camera.getNumberOfCameras()

        for (camera in 0..cameraCount) {
            Camera.getCameraInfo(camera, cameraInfo)
            if (cameraFacing == cameraInfo.facing) {
                cameraId = camera
                break
            }
        }

        return cameraId
    }


    private fun openSelectedCamera() {
        releaseSelectedCamera()
        if (selectedCameraId != INVALID_CAMERA_ID) {
            try {
                selectedCamera = Camera.open(selectedCameraId)
                val params = selectedCamera!!.parameters
                params.focusMode = Camera.Parameters.FOCUS_MODE_AUTO
                selectedCamera!!.parameters = params
                camera_preview.startCamera(selectedCamera, selectedCameraId)
            } catch (e: Exception) {
                Log.d(TAG, "Error opening camera", e)
            }
        }
    }

    private fun releaseSelectedCamera() {
        if (selectedCamera != null) {
            camera_preview.releaseCamera()
            selectedCamera?.release()
            selectedCamera = null
        }
    }


}
