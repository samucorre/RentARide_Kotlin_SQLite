package pf.dam.articulos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.semantics.text
import androidx.compose.ui.test.filter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import pf.dam.MainActivity
import pf.dam.R

class ArticulosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var articulosAdapter: ArticulosAdapter
    private lateinit var dbHelper: ArticulosSQLite
    private lateinit var addArticuloButton: FloatingActionButton
    private lateinit var homeButton: FloatingActionButton
    private lateinit var backButton: FloatingActionButton
    private lateinit var searchView: SearchView
    private lateinit var categoriasSpinner: Spinner
   // private lateinit var estadoChipGroup: ChipGroup
  //  private lateinit var toolbar: Toolbar
    private var primeraSeleccion = true // Variable para controlar la primera selección
    private lateinit var estadoRadioGroup: RadioGroup

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articulos)

        // Configura el Toolbar
       // val toolbar = findViewById<Toolbar>(R.id.toolbar)
    //    setSupportActionBar(toolbar)
        supportActionBar?.title = "Artículos"

        dbHelper = ArticulosSQLite(this)
        recyclerView = findViewById(R.id.articulosRecyclerView)
        searchView = findViewById(R.id.searchView)
        addArticuloButton = findViewById(R.id.addArticuloButton)
        backButton = findViewById(R.id.backButton)
        homeButton = findViewById(R.id.homeButton)
     //   categoriasSpinner = findViewById(R.id.categoriasSpinner)
      //  estadoChipGroup = findViewById(R.id.estadoChipGroup)
        estadoRadioGroup = findViewById(R.id.estadoRadioGroup)

        articulosAdapter = ArticulosAdapter(dbHelper.obtenerArticulos())
        recyclerView.adapter = articulosAdapter
        recyclerView.layoutManager = GridLayoutManager(this, 1)

        addArticuloButton.setOnClickListener {
            val intent = Intent(this, ArticuloAddActivity::class.java)
            startActivity(intent)
        }
        backButton.setOnClickListener {
            finish()
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
                filtrarArticulos(newText)
                return true
            }
        })

        // Configura el Spinner de categorías
//        val categorias = dbHelper.obtenerCategorias()
//        val categoriasAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
//        categoriasAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        categoriasSpinner.adapter = categoriasAdapter
//
//        categoriasSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                if (primeraSeleccion) {
//                    primeraSeleccion = false
//                } else {
//                    val categoriaSeleccionada = categorias[position]
//                    if (categoriaSeleccionada != "Todas las categorías") {
//                        filtrarArticulosPorCategoria(categoriaSeleccionada)
//                    } else {
//                        articulosAdapter.articulos = dbHelper.obtenerArticulos()
//                        articulosAdapter.notifyDataSetChanged()
//                    }
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                (categoriasSpinner.getChildAt(0) as TextView).text = "Categorías"
//            }
//        }
      /*  estadoChipGroup.setOnCheckedChangeListener { group, checkedId ->
            val estadoSeleccionado = when (checkedId) {
                R.id.disponibleChip -> EstadoArticulo.DISPONIBLE
                R.id.prestadoChip -> EstadoArticulo.PRESTADO
                R.id.noDisponibleChip -> EstadoArticulo.NO_DISPONIBLE
                else -> EstadoArticulo.DISPONIBLE
            }

            filtrarArticulosPorEstado(estadoSeleccionado)
        }*/
        estadoRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            val estadoSeleccionado = when (checkedId) {
                R.id.disponibleRadioButton -> EstadoArticulo.DISPONIBLE
                R.id.prestadoRadioButton -> EstadoArticulo.PRESTADO
                R.id.noDisponibleRadioButton -> EstadoArticulo.NO_DISPONIBLE
                else -> null
            }

            filtrarArticulosPorEstado(estadoSeleccionado)
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarListaArticulos()
    }

    private fun actualizarListaArticulos() {
        articulosAdapter.articulos = dbHelper.obtenerArticulos()
        articulosAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val articuloId = data?.getIntExtra("idArticulo", -1) ?: -1
            if (articuloId != -1) {
                val articuloActualizado = dbHelper.obtenerArticuloPorId(articuloId)
                if (articuloActualizado != null) {
                    articulosAdapter.articulos = dbHelper.obtenerArticulos()
                    articulosAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun filtrarArticulos(query: String?) {
        val articulosFiltrados = if (query.isNullOrEmpty()) {
            dbHelper.obtenerArticulos()
        } else {
            dbHelper.obtenerArticulos().filter { articulo ->
                articulo.nombre?.contains(query, ignoreCase = true) ?: false ||
                        articulo.categoria?.contains(query, ignoreCase = true) ?: false ||
                        articulo.tipo?.contains(query, ignoreCase = true) ?: false
            }
        }
        articulosAdapter.articulos = articulosFiltrados
        articulosAdapter.notifyDataSetChanged()
    }

//    private fun filtrarArticulosPorCategoria(categoria: String) {
//        val articulosFiltrados = dbHelper.obtenerArticulos().filter { articulo ->
//            articulo.categoria == categoria
//        }
//        articulosAdapter.articulos = articulosFiltrados
//        articulosAdapter.notifyDataSetChanged()
//    }

    private fun filtrarArticulosPorEstado(estado: EstadoArticulo?) {
        val articulosFiltrados = if (estado == null) {
            dbHelper.obtenerArticulos()
        } else {
            dbHelper.obtenerArticulos().filter { articulo -> articulo.estado == estado }
        }
        articulosAdapter.articulos = articulosFiltrados
        articulosAdapter.notifyDataSetChanged()
    }
}