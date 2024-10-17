package pf.dam.prestamos

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
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
import java.util.*

class PrestamoAddSocioActivity : AppCompatActivity() {

    private lateinit var dbHelper: PrestamosSQLite
    private lateinit var articulosDbHelper: ArticulosSQLite
    private lateinit var sociosDbHelper: SociosSQLite
    private lateinit var articuloSpinner: Spinner // <-- Spinner para seleccionar el artículo
    private lateinit var fechaInicioEditText: EditText
    private lateinit var fechaFinEditText: EditText
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
        fechaInicioEditText = findViewById(R.id.fechaInicioEditText)
     //   fechaFinEditText = findViewById(R.id.fechaFinEditText)
        infoEditText = findViewById(R.id.infoEditText)
        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)
        homeButton=findViewById(R.id.homeButton)

       val idSocioIntent = intent.getIntExtra("idSocio", -1)

        fechaInicioEditText.setOnClickListener {
            mostrarDatePicker(fechaInicioEditText)
        }

        // Obtener la lista de artículos y configurar el adaptador del Spinner
        val articulos = articulosDbHelper.obtenerArticulosDisponibles()
        val articulosAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            articulos.map { "${it.categoria} - ${it.nombre}" })
        articulosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        articuloSpinner.adapter = articulosAdapter

        // ... (código para configurar los listeners de los EditText) ...

        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        guardarButton.setOnClickListener {
            val posicionArticulo = articuloSpinner.selectedItemPosition// <-- Obtén el artículo seleccionado
            val fechaInicio = dateFormat.parse(fechaInicioEditText.text.toString())
         //   val fechaFin = dateFormat.parse(fechaFinEditText.text.toString())
            val info = infoEditText.text.toString()
            val idArticulo = articulos.getOrNull(posicionArticulo)?.idArticulo

            if (idArticulo != null && idSocioIntent != null) {
                val fechaFin: Date? = null
                val nuevoPrestamo =
                    Prestamo(null,
                        idArticulo,
                        idSocio=idSocioIntent,
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
}
