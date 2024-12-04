package pf.dam.prestamos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.MainActivity
import pf.dam.PrestamosAdapter
import pf.dam.R
import kotlin.text.contains

class PrestamosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var prestamosAdapter: PrestamosAdapter
    private lateinit var dbHelper: PrestamosSQLite
    private lateinit var addPrestamoButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton
    private lateinit var estadoRadioGroup: RadioGroup
    private lateinit var searchView: SearchView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.prestamos_activity)
        supportActionBar?.title = "RR - Préstamos"

        dbHelper = PrestamosSQLite(this)

        recyclerView = findViewById(R.id.prestamosRecyclerView)
        addPrestamoButton = findViewById(R.id.addPrestamoButton)
        homeButton = findViewById(R.id.homeButton)
        estadoRadioGroup = findViewById(R.id.estadoRadioGroup)
        searchView = findViewById(R.id.searchView)

        prestamosAdapter = PrestamosAdapter(dbHelper.getAllPrestamos())
        recyclerView.adapter = prestamosAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        addPrestamoButton.setOnClickListener {
            val intent = Intent(this, PrestamoAddActivity::class.java)
            startActivity(intent)
        }
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filtrarPrestamos(newText)
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

            filtrarPrestamosPorEstado(estadoSeleccionado)
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarListaPrestamos()
    }

    private fun actualizarListaPrestamos() {
        prestamosAdapter.prestamos = dbHelper.getAllPrestamos()
        prestamosAdapter.notifyDataSetChanged()
    }

    private fun filtrarPrestamos(query: String?) {
        val prestamosFiltrados = if (query.isNullOrEmpty()) {
            dbHelper.getAllPrestamos()
        } else {
            dbHelper.getAllPrestamos().filter { prestamo ->
                val articulosDbHelper = ArticulosSQLite(this)
                val articulo = articulosDbHelper.getArticuloById(prestamo.idArticulo)
                val coincideArticulo = articulo?.nombre?.contains(query, ignoreCase = true) ?: false ||
                        articulo?.categoria?.contains(query, ignoreCase = true) ?: false ||
                        articulo?.tipo?.contains(query, ignoreCase = true) ?: false
                val sociosDbHelper = SociosSQLite(this)
                val socio = sociosDbHelper.getSocioById(prestamo.idSocio)
                val coincideSocio = socio?.nombre?.contains(query, ignoreCase = true) ?: false ||
                        socio?.apellido?.contains(query, ignoreCase = true) ?: false ||
                        socio?.numeroSocio?.toString()?.contains(query, ignoreCase = true) ?: false
                val coincidePrestamo = prestamo.info?.contains(query, ignoreCase = true) ?: false ||
                        prestamo.estado.toString().contains(query, ignoreCase = true) ||
                        prestamo.fechaInicio.toString().contains(query, ignoreCase = true) ||
                        prestamo.fechaFin?.toString()?.contains(query, ignoreCase = true) ?: false

                coincideArticulo || coincideSocio || coincidePrestamo
            }
        }
        prestamosAdapter.prestamos = prestamosFiltrados
        prestamosAdapter.notifyDataSetChanged()
    }

    private fun filtrarPrestamosPorEstado(estado: EstadoPrestamo?) {
        val prestamosFiltrados = if (estado == null) {
            dbHelper.getAllPrestamos()
        } else {
            dbHelper.getAllPrestamos().filter { prestamo -> prestamo.estado == estado }
        }
        prestamosAdapter.prestamos = prestamosFiltrados
        prestamosAdapter.notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val prestamoId = data?.getIntExtra("idPrestamo", -1) ?: -1
            if (prestamoId != -1) {
                val prestamoActualizado = dbHelper.getPrestamoById(prestamoId)
                if (prestamoActualizado != null) {
                    prestamosAdapter.prestamos = dbHelper.getAllPrestamos()
                    prestamosAdapter.notifyDataSetChanged()
                }
            }
        }
    }
}
