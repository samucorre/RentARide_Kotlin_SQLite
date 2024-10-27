package pf.dam.articulos

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
import androidx.compose.foundation.layout.add
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.text
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import db.PrestamosSQLite
import pf.dam.MainActivity
import pf.dam.R
import pf.dam.prestamos.PrestamoAddArticuloActivity
import pf.dam.socios.Socio
import pf.dam.utils.ShowDeleteConfirmationDialog


class ArticuloDetalleActivity : AppCompatActivity() {
    private lateinit var editArticuloButton: FloatingActionButton
    private lateinit var deleteArticuloButton: FloatingActionButton
    private lateinit var backButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton
    private lateinit var addPrestamoButton: Button
    private lateinit var dbHelper: ArticulosSQLite
    private lateinit var dbHelperPrestamos: PrestamosSQLite
    private lateinit var nombreTextView: TextView
    private lateinit var categoriaTextView: TextView
    private lateinit var tipoTextView: TextView
    private lateinit var descripcionTextView: TextView
    private lateinit var estadoTextView: TextView
    private lateinit var imagenImageView: ImageView
    private lateinit var cantidadPrestamosTextView: TextView
    private lateinit var sociosTextView: TextView

    private var articuloId: Int = -1

    private val editArticuloLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val dbHelper = ArticulosSQLite(this)
            val articuloActualizado= dbHelper.obtenerArticuloPorId(articuloId)
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
            val articuloActualizado = dbHelper.obtenerArticuloPorId(articuloId)
            if (articuloActualizado != null) {
                mostrarArticulo(articuloActualizado)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articulo_detail)
        supportActionBar?.title = "RR - Artículo detalle"

        dbHelper = ArticulosSQLite(this)
        dbHelperPrestamos = PrestamosSQLite(this)

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
        cantidadPrestamosTextView = findViewById(R.id.cantidadPrestamosTextView)
        sociosTextView = findViewById(R.id.sociosTextView)

        articuloId = intent.getIntExtra("idArticulo", -1)
        val articulo = dbHelper.obtenerArticuloPorId(articuloId)
        val prestamos = dbHelperPrestamos.obtenerPrestamosPorArticulo(articuloId)
        // Log.e("ArticuloDetalleActivity", "ArticuloId: $articuloId")
        if (articulo != null) {
            mostrarArticulo(articulo)



            if (articulo.estado == EstadoArticulo.DISPONIBLE) {
                addPrestamoButton.visibility = View.VISIBLE // Mostrar el botón
            } else {
                addPrestamoButton.visibility = View.GONE // Ocultar el botón
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
                val idArticulo = dbHelper.obtenerIdArticuloBD(articulo)
                if (idArticulo != -1) {
                    val articulo =
                        dbHelper.obtenerArticuloPorId(idArticulo) // Obtener el artículo por ID

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
                val idArticulo = dbHelper.obtenerIdArticuloBD(articulo)
                if (idArticulo != -1) {
                    val articulo =
                        dbHelper.obtenerArticuloPorId(idArticulo) // Obtener el artículo por ID

                    if (articulo?.estado == EstadoArticulo.PRESTADO) {
                        Toast.makeText(
                            this,
                            "No se puede borrar el artículo porque está prestado",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else setContent {
                        var showDialog by remember { mutableStateOf(true) }

                        if (showDialog) {
                            ShowDeleteConfirmationDialog(
                                title = "Eliminar artículo",
                                message = "¿Estás seguro de que quieres eliminar este artículo?",
                                onPositiveButtonClick = {
                                    dbHelper.borrarArticulo(articuloId)



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

            val editArticuloLauncher = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    setResult(RESULT_OK, Intent().putExtra("articuloId", articuloId))
                    finish()
                }
            }
        }
    }


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
        // Obtén los préstamos del artículo
        val prestamos = articulo.idArticulo?.let { dbHelperPrestamos.obtenerPrestamosPorArticulo(it) }

        // Obtén la cantidad de préstamos
        val cantidadPrestamos = prestamos?.size

        // Obtén los socios de los préstamos
        val socios = mutableSetOf<Socio>()
        if (prestamos != null) {
            for (prestamo in prestamos) {
                val socio = dbHelperPrestamos.obtenerSocioPrestamoId(prestamo.idSocio) // Usa dbHelperPrestamos para obtener el socio
                if (socio != null) {
                    socios.add(socio)
                }
            }
        }

        // Muestra la cantidad de préstamos y los socios en los TextViews
        cantidadPrestamosTextView.text = "Cantidad de préstamos: $cantidadPrestamos"
        sociosTextView.text = "Socios: ${socios.joinToString { "${it.nombre} ${it.apellido}\n" }}"

    }
}