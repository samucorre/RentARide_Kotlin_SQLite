package pf.dam.socios

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import db.SociosSQLite
import pf.dam.R

class SocioDetalleActivity : AppCompatActivity() {
    private lateinit var editSocioButton: FloatingActionButton
    private lateinit var deleteSocioButton: FloatingActionButton
    private lateinit var backButton: FloatingActionButton
    private lateinit var dbHelper: SociosSQLite

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

        editSocioButton = findViewById(R.id.editSocioButton)
        deleteSocioButton = findViewById(R.id.deleteSocioButton)
        backButton = findViewById(R.id.backButton)

        nombreTextView = findViewById(R.id.nombreTextView)
        apellidoTextView = findViewById(R.id.apellidoTextView)
        numeroSocioTextView = findViewById(R.id.numeroSocioTextView)
        telefonoTextView = findViewById(R.id.telefonoTextView)
        emailTextView = findViewById(R.id.emailTextView)

        socioId = intent.getIntExtra("idSocio", -1)
        val socio = dbHelper.obtenerSocioPorId(socioId)

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

            deleteSocioButton.setOnClickListener {
                dbHelper.borrarSocio(socioId)
                Toast.makeText(this, "Socio eliminado", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }

            backButton.setOnClickListener {
                finish()
            }
        } else {
            Toast.makeText(this, "Socio no encontrado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun mostrarSocio(socio: Socio) {
        nombreTextView.text = "Nombre: ${socio.nombre}"
        apellidoTextView.text = "Apellido: ${socio.apellido}"
        numeroSocioTextView.text = "Número de socio: ${socio.numeroSocio}"
        telefonoTextView.text = "Teléfono: ${socio.telefono}"
        emailTextView.text = "Email: ${socio.email}"
    }
}
