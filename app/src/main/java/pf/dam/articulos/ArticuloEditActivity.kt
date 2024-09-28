/*package pf.dam.articulos

import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import db.ArticulosSQLite
import pf.dam.R

class ArticuloEditActivity : AppCompatActivity() {

    private lateinit var dbHelper: ArticulosSQLite
    private lateinit var nombreEditText: EditText
    private lateinit var categoriaEditText: EditText
    private lateinit var tipoEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var estadoEditText: EditText
    private lateinit var imagenImageView: ImageView

    private lateinit var guardarButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articulo_edit)

        dbHelper = ArticulosSQLite(this)
        nombreEditText = findViewById(R.id.nombreEditText)
        categoriaEditText = findViewById(R.id.categoriaEditText)
        tipoEditText = findViewById(R.id.tipoEditText)
        descripcionEditText = findViewById(R.id.descripcionEditText)
        estadoEditText = findViewById(R.id.estadoEditText)
        imagenImageView = findViewById(R.id.imagenImageView)
        guardarButton = findViewById(R.id.guardarButton)

        val articuloId = intent.getIntExtra("articuloId", -1)
        val articulo = dbHelper.obtenerArticuloPorId(articuloId)

        if (articulo != null) {
            nombreEditText.setText(articulo.nombre)
            categoriaEditText.setText(articulo.categoria)
            tipoEditText.setText(articulo.tipo)
            descripcionEditText.setText(articulo.descripcion)
            estadoEditText.setText(articulo.estado)
            if (articulo.rutaImagen != null) {
                val imagenBitmap = BitmapFactory.decodeFile(articulo.rutaImagen)
                imagenImageView.setImageBitmap(imagenBitmap)
            }

            guardarButton.setOnClickListener {
                val articuloActualizado = Articulo(
                  //  articulo.idArticulo,
                    categoriaEditText.text.toString(),
                    tipoEditText.text.toString(),
                    nombreEditText.text.toString(),
                    descripcionEditText.text.toString(),
                    estadoEditText.text.toString(),
                    articulo.rutaImagen
                )
                dbHelper.actualizarArticulo(articuloId, articuloActualizado)
                Toast.makeText(this, "Artículo actualizado", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        } else {
            Toast.makeText(this, "Artículo no encontrado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}*/

package pf.dam.articulos

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import db.ArticulosSQLite
import pf.dam.R
import java.io.File
import java.io.FileOutputStream
import java.util.*

class ArticuloEditActivity : AppCompatActivity() {

    private lateinit var dbHelper: ArticulosSQLite
    private lateinit var nombreEditText: EditText
    private lateinit var categoriaEditText: EditText
    private lateinit var tipoEditText: EditText
    private lateinit var descripcionEditText: EditText
    private lateinit var imagenImageView: ImageView
    private lateinit var guardarButton: Button
    private lateinit var volverButton: Button

    private lateinit var botonCamara: Button
    private lateinit var botonGaleria: Button
    private lateinit var estadoSpinner: Spinner

    private var articuloId: Int = -1
    private lateinit var articulo: Articulo
    private var imagenArticulo: Bitmap? = null
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_IMAGE_GALLERY = 2
    private val REQUEST_PERMISSION_CAMERA = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articulo_edit)

        dbHelper = ArticulosSQLite(this)
        nombreEditText = findViewById(R.id.nombreEditText)
        categoriaEditText = findViewById(R.id.categoriaEditText)
        tipoEditText = findViewById(R.id.tipoEditText)
        descripcionEditText = findViewById(R.id.descripcionEditText)
        imagenImageView = findViewById(R.id.imagenImageView)
        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)
        botonCamara = findViewById(R.id.botonCamara)
        botonGaleria = findViewById(R.id.botonGaleria)
        estadoSpinner = findViewById(R.id.estadoSpinner)

        articuloId = intent.getIntExtra("articuloId", -1)
        articulo = dbHelper.obtenerArticuloPorId(articuloId)!!

        if (articulo.rutaImagen != null) {
            try {
                val imagenBitmap = BitmapFactory.decodeFile(articulo.rutaImagen)
                imagenImageView.setImageBitmap(imagenBitmap)
            } catch (e: Exception) {
                Log.e("ArticuloEditActivity", "Error al cargar la imagen", e)
                // Mostrar una imagen por defecto si hay un error
            }
        }

        nombreEditText.setText(articulo.nombre)
        categoriaEditText.setText(articulo.categoria)
        tipoEditText.setText(articulo.tipo)
        descripcionEditText.setText(articulo.descripcion)

        // Configurar el adaptador del Spinner
        val estados = arrayOf(EstadoArticulo.DISPONIBLE, EstadoArticulo.PRESTADO)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        estadoSpinner.adapter = adapter

        // Establecer la selección inicial del Spinner
        val estadoIndex = estados.indexOf(articulo.estado)
        estadoSpinner.setSelection(if (estadoIndex >= 0) estadoIndex else 0)

        guardarButton.setOnClickListener {
            val nuevaRutaImagen = imagenArticulo?.let {
                val nombreArchivo = "articulo_${UUID.randomUUID()}"
                guardarImagenEnAlmacenamiento(it, nombreArchivo)
            } ?: articulo.rutaImagen

            val estadoSeleccionado = estadoSpinner.selectedItem as EstadoArticulo
            val articuloActualizado = Articulo(
                categoriaEditText.text.toString(),
                tipoEditText.text.toString(),
                nombreEditText.text.toString(),
                descripcionEditText.text.toString(),
                estadoSeleccionado,
                nuevaRutaImagen
            )
            dbHelper.actualizarArticulo(articuloId, articuloActualizado)
            Toast.makeText(this, "Artículo actualizado", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }
        volverButton.setOnClickListener { finish() }
        botonCamara.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_PERMISSION_CAMERA
                )
            } else {
                dispatchTakePictureIntent()
            }
        }

        botonGaleria.setOnClickListener {
            openGallery()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: ActivityNotFoundException) {
            Log.e("ArticuloEditActivity", "No se encontró la aplicación de cámara", e)
            Toast.makeText(this, "No se encontró la aplicación de cámara", Toast.LENGTH_SHORT).show()
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
            Log.e("ArticuloEditActivity", "Error al guardar la imagen", e)
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show()
            null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAPTURE -> {
                    imagenArticulo = data?.extras?.get("data") as Bitmap
                    imagenImageView.setImageBitmap(imagenArticulo)
                }
                REQUEST_IMAGE_GALLERY -> {
                    val imageUri = data?.data
                    imagenArticulo = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    imagenImageView.setImageBitmap(imagenArticulo)
                }
            }
        }
    }
}