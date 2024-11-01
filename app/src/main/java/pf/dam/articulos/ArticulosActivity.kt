package pf.dam.articulos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import pf.dam.MainActivity
import pf.dam.R

class ArticulosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var articulosAdapter: ArticulosAdapter
    private lateinit var db: ArticulosSQLite
    private lateinit var addArticuloButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton
    //private lateinit var backButton: FloatingActionButton
    private lateinit var searchView: SearchView

    private lateinit var estadoRadioGroup: RadioGroup

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articulos)

        // Configura el Toolbar
       // val toolbar = findViewById<Toolbar>(R.id.toolbar)
    //    setSupportActionBar(toolbar)
        supportActionBar?.title = "RR - Artículos"


        db = ArticulosSQLite(this)
        recyclerView = findViewById(R.id.articulosRecyclerView)
        searchView = findViewById(R.id.searchView)
        addArticuloButton = findViewById(R.id.addArticuloButton)
    //    backButton = findViewById(R.id.backButton)
        homeButton = findViewById(R.id.homeButton)
        estadoRadioGroup = findViewById(R.id.estadoRadioGroup)
        articulosAdapter = ArticulosAdapter(db.obtenerArticulos())
        recyclerView.adapter = articulosAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        addArticuloButton.setOnClickListener {
            val intent = Intent(this, ArticuloAddActivity::class.java)
            startActivity(intent)
        }
//        backButton.setOnClickListener {
//            setResult(RESULT_OK)
//            finish()
//        }
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                articulosAdapter.articulos = db.articulosDbHelper.filtrarArticulos(db.readableDatabase, newText) // Llamada a la función
                articulosAdapter.notifyDataSetChanged()
                return true
            }
        })

        estadoRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            val estadoSeleccionado = when (checkedId) {
                R.id.disponibleRadioButton -> EstadoArticulo.DISPONIBLE
                R.id.prestadoRadioButton -> EstadoArticulo.PRESTADO
                R.id.noDisponibleRadioButton -> EstadoArticulo.NO_DISPONIBLE
                else -> null
            }

            articulosAdapter.articulos = db.articulosDbHelper.filtrarArticulosPorEstado(db.readableDatabase, estadoSeleccionado) // Llamada a la función
            articulosAdapter.notifyDataSetChanged()
        }

    }

    override fun onResume() {
        super.onResume()
        actualizarListaArticulos()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val articuloId = data?.getIntExtra("idArticulo", -1) ?: -1
            if (articuloId != -1) {
                val articuloActualizado = db.obtenerArticuloPorId(articuloId)
                if (articuloActualizado != null) {
                    articulosAdapter.articulos = db.obtenerArticulos()
                    articulosAdapter.notifyDataSetChanged()
                }
            }
        }
    }

//    private fun actualizarListaArticulos() {
//        articulosAdapter.articulos = db.obtenerArticulos()
//        articulosAdapter.notifyDataSetChanged()
//    }
private fun actualizarListaArticulos() {
    val db = ArticulosSQLite(this)
    val articulos = db.obtenerArticulos()
    articulosAdapter.articulos = articulos
    articulosAdapter.notifyDataSetChanged()
}

//    private fun filtrarArticulos(query: String?) {
//        val articulosFiltrados = if (query.isNullOrEmpty()) {
//            db.obtenerArticulos()
//        } else {
//            db.obtenerArticulos().filter { articulo ->
//                articulo.nombre?.contains(query, ignoreCase = true) ?: false ||
//                        articulo.categoria?.contains(query, ignoreCase = true) ?: false ||
//                        articulo.tipo?.contains(query, ignoreCase = true) ?: false
//            }
//        }
//        articulosAdapter.articulos = articulosFiltrados
//        articulosAdapter.notifyDataSetChanged()
//    }
//    private fun filtrarArticulosPorEstado(estado: EstadoArticulo?) {
//        val articulosFiltrados = if (estado == null) {
//            db.obtenerArticulos()
//        } else {
//            db.obtenerArticulos().filter { articulo -> articulo.estado == estado }
//        }
//        articulosAdapter.articulos = articulosFiltrados
//        articulosAdapter.notifyDataSetChanged()
//    }
}