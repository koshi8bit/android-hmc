package com.koshi8bit.hmc

import android.R.attr.label
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    private var textView_qrs: TextView? = null
    private var only_receipt_format: CheckBox? = null
    val QR_SCAN_INDENT = 10


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView_qrs = findViewById(R.id.textView_qrs)
        only_receipt_format = findViewById(R.id.only_receipt_format)
    }

    fun onButtonClick(view: View?) {
        // barcodeLauncher.launch(ScanOptions())
        val intent = Intent(this, ContinuousCaptureActivity::class.java)
        intent.putExtra("only_receipt_format", only_receipt_format?.isChecked)
        startActivityForResult(intent, QR_SCAN_INDENT)
    }

    private fun copyToClipboard(text: CharSequence){
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label",text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, getString(R.string.copyed), Toast.LENGTH_LONG).show()
    }

    fun onCopyClick(view: View?) {
        copyToClipboard(textView_qrs?.text ?: "")
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // check that it is the SecondActivity with an OK result
        if (requestCode == QR_SCAN_INDENT) {
            if (resultCode == RESULT_OK) {
                val returnString = data!!.getStringExtra(Intent.EXTRA_TEXT)
                textView_qrs?.text = returnString
                copyToClipboard(textView_qrs?.text ?: "")
            }
        }
    }

}