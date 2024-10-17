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
import pf.dam.utils.ShowDeleteConfirmationDialog
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

   private  lateinit var prestamoIdTextView: TextView
    private lateinit var articuloTextView: TextView
    private lateinit var articuloImageView: ImageView
    private lateinit var socioTextView: TextView
    private lateinit var fechaInicioTextView: TextView
    private lateinit var fechaFinTextView: TextView
    private lateinit var infoTextView: TextView
    private lateinit var estadoTextView: TextView

    private var prestamoId: Int = -1
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    private val editPrestamoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val prestamoActualizado = prestamosDbHelper.obtenerPrestamoPorId(prestamoId)
            if (prestamoActualizado != null) {
                mostrarPrestamo(prestamoActualizado)
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prestamo_detail)

        prestamosDbHelper = PrestamosSQLite(this)
        articulosDbHelper = ArticulosSQLite(this)
        sociosDbHelper = SociosSQLite(this)

        editPrestamoButton = findViewById(R.id.editPrestamoButton)
        deletePrestamoButton = findViewById(R.id.deletePrestamoButton)
        cerrarPrestamoButton = findViewById(R.id.cerrarPrestamoButton)
        backButton = findViewById(R.id.backButton)
        homeButton = findViewById(R.id.homeButton)


        articuloTextView = findViewById(R.id.articuloTextView)
        socioTextView = findViewById(R.id.socioTextView)
        prestamoIdTextView = findViewById(R.id.prestamoIdTextView)
        fechaInicioTextView = findViewById(R.id.fechaInicioTextView)
        fechaFinTextView = findViewById(R.id.fechaFinTextView)
        infoTextView = findViewById(R.id.infoTextView)
        estadoTextView = findViewById(R.id.estadoTextView)
        articuloImageView = findViewById(R.id.articuloImageView)

        prestamoId = intent.getIntExtra("idPrestamo", -1)
        val prestamo = prestamosDbHelper.obtenerPrestamoPorId(prestamoId)

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
                cerrarPrestamoButton.visibility = View.GONE // Ocultar el botón
            } else {
                cerrarPrestamoButton.visibility = View.VISIBLE // Mostrar el botón
                cerrarPrestamoButton.setOnClickListener {
                    // Actualizar el estado del préstamo a CERRADO
                    prestamo.estado = EstadoPrestamo.CERRADO
                    prestamo.fechaFin = Date()
                    prestamosDbHelper.actualizarPrestamo(prestamo)

                    // Actualizar el estado del artículo a DISPONIBLE
                    articulosDbHelper.actualizarEstadoArticulo(
                        prestamo.idArticulo,
                        EstadoArticulo.DISPONIBLE
                    )

                    Toast.makeText(this, "Préstamo cerrado", Toast.LENGTH_SHORT).show()
                    cerrarPrestamoButton.visibility = View.GONE
                    mostrarPrestamo(prestamo)
                }
            }

            homeButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            editPrestamoButton.setOnClickListener {
                val intent = Intent(this, PrestamoEditActivity::class.java)
                intent.putExtra("prestamoId", prestamoId)
                editPrestamoLauncher.launch(intent)
            }

//            deletePrestamoButton.setOnClickListener {
//                // Actualizar el estado del artículo a DISPONIBLE
//                articulosDbHelper.actualizarEstadoArticulo(
//                    prestamo.idArticulo,
//                    EstadoArticulo.DISPONIBLE
//                )
//
//                dbHelper.borrarPrestamo(prestamoId)
//                Toast.makeText(this, "Préstamo eliminado", Toast.LENGTH_SHORT).show()
//                setResult(RESULT_OK)
//                finish()
//            }
            deletePrestamoButton.setOnClickListener {

                articulosDbHelper.actualizarEstadoArticulo(
                    prestamo.idArticulo,
                    EstadoArticulo.DISPONIBLE)

                if (prestamosDbHelper.estaSocioEnPrestamo(prestamo.idSocio)) {
                    // Mostrar un mensaje de error al usuario
                    Toast.makeText(this, "No se puede eliminar el préstamo porque el socio está en un préstamo activo", Toast.LENGTH_SHORT).show()
                } else {
                    // Mostrar el diálogo de confirmación de eliminación
                    setContent {
                        var showDialog by remember { mutableStateOf(true) }

                        if (showDialog) {
                            ShowDeleteConfirmationDialog(
                                title = "Eliminar préstamo",
                                message = "¿Estás seguro de que quieres eliminar este préstamo?",
                                onPositiveButtonClick = {
                                    prestamosDbHelper.borrarPrestamo(prestamoId)
                                    Toast.makeText(this@PrestamoDetalleActivity, "Préstamo eliminado", Toast.LENGTH_SHORT).show()
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
    private fun mostrarPrestamo(prestamo: Prestamo) {
        //artículo
        val articulo = articulosDbHelper.obtenerArticuloPorId(prestamo.idArticulo)
        val nombreArticulo = articulo?.nombre ?: "Artículo no encontrado"
        val categoriaArticulo = articulo?.categoria ?: ""
        val imagenArticulo = articulo?.rutaImagen ?: ""

        //socio
        val socio = sociosDbHelper.obtenerSocioPorId(prestamo.idSocio)
        val nombreSocio = socio?.nombre ?: "Socio no encontrado"
               val numeroSocio = socio?.numeroSocio ?: ""

        // Actualizar TextViews

        prestamoIdTextView.text = "ID del préstamo: $prestamoId"
        articuloTextView.text = "Artículo: $nombreArticulo ID:${prestamo.idArticulo}"

        socioTextView.text = "Socio: $nombreSocio \nNº socio: $numeroSocio"

        fechaInicioTextView.text = "Fecha inicio: ${dateFormat.format(prestamo.fechaInicio)}"
        // Manejar fechaFin: si es null, mostrar un mensaje o dejar el TextView vacío
            val fechaFinString = if (prestamo.fechaFin != null) {
            dateFormat.format(prestamo.fechaFin)
        } else {
                "  " // O "" para dejarlo vacío
        }
        fechaFinTextView.text =  "Fecha fin: ${fechaFinString}"

        infoTextView.text = "Información Adicional: ${prestamo.info}"
        estadoTextView.text = "Estado: ${prestamo.estado}"

            // Cargar y mostrar la imagen (similar a ArticuloDetalleActivity)
        val imageView: ImageView = findViewById(R.id.articuloImageView) // Asegúrate de tener un ImageView en tu layout
        if (imagenArticulo.isNotEmpty()) {
            try {
                val imagenBitmap = BitmapFactory.decodeFile(imagenArticulo)
                imageView.setImageBitmap(imagenBitmap)
            } catch (e: Exception) {
                Log.e("Error", "Error al cargar la imagen: ${e.message}")
                imageView.setImageResource(R.drawable.ico_imagen) // Imagen por defecto en caso de error
            }
        } else {
            imageView.setImageResource(R.drawable.ico_imagen) // Imagen por defecto si no hay imagen
        }
    }
}