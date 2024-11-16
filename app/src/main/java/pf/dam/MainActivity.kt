package pf.dam

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import java.util.Locale
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import db.ArticulosSQLite
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.articulos.ArticulosActivity
import pf.dam.articulos.EstadoArticulo
import pf.dam.prestamos.EstadoPrestamo
import pf.dam.prestamos.PrestamosActivity
import pf.dam.socios.SociosActivity
import pf.dam.utils.InsertarDatosIniciales
import pf.dam.utils.graficos.GraficosActivity
import pf.dam.utils.ImportExportActivity
import java.text.SimpleDateFormat

class MainActivity : AppCompatActivity() {

    private lateinit var dbArticulos: ArticulosSQLite
    private lateinit var dbSocios: SociosSQLite
    private lateinit var dbPrestamos: PrestamosSQLite
//    private lateinit var verArticulosButton: FloatingActionButton
//    private lateinit var articuloGraficos: FloatingActionButton
//    private lateinit var articuloNuevo: FloatingActionButton
//    private lateinit var verSociosButton: FloatingActionButton
//    private lateinit var socioNuevo: FloatingActionButton
//    private lateinit var verPrestamosButton: FloatingActionButton
//    private lateinit var prestamoGraficos: FloatingActionButton
//    private lateinit var prestamoNuevo: FloatingActionButton
//    private lateinit var button1: FloatingActionButton
//            private lateinit var button2: FloatingActionButton
//                    private lateinit var button3: FloatingActionButton
 //   private lateinit var bdBtton: FloatingActionButton
  //  private lateinit var graphButton: FloatingActionButton
 //   private lateinit var cerrarAppButton: FloatingActionButton
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

//    private lateinit var zona1TextView: TextView
//    private lateinit var zona2TextView: TextView
//    private lateinit var zona3TextView: TextView
//    private lateinit var zona4TextView: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar dbHelpers
        dbArticulos = ArticulosSQLite(this)
        dbSocios = SociosSQLite(this)
        dbPrestamos = PrestamosSQLite(this)

            if (dbSocios.obtenerSocios().isEmpty()) {
             InsertarDatosIniciales().insertarSociosIniciales(dbSocios)
             }
        if (dbArticulos.obtenerArticulos().isEmpty()) {
            InsertarDatosIniciales().insertarArticulosIniciales(dbArticulos)
            }
        if (dbPrestamos.obtenerPrestamos().isEmpty()) {
            InsertarDatosIniciales().insertarPrestamosIniciales(dbPrestamos)
        }


        // Inicializar botones
//        verArticulosButton = findViewById(R.id.verArticulosButton)
//        verSociosButton = findViewById(R.id.verSociosButton)
//        verPrestamosButton = findViewById(R.id.verPrestamosButton)
        //bdBtton = findViewById(R.id.bdButton)
       // graphButton = findViewById(R.id.graphButton)
//        articuloGraficos = findViewById(R.id.articuloGraficos)
//        articuloNuevo = findViewById(R.id.articuloNuevo)
//        prestamoGraficos = findViewById(R.id.prestamoGraficos)
//        prestamoNuevo = findViewById(R.id.prestamoNuevo)
//        socioNuevo = findViewById(R.id.socioNuevo)
      //  cerrarAppButton = findViewById(R.id.cerrarAppButton)
//        button1 = findViewById(R.id.button1)
//        button2 = findViewById(R.id.button2)
//        button3 = findViewById(R.id.button3)


        // Inicializar TextViews
//        zona1TextView = findViewById(R.id.zona1)
//        zona2TextView = findViewById(R.id.zona2)
//        zona3TextView = findViewById(R.id.zona3)
//        zona4TextView = findViewById(R.id.zona4)

        // Configurar listeners de botones
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

     //   bdBtton.setOnClickListener {
       //     val intent = Intent(this, ImportExportActivity::class.java)
     //       startActivity(intent)
     //   }

//        graphButton.setOnClickListener {
//            val intent = Intent(this, GraficosActivity::class.java)
//            startActivity(intent)
//        }

        // Configurar listener para cerrar la aplicación
      //  cerrarAppButton.setOnClickListener {
       //     finishAffinity()
      //  }

        // Configurar listeners para las CardViews
        val cardView1 = findViewById<CardView>(R.id.cardView1)
        val cardView2 = findViewById<CardView>(R.id.cardView2)
        val cardView3 = findViewById<CardView>(R.id.cardView3)
        val cardView4 = findViewById<CardView>(R.id.cardView4)
        val cardView5 = findViewById<CardView>(R.id.cardView5)

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
        cardView4.setOnClickListener{
            val intent = Intent(this, GraficosActivity::class.java)
            startActivity(intent)
        }
    cardView5.setOnClickListener    {
        val intent = Intent(this, ImportExportActivity::class.java)
        startActivity(intent)
    }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_close, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_cerrar_app -> {
                finishAffinity()
                true
            }
            else -> super.onOptionsItemSelected(item)
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

//        zona1TextView.text = "Artículos: $totalArticulos\n" +
//                "Disponibles: $totalArticulosDispos\n" +
//                "Prestados: $totalArticulosPrestados\n" +
//                "No disponibles: $totalArticulosNoDisponibles"
//        zona2TextView.text = "Socios: $totalSocios\n" +
//                "con préstamos activos: $totalSociosPrestamosActivos"
//        zona3TextView.text = "Préstamos: $totalPrestamos\n" +
//                "Activos: $totalPrestamosActivos\n" +
//                "Cerrados: $totalCerrados\n"
    }


}