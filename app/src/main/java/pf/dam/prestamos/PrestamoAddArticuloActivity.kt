package pf.dam.prestamos

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
import pf.dam.utils.FechaUtils
import java.text.ParseException
import java.util.Date

class PrestamoAddArticuloActivity : AppCompatActivity() {

    private lateinit var dbPrestamos: PrestamosSQLite
    private lateinit var dbArticulos: ArticulosSQLite
    private lateinit var dbSocios: SociosSQLite
    private  lateinit var fechaUtils : FechaUtils
    private lateinit var socioAutoCompleteTextView: AutoCompleteTextView
    private lateinit var fechaInicioButton: Button
    private lateinit var infoEditText: EditText
    private lateinit var guardarButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton
    private lateinit var adapter: ArrayAdapter<String>


    private var articuloId: Int = -1
    private var idSocioSeleccionado: Int? = null
    companion object {
        const val REQUEST_ADD_PRESTAMO = 1
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.prestamo_add_articulo_activity)
        supportActionBar?.title = "RR - Préstamo nuevo"

        dbPrestamos = PrestamosSQLite(this)
        dbArticulos = ArticulosSQLite(this)
        dbSocios = SociosSQLite(this)
        fechaUtils = FechaUtils(this)

        socioAutoCompleteTextView = findViewById(R.id.socioAutoCompleteTextView)
        fechaInicioButton = findViewById(R.id.fechaInicioButton)
        infoEditText = findViewById(R.id.infoEditText)
        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)
        homeButton = findViewById(R.id.homeButton)

        val idArticuloIntent = intent.getIntExtra("idArticulo", -1)

        fechaInicioButton.setOnClickListener {
            fechaUtils.mostrarDatePickerPrestamos(this,fechaInicioButton)
        }

        adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            dbSocios.getAllSocios().map { "${it.nombre} ${it.apellido} - ${it.numeroSocio}" })
        socioAutoCompleteTextView.setAdapter(adapter)

         socioAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val socioSeleccionado = adapter.getItem(position)
            idSocioSeleccionado = dbSocios.getAllSocios()
                .find { "${it.nombre} ${it.apellido} - ${it.numeroSocio}" == socioSeleccionado }?.idSocio
        }
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        guardarButton.setOnClickListener {
            val fechaInicioString = fechaInicioButton.text.toString()
            val info = infoEditText.text.toString()
            val idSocio = idSocioSeleccionado

            if (idSocio != null && !fechaInicioString.isEmpty() && idArticuloIntent != null) {
                try {
                    val fechaInicio = fechaUtils.dateFormat.parse(fechaInicioString)
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
                    dbPrestamos.addrPrestamo(nuevoPrestamo)
                    dbArticulos.actualizarEstadoArticulo(
                        idArticuloIntent,
                        EstadoArticulo.PRESTADO
                    )

                    Toast.makeText(this, "Préstamo añadido", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                } catch (e: ParseException) {
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
        val socios = dbSocios.getAllSocios()
        return socios.find { socio ->
            val valoresSocio = listOfNotNull(
                socio.nombre,
                socio.apellido,
                socio.numeroSocio,
                socio.email,
                socio.telefono
            ).map { it.toString() }
            valoresSocio.any { valor ->
                return@any valor.contains(textoBusqueda, ignoreCase = true)
            }
        }
    }
}