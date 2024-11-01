package pf.dam

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.compose.ui.semantics.text
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.articulos.ArticuloAddActivity
import pf.dam.articulos.ArticulosActivity
import pf.dam.articulos.EstadoArticulo
import pf.dam.prestamos.EstadoPrestamo
import pf.dam.prestamos.PrestamoAddActivity
import pf.dam.prestamos.PrestamosActivity
import pf.dam.socios.SocioAddActivity
import pf.dam.socios.SociosActivity
import pf.dam.utils.ArticulosGraphs
import pf.dam.utils.GraficosActivity
import pf.dam.utils.ImportExportActivity
import pf.dam.utils.PrestamosGraphs

class MainActivity : AppCompatActivity() {

    private lateinit var dbArticulos: ArticulosSQLite
    private lateinit var dbSocios: SociosSQLite
    private lateinit var dbPrestamos: PrestamosSQLite
    private lateinit var verArticulosButton: FloatingActionButton
    private lateinit var articuloGraficos: FloatingActionButton
    private lateinit var articuloNuevo: FloatingActionButton
    private lateinit var verSociosButton: FloatingActionButton
    private lateinit var socioNuevo: FloatingActionButton
    private lateinit var verPrestamosButton: FloatingActionButton
    private lateinit var prestamoGraficos: FloatingActionButton
    private lateinit var prestamoNuevo: FloatingActionButton
    private lateinit var bdBtton: FloatingActionButton
    private lateinit var graphButton: FloatingActionButton
    private lateinit var cerrarAppButton: FloatingActionButton

    private lateinit var zona1TextView: TextView
    private lateinit var zona2TextView: TextView
    private lateinit var zona3TextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar dbHelpers
        dbArticulos = ArticulosSQLite(this)
        dbSocios = SociosSQLite(this)
        dbPrestamos = PrestamosSQLite(this)

        // Inicializar botones
        verArticulosButton = findViewById(R.id.verArticulosButton)
        verSociosButton = findViewById(R.id.verSociosButton)
        verPrestamosButton = findViewById(R.id.verPrestamosButton)
        bdBtton = findViewById(R.id.bdButton)
        graphButton = findViewById(R.id.graphButton)
        articuloGraficos = findViewById(R.id.articuloGraficos)
        articuloNuevo = findViewById(R.id.articuloNuevo)
        prestamoGraficos = findViewById(R.id.prestamoGraficos)
        prestamoNuevo = findViewById(R.id.prestamoNuevo)
        socioNuevo = findViewById(R.id.socioNuevo)
        cerrarAppButton = findViewById(R.id.cerrarAppButton)

        // Inicializar TextViews
        zona1TextView = findViewById(R.id.zona1)
        zona2TextView = findViewById(R.id.zona2)
        zona3TextView = findViewById(R.id.zona3)

        // Configurar listeners de botones
        verArticulosButton.setOnClickListener {
            val intent = Intent(this, ArticulosActivity::class.java)
            startActivity(intent)
        }
        articuloGraficos.setOnClickListener {
            val intent = Intent(this, ArticulosGraphs::class.java)
            startActivity(intent)
        }
        articuloNuevo.setOnClickListener {
            val intent = Intent(this, ArticuloAddActivity::class.java)
            startActivity(intent)
        }

        verSociosButton.setOnClickListener {
            val intent = Intent(this, SociosActivity::class.java)
            startActivity(intent)
        }
        socioNuevo.setOnClickListener {
            val intent = Intent(this, SocioAddActivity::class.java)
            startActivity(intent)
        }

        verPrestamosButton.setOnClickListener {
            val intent = Intent(this, PrestamosActivity::class.java)
            startActivity(intent)
        }
        prestamoGraficos.setOnClickListener {
            val intent = Intent(this, PrestamosGraphs::class.java)
            startActivity(intent)
        }
        prestamoNuevo.setOnClickListener {
            val intent = Intent(this, PrestamoAddActivity::class.java)
            startActivity(intent)
        }

        bdBtton.setOnClickListener {
            val intent = Intent(this, ImportExportActivity::class.java)
            startActivity(intent)
        }

        graphButton.setOnClickListener {
            val intent = Intent(this, GraficosActivity::class.java)
            startActivity(intent)
        }

        // Configurar listener para cerrar la aplicación
        cerrarAppButton.setOnClickListener {
            finishAffinity()
        }

        // Configurar listeners para las CardViews
        val cardView1 = findViewById<CardView>(R.id.cardView1)
        val cardView2 = findViewById<CardView>(R.id.cardView2)
        val cardView3 = findViewById<CardView>(R.id.cardView3)

        cardView1.setOnClickListener {
            val intent = Intent(this, ArticulosActivity::class.java)
            startActivity(intent)
        }

        cardView2.setOnClickListener {
            val intent = Intent(this, SociosActivity::class.java)
            startActivity(intent)
        }

        cardView3.setOnClickListener {
            val intent = Intent(this, PrestamosActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarInformacion() // Llama a la función para actualizar la información
    }

    private fun actualizarInformacion() {
        // Actualizar los TextViews con la información actualizada
        val totalArticulos = dbArticulos.obtenerArticulos().size
        val totalArticulosDispos =
            dbArticulos.obtenerArticulos().count { it.estado == EstadoArticulo.DISPONIBLE }
        val totalArticulosPrestados =
            dbArticulos.obtenerArticulos().count { it.estado == EstadoArticulo.PRESTADO }
        val totalArticulosNoDisponibles =
            dbArticulos.obtenerArticulos().count { it.estado == EstadoArticulo.NO_DISPONIBLE }
        val totalSocios = dbSocios.obtenerSocios().size
        val totalSociosPrestamosActivos = dbPrestamos.obtenerPrestamos()
            .filter { it.estado == EstadoPrestamo.ACTIVO }
            .distinctBy { it.idSocio }
            .count()
        val totalPrestamos = dbPrestamos.obtenerPrestamos().size
        val totalPrestamosActivos =
            dbPrestamos.obtenerPrestamos().count { it.estado == EstadoPrestamo.ACTIVO }
        val totalCerrados =
            dbPrestamos.obtenerPrestamos().count { it.estado == EstadoPrestamo.CERRADO }

        zona1TextView.text = "Artículos: $totalArticulos\n" +
                "Disponibles: $totalArticulosDispos\n" +
                "Prestados: $totalArticulosPrestados\n" +
                "No disponibles: $totalArticulosNoDisponibles"
        zona2TextView.text = "Socios: $totalSocios\n" +
                "con préstamos activos: $totalSociosPrestamosActivos"
        zona3TextView.text = "Préstamos: $totalPrestamos\n" +
                "Activos: $totalPrestamosActivos\n" +
                "Cerrados: $totalCerrados\n"
    }
}
//package pf.dam
//
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.content.Intent
//import android.os.Bundle
//import android.widget.TextView
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.appcompat.app.AppCompatActivity
//import androidx.cardview.widget.CardView
//import androidx.compose.ui.semantics.text
//import com.google.android.material.floatingactionbutton.FloatingActionButton
//import db.ArticulosSQLite
//import db.PrestamosSQLite
//import db.SociosSQLite
//import pf.dam.articulos.ArticuloAddActivity
//import pf.dam.articulos.ArticulosActivity
//import pf.dam.articulos.EstadoArticulo
//import pf.dam.prestamos.EstadoPrestamo
//import pf.dam.prestamos.PrestamoAddActivity
//import pf.dam.prestamos.PrestamosActivity
//import pf.dam.socios.SocioAddActivity
//import pf.dam.socios.SociosActivity
//import pf.dam.utils.ArticulosGraphs
//import pf.dam.utils.GraficosActivity
//import pf.dam.utils.ImportExportActivity
//import pf.dam.utils.PrestamosGraphs
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var dbArticulos: ArticulosSQLite
//    private lateinit var dbSocios: SociosSQLite
//    private lateinit var dbPrestamos: PrestamosSQLite
//    private lateinit var verArticulosButton: FloatingActionButton
//    private lateinit var articuloGraficos: FloatingActionButton
//    private lateinit var articuloNuevo: FloatingActionButton
//    private lateinit var verSociosButton: FloatingActionButton
//    private lateinit var socioNuevo: FloatingActionButton
//    private lateinit var verPrestamosButton: FloatingActionButton
//    private lateinit var prestamoGraficos: FloatingActionButton
//    private lateinit var prestamoNuevo: FloatingActionButton
//    private lateinit var bdBtton: FloatingActionButton
//    private lateinit var graphButton: FloatingActionButton
//    private lateinit var cerrarAppButton: FloatingActionButton
//
//    private lateinit var totalArticulosTextView: TextView
//    private lateinit var totalSociosTextView: TextView
//    private lateinit var totalPrestamosTextView: TextView
//
//    private val addArticuloLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            actualizarInformacion()
//        }
//    }
//
//    private val addSocioLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            actualizarInformacion()
//        }
//    }
//
//    private val addPrestamoLauncher = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result.resultCode == Activity.RESULT_OK) {
//            actualizarInformacion()
//        }
//    }
//
//    @SuppressLint("MissingInflatedId")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // Inicializar dbHelpers
//        dbArticulos = ArticulosSQLite(this)
//        dbSocios = SociosSQLite(this)
//        dbPrestamos = PrestamosSQLite(this)
//
//
//        val totalArticulos = dbArticulos.obtenerArticulos().size
//        val totalArticulosDispos =
//            dbArticulos.obtenerArticulos().count { it.estado == EstadoArticulo.DISPONIBLE }
//        val totalArticulosPrestados =
//            dbArticulos.obtenerArticulos().count { it.estado == EstadoArticulo.PRESTADO }
//        val totalArticulosNoDisponibles =
//            dbArticulos.obtenerArticulos().count { it.estado == EstadoArticulo.NO_DISPONIBLE }
//        val totalSocios = dbSocios.obtenerSocios().size
//        val totalSociosPrestamosActivos = dbPrestamos.obtenerPrestamos()
//            .filter { it.estado == EstadoPrestamo.ACTIVO }
//            .distinctBy { it.idSocio }
//            .count()
//        val totalPrestamos = dbPrestamos.obtenerPrestamos().size
//        val totalPrestamosActivos =
//            dbPrestamos.obtenerPrestamos().count { it.estado == EstadoPrestamo.ACTIVO }
//        val totalCerrados =
//            dbPrestamos.obtenerPrestamos().count { it.estado == EstadoPrestamo.CERRADO }
//
//        val zona1TextView = findViewById<TextView>(R.id.zona1)
//        val zona2TextView = findViewById<TextView>(R.id.zona2)
//        val zona3TextView = findViewById<TextView>(R.id.zona3)
//
//        zona1TextView.text = "Artículos: $totalArticulos\n" +
//                "Disponibles: $totalArticulosDispos\n" +
//                "Prestados: $totalArticulosPrestados\n" +
//                "No disponibles: $totalArticulosNoDisponibles"
//        zona2TextView.text = "Socios: $totalSocios\n" +
//                "con préstamos activos: $totalSociosPrestamosActivos"
//        zona3TextView.text = "Préstamos: $totalPrestamos\n" +
//                "Activos: $totalPrestamosActivos\n" +
//                "Cerrados: $totalCerrados\n"
//
//        // Inicializar botones
//        verArticulosButton = findViewById(R.id.verArticulosButton)
//        verSociosButton = findViewById(R.id.verSociosButton)
//        verPrestamosButton = findViewById(R.id.verPrestamosButton)
//        bdBtton = findViewById(R.id.bdButton)
//        graphButton = findViewById(R.id.graphButton)
//        articuloGraficos = findViewById(R.id.articuloGraficos)
//        articuloNuevo = findViewById(R.id.articuloNuevo)
//        prestamoGraficos = findViewById(R.id.prestamoGraficos)
//        prestamoNuevo = findViewById(R.id.prestamoNuevo)
//        socioNuevo = findViewById(R.id.socioNuevo)
//
//
////
////
////
////        if (dbHelperArticulos.obtenerArticulos().isEmpty()) {
////            Toast.makeText(this, "No hay artículos", Toast.LENGTH_SHORT).show()
////
////            //sin ruta imagen para null e icono
////
////            dbHelperArticulos.insertarArticulo(Articulo(null, "Bicicleta", "MTB", "Bici001","D01", EstadoArticulo.DISPONIBLE,"" ))
////            dbHelperArticulos.insertarArticulo(Articulo(null, "Bicicleta", "Carretera", "Bici002", "D02",EstadoArticulo.DISPONIBLE,""))
////            dbHelperArticulos.insertarArticulo(Articulo(null, "Bicicleta", "Eléctrica", "Bici003","D03", EstadoArticulo.DISPONIBLE,""))
////            dbHelperArticulos.insertarArticulo(Articulo(null, "Kayak", "Rígido", "Kayak001","D01", EstadoArticulo.DISPONIBLE ,""))
////            dbHelperArticulos.insertarArticulo(Articulo(null, "Kayak", "Hinchable", "Kayak002", "D02",EstadoArticulo.DISPONIBLE,""))
////            dbHelperArticulos.insertarArticulo(Articulo(null, "Kayak", "Eléctrica", "Kayak003","D03", EstadoArticulo.DISPONIBLE,""))
////            dbHelperArticulos.insertarArticulo(Articulo(null, "PadellSurf", "Hinchable", "Tabla001", "D02",EstadoArticulo.DISPONIBLE,""))
////            dbHelperArticulos.insertarArticulo(Articulo(null, "Patinete", "Eléctrica", "Patín001","D03", EstadoArticulo.DISPONIBLE,""))
////
////
////        }
////        if (dbHelperSocios.obtenerSocios().isEmpty()) {
////            Toast.makeText(this, "No hay socios", Toast.LENGTH_SHORT).show()
////            dbHelperSocios.insertarSocio(Socio(null, "Samuel", "Correa Pazos", 1,666666666,"ejemplo@samu.com"))
////        dbHelperSocios.insertarSocio(Socio(null, "Antonio", "Correa Pazos", 2,666666666,"ejemplo@samu.com"))
////        dbHelperSocios.insertarSocio(Socio(null, "Raúl", "Correa Pazos", 3,666666666,"ejemplo@samu.com"))
////        dbHelperSocios.insertarSocio(Socio(null, "Digo", "Correa Pazos", 4,666666666,"ejemplo@samu.com"))
////      }
////        if (dbHelperPrestamos.obtenerPrestamos().isEmpty()) {
////            Toast.makeText(this, "No hay préstamos", Toast.LENGTH_SHORT).show()
////        }
//        // Configurar listeners de botones
//        verArticulosButton.setOnClickListener {
//            val intent = Intent(this, ArticulosActivity::class.java)
//            startActivity(intent)
//        }
//        articuloGraficos.setOnClickListener {
//            val intent = Intent(this, ArticulosGraphs::class.java)
//            startActivity(intent)
//        }
//        articuloNuevo.setOnClickListener {
//            val intent = Intent(this, ArticuloAddActivity::class.java)
//            startActivity(intent)
//        }
//
//        verSociosButton.setOnClickListener {
//            val intent = Intent(this, SociosActivity::class.java)
//            startActivity(intent)
//        }
//        socioNuevo.setOnClickListener {
//            val intent = Intent(this, SocioAddActivity::class.java)
//            startActivity(intent)
//        }
//
//        verPrestamosButton.setOnClickListener {
//            val intent = Intent(this, PrestamosActivity::class.java)
//            startActivity(intent)
//        }
//        prestamoGraficos.setOnClickListener {
//            val intent = Intent(this, PrestamosGraphs::class.java)
//            startActivity(intent)
//        }
//        prestamoNuevo.setOnClickListener {
//            val intent = Intent(this, PrestamoAddActivity::class.java)
//            startActivity(intent)
//        }
//
//        bdBtton.setOnClickListener {
//            val intent = Intent(this, ImportExportActivity::class.java)
//            startActivity(intent)
//
//        }
//
//        graphButton.setOnClickListener {
//            val intent = Intent(this, GraficosActivity::class.java)
//            startActivity(intent)
//        }
//
//        val cardView1 = findViewById<CardView>(R.id.cardView1)
//        val cardView2 = findViewById<CardView>(R.id.cardView2)
//        val cardView3 = findViewById<CardView>(R.id.cardView3)
//
//        cardView1.setOnClickListener {
//            // Acción a realizar cuando se pulsa cardViewZona1
//            val intent = Intent(this, ArticulosActivity::class.java)
//            startActivity(intent)
//        }
//
//        cardView2.setOnClickListener {
//            // Acción a realizar cuando se pulsa cardViewZona2
//            val intent = Intent(this, SociosActivity::class.java)
//            startActivity(intent)
//        }
//
//        cardView3.setOnClickListener {
//            // Acción a realizar cuando se pulsa cardViewZona3
//            val intent = Intent(this, PrestamosActivity::class.java)
//            startActivity(intent)
//        }
//
//        val cerrarAppButton = findViewById<FloatingActionButton>(R.id.cerrarAppButton)
//
//        cerrarAppButton.setOnClickListener {
//            finishAffinity()
//        }
//    }
//
//
//}
