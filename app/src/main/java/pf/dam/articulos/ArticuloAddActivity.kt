package pf.dam.articulos

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import pf.dam.MainActivity
import pf.dam.R
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ArticuloAddActivity : AppCompatActivity() {
    private lateinit var dbHelper: ArticulosSQLite
    private lateinit var nombreEditText: EditText
    private lateinit var categoriaEditText: EditText
    private lateinit var tipoEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var guardarButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton
    private lateinit var botonCamara: Button
    private lateinit var botonGaleria: Button
    private var imagenArticulo: Bitmap? = null
    private lateinit var imageView: ImageView
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2
    private lateinit var estadoCheckBox: CheckBox
    private val CODIGO_SOLICITUD_PERMISOS = 1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.articulo_add_activity)
        supportActionBar?.title = "RR - Artículo nuevo"

        dbHelper = ArticulosSQLite(this)
        nombreEditText = findViewById(R.id.nombreEditText)
        categoriaEditText = findViewById(R.id.categoriaEditText)
        tipoEditText = findViewById(R.id.tipoEditText)
        descripcionEditText = findViewById(R.id.descripcionEditText)
        guardarButton = findViewById(R.id.guardarButton)
        homeButton = findViewById(R.id.homeButton)
        volverButton = findViewById(R.id.volverButton)
        botonCamara = findViewById(R.id.botonCamara)
        botonGaleria = findViewById(R.id.botonGaleria)
        imageView = findViewById(R.id.imageView)
        estadoCheckBox = findViewById(R.id.estadoCheckBox)

        if (estadoCheckBox.isChecked) EstadoArticulo.DISPONIBLE else EstadoArticulo.NO_DISPONIBLE

        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        volverButton.setOnClickListener { finish() }

        botonCamara.setOnClickListener {
            if (tienePermisos()) {
                tomarFoto()
            } else {
                solicitarPermisos()
            }
        }

        botonGaleria.setOnClickListener {
            if (tienePermisos()) {
                abrirGaleria()
            } else {
                solicitarPermisos()
            }
        }

        guardarButton.setOnClickListener {
            val categoria = categoriaEditText.text.toString()
            val tipo = tipoEditText.text.toString()
            val nombre = nombreEditText.text.toString()
            val descripcion = descripcionEditText.text.toString()
            val estado = if (estadoCheckBox.isChecked) EstadoArticulo.DISPONIBLE else EstadoArticulo.NO_DISPONIBLE

            val rutaImagen = imagenArticulo?.let {
                val nombreArchivo = "articulo_${UUID.randomUUID()}"
                guardarImagen(it, nombreArchivo)
            }

            if (categoria.isBlank() || tipo.isBlank() || nombre.isBlank() || descripcion.isBlank()) {
                Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                val nuevoArticulo = Articulo(categoria = categoria, tipo = tipo, nombre = nombre, descripcion = descripcion, estado = estado, rutaImagen = rutaImagen)
                dbHelper.addArticulo(nuevoArticulo)

                Toast.makeText(this, "Artículo añadido", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun tomarFoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // Manejar el caso en que no haya una actividad de cámara disponible
        }
    }

    private fun abrirGaleria() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY)
    }

    private fun guardarImagen(imagen: Bitmap, nombreArchivo: String): String? {
        val directorio = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val archivo = File(directorio, "$nombreArchivo.jpg")

        return try {
            val outputStream = FileOutputStream(archivo)
            imagen.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            archivo.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    imagenArticulo = imageBitmap
                    imageView.setImageBitmap(imagenArticulo)
                }
                REQUEST_IMAGE_GALLERY -> {
                    val imageUri = data?.data
                    imagenArticulo = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    imageView.setImageBitmap(imagenArticulo)
                }
            }
        }
    }

    private fun tienePermisos(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
    }

    private fun solicitarPermisos() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            CODIGO_SOLICITUD_PERMISOS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CODIGO_SOLICITUD_PERMISOS) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permisos concedidos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permisos denegados", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
