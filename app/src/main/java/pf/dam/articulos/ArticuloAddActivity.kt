package pf.dam.articulos

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import db.ArticulosSQLite
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
    private lateinit var guardarButton: Button
    private lateinit var volverButton: Button
    private lateinit var botonCamara: Button
    private lateinit var botonGaleria: Button
    private var imagenArticulo: Bitmap? = null
    private lateinit var imageView: ImageView
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2
    private val REQUEST_PERMISSION_CAMERA = 100
    private lateinit var estadoSpinner: Spinner

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articulo_add)

        dbHelper = ArticulosSQLite(this)
        nombreEditText = findViewById(R.id.nombreEditText)
        categoriaEditText = findViewById(R.id.categoriaEditText)
        tipoEditText = findViewById(R.id.tipoEditText)
        descripcionEditText = findViewById(R.id.descripcionEditText)

        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)
        botonCamara = findViewById(R.id.botonCamara)
        botonGaleria = findViewById(R.id.botonGaleria)
        imageView = findViewById(R.id.imageView)
        estadoSpinner = findViewById(R.id.estadoSpinner)

        val estados = arrayOf(EstadoArticulo.DISPONIBLE, EstadoArticulo.PRESTADO)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        estadoSpinner.adapter = adapter

        volverButton.setOnClickListener { finish() }

        botonCamara.setOnClickListener {
            dispatchTakePictureIntent()
        }

        botonGaleria.setOnClickListener {
            openGallery()
        }

        guardarButton.setOnClickListener {
            val categoria = categoriaEditText.text.toString()
            val tipo = tipoEditText.text.toString()
            val nombre = nombreEditText.text.toString()
            val descripcion = descripcionEditText.text.toString()
            val estadoSeleccionado = estadoSpinner.selectedItem as EstadoArticulo

            val rutaImagen = imagenArticulo?.let {
                val nombreArchivo = "articulo_${UUID.randomUUID()}"
                guardarImagenEnAlmacenamiento(it, nombreArchivo)
            }

            if (categoria.isBlank() || tipo.isBlank() || nombre.isBlank() || descripcion.isBlank() ) {
                Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                val nuevoArticulo = Articulo(categoria = categoria, tipo = tipo, nombre = nombre, descripcion = descripcion, estado = estadoSeleccionado, rutaImagen = rutaImagen)
                dbHelper.insertarArticulo(nuevoArticulo)

                Toast.makeText(this, "Artículo añadido", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            // Mostrar un mensaje de error si no hay aplicación de cámara
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY)
    }

    private fun guardarImagenEnAlmacenamiento(imagen: Bitmap, nombreArchivo: String): String? {
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
}