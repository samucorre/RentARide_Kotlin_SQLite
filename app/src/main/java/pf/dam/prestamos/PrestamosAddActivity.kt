package pf.dam.prestamos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.MainActivity
import pf.dam.R
import pf.dam.articulos.EstadoArticulo
import pf.dam.utils.FechaUtils
import java.text.ParseException
import java.util.Date

class PrestamoAddActivity : AppCompatActivity() {

    private lateinit var dbPrestamos: PrestamosSQLite
    private lateinit var dbArticulos: ArticulosSQLite
    private lateinit var dbSocios: SociosSQLite
    private  lateinit var fechaUtils : FechaUtils
    private lateinit var articuloSpinner: Spinner
    private lateinit var socioSpinner: Spinner
    private lateinit var fechaInicioButton: Button
    private lateinit var infoEditText: EditText
    private lateinit var guardarButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton
    private lateinit var socioLabelTextView : TextView
    private lateinit var articuloLabelTextView : TextView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.prestamo_add_activity)
        supportActionBar?.title = "RR - Préstamo nuevo"


        dbPrestamos = PrestamosSQLite(this)
        dbArticulos = ArticulosSQLite(this)
        dbSocios = SociosSQLite(this)
        fechaUtils = FechaUtils(this)

        articuloSpinner = findViewById(R.id.articuloSpinner)
        socioSpinner = findViewById(R.id.socioSpinner)
        fechaInicioButton = findViewById(R.id.fechaInicioButton)
        socioLabelTextView= findViewById(R.id.socioLabelTextView)
        articuloLabelTextView = findViewById(R.id.articuloLabelTextView)
        infoEditText = findViewById(R.id.infoEditText)
        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)
        homeButton = findViewById(R.id.homeButton)

        volverButton.setOnClickListener { finish() }

        val articulos = dbArticulos.obtenerArticulosDisponibles()
        val socios = dbSocios.getAllSocios()

        fechaInicioButton.setOnClickListener {
            fechaUtils.mostrarDatePickerPrestamos(this, fechaInicioButton)
        }

        val articulosAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            articulos.map { "${it.categoria} - ${it.nombre}" })
        articulosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        articuloSpinner.adapter = articulosAdapter

        val sociosAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            socios.map { "${it.nombre} ${it.apellido} - ${it.numeroSocio}" })
        sociosAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        socioSpinner.adapter = sociosAdapter

        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        guardarButton.setOnClickListener {
            val posicionArticulo = articuloSpinner.selectedItemPosition
            val posicionSocio = socioSpinner.selectedItemPosition
            val fechaInicioString = fechaInicioButton.text.toString()
            val info = infoEditText.text.toString()

                if (fechaInicioString.isEmpty()) {
                      Toast.makeText(this, "La fecha de inicio es obligatoria", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val fechaInicio = fechaUtils.dateFormat.parse(fechaInicioString)
                val idArticulo = articulos.getOrNull(posicionArticulo)?.idArticulo
                val idSocio = socios.getOrNull(posicionSocio)?.idSocio

                if (idArticulo != null && idSocio != null && fechaInicio != null) {
                    val fechaFin: Date? = null
                    val nuevoPrestamo = Prestamo(null, idArticulo, idSocio, fechaInicio, fechaFin, info, EstadoPrestamo.ACTIVO)
                    dbPrestamos.addrPrestamo(nuevoPrestamo)
                    dbArticulos.actualizarEstadoArticulo(idArticulo, EstadoArticulo.PRESTADO)
                    Toast.makeText(this, "Préstamo añadido", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Artículo o socio no encontrado", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ParseException) {
                        Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show()
            }
        }
    }
}