package pf.dam.socios

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.SociosSQLite
import pf.dam.MainActivity
import pf.dam.R
import pf.dam.utils.DateUtil
import java.text.ParseException

class SocioAddActivity : AppCompatActivity() {
    private lateinit var dbSocios: SociosSQLite
    private lateinit var dateUtil: DateUtil
    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var numeroSocioEditText: EditText
    private lateinit var telefonoEditText: EditText
    private lateinit var emailEditText: EditText
//    private lateinit var fechaNacimientoEditText: EditText
//    private lateinit var fechaIngresoSocioEditText: EditText

        private lateinit var fechaNacimientoButton: Button
    private lateinit var fechaIngresoSocioButton: Button
    private lateinit var generoRadioGroup: RadioGroup
    private lateinit var guardarButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socio_add)

        dbSocios = SociosSQLite(this)
        dateUtil = DateUtil(this)

        nombreEditText = findViewById(R.id.nombreEditText)
        apellidoEditText = findViewById(R.id.apellidoEditText)
        numeroSocioEditText = findViewById(R.id.numeroSocioEditText)
        telefonoEditText = findViewById(R.id.telefonoEditText)
        emailEditText = findViewById(R.id.emailEditText)
        guardarButton = findViewById(R.id.guardarButton)
        volverButton = findViewById(R.id.volverButton)
        homeButton = findViewById(R.id.homeButton)
        emailEditText = findViewById(R.id.emailEditText)
//        fechaNacimientoEditText = findViewById(R.id.fechaNacimientoEditText)
//        fechaIngresoSocioEditText = findViewById(R.id.fechaIngresoSocioEditText)
        fechaNacimientoButton = findViewById(R.id.fechaNacimientoButton)
        fechaIngresoSocioButton = findViewById(R.id.fechaIngresoSocioButton)
        generoRadioGroup = findViewById(R.id.generoRadioGroup)

        volverButton.setOnClickListener { finish() }
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

//        fechaNacimientoEditText.setOnClickListener {
//            dateUtil.mostrarDatePickerEditText(this, fechaNacimientoEditText)
//        }
//        fechaIngresoSocioEditText.setOnClickListener {
//            dateUtil.mostrarDatePickerEditText(this, fechaIngresoSocioEditText)
//        }
        fechaNacimientoButton.setOnClickListener {
            dateUtil.mostrarDatePicker(this, fechaNacimientoButton)
        }
        fechaIngresoSocioButton.setOnClickListener {
            dateUtil.mostrarDatePicker(this, fechaIngresoSocioButton)
        }


//
        guardarButton.setOnClickListener {

            val nombre = nombreEditText.text.toString()
            val apellido = apellidoEditText.text.toString()
            val numeroSocio = numeroSocioEditText.text.toString().toIntOrNull()
            val telefono = telefonoEditText.text.toString().toIntOrNull()
            val email = emailEditText.text.toString()
            val fechaNacimientoString = fechaNacimientoButton.text.toString()
            val fechaIngresoSocioString = fechaNacimientoButton.text.toString()
            if (fechaNacimientoString.isEmpty()|| fechaIngresoSocioString.isEmpty()) {
                // Mostrar un mensaje de error al usuario
                Toast.makeText(this, "La fechas son obligatorias", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
              val genero = when (generoRadioGroup.checkedRadioButtonId) {
                R.id.hombreRadioButton -> Genero.HOMBRE
                R.id.mujerRadioButton -> Genero.MUJER
                else -> null
            }

            try {
                val fechaNacimiento = dateUtil.dateFormat.parse(fechaNacimientoString)
                val fechaIngresoSocio = dateUtil.dateFormat.parse(fechaIngresoSocioString)
                val nuevoSocio = Socio(
                    nombre = nombre,
                    apellido = apellido,
                    numeroSocio = numeroSocio,
                    telefono = telefono,
                    email = email,
                    fechaNacimiento = fechaNacimiento,
                    fechaIngresoSocio = fechaIngresoSocio,
                    genero = genero
                )
                dbSocios.insertarSocio(nuevoSocio)
                Toast.makeText(this, "Socio añadido", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: ParseException) {
                Log.e("Error", "Error al analizar la fecha: ${e.message}")
                Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show()


            }
        }
    }
}