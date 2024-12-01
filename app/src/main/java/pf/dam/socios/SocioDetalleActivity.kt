package pf.dam.socios

import android.annotation.SuppressLint
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
import pf.dam.utils.DialogoUtil
import java.text.SimpleDateFormat

class SocioDetalleActivity : AppCompatActivity() {
    private lateinit var editSocioButton: FloatingActionButton
    private lateinit var deleteSocioButton: FloatingActionButton
    private lateinit var backButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton
    private lateinit var addPrestamoButton: Button
    private lateinit var sociosDb: SociosSQLite
    private lateinit var prestamosDb:PrestamosSQLite

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
    private val editSocioLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val dbHelper = SociosSQLite(this)
            val socioActualizado = dbHelper.getSocioById(socioId)
            if (socioActualizado != null) {
                mostrarSocio(socioActualizado)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.socio_detail_activity)
        supportActionBar?.title = "RR - Socio detalle"

        sociosDb = SociosSQLite(this)
        prestamosDb = PrestamosSQLite(this)

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
        val socio = sociosDb.getSocioById(socioId)

        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        if (socio != null) {
            mostrarSocio(socio)
//
//            editSocioButton.setOnClickListener {
//                val intent = Intent(this, SocioEditActivity::class.java)
//                intent.putExtra("socioId", socioId)
//                editSocioLauncher.launch(intent)
//            }

        editSocioButton.setOnClickListener {
            // Verificar si el socio tiene préstamos activos
            val tienePrestamosActivos = prestamosDb.estaSocioEnPrestamoActivo(socioId)

            if (tienePrestamosActivos) {
                Toast.makeText(
                    this,
                    "No se puede editar el socio. Está presente en un préstamo activo.",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val intent = Intent(this, SocioEditActivity::class.java)
                intent.putExtra("socioId", socioId)
                editSocioLauncher.launch(intent)
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
                val tieneRegistrosEnPrestamos = prestamosDb.estaSocioEnPrestamoActivo(socioId)

                if (tieneRegistrosEnPrestamos) {
                    Toast.makeText(
                        this,
                        "No se puede eliminar el socio porque tiene registros con préstamos activos",
                        Toast.LENGTH_SHORT
                    ).show()
                }else setContent {
                    var showDialog by remember { mutableStateOf(true) }

                    if (showDialog) {
                        DialogoUtil(this).ShowDeleteConfirmationDialog(
                            title = "Eliminar socio",
                            message = "¿Estás seguro de que quieres eliminar este socio?",
                            onPositiveButtonClick = {
                                sociosDb.deleteSocio(socioId)
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


    @SuppressLint("SetTextI18n")
    private fun mostrarSocio(socio: Socio) {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        nombreTextView.text = "${socio.nombre}"+" "+"${ socio.apellido}"
        numeroSocioTextView.text = "Nº Socio: ${socio.numeroSocio}"
        telefonoTextView.text = "${socio.telefono}"
        emailTextView.text = "${socio.email}"
        fechaNacimientoTextView.text = "Fecha de nacimiento: ${dateFormat.format(socio.fechaNacimiento)}"
        fechaIngresoSocioTextView.text = "Fecha de ingreso: ${dateFormat.format(socio.fechaIngresoSocio)}"
        generoTextView.text = "Género: ${socio.genero}"

        val prestamos = socio.idSocio?.let { prestamosDb.getPrestamosBySocio(it) }

        val cantidadPrestamos = prestamos?.size
        val articulos = mutableSetOf<Articulo>()
        if (prestamos != null) {
            for (prestamo in prestamos) {
                val articulo = prestamosDb.getPrestamoByIdArticulo(prestamo.idArticulo)
                if (articulo != null) {
                    articulos.add(articulo)
                }
            }
        }

        cantidadPrestamosTextView.text = "Cantidad de préstamos: \t$cantidadPrestamos"
        articulosTextView.text = "Últimos artículos usados:"+
                "\n${articulos.distinctBy { it.idArticulo }.takeLast(3).joinToString (separator = "")  {"${it.nombre.toString()}\n"}}"
    }


}