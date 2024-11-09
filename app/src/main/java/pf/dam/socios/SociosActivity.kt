package pf.dam.socios

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.compose.ui.test.filter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.MainActivity
import pf.dam.R
import pf.dam.prestamos.EstadoPrestamo
import kotlin.text.contains

class SociosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var sociosAdapter: SociosAdapter
    private lateinit var dbSocios: SociosSQLite
    private lateinit var dbPrestamos: PrestamosSQLite
    private lateinit var addSocioButton: FloatingActionButton
    private lateinit var backButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton
    private lateinit var estadoRadioGroup: RadioGroup


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_socios)

        dbSocios = SociosSQLite(this)
        dbPrestamos = PrestamosSQLite(this)

        recyclerView = findViewById(R.id.sociosRecyclerView)
        searchView = findViewById(R.id.searchView)
        addSocioButton = findViewById(R.id.addSocioButton)
      //  backButton = findViewById(R.id.backButton)
        homeButton = findViewById(R.id.homeButton)
        estadoRadioGroup = findViewById(R.id.estadoRadioGroup)

        sociosAdapter = SociosAdapter(dbSocios.obtenerSocios())
        recyclerView.adapter = sociosAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        supportActionBar?.title = "RR - Socios"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarSociosConPrestamos(newText)
                return true
            }
        })
        estadoRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val estadoSeleccionado = when (checkedId) {
                R.id.todosRadioButton -> null
                R.id.disponibleRadioButton -> EstadoPrestamo.ACTIVO
                R.id.prestadoRadioButton -> EstadoPrestamo.CERRADO
                else -> null
            }

            filtrarSociosPorPrestamosActivos(estadoSeleccionado)
        }

        addSocioButton.setOnClickListener{
            val intent = Intent(this, SocioAddActivity::class.java)
            startActivity(intent)
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

}
    override fun onResume() {
        super.onResume()
        actualizarListaSocios()
    }

    private fun actualizarListaSocios() {
        sociosAdapter.socios = dbSocios.obtenerSocios()
        sociosAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val socioId = data?.getIntExtra("idSocio", -1) ?: -1
            if (socioId != -1) {
                val socioActualizado = dbSocios.obtenerSocioPorId(socioId)
                if (socioActualizado != null) {
                    // Actualizar la vista con articuloActualizado
                    sociosAdapter.socios = dbSocios.obtenerSocios()
                    sociosAdapter.notifyDataSetChanged()
                }
            }
        }
    }
    private fun filtrarSociosConPrestamos(query: String?) {
        val sociosFiltrados = if (query.isNullOrEmpty()) {
            dbSocios.obtenerSocios()
        } else {
            dbSocios.obtenerSocios().filter { socio ->
                socio.nombre?.contains(query, ignoreCase = true) ?: false ||
                        socio.apellido?.contains(query, ignoreCase = true) ?: false ||
                        socio.numeroSocio.toString().contains(query, ignoreCase = true) ||
                        socio.telefono.toString().contains(query, ignoreCase = true) ||
                        socio.email?.contains(query, ignoreCase = true) ?: false ||
                        socio.fechaNacimiento?.toString()?.contains(query, ignoreCase = true) ?: false ||
                        socio.fechaIngresoSocio?.toString()?.contains(query, ignoreCase = true) ?: false ||
                        socio.genero?.toString()?.contains(query, ignoreCase = true) ?: false
            }
        }
        sociosAdapter.socios = sociosFiltrados
        sociosAdapter.notifyDataSetChanged()
    }
        private fun filtrarSociosPorPrestamosActivos(estadoSeleccionado: EstadoPrestamo?) {
            val sociosFiltrados = when (estadoSeleccionado) {
                EstadoPrestamo.ACTIVO -> dbPrestamos.obtenerSociosConPrestamosActivos(this)
                EstadoPrestamo.CERRADO -> dbPrestamos.obtenerSociosConPrestamosCerrados(this)
                else -> dbSocios.obtenerSocios() // Todos los socios
            }
            sociosAdapter.socios = sociosFiltrados as List<Socio>
            sociosAdapter.notifyDataSetChanged()
        }
    }


