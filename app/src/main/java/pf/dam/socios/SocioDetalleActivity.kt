package pf.dam.socios

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.MainActivity
import pf.dam.R
import pf.dam.prestamos.PrestamoAddActivity
import pf.dam.prestamos.PrestamoAddSocioActivity
import pf.dam.utils.ShowDeleteConfirmationDialog

class SocioDetalleActivity : AppCompatActivity() {
    private lateinit var editSocioButton: FloatingActionButton
    private lateinit var deleteSocioButton: FloatingActionButton
    private lateinit var backButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton
    private lateinit var addPrestamoButton: Button
    private lateinit var dbHelper: SociosSQLite
    private lateinit var prestamosDbHelper:PrestamosSQLite

    private lateinit var nombreTextView: TextView
    private lateinit var apellidoTextView: TextView
    private lateinit var numeroSocioTextView: TextView
    private lateinit var telefonoTextView: TextView
    private lateinit var emailTextView: TextView

    private var socioId: Int = -1

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
        apellidoTextView = findViewById(R.id.apellidoTextView)
        numeroSocioTextView = findViewById(R.id.numeroSocioTextView)
        telefonoTextView = findViewById(R.id.telefonoTextView)
        emailTextView = findViewById(R.id.emailTextView)

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
                val prestamosActivos = prestamosDbHelper.estaSocioEnPrestamo(socioId)
                if (prestamosActivos) {
                    Toast.makeText(
                        this,
                        "No se puede eliminar el socio porque tiene préstamos activos",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    setContent {
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
            }}}


    private fun mostrarSocio(socio: Socio) {
        nombreTextView.text = "Nombre: ${socio.nombre}"
        apellidoTextView.text = "Apellido: ${socio.apellido}"
        numeroSocioTextView.text = "Número de socio: ${socio.numeroSocio}"
        telefonoTextView.text = "Teléfono: ${socio.telefono}"
        emailTextView.text = "Email: ${socio.email}"
    }
}

