package pf.dam.prestamos

import android.annotation.SuppressLint
import android.app.Activity
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
import pf.dam.socios.Socio
import pf.dam.utils.DateUtil
import java.text.ParseException
import java.util.Date

class PrestamoAddArticuloActivity : AppCompatActivity() {

    private lateinit var dbHelper: PrestamosSQLite
    private lateinit var articulosDbHelper: ArticulosSQLite
    private lateinit var sociosDbHelper: SociosSQLite
    private  lateinit var dateUtil : DateUtil
    private lateinit var socioAutoCompleteTextView: AutoCompleteTextView
    private lateinit var fechaInicioButton: Button
    private lateinit var infoEditText: EditText
    private lateinit var guardarButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton

    private var articuloId: Int = -1
    private var idSocioSeleccionado: Int? = null
    private lateinit var adapter: ArrayAdapter<String>

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prestamo_add_articulo)
        supportActionBar?.title = "RR - Préstamo nuevo"

        dbHelper = PrestamosSQLite(this)
        articulosDbHelper = ArticulosSQLite(this)
        sociosDbHelper = SociosSQLite(this)
        dateUtil = DateUtil(this)

        socioAutoCompleteTextView = findViewById(R.id.socioAutoCompleteTextView)
        fechaInicioButton = findViewById(R.id.fechaInicioButton)
        infoEditText = findViewById(R.id.infoEditText)
        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)
        homeButton = findViewById(R.id.homeButton)

        val idArticuloIntent = intent.getIntExtra("idArticulo", -1)

        fechaInicioButton.setOnClickListener {
            dateUtil.mostrarDatePicker(this,fechaInicioButton)
        }

        // Crear el adaptador con los nombres de los socios
        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            sociosDbHelper.obtenerSocios().map { "${it.nombre} ${it.apellido}" })
        socioAutoCompleteTextView.setAdapter(adapter)

        // Manejar la selección de un socio
        socioAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val socioSeleccionado = adapter.getItem(position)
            idSocioSeleccionado = sociosDbHelper.obtenerSocios()
                .find { "${it.nombre} ${it.apellido}" == socioSeleccionado }?.idSocio
        }
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        guardarButton.setOnClickListener {
            val fechaInicioString = fechaInicioButton.text.toString()
            val info = infoEditText.text.toString()
            val idSocio = idSocioSeleccionado

            // Validar socio y fechaInicio
            if (idSocio != null && !fechaInicioString.isEmpty() && idArticuloIntent != null) {
                try {
                    val fechaInicio = dateUtil.dateFormat.parse(fechaInicioString)
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
                } catch (e: ParseException) {
                    // Manejar la excepción, por ejemplo, mostrando un mensaje de error al usuario
                    Log.e("Error", "Error al analizar la fecha: ${e.message}")
                    Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT)
                        .show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Por favor, selecciona un socio e introduce la fecha de inicio",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        volverButton.setOnClickListener {
            finish()
        }

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