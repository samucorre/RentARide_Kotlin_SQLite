package pf.dam.prestamos

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.MainActivity
import pf.dam.R
import pf.dam.articulos.EstadoArticulo
import pf.dam.socios.Socio
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrestamoAddArticuloActivity : AppCompatActivity() {

    private lateinit var dbHelper: PrestamosSQLite
    private lateinit var articulosDbHelper: ArticulosSQLite
    private lateinit var sociosDbHelper: SociosSQLite
    private lateinit var socioAutoCompleteTextView: AutoCompleteTextView
    private lateinit var fechaInicioEditText: EditText
    private lateinit var infoEditText: EditText
    private lateinit var guardarButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val calendar = Calendar.getInstance()

    private var articuloId: Int = -1
    private var idSocioSeleccionado: Int? = null
    private lateinit var adapter: ArrayAdapter<String>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prestamo_add_articulo)

        dbHelper = PrestamosSQLite(this)
        articulosDbHelper = ArticulosSQLite(this)
        sociosDbHelper = SociosSQLite(this)

        socioAutoCompleteTextView = findViewById(R.id.socioAutoCompleteTextView)
        fechaInicioEditText = findViewById(R.id.fechaInicioEditText)
        infoEditText = findViewById(R.id.infoEditText)
        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)
        homeButton = findViewById(R.id.homeButton)

        val idArticuloIntent = intent.getIntExtra("idArticulo", -1)

        fechaInicioEditText.setOnClickListener {
            mostrarDatePicker(fechaInicioEditText)
        }

        // Crear el adaptador con los nombres de los socios
        adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, sociosDbHelper.obtenerSocios().map { "${it.nombre} ${it.apellido}" })
        socioAutoCompleteTextView.setAdapter(adapter)

        // Manejar la selección de un socio
        socioAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val socioSeleccionado = adapter.getItem(position)
            idSocioSeleccionado = sociosDbHelper.obtenerSocios().find { "${it.nombre} ${it.apellido}" == socioSeleccionado }?.idSocio
        }
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        guardarButton.setOnClickListener {
            val fechaInicio = dateFormat.parse(fechaInicioEditText.text.toString())
            val info = infoEditText.text.toString()
            val idSocio = idSocioSeleccionado

            if (idSocio != null && idArticuloIntent != null) {
                val fechaFin: Date? = null
                val nuevoPrestamo = Prestamo(
                    null,
                    idArticulo = idArticuloIntent,
                    idSocio,
                    fechaInicio,
                    fechaFin,
                    info,
                    EstadoPrestamo.ACTIVO
                )
                dbHelper.insertarPrestamo(nuevoPrestamo)
                articulosDbHelper.actualizarEstadoArticulo(
                    idArticuloIntent,
                    EstadoArticulo.PRESTADO
                )

                Toast.makeText(this, "Préstamo añadido", Toast.LENGTH_SHORT).show()
                setResult(Activity.RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "Por favor, selecciona un socio", Toast.LENGTH_SHORT).show()
            }
        }

        volverButton.setOnClickListener {
            finish()
        }
    }

    private fun mostrarDatePicker(editText: EditText) {
        val datePicker = android.app.DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                editText.setText(dateFormat.format(calendar.time))
            },
            calendar.get(java.util.Calendar.YEAR),
            calendar.get(java.util.Calendar.MONTH),
            calendar.get(java.util.Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun buscarSocio(textoBusqueda: String): Socio? {
        val socios = sociosDbHelper.obtenerSocios()
        return socios.find { socio ->
            val valoresSocio = listOfNotNull(
                socio.nombre,
                socio.apellido,
                socio.numeroSocio,
                socio.email,
                socio.telefono
            ).map { it.toString() } // <-- Aquí se convierten todos los valores a String
            valoresSocio.any { valor ->
                return@any valor.contains(textoBusqueda, ignoreCase = true)
            }
        }
    }
}