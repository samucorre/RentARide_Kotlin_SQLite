package pf.dam.socios

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.MainActivity
import pf.dam.R
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale


class SocioEditActivity : AppCompatActivity() {

    private lateinit var dbSocios: SociosSQLite
    private lateinit var dbPrestamos: PrestamosSQLite
    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var numeroSocioEditText: EditText
    private lateinit var telefonoEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var fechaNacimientoEditText: EditText
    private lateinit var fechaIngresoSocioEditText: EditText
    private lateinit var generoRadioGroup: RadioGroup
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())


    private lateinit var guardarButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton

    private var socioId: Int = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socio_edit)

        dbSocios = SociosSQLite(this)
        dbPrestamos = PrestamosSQLite(this)
        nombreEditText = findViewById(R.id.nombreEditText)
        apellidoEditText = findViewById(R.id.apellidoEditText)
        numeroSocioEditText = findViewById(R.id.numeroSocioEditText)
        telefonoEditText = findViewById(R.id.telefonoEditText)
        emailEditText = findViewById(R.id.emailEditText)
        fechaNacimientoEditText = findViewById(R.id.fechaNacimientoEditText)
        fechaIngresoSocioEditText = findViewById(R.id.fechaIngresoSocioEditText)
        generoRadioGroup = findViewById(R.id.generoRadioGroup)
        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)
        homeButton = findViewById(R.id.homeButton)

        socioId = intent.getIntExtra("socioId", -1)
        val socio = dbSocios.obtenerSocioPorId(socioId)

        if (socio != null) {
            nombreEditText.setText(socio.nombre)
            apellidoEditText.setText(socio.apellido)
            numeroSocioEditText.setText(socio.numeroSocio.toString())
            telefonoEditText.setText(socio.telefono.toString())
            emailEditText.setText(socio.email)
            fechaNacimientoEditText.setText(socio.fechaNacimiento.toString())
            fechaIngresoSocioEditText.setText(socio.fechaIngresoSocio.toString())
            when (socio.genero) {
                Genero.HOMBRE -> generoRadioGroup.check(R.id.hombreRadioButton)
                Genero.MUJER -> generoRadioGroup.check(R.id.mujerRadioButton)
                else -> { /* No hacer nada o establecer un valor por defecto */ }
            }


            homeButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
            guardarButton.setOnClickListener {
                val nombre = nombreEditText.text.toString()
                val apellido = apellidoEditText.text.toString()
                val numeroSocio = numeroSocioEditText.text.toString().toIntOrNull() ?: 0
                val telefono = telefonoEditText.text.toString().toIntOrNull() ?: 0
                val email = emailEditText.text.toString()
                val fechaNacimiento = try {
                    dateFormat.parse(fechaNacimientoEditText.text.toString())
                } catch (e: ParseException) {
                    null // o un valor por defecto si lo prefieres
                }
                val fechaIngresoSocio = try {
                    dateFormat.parse(fechaIngresoSocioEditText.text.toString())
                } catch (e: ParseException) {
                    null // o un valor por defecto si lo prefieres
                }

                // Manejar el caso nulo para genero
                val genero = when (generoRadioGroup.checkedRadioButtonId) {
                    R.id.hombreRadioButton -> Genero.HOMBRE
                    R.id.mujerRadioButton -> Genero.MUJER
                    else -> null // o el valor por defecto que desees
                }

                try {
                    if (socio.idSocio?.let { dbPrestamos.estaSocioEnPrestamoActivo(it) }
                            ?: false) { // Llamar a la función desde prestamosDbHelper
                        // Mostrar un mensaje de error al usuario
                        Toast.makeText(
                            this,
                            "No se puede editar el socio. Está presente en un préstamo activo.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val socioActualizado =
                            Socio(socio.idSocio, nombre, apellido, numeroSocio,
                                telefono, email, fechaNacimiento,
                                fechaIngresoSocio,
                                genero)
                        dbSocios.actualizarSocio(socioActualizado)
                        Toast.makeText(this, "Socio actualizado", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish()
                    }
                } catch (e: SQLiteException) {
                    // Manejar la excepción, por ejemplo, mostrar un mensaje de error al usuario
                    Log.e("SocioEditActivity", "Error al acceder a la base de datos: ${e.message}")
                    Toast.makeText(this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            volverButton.setOnClickListener { finish() }
        }
    }
}
