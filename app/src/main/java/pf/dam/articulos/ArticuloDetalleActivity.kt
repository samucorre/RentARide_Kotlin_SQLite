package pf.dam.articulos

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import db.PrestamosSQLite
import pf.dam.MainActivity
import pf.dam.R
import pf.dam.prestamos.PrestamoAddArticuloActivity
import pf.dam.socios.Socio
import pf.dam.utils.DialogoUtil


class ArticuloDetalleActivity : AppCompatActivity() {
    private lateinit var editArticuloButton: FloatingActionButton
    private lateinit var deleteArticuloButton: FloatingActionButton
    private lateinit var backButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton
    private lateinit var addPrestamoButton: Button
    private lateinit var dbArticulos: ArticulosSQLite
    private lateinit var dbPrestamos: PrestamosSQLite
    private lateinit var nombreTextView: TextView
    private lateinit var categoriaTextView: TextView
    private lateinit var tipoTextView: TextView
    private lateinit var descripcionTextView: TextView
    private lateinit var estadoTextView: TextView
    private lateinit var imagenImageView: ImageView
    private lateinit var socioUsandoTextView: TextView
    private lateinit var cantidadPrestamosTextView: TextView
    private lateinit var sociosTextView: TextView

    private var articuloId: Int = -1

    companion object {
        const val REQUEST_ADD_PRESTAMO = 1
    }

    private val editArticuloLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val dbHelper = ArticulosSQLite(this)
            val articuloActualizado = dbHelper.getArticuloById(articuloId)
            if (articuloActualizado != null) {
                mostrarArticulo(articuloActualizado)
            }
        }
    }
    private val addPrestamoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Actualiza la vista del artículo
            val articuloActualizado = dbArticulos.getArticuloById(articuloId)
            if (articuloActualizado != null) {
                mostrarArticulo(articuloActualizado)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.articulo_detail_activity)
        supportActionBar?.title = "RR - Artículo detalle"

        dbArticulos = ArticulosSQLite(this)
        dbPrestamos = PrestamosSQLite(this)

        editArticuloButton = findViewById(R.id.editArticuloButton)
        deleteArticuloButton = findViewById(R.id.deleteArticuloButton)
        addPrestamoButton = findViewById(R.id.addPrestamoButton)
        homeButton = findViewById(R.id.homeButton)
        backButton = findViewById(R.id.backButton)
        nombreTextView = findViewById(R.id.nombreTextView)
        categoriaTextView = findViewById(R.id.categoriaTextView)
        tipoTextView = findViewById(R.id.tipoTextView)
        descripcionTextView = findViewById(R.id.descripcionTextView)
        estadoTextView = findViewById(R.id.estadoTextView)
        imagenImageView = findViewById(R.id.imagenImageView)
        socioUsandoTextView = findViewById(R.id.socioUsandoTextView)
        cantidadPrestamosTextView = findViewById(R.id.cantidadPrestamosTextView)
        sociosTextView = findViewById(R.id.sociosTextView)

        articuloId = intent.getIntExtra("idArticulo", -1)
        val articulo = dbArticulos.getArticuloById(articuloId)
        if (articulo != null) {
            mostrarArticulo(articulo)
            if (articulo.estado == EstadoArticulo.DISPONIBLE) {
                addPrestamoButton.visibility = View.VISIBLE
            } else {
                addPrestamoButton.visibility = View.GONE
            }

            backButton.setOnClickListener {
                finish()
            }

            addPrestamoButton.setOnClickListener {
                val intent = Intent(this, PrestamoAddArticuloActivity::class.java)
                intent.putExtra("idArticulo", articuloId)
                addPrestamoLauncher.launch(intent)
            }
            homeButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            editArticuloButton.setOnClickListener {
                val idArticulo = dbArticulos.getIdArticuloBd(articulo)
                if (idArticulo != -1) {
                    val articulo =
                        dbArticulos.getArticuloById(idArticulo)

                    if (articulo?.estado == EstadoArticulo.PRESTADO) {
                        Toast.makeText(
                            this,
                            "No se puede editar el artículo porque está prestado",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val intent = Intent(this, ArticuloEditActivity::class.java)
                        intent.putExtra("articuloId", articuloId)
                        editArticuloLauncher.launch(intent)
                    }
                }
            }

            deleteArticuloButton.setOnClickListener {
                val idArticulo = dbArticulos.getIdArticuloBd(articulo)
                if (idArticulo != -1) {
                    val articulo =
                        dbArticulos.getArticuloById(idArticulo)

                    if (articulo?.estado == EstadoArticulo.PRESTADO) {
                        Toast.makeText(
                            this,
                            "No se puede borrar el artículo porque está prestado",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else setContent {
                        var showDialog by remember { mutableStateOf(true) }

                        if (showDialog) {
                            DialogoUtil(this).ShowDeleteConfirmationDialog(
                                title = "Eliminar artículo",
                                message = "¿Estás seguro de que quieres eliminar este artículo?",
                                onPositiveButtonClick = {
                                    dbArticulos.deleteArticulo(articuloId)
                                    Toast.makeText(
                                        this@ArticuloDetalleActivity,
                                        "Artículo eliminado",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    setResult(RESULT_OK)
                                    finish()
                                    showDialog = false
                                },
                                onDismissRequest = {
                                    showDialog = false
                                    finish()
                                }
                            )
                        }
                    }
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun mostrarArticulo(articulo: Articulo) {
        nombreTextView.text = articulo.nombre ?: ""
        categoriaTextView.text = articulo.categoria ?: ""
        tipoTextView.text = articulo.tipo ?: ""
        descripcionTextView.text = articulo.descripcion ?: ""
        estadoTextView.text = articulo.estado?.name ?: ""
        if (articulo.rutaImagen != null && articulo.rutaImagen.isNotEmpty()) {
            try {
                val imagenBitmap = BitmapFactory.decodeFile(articulo.rutaImagen)
                imagenImageView.setImageBitmap(imagenBitmap)
            } catch (e: Exception) {
                Log.e("Error", "Error al cargar la imagen: ${e.message}")
                imagenImageView.setImageResource(R.drawable.ico_imagen)
            }
        } else {
            imagenImageView.setImageResource(R.drawable.ico_imagen)
        }

        val prestamos = articulo.idArticulo?.let { dbPrestamos.getPrestamosByArticulo(it) }

        val cantidadPrestamos = prestamos?.size

        val socios = mutableSetOf<Socio>()
        if (prestamos != null) {
            for (prestamo in prestamos) {
                val socio =
                    dbPrestamos.getPrestamoByIdSocio(prestamo.idSocio)
                if (socio != null) {
                    socios.add(socio)
                }
            }
        }
        cantidadPrestamosTextView.text = "Cantidad de préstamos: \t$cantidadPrestamos"
        if (articulo.estado == EstadoArticulo.PRESTADO) {
            val idSocio = articulo.idArticulo?.let { dbPrestamos.getIdSocioPrestamoActivo(it) }
            if (idSocio != null) {
                Log.d(
                    "ArticuloDetalleActivity",
                    "ID del socio que tiene el artículo prestado: $idSocio"
                )
            }
            val socio = idSocio?.let { dbPrestamos.getPrestamoByIdSocio(it) }
            val nombreSocio = socio?.nombre ?: "Socio no encontrado"
            val apellidoSocio = socio?.apellido ?: ""
            socioUsandoTextView.text = "Artículo prestado a: $nombreSocio $apellidoSocio"
            socioUsandoTextView.visibility = View.VISIBLE
        } else {
            socioUsandoTextView.visibility = View.GONE
        }
        sociosTextView.text = "Usado por los socios:\n \t${
            socios.distinctBy { it.idSocio }.takeLast(3)
                .joinToString(separator = "\n") { "${it.nombre} ${it.apellido}" }
        }"
        if (articulo.estado == EstadoArticulo.DISPONIBLE) {
            addPrestamoButton.visibility = View.VISIBLE
        } else {
            addPrestamoButton.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_ADD_PRESTAMO && resultCode == Activity.RESULT_OK) {
            val articuloActualizado = dbArticulos.getArticuloById(articuloId)
            if (articuloActualizado != null) {
                mostrarArticulo(articuloActualizado)

            }
        }
    }
}