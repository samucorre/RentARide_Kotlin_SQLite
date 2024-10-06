package pf.dam.socios

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.setText
import androidx.compose.ui.semantics.text
import db.SociosSQLite
import pf.dam.R

class SocioEditActivity : AppCompatActivity() {

    private lateinit var dbHelper: SociosSQLite
    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var numeroSocioEditText: EditText
    private lateinit var telefonoEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var guardarButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socio_edit)

        dbHelper = SociosSQLite(this)
        nombreEditText = findViewById(R.id.nombreEditText)
        apellidoEditText = findViewById(R.id.apellidoEditText)
        numeroSocioEditText = findViewById(R.id.numeroSocioEditText)
        telefonoEditText = findViewById(R.id.telefonoEditText)
        emailEditText = findViewById(R.id.emailEditText)
        guardarButton = findViewById(R.id.guardarButton)

        val socioId = intent.getIntExtra("socioId", -1)
        val socio = dbHelper.obtenerSocioPorId(socioId)

        if (socio != null) {
            nombreEditText.setText(socio.nombre)
            apellidoEditText.setText(socio.apellido)
            numeroSocioEditText.setText(socio.numeroSocio.toString())
            telefonoEditText.setText(socio.telefono.toString())
            emailEditText.setText(socio.email)

            guardarButton.setOnClickListener {
                val socioActualizado = Socio(
                    socio.idSocio, // Incluir el idSocio

                    nombreEditText.text.toString(),
                    apellidoEditText.text.toString(),
                    numeroSocioEditText.text.toString().toInt(),
                    telefonoEditText.text.toString().toInt(),
                    emailEditText.text.toString()
                )
                dbHelper.actualizarSocio(socioActualizado)
               // dbHelper.actualizarSocio(socioActualizado)
                Toast.makeText(this, "Socio actualizado", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            }
        } else {
            Toast.makeText(this, "Socio no encontrado", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}