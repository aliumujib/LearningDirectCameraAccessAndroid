package aliumujib.com.learningtodirectlyusetheandroidcamera

import android.app.AlertDialog
import android.support.v4.app.Fragment
import android.os.Bundle
import android.view.*


/**
 * A placeholder fragment containing a simple view.
 */
class CameraActivityFragment : Fragment() {


    val hasFrontCamera = false
    val hasAnyCamera = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_camera, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    fun enableFrontCamera(menu: Menu, enable: Boolean) {
        menu.findItem(R.id.action_front_camera).isEnabled = false
    }

    fun enableCamera(menu: Menu, enable: Boolean) {
        menu.findItem(R.id.action_front_camera).isEnabled = enable
        menu.findItem(R.id.action_back_camera).isEnabled = enable
        menu.findItem(R.id.close_camera).isEnabled = enable
    }

    fun showCameraUnavailableDialog() {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        alertDialogBuilder.setTitle("No Camera Available")
        alertDialogBuilder.setMessage("This device doesn't have a camera attached")
        alertDialogBuilder.show()
    }


}
