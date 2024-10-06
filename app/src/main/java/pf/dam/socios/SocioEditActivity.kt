package pf.dam.socios

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.R

class SocioEditActivity : AppCompatActivity() {

    private lateinit var dbHelper: SociosSQLite
    private lateinit var prestamosDbHelper: PrestamosSQLite // Instancia de PrestamosSQLite

    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var numeroSocioEditText: EditText
    private lateinit var telefonoEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var guardarButton: Button
    private lateinit var volverButton: Button

    private var socioId: Int = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socio_edit)

        dbHelper = SociosSQLite(this)
        prestamosDbHelper = PrestamosSQLite(this)
        nombreEditText = findViewById(R.id.nombreEditText)
        apellidoEditText = findViewById(R.id.apellidoEditText)
        numeroSocioEditText = findViewById(R.id.numeroSocioEditText)
        telefonoEditText = findViewById(R.id.telefonoEditText)
        emailEditText = findViewById(R.id.emailEditText)
        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)

        socioId = intent.getIntExtra("socioId", -1)
        val socio = dbHelper.obtenerSocioPorId(socioId)

        if (socio != null) {
            nombreEditText.setText(socio.nombre)
            apellidoEditText.setText(socio.apellido)
            numeroSocioEditText.setText(socio.numeroSocio.toString())
            telefonoEditText.setText(socio.telefono.toString())
            emailEditText.setText(socio.email)

            guardarButton.setOnClickListener {
                val nombre = nombreEditText.text.toString()
                val apellido = apellidoEditText.text.toString()
                val numeroSocio = numeroSocioEditText.text.toString().toIntOrNull() ?: 0
                val telefono = telefonoEditText.text.toString().toIntOrNull() ?: 0
                val email = emailEditText.text.toString()

                try {
                    if (socio.idSocio?.let { prestamosDbHelper.estaSocioEnPrestamo(it) }
                            ?: false) { // Llamar a la función desde prestamosDbHelper
                        // Mostrar un mensaje de error al usuario
                        Toast.makeText(
                            this,
                            "No se puede editar el socio. Está presente en un préstamo activo.",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val socioActualizado =
                            Socio(socio.idSocio, nombre, apellido, numeroSocio, telefono, email)
                        dbHelper.actualizarSocio(socioActualizado)
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
