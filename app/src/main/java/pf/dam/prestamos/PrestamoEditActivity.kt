package pf.dam.prestamos

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import db.PrestamosSQLite
import pf.dam.MainActivity
import pf.dam.R
import pf.dam.articulos.EstadoArticulo
import java.text.SimpleDateFormat
import java.util.*

class PrestamoEditActivity : AppCompatActivity() {

    private lateinit var dbHelper: PrestamosSQLite
    private lateinit var articulosDbHelper: ArticulosSQLite
    private lateinit var fechaInicioEditText: EditText
    private lateinit var fechaFinEditText: EditText
    private lateinit var infoEditText: EditText
    private lateinit var estadoSwitch: Switch
    private lateinit var guardarButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val calendar = Calendar.getInstance()

    private var prestamoId: Int = -1
    private lateinit var prestamo: Prestamo

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prestamo_edit)

        dbHelper = PrestamosSQLite(this)
        articulosDbHelper = ArticulosSQLite(this)
        fechaInicioEditText = findViewById(R.id.fechaInicioEditText)

        infoEditText = findViewById(R.id.infoEditText)
        estadoSwitch = findViewById(R.id.estadoSwitch)
        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)
        homeButton = findViewById(R.id.homeButton)

        prestamoId = intent.getIntExtra("prestamoId", -1)
        prestamo = dbHelper.obtenerPrestamoPorId(prestamoId)!!

        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        if (prestamo.estado == EstadoPrestamo.CERRADO) {
            fechaInicioEditText.isEnabled = false
            infoEditText.isEnabled = true
            estadoSwitch.isEnabled = false
            guardarButton.isEnabled = false
        }

        fechaInicioEditText.setText(dateFormat.format(prestamo.fechaInicio))

        infoEditText.setText(prestamo.info)

        estadoSwitch.isChecked = prestamo.estado == EstadoPrestamo.ACTIVO

        fechaInicioEditText.setOnClickListener {
            mostrarDatePicker(fechaInicioEditText)
        }


        guardarButton.setOnClickListener {
            val fechaInicio = dateFormat.parse(fechaInicioEditText.text.toString())
            //val fechaFin = dateFormat.parse(fechaFinEditText.text.toString())
            val estadoSeleccionado = if (estadoSwitch.isChecked) EstadoPrestamo.ACTIVO else EstadoPrestamo.CERRADO
            val fechaFin = if (estadoSeleccionado == EstadoPrestamo.CERRADO) {
                Date() // Fecha actual
            } else {
                prestamo.fechaFin // Mantener la fecha de fin original si el estado no es CERRADO
            }
            val info = infoEditText.text.toString()


            val prestamoActualizado = Prestamo(
                prestamo.idPrestamo,
                prestamo.idArticulo,
                prestamo.idSocio,
                fechaInicio,
                fechaFin,
                info,
                estadoSeleccionado
            )
            dbHelper.actualizarPrestamo(prestamoActualizado)

            if (estadoSeleccionado == EstadoPrestamo.CERRADO) {
                articulosDbHelper.actualizarEstadoArticulo(prestamo.idArticulo, EstadoArticulo.DISPONIBLE)
            }

            Toast.makeText(this, "Préstamo actualizado", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        }

        volverButton.setOnClickListener { finish() }
    }

    private fun mostrarDatePicker(editText: EditText) {
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                editText.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }
}