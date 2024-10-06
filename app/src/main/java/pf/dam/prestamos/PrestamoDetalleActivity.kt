package pf.dam.prestamos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import db.PrestamosSQLite
import pf.dam.R
import pf.dam.articulos.EstadoArticulo
import java.text.SimpleDateFormat
import java.util.Locale

class PrestamoDetalleActivity : AppCompatActivity() {
    private lateinit var editPrestamoButton: FloatingActionButton
    private lateinit var deletePrestamoButton: FloatingActionButton
    private lateinit var backButton: FloatingActionButton
    private lateinit var dbHelper: PrestamosSQLite
    private lateinit var articulosDbHelper: ArticulosSQLite

    private lateinit var articuloTextView: TextView
    private lateinit var socioTextView: TextView
    private lateinit var fechaInicioTextView: TextView
    private lateinit var fechaFinTextView: TextView
    private lateinit var infoTextView: TextView

    private var prestamoId: Int = -1
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    private val editPrestamoLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val prestamoActualizado = dbHelper.obtenerPrestamoPorId(prestamoId)
            if (prestamoActualizado != null) {
                mostrarPrestamo(prestamoActualizado)
            }
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prestamo_detail)

        dbHelper = PrestamosSQLite(this)
        articulosDbHelper = ArticulosSQLite(this)

        editPrestamoButton = findViewById(R.id.editPrestamoButton)
        deletePrestamoButton = findViewById(R.id.deletePrestamoButton)
        backButton = findViewById(R.id.backButton)

        articuloTextView = findViewById(R.id.articuloTextView)
        socioTextView = findViewById(R.id.socioTextView)
        fechaInicioTextView = findViewById(R.id.fechaInicioTextView)
        fechaFinTextView = findViewById(R.id.fechaFinTextView)
        infoTextView = findViewById(R.id.infoTextView)

        prestamoId = intent.getIntExtra("idPrestamo", -1)
        val prestamo = dbHelper.obtenerPrestamoPorId(prestamoId)

        if (prestamo != null) {
            mostrarPrestamo(prestamo)

            editPrestamoButton.setOnClickListener {
                val intent = Intent(this, PrestamoEditActivity::class.java)
                intent.putExtra("prestamoId", prestamoId)
                editPrestamoLauncher.launch(intent)
            }
            val editPrestamoLauncher = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == RESULT_OK) {
                    setResult(RESULT_OK, Intent().putExtra("prestamoId", prestamoId))
                    finish()
                }
            }

            deletePrestamoButton.setOnClickListener {
                // Actualizar el estado del artículo a DISPONIBLE
                articulosDbHelper.actualizarEstadoArticulo(
                    prestamo.idArticulo,
                    EstadoArticulo.DISPONIBLE
                )

                dbHelper.borrarPrestamo(prestamoId)
                Toast.makeText(this, "Préstamo eliminado", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }

            backButton.setOnClickListener {
                finish()
            }
        } else {
            Toast.makeText(this, "Préstamo no encontrado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun mostrarPrestamo(prestamo: Prestamo) {
        articuloTextView.text = "ID de Artículo: ${prestamo.idArticulo}"
        socioTextView.text = "ID de Socio: ${prestamo.idSocio}"
        fechaInicioTextView.text = "Fecha de Inicio: ${dateFormat.format(prestamo.fechaInicio)}"
        fechaFinTextView.text = "Fecha de Fin: ${dateFormat.format(prestamo.fechaFin)}"
        infoTextView.text = "Información Adicional: ${prestamo.info}"
    }
}