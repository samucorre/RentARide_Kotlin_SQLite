package pf.dam.prestamos

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import androidx.core.widget.addTextChangedListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.MainActivity
import pf.dam.R
import pf.dam.articulos.Articulo
import pf.dam.articulos.EstadoArticulo
import pf.dam.socios.Socio
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PrestamoAddSocioActivity : AppCompatActivity() {

    private lateinit var dbHelper: PrestamosSQLite
    private lateinit var articulosDbHelper: ArticulosSQLite
    private lateinit var sociosDbHelper: SociosSQLite
    private lateinit var articuloSpinner: Spinner
    private lateinit var busquedaArticuloEditText: EditText
    private lateinit var fechaInicioEditText: EditText
    // private lateinit var fechaFinEditText: EditText
    private lateinit var infoEditText: EditText
    private lateinit var guardarButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val calendar = Calendar.getInstance()

    private var socioId: Int = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prestamo_add_socio) // Nuevo layout

        dbHelper = PrestamosSQLite(this)
        articulosDbHelper = ArticulosSQLite(this)
        sociosDbHelper = SociosSQLite(this)

        articuloSpinner = findViewById(R.id.articuloSpinner) // <-- Inicializa el Spinner
        busquedaArticuloEditText = findViewById(R.id.busquedaArticuloEditText) // <-- Inicializa el EditText de búsqueda
        fechaInicioEditText = findViewById(R.id.fechaInicioEditText)
        // fechaFinEditText = findViewById(R.id.fechaFinEditText)
        infoEditText = findViewById(R.id.infoEditText)
        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)
        homeButton = findViewById(R.id.homeButton)

        val idSocioIntent = intent.getIntExtra("idSocio", -1)

        fechaInicioEditText.setOnClickListener {
            mostrarDatePicker(fechaInicioEditText)
        }

        // Obtener la lista de artículos y configurar el adaptador del Spinner
        val articulos = articulosDbHelper.obtenerArticulosDisponibles()
        val articulosAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            articulos.map { "${it.categoria} - ${it.nombre}" }
        )
        articulosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        articuloSpinner.adapter = articulosAdapter

        busquedaArticuloEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                filtrarArticulos(s.toString())
            }
        })

        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        guardarButton.setOnClickListener {
            val posicionArticulo = articuloSpinner.selectedItemPosition // <-- Obtén el artículo seleccionado
            val fechaInicio = dateFormat.parse(fechaInicioEditText.text.toString())
            // val fechaFin = dateFormat.parse(fechaFinEditText.text.toString())
            val info = infoEditText.text.toString()
            val idArticulo = articulos.getOrNull(posicionArticulo)?.idArticulo

            if (idArticulo != null && idSocioIntent != null) {
                val fechaFin: Date? = null
                val nuevoPrestamo = Prestamo(
                    null,
                    idArticulo,
                    idSocio = idSocioIntent,
                    fechaInicio,
                    fechaFin,
                    info,
                    EstadoPrestamo.ACTIVO
                )
                dbHelper.insertarPrestamo(nuevoPrestamo)
                articulosDbHelper.actualizarEstadoArticulo(idArticulo, EstadoArticulo.PRESTADO)

                Toast.makeText(this, "Préstamo añadido", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        volverButton.setOnClickListener { finish() }
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

    private fun filtrarArticulos(textoBusqueda: String) {
        val textoBusqueda = textoBusqueda.trim()
        val articulosFiltrados = articulosDbHelper.obtenerArticulosDisponibles()
            .filter { articulo ->
                articulo.nombre?.contains(textoBusqueda, ignoreCase = true) ?: false ||
                        articulo.categoria?.contains(textoBusqueda, ignoreCase = true) ?: false
            }
        val articulosAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            articulosFiltrados.map { "${it.categoria} - ${it.nombre}" }
        )
        articulosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        articuloSpinner.adapter = articulosAdapter
    }
}