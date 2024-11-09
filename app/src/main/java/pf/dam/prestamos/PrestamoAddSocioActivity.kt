package pf.dam.prestamos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.MainActivity
import pf.dam.R
import pf.dam.articulos.EstadoArticulo
import pf.dam.utils.FechaUtil
import java.text.ParseException
import java.util.Date

class PrestamoAddSocioActivity : AppCompatActivity() {

    private lateinit var dbHelper: PrestamosSQLite
    private lateinit var articulosDb: ArticulosSQLite
    private lateinit var sociosDb: SociosSQLite
    private  lateinit var fechaUtil : FechaUtil
//    private lateinit var articuloSpinner: Spinner
   private lateinit var articuloAutoCompleteTextView: AutoCompleteTextView
    private lateinit var fechaInicioButton: Button
    // private lateinit var fechaFinEditText: EditText
    private lateinit var infoEditText: EditText
    private lateinit var guardarButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton
    private lateinit var adapter: ArrayAdapter<String>

    private var socioId: Int = -1
    private var idArticuloleccionado: Int? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prestamo_add_socio) // Nuevo layout
        supportActionBar?.title = "RR - Préstamo nuevo"

        dbHelper = PrestamosSQLite(this)
        articulosDb = ArticulosSQLite(this)
        sociosDb = SociosSQLite(this)
        fechaUtil = FechaUtil(this)

//        articuloSpinner = findViewById(R.id.articuloSpinner)
        articuloAutoCompleteTextView = findViewById(R.id.articuloAutoCompleteTextView)
        fechaInicioButton = findViewById(R.id.fechaInicioButton)
        infoEditText = findViewById(R.id.infoEditText)
        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)
        homeButton = findViewById(R.id.homeButton)

        val idSocioIntent = intent.getIntExtra("idSocio", -1)

        fechaInicioButton.setOnClickListener {
            fechaUtil.mostrarDatePickerPrestamos(this,fechaInicioButton)
        }

//        // Obtener la lista de artículos y configurar el adaptador del Spinner
//        val articulos = articulosDb.obtenerArticulosDisponibles()
//        val articulosAdapter = ArrayAdapter(
//            this,
//            android.R.layout.simple_spinner_item,
//            articulos.map { "${it.categoria} - ${it.nombre}" }
//        )
//        articulosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        articuloSpinner.adapter = articulosAdapter

//        busquedaArticuloEditText.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//
//            override fun afterTextChanged(s: Editable?) {
//                filtrarArticulos(s.toString())
//            }
//        })
//
         adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            articulosDb.obtenerArticulos().map { "${it.nombre} ${it.categoria}" })
        articuloAutoCompleteTextView.setAdapter(adapter)

        // Manejar la selección de un socio
        articuloAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val socioSeleccionado = adapter.getItem(position)
            idArticuloleccionado = articulosDb.obtenerArticulos()
                .find { "${it.nombre} ${it.categoria}" == socioSeleccionado }?.idArticulo
        }



        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        guardarButton.setOnClickListener {
//            val posicionArticulo = articuloSpinner.selectedItemPosition // <-- Obtén el artículo seleccionado
            val fechaInicioString = fechaInicioButton.text.toString()
            // val fechaFin = dateFormat.parse(fechaFinEditText.text.toString())
            val info = infoEditText.text.toString()
            val idArticulo = idArticuloleccionado

            // Validar artículo y fechaInicio
            if (idArticulo != null && !fechaInicioString.isEmpty() && idSocioIntent != null) {
                try {
                    val fechaInicio = fechaUtil.dateFormat.parse(fechaInicioString)
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
                    articulosDb.actualizarEstadoArticulo(idArticulo, EstadoArticulo.PRESTADO)

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

//    private fun filtrarArticulos(textoBusqueda: String) {
//        val textoBusqueda = textoBusqueda.trim()
//        val articulosFiltrados = articulosDb.obtenerArticulosDisponibles()
//            .filter { articulo ->
//                articulo.nombre?.contains(textoBusqueda, ignoreCase = true) ?: false ||
//                        articulo.categoria?.contains(textoBusqueda, ignoreCase = true) ?: false
//            }
//        val articulosAdapter = ArrayAdapter(
//            this,
//            android.R.layout.simple_spinner_item,
//            articulosFiltrados.map { "${it.categoria} - ${it.nombre}" }
//        )
//      //  articulosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//      //  articuloSpinner.adapter = articulosAdapter
//    }
}