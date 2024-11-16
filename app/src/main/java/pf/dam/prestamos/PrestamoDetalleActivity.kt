package pf.dam.prestamos

import android.annotation.SuppressLint
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
import db.SociosSQLite
import pf.dam.MainActivity
import pf.dam.R
import pf.dam.articulos.EstadoArticulo
import pf.dam.utils.DialogoUtil
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrestamoDetalleActivity : AppCompatActivity() {
    private lateinit var editPrestamoButton: FloatingActionButton
    private lateinit var deletePrestamoButton: FloatingActionButton
    private lateinit var backButton: FloatingActionButton
    private lateinit var cerrarPrestamoButton: Button
    private lateinit var homeButton: FloatingActionButton
    private lateinit var prestamosDbHelper: PrestamosSQLite
    private lateinit var articulosDbHelper: ArticulosSQLite
    private lateinit var sociosDbHelper: SociosSQLite
    private lateinit var prestamoIdTextView: TextView
    private lateinit var datosArticuloTextView: TextView
    private lateinit var datos1ArticuloTextView: TextView
    private lateinit var datos2ArticuloTextView: TextView
    private lateinit var articuloImageView: ImageView
    private lateinit var datosSocioTextView: TextView
    private lateinit var datos1SocioTextView: TextView
    private lateinit var datos2SocioTextView: TextView
    private lateinit var fechaInicioTextView: TextView
    private lateinit var fechaFinTextView: TextView
    private lateinit var infoTextView: TextView
    private lateinit var estadoTextView: TextView

    private var prestamoId: Int = -1
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

//    private val editPrestamoLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == RESULT_OK) {
//            val prestamoActualizado = prestamosDbHelper.getPrestamoById(prestamoId)
//            if (prestamoActualizado != null) {
//                mostrarPrestamo(prestamoActualizado)
//            }
//        }
//    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.prestamo_detail_activity)
        supportActionBar?.title = "RR - Préstamo detalle"

        prestamosDbHelper = PrestamosSQLite(this)
        articulosDbHelper = ArticulosSQLite(this)
        sociosDbHelper = SociosSQLite(this)

        editPrestamoButton = findViewById(R.id.editPrestamoButton)
        deletePrestamoButton = findViewById(R.id.deletePrestamoButton)
        cerrarPrestamoButton = findViewById(R.id.cerrarPrestamoButton)
        backButton = findViewById(R.id.backButton)
        homeButton = findViewById(R.id.homeButton)
        prestamoIdTextView = findViewById(R.id.prestamoIdTextView)
        fechaInicioTextView = findViewById(R.id.fechaInicioTextView)
        fechaFinTextView = findViewById(R.id.fechaFinTextView)
        infoTextView = findViewById(R.id.infoTextView)
        estadoTextView = findViewById(R.id.estadoTextView)
        articuloImageView = findViewById(R.id.articuloImageView)
        datosArticuloTextView = findViewById(R.id.datosArticuloTextView)
        datos1ArticuloTextView = findViewById(R.id.datos1ArticuloTextView)
        datos2ArticuloTextView = findViewById(R.id.datos2ArticuloTextView)
        datosSocioTextView = findViewById(R.id.datosSocioTextView)
        datos1SocioTextView = findViewById(R.id.datos1SocioTextView)
        datos2SocioTextView = findViewById(R.id.datos2SocioTextView)



        prestamoId = intent.getIntExtra("idPrestamo", -1)
        val prestamo = prestamosDbHelper.getPrestamoById(prestamoId)

        if (prestamo != null) {
            mostrarPrestamo(prestamo)

            val editPrestamoLauncher = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    setResult(RESULT_OK, Intent().putExtra("prestamoId", prestamoId))
                    finish()
                }
            }

            if (prestamo.estado == EstadoPrestamo.CERRADO) {
                cerrarPrestamoButton.visibility = View.GONE
                editPrestamoButton.visibility = View.GONE
            } else {
                cerrarPrestamoButton.visibility = View.VISIBLE
                editPrestamoButton.visibility = View.VISIBLE
                cerrarPrestamoButton.setOnClickListener {
                    prestamo.estado = EstadoPrestamo.CERRADO
                    prestamo.fechaFin = Date()
                    prestamosDbHelper.updatePrestamo(prestamo)
                    articulosDbHelper.actualizarEstadoArticulo(
                        prestamo.idArticulo,
                        EstadoArticulo.DISPONIBLE
                    )

                    Toast.makeText(this, "Préstamo cerrado", Toast.LENGTH_SHORT).show()
                    cerrarPrestamoButton.visibility = View.GONE
                    mostrarPrestamo(prestamo)
                }
                editPrestamoButton.setOnClickListener {
                    val intent = Intent(this, PrestamoEditActivity::class.java)
                    intent.putExtra("prestamoId", prestamoId)
                    editPrestamoLauncher.launch(intent)
                }
            }

            homeButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }

            deletePrestamoButton.setOnClickListener {
                if (prestamo.estado == EstadoPrestamo.ACTIVO) {
                    Toast.makeText(
                        this,
                        "No se puede eliminar un préstamo en estado activo",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                } else {
                    setContent {
                        var showDialog by remember { mutableStateOf(true) }
                        if (showDialog) {
                            DialogoUtil(this).ShowDeleteConfirmationDialog(
                                title = "Eliminar préstamo",
                                message = "¿Estás seguro de que quieres eliminar este préstamo?",
                                onPositiveButtonClick = {
                                    prestamosDbHelper.deletePrestamo(prestamoId)
                                    articulosDbHelper.actualizarEstadoArticulo(
                                        prestamo.idArticulo,
                                        EstadoArticulo.DISPONIBLE
                                    )
                                    Toast.makeText(
                                        this@PrestamoDetalleActivity,
                                        "Préstamo eliminado",
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

            backButton.setOnClickListener {
                finish()
            }

        }
    }

    @SuppressLint("SetTextI18n")
    private fun mostrarPrestamo(prestamo: Prestamo) {
        val articulo = articulosDbHelper.getArticuloById(prestamo.idArticulo)
        val nombreArticulo = articulo?.nombre ?: "Artículo no encontrado"
        val categoriaArticulo = articulo?.categoria ?: ""
        val tipoArticulo = articulo?.tipo ?: ""
        val imagenArticulo = articulo?.rutaImagen ?: ""
        val socio = sociosDbHelper.getSocioById(prestamo.idSocio)
        val nombreSocio = socio?.nombre ?: "Socio no encontrado"
        val apellidoSocio = socio?.apellido ?: ""
        val numeroSocio = socio?.numeroSocio ?: ""
        val telefonoSocio = socio?.telefono ?: ""
        prestamoIdTextView.text = "ID del préstamo: $prestamoId"
        datosArticuloTextView.text = "$nombreArticulo"
        datos1ArticuloTextView.text = categoriaArticulo
        datos2ArticuloTextView.text = tipoArticulo
        datosSocioTextView.text = "Socio nº: $numeroSocio"
        datos1SocioTextView.text = "$nombreSocio $apellidoSocio"
        datos2SocioTextView.text = "$telefonoSocio"
        fechaInicioTextView.text = "Fecha inicio: ${dateFormat.format(prestamo.fechaInicio)}"
        if (prestamo.estado == EstadoPrestamo.ACTIVO) {
            fechaFinTextView.visibility = View.GONE
        } else {
            fechaFinTextView.visibility = View.VISIBLE
            val fechaFinString = if (prestamo.fechaFin != null) {
                dateFormat.format(prestamo.fechaFin)
            } else {
                "  "
            }
            fechaFinTextView.text = "Fecha fin: ${fechaFinString}"
        }
        infoTextView.text = "Información Adicional: ${prestamo.info}"
        estadoTextView.text = "Estado: ${prestamo.estado}"
        val imageView: ImageView = findViewById(R.id.articuloImageView)
        if (imagenArticulo.isNotEmpty()) {
            try {
                val imagenBitmap = BitmapFactory.decodeFile(imagenArticulo)
                imageView.setImageBitmap(imagenBitmap)
            } catch (e: Exception) {
                Log.e("Error", "Error al cargar la imagen: ${e.message}")
                imageView.setImageResource(R.drawable.ico_imagen)
            }
        } else {
            imageView.setImageResource(R.drawable.ico_imagen)
        }
    }
}