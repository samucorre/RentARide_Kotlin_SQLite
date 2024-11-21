package pf.dam.prestamos

import android.annotation.SuppressLint
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
import pf.dam.utils.FechaUtils
import java.text.ParseException
import java.util.Date

class PrestamoAddSocioActivity : AppCompatActivity() {

    private lateinit var dbHelper: PrestamosSQLite
    private lateinit var articulosDb: ArticulosSQLite
    private lateinit var sociosDb: SociosSQLite
    private  lateinit var fechaUtils : FechaUtils
   private lateinit var articuloAutoCompleteTextView: AutoCompleteTextView
    private lateinit var fechaInicioButton: Button
    private lateinit var infoEditText: EditText
    private lateinit var guardarButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton
    private lateinit var adapter: ArrayAdapter<String>

    private var idArticuloleccionado: Int? = null

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.prestamo_add_socio_activity)
        supportActionBar?.title = "RR - Préstamo nuevo"

        dbHelper = PrestamosSQLite(this)
        articulosDb = ArticulosSQLite(this)
        sociosDb = SociosSQLite(this)
        fechaUtils = FechaUtils(this)

        articuloAutoCompleteTextView = findViewById(R.id.articuloAutoCompleteTextView)
        fechaInicioButton = findViewById(R.id.fechaInicioButton)
        infoEditText = findViewById(R.id.infoEditText)
        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)
        homeButton = findViewById(R.id.homeButton)

        val idSocioIntent = intent.getIntExtra("idSocio", -1)

        fechaInicioButton.setOnClickListener {
            fechaUtils.mostrarDatePickerPrestamos(this,fechaInicioButton)
        }

         adapter = ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            articulosDb.getAllArticulos().map { "${it.nombre} ${it.categoria}" })
        articuloAutoCompleteTextView.setAdapter(adapter)

        articuloAutoCompleteTextView.setOnItemClickListener { parent, view, position, id ->
            val socioSeleccionado = adapter.getItem(position)
            idArticuloleccionado = articulosDb.getAllArticulos()
                .find { "${it.nombre} ${it.categoria}" == socioSeleccionado }?.idArticulo
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        guardarButton.setOnClickListener {
            val fechaInicioString = fechaInicioButton.text.toString()
            val info = infoEditText.text.toString()
            val idArticulo = idArticuloleccionado

            if (idArticulo != null && !fechaInicioString.isEmpty() && idSocioIntent != null) {
                try {
                    val fechaInicio = fechaUtils.dateFormat.parse(fechaInicioString)
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
                    dbHelper.addrPrestamo(nuevoPrestamo)
                    articulosDb.actualizarEstadoArticulo(idArticulo, EstadoArticulo.PRESTADO)

                    Toast.makeText(this, "Préstamo añadido", Toast.LENGTH_SHORT).show()
                    finish()
                } catch (e: ParseException) {
                    Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show()
                }
            } else {
                val mensajeError = if (idArticulo == null) {
                    "Por favor, selecciona un artículo"
                } else if (fechaInicioString.isEmpty()) {
                    "Por favor, introduce la fecha de inicio"
                } else {
                    "Error desconocido"
                }
                Toast.makeText(this, mensajeError, Toast.LENGTH_SHORT).show()
            }
        }
        volverButton.setOnClickListener { finish() }
    }
}