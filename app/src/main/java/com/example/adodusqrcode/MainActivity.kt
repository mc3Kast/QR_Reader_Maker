package com.example.adodusqrcode

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class MainActivity : AppCompatActivity() {

    var im: ImageView? = null
    var bGenerate: Button? = null
    var shareButton: FloatingActionButton? = null
    var edText: EditText? = null
    var btScanner: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        im = findViewById(R.id.imageView)
        bGenerate = findViewById(R.id.button)
        shareButton = findViewById(R.id.btShare)
        edText = findViewById(R.id.edText)
        btScanner = findViewById(R.id.btScanner)
        shareButton?.isEnabled = false
        shareButton?.isClickable = false
        btScanner?.setOnClickListener {
            val intent = Intent(this, Scanner::class.java)
            startActivity(intent)
        }
        bGenerate?.setOnClickListener{
            generateQrCode()
        }
        shareButton?.setOnClickListener{
                shareBitmap(QRGEncoder(edText?.text.toString(), null, QRGContents.Type.TEXT, 500).getBitmap())
        }
    }

    private fun generateQrCode(){
        val qrgEncoder = QRGEncoder(edText?.text.toString(), null, QRGContents.Type.TEXT, 500)
        qrgEncoder.setColorBlack(Color.RED)
        qrgEncoder.setColorWhite(Color.BLUE)
        try {
            // Getting QR-Code as Bitmap
            val bitmap = qrgEncoder.getBitmap()
            // Setting Bitmap to ImageView
            im?.setImageBitmap(bitmap)
            shareButton?.isEnabled = true
            shareButton?.isClickable = true
        } catch (e: Exception) {

        }

    }

    private fun shareBitmap(bitmap: Bitmap) {
        //---Save bitmap to external cache directory---//
        //get cache directory
        val cachePath = File(externalCacheDir, "my_images/")
        cachePath.mkdirs()

        //create png file
        val file = File(cachePath, "Image_123.png")
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //---Share File---//
        //get file uri
        val myImageFileUri: Uri =
            FileProvider.getUriForFile(this, applicationContext.packageName + ".provider", file)

        //create a intent
        val intent = Intent(Intent.ACTION_SEND)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.putExtra(Intent.EXTRA_STREAM, myImageFileUri)
        intent.type = "image/png"
        startActivity(Intent.createChooser(intent, "Share with"))
    }
}