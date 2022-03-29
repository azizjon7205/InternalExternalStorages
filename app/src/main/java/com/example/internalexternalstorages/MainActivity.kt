package com.example.internalexternalstorages

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.io.*
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    var isPersistent = true
    var isInternal = false

    var readPermissionGranted = false
    var writePermissionGranted = false

    private lateinit var b_get_permission: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

        requestPermissions()
        checkStoragePaths()
        createInternalFile()
    }

    private fun initViews() {
        val b_save_internal: Button = findViewById(R.id.b_save_internal)
        val b_read_internal: Button = findViewById(R.id.b_read_internal)
        val b_delete_internal: Button = findViewById(R.id.b_delete_internal)
        val b_save_external: Button = findViewById(R.id.b_save_external)
        val b_read_external: Button = findViewById(R.id.b_read_external)
        val b_delete_external: Button = findViewById(R.id.b_delete_external)
        val b_take_photo: Button = findViewById(R.id.b_take_photo)
        b_get_permission = findViewById(R.id.b_get_permission)

        val tv_internal: TextView = findViewById(R.id.tv_internal)
        val tv_external: TextView = findViewById(R.id.tv_external)

        b_save_internal.setOnClickListener {
            saveInternalFile("I study at PDP Academy!")
        }
        b_read_internal.setOnClickListener {
            readInternalStorage()
        }
        b_delete_internal.setOnClickListener {
            deleteInternalFile()
        }

        b_save_external.setOnClickListener {
            saveExternalFile("I am a senior developer")
        }
        b_read_external.setOnClickListener {
            readExternalStorage()
        }
        b_delete_external.setOnClickListener {
            deleteExternalFile()
        }

        b_take_photo.setOnClickListener {
            takePhoto.launch()
        }

        b_get_permission.setOnClickListener {
            callAppSettings()
        }
    }

    private fun checkStoragePaths() {
        val internal_m1 = getDir("custom", 0)
        val internal_m2 = filesDir

        val external_m1 = getExternalFilesDir(null)
        val external_m2 = externalCacheDir
        val external_m3 = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        Log.d("StorageActivity", internal_m1.absolutePath)
        Log.d("StorageActivity", internal_m2.absolutePath)
        Log.d("StorageActivity", external_m1!!.path)
        Log.d("StorageActivity", external_m2!!.absolutePath)
        Log.d("StorageActivity", external_m3!!.absolutePath)

    }

    private fun createInternalFile() {
        val fileName = "pdp_internal.txt"
        val file: File

        file = if (isPersistent) {
            File(filesDir, fileName)
        } else {
            File(cacheDir, fileName)
        }

        if (!file.exists()) {
            try {
                file.createNewFile()
                Toast.makeText(this,
                    String.format("File %s has been created", fileName),
                    Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Toast.makeText(this,
                    String.format("File %s creation failed", fileName),
                    Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this,
                String.format("File %s already exist", fileName),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveInternalFile(data: String) {
        val fileName = "pdp_internal.txt"
        try {
            val fileOutputStream: FileOutputStream
            fileOutputStream = if (isPersistent) {
                openFileOutput(fileName, MODE_PRIVATE)
            } else {
                val file = File(cacheDir, fileName)
                FileOutputStream(file)
            }
            fileOutputStream.write(data.toByteArray(Charset.forName("UTF-8")))
            Toast.makeText(this,
                String.format("Write to %s successful", fileName),
                Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this,
                String.format("Write to file %s failed", fileName),
                Toast.LENGTH_SHORT).show()

        }
    }

    private fun readInternalStorage() {
        val fileName = "pdp_internal.txt"
        try {
            val fileInputStream: FileInputStream
            fileInputStream = if (isPersistent) {
                openFileInput(fileName)
            } else {
                val file = File(cacheDir, fileName)
                FileInputStream(file)
            }
            val inputStreamReader = InputStreamReader(fileInputStream, Charset.forName("UTF-8"))
            val lines: MutableList<String> = ArrayList()
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()
            while (line != null) {
                lines.add(line)
                line = reader.readLine()
            }
            val readText = TextUtils.join("\n", lines)
            Log.d("StorageActivity", "Result Internal-> $readText")
            Toast.makeText(this,
                String.format("Read from file %s successful", fileName),
                Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this,
                String.format("Read from file %s failed", fileName),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteInternalFile() {
        val fileName = "pdp_internal.txt"
        val file = if (isPersistent) File(filesDir, fileName) else File(cacheDir, fileName)
        if (file.exists()) {
            if (file.delete())
                Toast.makeText(this, "Deleting internalFile successful", Toast.LENGTH_SHORT).show()
            else Toast.makeText(this, "Deleting internalFile unsuccessful", Toast.LENGTH_SHORT).show()
        } else
            Toast.makeText(this, "InternalFile doesn`t exist", Toast.LENGTH_SHORT).show()

    }

    private fun saveExternalFile(data: String) {
        val fileName = "pdp_internal.txt"
        val file: File
        file = if (isPersistent) {
            File(getExternalFilesDir(null), fileName)
        } else {
            File(externalCacheDir, fileName)
        }

        try {
            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(data.toByteArray(Charset.forName("UTF-8")))
            Toast.makeText(this,
                String.format("Write to %s successful", fileName),
                Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this,
                String.format("Write to file %s failed", fileName),
                Toast.LENGTH_SHORT).show()

        }
    }

    private fun readExternalStorage() {
        val fileName = "pdp_internal.txt"
        try {
            val file = if (isPersistent) {
                File(getExternalFilesDir(null), fileName)
            } else {
                File(cacheDir, fileName)
            }
            val fileInputStream = FileInputStream(file)
            val inputStreamReader = InputStreamReader(fileInputStream, Charset.forName("UTF-8"))
            val lines: MutableList<String> = ArrayList()
            val reader = BufferedReader(inputStreamReader)
            var line = reader.readLine()
            while (line != null) {
                lines.add(line)
                line = reader.readLine()
            }
            val readText = TextUtils.join("\n", lines)
            Log.d("StorageActivity", "Result External-> $readText")
            Toast.makeText(this,
                String.format("Read from file %s successful", fileName),
                Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this,
                String.format("Read from file %s failed", fileName),
                Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteExternalFile() {
        val fileName = "pdp_internal.txt"
        val file = if (isPersistent) {
            File(getExternalFilesDir(null), fileName)
        } else {
            File(cacheDir, fileName)
        }
        if (file.exists()) {
            if (file.delete())
                Toast.makeText(this, "Deleting ExternalFile successful", Toast.LENGTH_SHORT).show()
            else Toast.makeText(this, "Deleting ExternalFile unsuccessful", Toast.LENGTH_SHORT).show()
        } else
            Toast.makeText(this, "ExternalFile doesn`t exist", Toast.LENGTH_SHORT).show()

    }

    private fun callAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri: Uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun requestPermissions() {
        val hasReadPermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val hasWritePermission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val minSdk29 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        readPermissionGranted = hasReadPermission
        writePermissionGranted = hasWritePermission || minSdk29

        val permissionsToRequest = mutableListOf<String>()
        if (!readPermissionGranted) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (!writePermissionGranted) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissionsToRequest.isNotEmpty()) {
            permissionlauncher.launch(permissionsToRequest.toTypedArray())
        }
    }

    private val permissionlauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            readPermissionGranted =
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] ?: readPermissionGranted
            writePermissionGranted =
                permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: writePermissionGranted

            if (readPermissionGranted) Toast.makeText(this,
                "READ_EXTERNAL_STORAGE",
                Toast.LENGTH_SHORT).show()
            if (writePermissionGranted){
                b_get_permission.visibility = View.GONE
                Toast.makeText(this, "WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show()
            }
            else{
                b_get_permission.visibility = View.VISIBLE
                Toast.makeText(this, "WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val takePhoto = registerForActivityResult(ActivityResultContracts.TakePicturePreview()){ bitmap ->
        val fileName = UUID.randomUUID().toString()

        val isPhotoSaved = if (isInternal){
            savePhotoToInternal(fileName, bitmap!!)
        } else{
            if (writePermissionGranted){
                savePhotoToExternalStorage(fileName, bitmap!!)
            }else{
                false
            }
        }
        if (isPhotoSaved){
            Toast.makeText(this, "Photo saved successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Photo saved successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun savePhotoToExternalStorage(fileName: String, bitmap: Bitmap): Boolean {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else{
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "$fileName.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "images/jpeg")
            put(MediaStore.Images.Media.WIDTH, bitmap.width)
            put(MediaStore.Images.Media.HEIGHT, bitmap.height)
        }

        return try {
            contentResolver.insert(collection, contentValues)?.also { uri ->
                contentResolver.openOutputStream(uri).use { outputStream ->
                    if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)){
                        throw IOException("Couldn't save bitmap")
                    }
                }
            } ?: throw IOException("Couldn't create MediaStore entry")
            true
        } catch (e: IOException){
            e.printStackTrace()
            false
        }
    }

    private fun savePhotoToInternal(fileName: String, bitmap: Bitmap): Boolean {
        return try {
            openFileOutput("$fileName.jpg", MODE_PRIVATE).use { stream ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 95, stream)){
                    throw IOException("Couldn't save bitmap.")
                }
            }
            true
        } catch (e: IOException){
            e.printStackTrace()
            false
        }
    }

    override fun onResume() {
        super.onResume()
        writePermissionGranted = ContextCompat.checkSelfPermission(this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        if (writePermissionGranted){
            b_get_permission.visibility = View.GONE
//            Toast.makeText(this, "WRITE_EXTERNAL_STORAGE", Toast.LENGTH_SHORT).show()
        }
        else{
            b_get_permission.visibility = View.VISIBLE
//            Toast.makeText(this, "WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT).show()
        }
    }
}