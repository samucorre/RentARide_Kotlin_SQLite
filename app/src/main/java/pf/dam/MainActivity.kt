package pf.dam

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.semantics.text
import db.ArticulosSQLite
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.articulos.Articulo
import pf.dam.articulos.ArticulosActivity
import pf.dam.articulos.EstadoArticulo
import pf.dam.prestamos.EstadoPrestamo
import pf.dam.prestamos.PrestamosActivity
import pf.dam.socios.Socio
import pf.dam.socios.SociosActivity

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelperArticulos: ArticulosSQLite
    private lateinit var dbHelperSocios: SociosSQLite
    private lateinit var dbHelperPrestamos: PrestamosSQLite
    private lateinit var verArticulosButton: Button
    private lateinit var verSociosButton: Button
    private lateinit var verPrestamosButton: Button
    private lateinit var totalArticulosTextView: TextView
    private lateinit var totalSociosTextView: TextView
    private lateinit var totalPrestamosTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar dbHelpers
        dbHelperArticulos = ArticulosSQLite(this)
        dbHelperSocios = SociosSQLite(this)
        dbHelperPrestamos = PrestamosSQLite(this)

        val totalArticulos = dbHelperArticulos.obtenerArticulos().size
        val totalArticulosDispos = dbHelperArticulos.obtenerArticulos().count { it.estado == EstadoArticulo.DISPONIBLE }
        val totalArticulosPrestados = dbHelperArticulos.obtenerArticulos().count { it.estado == EstadoArticulo.PRESTADO }
        val totalSocios = dbHelperSocios.obtenerSocios().size
        val totalSociosPrestamosActivos=dbHelperPrestamos.obtenerPrestamos()
            .filter { it.estado == EstadoPrestamo.ACTIVO }
            .distinctBy { it.idSocio }
            .count()
        val totalPrestamos = dbHelperPrestamos.obtenerPrestamos().size
        val totalPrestamosActivos = dbHelperPrestamos.obtenerPrestamos().count { it.estado == EstadoPrestamo.ACTIVO }
        val totalCerrados = dbHelperPrestamos.obtenerPrestamos().count { it.estado == EstadoPrestamo.CERRADO }

        val zona1TextView = findViewById<TextView>(R.id.zona1)
        val zona2TextView = findViewById<TextView>(R.id.zona2)
        val zona3TextView = findViewById<TextView>(R.id.zona3)

        zona1TextView.text = "Artículos: $totalArticulos\n"+
                "Disponibles: $totalArticulosDispos\n" +
                "Prestados: $totalArticulosPrestados\n"
        zona2TextView.text = "Socios: $totalSocios\n"+
                "con préstamos activos: $totalSociosPrestamosActivos"
        zona3TextView.text = "Préstamos: $totalPrestamos\n"+
                "Activos: $totalPrestamosActivos\n" +
                "Cerrados: $totalCerrados\n"

        // Inicializar botones
        verArticulosButton = findViewById(R.id.verArticulosButton)
        verSociosButton = findViewById(R.id.verSociosButton)
        verPrestamosButton = findViewById(R.id.verPrestamosButton)


        if (dbHelperArticulos.obtenerArticulos().isEmpty()) {
            Toast.makeText(this, "No hay artículos", Toast.LENGTH_SHORT).show()
        }
        if (dbHelperSocios.obtenerSocios().isEmpty()) {
            Toast.makeText(this, "No hay socios", Toast.LENGTH_SHORT).show()
        }
        if (dbHelperPrestamos.obtenerPrestamos().isEmpty()) {
            Toast.makeText(this, "No hay préstamos", Toast.LENGTH_SHORT).show()
        }
        // Configurar listeners de botones
        verArticulosButton.setOnClickListener {
            val intent = Intent(this, ArticulosActivity::class.java)
            startActivity(intent)
        }

        verSociosButton.setOnClickListener {
            val intent = Intent(this, SociosActivity::class.java)
            startActivity(intent)
        }

        verPrestamosButton.setOnClickListener {
            val intent = Intent(this, PrestamosActivity::class.java)
            startActivity(intent)
        }

    }
   }