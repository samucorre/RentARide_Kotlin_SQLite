package pf.dam.socios

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.SociosSQLite
import pf.dam.MainActivity
import pf.dam.R

class SocioAddActivity : AppCompatActivity() {
    private lateinit var dbHelper: SociosSQLite
    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var numeroSocioEditText: EditText
    private lateinit var telefonoEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var guardarButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socio_add)

        dbHelper = SociosSQLite(this)
        nombreEditText = findViewById(R.id.nombreEditText)
        apellidoEditText = findViewById(R.id.apellidoEditText)
        numeroSocioEditText = findViewById(R.id.numeroSocioEditText)
        telefonoEditText = findViewById(R.id.telefonoEditText)
        emailEditText = findViewById(R.id.emailEditText)
        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)
        homeButton = findViewById(R.id.homeButton)

        volverButton.setOnClickListener { finish() }

      /* PARA NO ADMITIR CAMPOS VACIOS
        guardarButton.setOnClickListener {

            val nombre = nombreEditText.text.toString()
            val apellido = apellidoEditText.text.toString()
            val numeroSocio = numeroSocioEditText.text.toString().toIntOrNull() ?: 0
            val telefono = telefonoEditText.text.toString().toIntOrNull() ?: 0
            val email = emailEditText.text.toString()

            val nuevoSocio = Socio(nombre, apellido, numeroSocio, telefono, email)
            dbHelper.insertarSocio(nuevoSocio)

            Toast.makeText(this, "Socio añadido", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}*/
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        guardarButton.setOnClickListener {
            val nombre = nombreEditText.text.toString()
            val apellido = apellidoEditText.text.toString()
            val numeroSocio = numeroSocioEditText.text.toString().toIntOrNull()
            val telefono = telefonoEditText.text.toString().toIntOrNull()
            val email = emailEditText.text.toString()

            if (nombre.isBlank() || apellido.isBlank() || numeroSocio == null || telefono == null || email.isBlank()) {
                Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                val nuevoSocio = Socio(nombre =nombre, apellido =apellido, numeroSocio =numeroSocio, telefono = telefono, email =email)
                dbHelper.insertarSocio(nuevoSocio)

                Toast.makeText(this, "Socio añadido", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}