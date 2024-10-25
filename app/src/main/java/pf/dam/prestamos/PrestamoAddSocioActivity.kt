package pf.dam.prestamos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.MainActivity
import pf.dam.R
import pf.dam.articulos.EstadoArticulo
import pf.dam.utils.DateUtil
import java.text.ParseException
import java.util.Date

class PrestamoAddSocioActivity : AppCompatActivity() {

    private lateinit var dbHelper: PrestamosSQLite
    private lateinit var articulosDbHelper: ArticulosSQLite
    private lateinit var sociosDbHelper: SociosSQLite
    private  lateinit var dateUtil : DateUtil
    private lateinit var articuloSpinner: Spinner
    private lateinit var busquedaArticuloEditText: EditText
    private lateinit var fechaInicioButton: Button
    // private lateinit var fechaFinEditText: EditText
    private lateinit var infoEditText: EditText
    private lateinit var guardarButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton

    private var socioId: Int = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prestamo_add_socio) // Nuevo layout
        supportActionBar?.title = "RR - Préstamo nuevo"

        dbHelper = PrestamosSQLite(this)
        articulosDbHelper = ArticulosSQLite(this)
        sociosDbHelper = SociosSQLite(this)
        dateUtil = DateUtil(this)

        articuloSpinner = findViewById(R.id.articuloSpinner) // <-- Inicializa el Spinner
        busquedaArticuloEditText = findViewById(R.id.busquedaArticuloEditText) // <-- Inicializa el EditText de búsqueda
        fechaInicioButton = findViewById(R.id.fechaInicioButton)
        // fechaFinEditText = findViewById(R.id.fechaFinEditText)
        infoEditText = findViewById(R.id.infoEditText)
        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)
        homeButton = findViewById(R.id.homeButton)

        val idSocioIntent = intent.getIntExtra("idSocio", -1)

        fechaInicioButton.setOnClickListener {
            dateUtil.mostrarDatePicker(this,fechaInicioButton)
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
            val fechaInicioString = fechaInicioButton.text.toString()
            // val fechaFin = dateFormat.parse(fechaFinEditText.text.toString())
            val info = infoEditText.text.toString()
            val idArticulo = articulos.getOrNull(posicionArticulo)?.idArticulo

            // Validar artículo y fechaInicio
            if (idArticulo != null && !fechaInicioString.isEmpty() && idSocioIntent != null) {
                try {
                    val fechaInicio = dateUtil.dateFormat.parse(fechaInicioString)
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
                } catch (e: ParseException) {
                    // Manejar la excepción, por ejemplo, mostrando un mensaje de error al usuario
                    Log.e("Error", "Error al analizar la fecha: ${e.message}")
                    Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Mostrar un mensaje de error al usuario si el artículo o la fecha de inicio no están seleccionados
                val mensajeError = if (idArticulo == null) {
                    "Por favor, selecciona un artículo"
                } else if (fechaInicioString.isEmpty()) {
                    "Por favor, introduce la fecha de inicio"
                } else {
                    "Error desconocido" // O cualquier otro mensaje de error genérico
                }
                Toast.makeText(this, mensajeError, Toast.LENGTH_SHORT).show()
            }
        }
        volverButton.setOnClickListener { finish() }
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