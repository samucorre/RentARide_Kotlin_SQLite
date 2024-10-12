package pf.dam.prestamos

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import db.ArticulosSQLite
import db.PrestamosSQLite
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
    private lateinit var estadoSpinner: Spinner
    private lateinit var guardarButton: Button
    private lateinit var volverButton: Button

    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val calendar = Calendar.getInstance()

    private var prestamoId: Int = -1
    private lateinit var prestamo: Prestamo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prestamo_edit)

        dbHelper = PrestamosSQLite(this)
        articulosDbHelper = ArticulosSQLite(this)
        fechaInicioEditText = findViewById(R.id.fechaInicioEditText)

        infoEditText = findViewById(R.id.infoEditText)
        estadoSpinner = findViewById(R.id.estadoSpinner)
        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)

        prestamoId = intent.getIntExtra("prestamoId", -1)
        prestamo = dbHelper.obtenerPrestamoPorId(prestamoId)!!

        fechaInicioEditText.setText(dateFormat.format(prestamo.fechaInicio))

        infoEditText.setText(prestamo.info)

        // Configurar el adaptador del Spinner
        val estados = arrayOf(EstadoPrestamo.ACTIVO, EstadoPrestamo.CERRADO)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, estados)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        estadoSpinner.adapter = adapter

        // Establecer la selección inicial del Spinner
        val estadoIndex = estados.indexOf(prestamo.estado)
        estadoSpinner.setSelection(if (estadoIndex >= 0) estadoIndex else 0)

        fechaInicioEditText.setOnClickListener {
            mostrarDatePicker(fechaInicioEditText)
        }


        guardarButton.setOnClickListener {
            val fechaInicio = dateFormat.parse(fechaInicioEditText.text.toString())
            //val fechaFin = dateFormat.parse(fechaFinEditText.text.toString())
            val estadoSeleccionado = estadoSpinner.selectedItem as EstadoPrestamo
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