package pf.dam.prestamos

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import db.ArticulosSQLite
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.R
import pf.dam.articulos.Articulo
import pf.dam.articulos.EstadoArticulo
import pf.dam.socios.Socio
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class PrestamoAddActivity : AppCompatActivity() {

    private lateinit var dbHelper: PrestamosSQLite
    private lateinit var articulosDbHelper: ArticulosSQLite
    private lateinit var sociosDbHelper: SociosSQLite
    private lateinit var articuloSpinner: Spinner
    private lateinit var socioSpinner: Spinner
    private lateinit var fechaInicioEditText: EditText
    private lateinit var fechaFinEditText: EditText
    private lateinit var infoEditText: EditText
    private lateinit var guardarButton: Button
    private lateinit var volverButton: Button

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val calendar = Calendar.getInstance()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prestamo_add)

        dbHelper = PrestamosSQLite(this)
        articulosDbHelper = ArticulosSQLite(this)
        sociosDbHelper = SociosSQLite(this)
        articuloSpinner = findViewById(R.id.articuloSpinner)
        socioSpinner = findViewById(R.id.socioSpinner)
        fechaInicioEditText = findViewById(R.id.fechaInicioEditText)
        fechaFinEditText = findViewById(R.id.fechaFinEditText)
        infoEditText = findViewById(R.id.infoEditText)
        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)

        volverButton.setOnClickListener { finish() }

        fechaInicioEditText.setOnClickListener {
            mostrarDatePicker(fechaInicioEditText)
        }

        fechaFinEditText.setOnClickListener {
            mostrarDatePicker(fechaFinEditText)
        }

        // Rellenar Spinners
        val articulos = articulosDbHelper.obtenerArticulosDisponibles()
        val socios = sociosDbHelper.obtenerSocios()

        val articulosAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            articulos.map { "${it.categoria} - ${it.nombre}" })
        articulosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        articuloSpinner.adapter = articulosAdapter

        val sociosAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            socios.map { "${it.nombre} ${it.apellido}" })
        sociosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        socioSpinner.adapter = sociosAdapter

       /* guardarButton.setOnClickListener {
            val articuloSeleccionado = articuloSpinner.selectedItem as? Articulo
            val socioSeleccionado = socioSpinner.selectedItem as? Socio
            val fechaInicio = dateFormat.parse(fechaInicioEditText.text.toString())
            val fechaFin = if (fechaFinEditText.text.toString().isNotBlank()) {
                dateFormat.parse(fechaFinEditText.text.toString())
            } else {
                null
            }
            val info = infoEditText.text.toString()

            if (articuloSeleccionado == null || socioSeleccionado == null || fechaInicio == null) {
                Toast.makeText(
                    this,
                    "Por favor, selecciona un artículo y un socio, e introduce la fecha de inicio",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val articulo = articulos.find { "{it.nombre}" = articuloSeleccionado.toString() }
                if (articulo != null) {
                    val idArticulo =
                        articulosDbHelper.obtenerIdArticuloDisponibleBD(articuloSeleccionado)
                    val idSocio = sociosDbHelper.obtenerIdSocioBD(socioSeleccionado)

                    val nuevoPrestamo = fechaFin?.let {
                        Prestamo(idArticulo, idSocio, fechaInicio, it, info)
                    }
                    if (nuevoPrestamo != null) {
                        dbHelper.insertarPrestamo(nuevoPrestamo)
                    }

                    // Actualizar el estado del artículo a PRESTADO
                    articulosDbHelper.actualizarEstadoArticulo(idArticulo, EstadoArticulo.PRESTADO)

                    Toast.makeText(this, "Préstamo añadido", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Artículo no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
        }*/

        guardarButton.setOnClickListener {
            val posicionArticulo = articuloSpinner.selectedItemPosition
            val posicionSocio = socioSpinner.selectedItemPosition
            val fechaInicio = dateFormat.parse(fechaInicioEditText.text.toString())
            val fechaFin = if (fechaFinEditText.text.toString().isNotBlank()) {
                dateFormat.parse(fechaFinEditText.text.toString())
            } else {
                null
            }
            val info = infoEditText.text.toString()

            if (posicionArticulo == -1 || posicionSocio == -1 || fechaInicio == null) {
                Toast.makeText(
                    this,
                    "Por favor, selecciona un artículo y un socio, e introduce la fecha de inicio",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val idArticulo = articulos.getOrNull(posicionArticulo)?.idArticulo
                val idSocio = socios.getOrNull(posicionSocio)?.idSocio

                if (idArticulo != null && idSocio != null) {
                    val nuevoPrestamo = fechaFin?.let {
                        Prestamo(null, idArticulo, idSocio, fechaInicio, it, info)
                    } ?: Prestamo(null, idArticulo, idSocio, fechaInicio, fechaInicio, info) // Si fechaFin es null, se usa fechaInicio

                    dbHelper.insertarPrestamo(nuevoPrestamo)

                    // Actualizar el estado del artículo a PRESTADO
                    articulosDbHelper.actualizarEstadoArticulo(idArticulo, EstadoArticulo.PRESTADO)

                    Toast.makeText(this, "Préstamo añadido", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Artículo o socio no encontrado", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    private fun mostrarDatePicker(editText: EditText) {
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                editText.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }
}