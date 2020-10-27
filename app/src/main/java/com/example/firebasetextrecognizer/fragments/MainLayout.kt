
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.firebasetextrecognizer.MainActivity
import com.example.firebasetextrecognizer.Permission
import com.example.firebasetextrecognizer.R
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import java.io.IOException

class MainLayout : Fragment() {
    private var imageView: ImageView? = null
    private var label: TextView? = null
    private var btnTakeSnap: Button? = null
    private var btnSelectImage: Button? = null
    private var permission: Permission? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_main_layout, container, false)
        permission = activity?.let { Permission(it) }
        imageView = view.findViewById(R.id.iv_image_holder)
        label = view.findViewById(R.id.tv_label)
        btnTakeSnap = view.findViewById(R.id.btn_take_snap)
        btnTakeSnap?.setOnClickListener(View.OnClickListener { takeSnapShot() })
        btnSelectImage = view.findViewById(R.id.btn_select_image)
        btnSelectImage?.setOnClickListener(View.OnClickListener {
            permission!!.grantAccess(R.string.dialog_msg_to_load_image)
            if (permission!!.isAccessGranted) {
                selectImageFromStorage()
            }
        })
        return view
    }

    /**
     * Method to take capture image from other camera app.
     */
    private fun takeSnapShot() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity!!.packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    /**
     * Method to choose any one image from internal or external storage.
     */
    private fun selectImageFromStorage() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/*"
        startActivityForResult(intent, READ_REQUEST_CODE)
    }

    /**
     * This method will be called if the image is captured using camera.
     * @param imageBitmap
     */
    private fun firebaseVisionImageFromBitmap(imageBitmap: Bitmap) {
        val image = FirebaseVisionImage.fromBitmap(imageBitmap)
        recognizeText(image)
    }

    /**
     * This method will be called if the image is selected from internal or external storage.
     * @param uri
     */
    private fun firebaseVisionImageFromFile(uri: Uri?) {
        val image: FirebaseVisionImage
        try {
            image = FirebaseVisionImage.fromFilePath(activity!!, uri!!)
            recognizeText(image)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    /**
     * This method is used to extract the text from the image
     * @param image
     */
    private fun recognizeText(image: FirebaseVisionImage) {
        val detector = FirebaseVision.getInstance()
                .onDeviceTextRecognizer
        detector.processImage(image)
                .addOnSuccessListener { firebaseVisionText -> processText(firebaseVisionText) }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    Toast.makeText(activity, "No text found", Toast.LENGTH_SHORT).show()
                }
    }

    /**
     * This method is used to process the text and send the result (text) to ResultLayout fragment
     * @param firebaseVisionText
     */
    private fun processText(firebaseVisionText: FirebaseVisionText) {
        if (firebaseVisionText.textBlocks.isEmpty()) {
            Toast.makeText(activity, "No text found or Text may not be clear", Toast.LENGTH_LONG).show()
        } else {
            var text = ""
            for (block in firebaseVisionText.textBlocks) {
                text = text + block.text + " "
            }
            val tag = (activity as MainActivity?)!!.tag_ResultLayout
            val fragment = activity!!.supportFragmentManager
                    .findFragmentByTag(tag) as ResultLayout?
            fragment!!.setResult(text)
            (activity as MainActivity?)!!.openResultLayout()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val extras = data?.extras
            val imageBitmap = extras!!["data"] as Bitmap
            label!!.visibility = View.INVISIBLE
            imageView!!.setImageBitmap(imageBitmap)
            firebaseVisionImageFromBitmap(imageBitmap)
        }
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            var uri: Uri? = null
            if (data != null) {
                uri = data.data
                label!!.visibility = View.INVISIBLE
                imageView!!.setImageURI(uri)
                firebaseVisionImageFromFile(uri)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permission!!.isAccessGranted = true
                } else {
                    permission!!.isAccessGranted = false
                }
                return
            }
        }
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val READ_REQUEST_CODE = 42
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100
    }
}