package pf.dam.socios

import android.content.Intent
import java.util.Locale

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.MainActivity
import pf.dam.R
import pf.dam.articulos.Articulo
import pf.dam.prestamos.PrestamoAddSocioActivity
import pf.dam.utils.ShowDeleteConfirmationDialog
import java.text.SimpleDateFormat
import kotlin.text.format

class SocioDetalleActivity : AppCompatActivity() {
    private lateinit var editSocioButton: FloatingActionButton
    private lateinit var deleteSocioButton: FloatingActionButton
    private lateinit var backButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton
    private lateinit var addPrestamoButton: Button
    private lateinit var dbHelper: SociosSQLite
    private lateinit var prestamosDbHelper:PrestamosSQLite

    private lateinit var nombreTextView: TextView
    private lateinit var numeroSocioTextView: TextView
    private lateinit var telefonoTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var fechaNacimientoTextView: TextView
    private lateinit var fechaIngresoSocioTextView: TextView
    private lateinit var generoTextView: TextView
    private lateinit var cantidadPrestamosTextView: TextView
    private lateinit var articulosTextView: TextView

    private var socioId: Int = -1
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


    private val editSocioLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val dbHelper = SociosSQLite(this)
            val socioActualizado = dbHelper.obtenerSocioPorId(socioId)
            if (socioActualizado != null) {
                mostrarSocio(socioActualizado)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socio_detail)

        dbHelper = SociosSQLite(this)
        prestamosDbHelper = PrestamosSQLite(this)

        editSocioButton = findViewById(R.id.editSocioButton)
        addPrestamoButton = findViewById(R.id.addPrestamoButton)
        deleteSocioButton = findViewById(R.id.deleteSocioButton)
        backButton = findViewById(R.id.backButton)
        homeButton = findViewById(R.id.homeButton)

        nombreTextView = findViewById(R.id.nombreTextView)
        numeroSocioTextView = findViewById(R.id.numeroSocioTextView)
        telefonoTextView = findViewById(R.id.telefonoTextView)
        emailTextView = findViewById(R.id.emailTextView)
        cantidadPrestamosTextView= findViewById(R.id.cantidadPrestamosTextView)
        articulosTextView = findViewById(R.id.articulosTextView)
        fechaNacimientoTextView = findViewById(R.id.fechaNacimientoTextView)
        fechaIngresoSocioTextView = findViewById(R.id.fechaIngresoSocioTextView)
        generoTextView = findViewById(R.id.generoTextView)

        socioId = intent.getIntExtra("idSocio", -1)
        val socio = dbHelper.obtenerSocioPorId(socioId)

        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        if (socio != null) {
            mostrarSocio(socio)

            editSocioButton.setOnClickListener {
                val intent = Intent(this, SocioEditActivity::class.java)
                intent.putExtra("socioId", socioId)
                editSocioLauncher.launch(intent)
            }
            val editSocioLauncher = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    setResult(RESULT_OK, Intent().putExtra("socioId", socioId))
                    finish()
                }
            }

            backButton.setOnClickListener {
                finish()
            }
            addPrestamoButton.setOnClickListener {
                val intent = Intent(this, PrestamoAddSocioActivity::class.java)
                intent.putExtra("idSocio", socioId)
                startActivity(intent)
            }
            deleteSocioButton.setOnClickListener {
                // Verificar si el socio tiene algún registro en la tabla de préstamos
                val tieneRegistrosEnPrestamos = prestamosDbHelper.estaSocioEnPrestamoActivo(socioId)

                if (tieneRegistrosEnPrestamos) {
                    Toast.makeText(
                        this,
                        "No se puede eliminar el socio porque tiene registros en préstamos",
                        Toast.LENGTH_SHORT
                    ).show()
                }else setContent {
                    var showDialog by remember { mutableStateOf(true) }

                    if (showDialog) {
                        ShowDeleteConfirmationDialog(
                            title = "Eliminar socio",
                            message = "¿Estás seguro de que quieres eliminar este socio?",
                            onPositiveButtonClick = {
                                dbHelper.borrarSocio(socioId)



                                Toast.makeText(
                                    this@SocioDetalleActivity,
                                    "Socio eliminado",
                                    Toast.LENGTH_SHORT
                                ).show()
                                setResult(RESULT_OK)
                                finish()
                                showDialog = false
                            },
                            onDismissRequest = {
                                showDialog = false
                                finish()
                            }
                        )
                    }
                }
            }
        }
    }


    private fun mostrarSocio(socio: Socio) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        nombreTextView.text = "${socio.nombre}"+" "+"${ socio.apellido}"
        numeroSocioTextView.text = "Nº Socio: ${socio.numeroSocio}"
        telefonoTextView.text = "${socio.telefono}"
        emailTextView.text = "${socio.email}"
        fechaNacimientoTextView.text = "Fecha de nacimiento: ${dateFormat.format(socio.fechaNacimiento)}"
        fechaIngresoSocioTextView.text = "Fecha de ingreso: ${dateFormat.format(socio.fechaIngresoSocio)}"
        generoTextView.text = "Género: ${socio.genero}"

        val prestamos = socio.idSocio?.let { prestamosDbHelper.obtenerPrestamosPorSocio(it) }

        // Obtén la cantidad de préstamos
        val cantidadPrestamos = prestamos?.size

        // Obtén los artículos de los préstamos
        val articulos = mutableSetOf<Articulo>()
        if (prestamos != null) {
            for (prestamo in prestamos) {
                val articulo = prestamosDbHelper.obtenerArticuloPrestamoId(prestamo.idArticulo) // Usa prestamosDbHelper para obtener el artículo
                if (articulo != null) {
                    articulos.add(articulo)
                }
            }
        }

        // Muestra la cantidad de préstamos y los artículos en los TextViews
        cantidadPrestamosTextView.text = "Cantidad de préstamos: \t$cantidadPrestamos"
        articulosTextView.text = "Últimos artículos usados:"+
                "\n${articulos.distinctBy { it.idArticulo }.takeLast(3).joinToString (separator = "")  {"${it.nombre.toString()}\n"}}"
    }


}