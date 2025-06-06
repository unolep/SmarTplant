package com.tflite.DeteksipenyakittanamanVGG16

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_pendeteksi.*

class Pendeteksi : AppCompatActivity() {

    private lateinit var classifier: Klasifikasi
    //mendeklarasikan komponen TextView resultbar yang akan dimanipulasi
    private lateinit var resultbar: TextView
    private lateinit var processtime: TextView
    //deklarasi variabel lastprocessingtimems bertipe data long
    private var lastProcessingTimeMs: Long = 0

    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pendeteksi)

        //mengganti nama title bar tiap activity
        if (supportActionBar != null) {
            (supportActionBar as ActionBar).title = "Pendeteksi Penyakit Tanaman"
        }

        //obyek TextView resultbar disesuaikan (cast) dengan komponen TextView ber-ID result_bar di layout activity_pendeteksi.xml melalui metode findViewById().
        resultbar = findViewById(R.id.result_bar)
        processtime = findViewById(R.id.process_time_bar)

        classifier = Klasifikasi(assets)

        if (!canUseCamera()) {
            requestCameraPermissions()
        } else {
            setupCamera()
        }
    }

    private fun requestCameraPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CAMERA_CODE
        )
    }

    @SuppressLint("MissingPermission", "SetTextI18n")
    private fun setupCamera() {
        camera.addPictureTakenListener {
            AsyncTask.execute {
                val startTime = SystemClock.uptimeMillis()//menghitung waktu awal
                val recognitions = classifier.recognize(it.data)
                val txt = recognitions.joinToString(separator = "\n")
                lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime//menghitung lamanya proses
                val waktu = lastProcessingTimeMs.toString()//konversi ke string
                runOnUiThread {
                    /*menampilkan hasil pada UI*/
                    //Toast.makeText(this, txt, Toast.LENGTH_LONG).show()
                    /*menampilkan hasil pada layout TextView*/
                    resultbar.text = txt
                    processtime.text = "$waktu ms "
                }
            }
        }

        capturePhoto.setOnClickListener {
            camera.capture()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (REQUEST_CAMERA_CODE == requestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupCamera()
            } else {
                Toast.makeText(this, "App needs camera in order to work.", Toast.LENGTH_LONG).show()
                requestCameraPermissions()
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        if (canUseCamera()) {
            camera.start()
        }
    }

    override fun onPause() {
        if (canUseCamera()) {
            camera.stop()
        }
        super.onPause()
    }

    override fun onDestroy() {
        if (canUseCamera()) {
            camera.destroy()
        }
        super.onDestroy()
    }

    private fun canUseCamera() =
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    companion object {
        private const val REQUEST_CAMERA_CODE = 1
    }
}
