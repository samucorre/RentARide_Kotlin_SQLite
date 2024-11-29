package pf.dam.socios

import android.annotation.SuppressLint
import android.content.Intent
import android.database.sqlite.SQLiteException
import android.os.Bundle
import android.text.Editable
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.MainActivity
import pf.dam.R
import pf.dam.utils.FechaUtils
import java.text.ParseException


class SocioEditActivity : AppCompatActivity() {

    private lateinit var dbSocios: SociosSQLite
    private lateinit var dbPrestamos: PrestamosSQLite
    private lateinit var fechaUtils: FechaUtils
    private lateinit var nombreEditText: EditText
    private lateinit var apellidoEditText: EditText
    private lateinit var numeroSocioEditText: EditText
    private lateinit var telefonoEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var fechaNacimientoEditText: EditText
    private lateinit var fechaIngresoSocioEditText: EditText
    private lateinit var generoRadioGroup: RadioGroup
    private lateinit var guardarButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton

    private var socioId: Int = -1

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.socio_edit_activity)
        supportActionBar?.title = "RR - Editar socio"

        dbSocios = SociosSQLite(this)
        dbPrestamos = PrestamosSQLite(this)
        fechaUtils = FechaUtils(this)

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
        val socio = dbSocios.getSocioById(socioId)

        fechaNacimientoEditText.setOnClickListener {
            fechaUtils.mostrarDatePickerEditText(this, fechaNacimientoEditText)
        }

        fechaIngresoSocioEditText.setOnClickListener {
            fechaUtils.mostrarDatePickerEditText(this, fechaIngresoSocioEditText)
        }


        if (socio != null) {
            nombreEditText.setText(socio.nombre)
            apellidoEditText.setText(socio.apellido)
            numeroSocioEditText.setText(socio.numeroSocio.toString())
            telefonoEditText.setText(socio.telefono.toString())
            emailEditText.setText(socio.email)
            fechaNacimientoEditText.text =
                Editable.Factory.getInstance().newEditable(socio.fechaNacimiento?.let {
                    fechaUtils.dateFormat.format(it)
                })
            fechaIngresoSocioEditText.text =
                Editable.Factory.getInstance().newEditable(socio.fechaIngresoSocio?.let {
                    fechaUtils.dateFormat.format(it)
                })

            when (socio.genero) {
                Genero.HOMBRE -> generoRadioGroup.check(R.id.hombreRadioButton)
                Genero.MUJER -> generoRadioGroup.check(R.id.mujerRadioButton)
                else -> {}
            }

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
                val fechaNacimientoString = fechaNacimientoEditText.text.toString()
                val fechaIngresoSocioString = fechaIngresoSocioEditText.text.toString()
                val genero = when (generoRadioGroup.checkedRadioButtonId) {
                    R.id.hombreRadioButton -> Genero.HOMBRE
                    R.id.mujerRadioButton -> Genero.MUJER
                    else -> null
                }
                if (nombre.isBlank() || apellido.isBlank() || numeroSocio == null || telefono == null || email.isBlank() || fechaNacimientoString.isBlank() || fechaIngresoSocioString.isBlank() || genero == null) {
                    Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                try {
                    val fechaNacimiento = fechaUtils.dateFormat.parse(fechaNacimientoString)
                    val fechaIngresoSocio = fechaUtils.dateFormat.parse(fechaIngresoSocioString)
                    val genero = when (generoRadioGroup.checkedRadioButtonId) {
                        R.id.hombreRadioButton -> Genero.HOMBRE
                        R.id.mujerRadioButton -> Genero.MUJER
                        else -> null
                    }
                    try {
                        if (socio.idSocio?.let { dbPrestamos.estaSocioEnPrestamoActivo(it) } == true) {
                            Toast.makeText(
                                this,
                                "No se puede editar el socio. Está presente en un préstamo activo.",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val socioActualizado = Socio(
                                socio.idSocio, nombre, apellido, numeroSocio,
                                telefono, email, fechaNacimiento,
                                fechaIngresoSocio,
                                genero
                            )
                            dbSocios.updateSocio(socioActualizado)
                            Toast.makeText(this, "Socio actualizado", Toast.LENGTH_SHORT).show()
                            setResult(RESULT_OK)
                            finish()
                        }
                    } catch (e: SQLiteException) {
                        Toast.makeText(
                            this,
                            "Error al acceder a la base de datos",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } catch (e: ParseException) {
                    Toast.makeText(this, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show()
                }
            }
            volverButton.setOnClickListener { finish() }
        }
    }
}
