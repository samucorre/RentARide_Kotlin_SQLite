package pf.dam

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.articulos.Articulo
import pf.dam.articulos.ArticuloAddActivity
import pf.dam.articulos.ArticulosActivity
import pf.dam.articulos.EstadoArticulo
import pf.dam.prestamos.EstadoPrestamo
import pf.dam.prestamos.PrestamoAddActivity
import pf.dam.prestamos.PrestamosActivity
import pf.dam.socios.Socio
import pf.dam.socios.SocioAddActivity
import pf.dam.socios.SociosActivity
import pf.dam.utils.ArticulosGraphs
import pf.dam.utils.GraficosActivity
import pf.dam.utils.ImportExportActivity
import pf.dam.utils.PrestamosGraphs

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelperArticulos: ArticulosSQLite
    private lateinit var dbHelperSocios: SociosSQLite
    private lateinit var dbHelperPrestamos: PrestamosSQLite
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

    private lateinit var totalArticulosTextView: TextView
    private lateinit var totalSociosTextView: TextView
    private lateinit var totalPrestamosTextView: TextView



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar dbHelpers
        dbHelperArticulos = ArticulosSQLite(this)
        dbHelperSocios = SociosSQLite(this)
        dbHelperPrestamos = PrestamosSQLite(this)



        val totalArticulos = dbHelperArticulos.obtenerArticulos().size
        val totalArticulosDispos =
            dbHelperArticulos.obtenerArticulos().count { it.estado == EstadoArticulo.DISPONIBLE }
        val totalArticulosPrestados =
            dbHelperArticulos.obtenerArticulos().count { it.estado == EstadoArticulo.PRESTADO }
        val totalArticulosNoDisponibles =
            dbHelperArticulos.obtenerArticulos().count { it.estado == EstadoArticulo.NO_DISPONIBLE }
        val totalSocios = dbHelperSocios.obtenerSocios().size
        val totalSociosPrestamosActivos = dbHelperPrestamos.obtenerPrestamos()
            .filter { it.estado == EstadoPrestamo.ACTIVO }
            .distinctBy { it.idSocio }
            .count()
        val totalPrestamos = dbHelperPrestamos.obtenerPrestamos().size
        val totalPrestamosActivos =
            dbHelperPrestamos.obtenerPrestamos().count { it.estado == EstadoPrestamo.ACTIVO }
        val totalCerrados =
            dbHelperPrestamos.obtenerPrestamos().count { it.estado == EstadoPrestamo.CERRADO }

        val zona1TextView = findViewById<TextView>(R.id.zona1)
        val zona2TextView = findViewById<TextView>(R.id.zona2)
        val zona3TextView = findViewById<TextView>(R.id.zona3)

        zona1TextView.text = "Artículos: $totalArticulos\n" +
                "Disponibles: $totalArticulosDispos\n" +
                "Prestados: $totalArticulosPrestados\n" +
                "No disponibles: $totalArticulosNoDisponibles"
        zona2TextView.text = "Socios: $totalSocios\n" +
                "con préstamos activos: $totalSociosPrestamosActivos"
        zona3TextView.text = "Préstamos: $totalPrestamos\n" +
                "Activos: $totalPrestamosActivos\n" +
                "Cerrados: $totalCerrados\n"

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


//
//
//
        if (dbHelperArticulos.obtenerArticulos().isEmpty()) {
            Toast.makeText(this, "No hay artículos", Toast.LENGTH_SHORT).show()

            //sin ruta imagen para null e icono

            dbHelperArticulos.insertarArticulo(Articulo(null, "Bicicleta", "MTB", "Bici001","D01", EstadoArticulo.DISPONIBLE,"" ))
            dbHelperArticulos.insertarArticulo(Articulo(null, "Bicicleta", "Carretera", "Bici002", "D02",EstadoArticulo.DISPONIBLE,""))
            dbHelperArticulos.insertarArticulo(Articulo(null, "Bicicleta", "Eléctrica", "Bici003","D03", EstadoArticulo.DISPONIBLE,""))
            dbHelperArticulos.insertarArticulo(Articulo(null, "Kayak", "Rígido", "Kayak001","D01", EstadoArticulo.DISPONIBLE ,""))
            dbHelperArticulos.insertarArticulo(Articulo(null, "Kayak", "Hinchable", "Kayak002", "D02",EstadoArticulo.DISPONIBLE,""))
            dbHelperArticulos.insertarArticulo(Articulo(null, "Kayak", "Eléctrica", "Kayak003","D03", EstadoArticulo.DISPONIBLE,""))
            dbHelperArticulos.insertarArticulo(Articulo(null, "PadellSurf", "Hinchable", "Tabla001", "D02",EstadoArticulo.DISPONIBLE,""))
            dbHelperArticulos.insertarArticulo(Articulo(null, "Patinete", "Eléctrica", "Patín001","D03", EstadoArticulo.DISPONIBLE,""))


        }
        if (dbHelperSocios.obtenerSocios().isEmpty()) {
            Toast.makeText(this, "No hay socios", Toast.LENGTH_SHORT).show()
            dbHelperSocios.insertarSocio(Socio(null, "Samuel", "Correa Pazos", 1,666666666,"ejemplo@samu.com"))
        dbHelperSocios.insertarSocio(Socio(null, "Antonio", "Correa Pazos", 2,666666666,"ejemplo@samu.com"))
        dbHelperSocios.insertarSocio(Socio(null, "Raúl", "Correa Pazos", 3,666666666,"ejemplo@samu.com"))
        dbHelperSocios.insertarSocio(Socio(null, "Digo", "Correa Pazos", 4,666666666,"ejemplo@samu.com"))
      }
        if (dbHelperPrestamos.obtenerPrestamos().isEmpty()) {
            Toast.makeText(this, "No hay préstamos", Toast.LENGTH_SHORT).show()
        }
        // Configurar listeners de botones
        verArticulosButton.setOnClickListener {
            val intent = Intent(this, ArticulosActivity::class.java)
            startActivity(intent)
        }
        articuloGraficos.setOnClickListener {
            val intent = Intent(this,ArticulosGraphs::class.java)
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

    }
}