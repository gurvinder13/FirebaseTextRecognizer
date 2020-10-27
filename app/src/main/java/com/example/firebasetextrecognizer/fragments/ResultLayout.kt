
import android.app.Dialog
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.firebasetextrecognizer.MainActivity
import com.example.firebasetextrecognizer.Permission
import com.example.firebasetextrecognizer.R
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

class ResultLayout : Fragment() {
    private var label: TextView? = null
    private var resultHolder: EditText? = null
    private var edtFileName: EditText? = null
    private var deleteBtn: ImageView? = null
    private var saveBtn: ImageView? = null
    private var permission: Permission? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_result_layout, container, false)
        (activity as MainActivity?)!!.tag_ResultLayout = tag
        permission = activity?.let { Permission(it) }
        label = view.findViewById(R.id.tv_label)
        resultHolder = view.findViewById(R.id.edt_result_holder)
        resultHolder?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.length == 0) {
                    label?.setVisibility(View.VISIBLE)
                } else {
                    label?.setVisibility(View.INVISIBLE)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        deleteBtn = view.findViewById(R.id.btn_delete)
        deleteBtn?.setOnClickListener(View.OnClickListener { clearData() })
        saveBtn = view.findViewById(R.id.btn_save)
        saveBtn?.setOnClickListener(View.OnClickListener {
            val msg = resultHolder?.getText().toString()
            if (!TextUtils.isEmpty(msg)) {
                permission!!.grantAccess(R.string.dialog_msg_to_save_text_file)
                if (permission!!.isAccessGranted) {
                    saveConfirmationDialog(msg)
                }
            }
        })
        return view
    }

    /**
     * A confirmation dialog which also sets the name of the file.
     * @param msg
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun saveConfirmationDialog(msg: String) {
        val dialog = activity?.let { Dialog(it) }
        dialog?.setContentView(R.layout.dialog_save_confirmation)
        edtFileName = dialog?.findViewById(R.id.edt_file_name)
        val button = dialog?.findViewById<Button>(R.id.btn_confirm_save)
        button?.setOnClickListener {
            val fileName = edtFileName?.getText().toString()
            if (!TextUtils.isEmpty(fileName)) {
                dialog.dismiss()
                saveInFile(msg, fileName)
            } else {
                edtFileName?.setError("file name cannot be empty")
            }
        }
        dialog?.setCancelable(true)
        dialog?.show()
    }

    /**
     * Storing the .txt file publicly in a particular folder in external storage.
     * @param msg
     * @param fileName
     */
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun saveInFile(msg: String, fileName: String) {
        try {
            val dir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "Text Recognition")
            if (!dir.exists()) {
                Toast.makeText(activity, "created", Toast.LENGTH_SHORT).show()
                dir.mkdirs()
            }
            if (dir.exists()) {
                val file = File(dir, "$fileName.txt")
                if (!file.exists()) {
                    file.createNewFile()
                    val writer = BufferedWriter(FileWriter(file, false))
                    writer.write(msg)
                    writer.close()
                    MediaScannerConnection.scanFile(activity, arrayOf(file.toString()), null, null)
                    Toast.makeText(activity, "Successfully Saved in Documents/Text Recognition/$fileName.txt", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(activity, "File already exists\nUnable to save data", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(activity, "Unable to create directory", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Clearing editText
     */
    private fun clearData() {
        resultHolder!!.setText("")
        label!!.visibility = View.VISIBLE
    }

    /**
     * Displaying the text from the image into an editText
     * @param msg
     */
    fun setResult(msg: String?) {
        label!!.visibility = View.INVISIBLE
        resultHolder!!.setText(msg)
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
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 100
    }
}