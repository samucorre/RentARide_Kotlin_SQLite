package pf.dam.utils.graficos

import android.annotation.SuppressLint
import android.content.Intent
//import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import db.PrestamosSQLite
import db.SociosSQLite
import pf.dam.MainActivity
//import androidx.core.text.color
//import com.github.mikephil.charting.charts.*
//import com.github.mikephil.charting.data.*
//import com.github.mikephil.charting.utils.ColorTemplate
import pf.dam.R
import pf.dam.articulos.EstadoArticulo
import pf.dam.prestamos.EstadoPrestamo
import pf.dam.utils.FechaUtils

class GraficosActivity : AppCompatActivity() {
    private lateinit var dbArticulos: ArticulosSQLite
    private lateinit var dbSocios: SociosSQLite
    private lateinit var dbPrestamos: PrestamosSQLite
    private lateinit var fechaUtils: FechaUtils
    private lateinit var zona1TextView: TextView
    private lateinit var zona2TextView: TextView
    private lateinit var zona3TextView: TextView
    private lateinit var homeButton: FloatingActionButton

    override fun onContentChanged() {
        super.onContentChanged()

    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grahps_activity)
        supportActionBar?.title = "RR - Informes Gráficos"
        dbArticulos = ArticulosSQLite(this)
        dbSocios = SociosSQLite(this)
        dbPrestamos = PrestamosSQLite(this)
        fechaUtils = FechaUtils(this)

        zona1TextView = findViewById(R.id.zona1)
        zona2TextView = findViewById(R.id.zona2)
        zona3TextView = findViewById(R.id.zona3)
        homeButton = findViewById(R.id.homeButton)
        val cardView1 = findViewById<CardView>(R.id.cardView1)
        val cardView2 = findViewById<CardView>(R.id.cardView2)
        val cardView3 = findViewById<CardView>(R.id.cardView3)
        val pieChartArticulos = cardView1.findViewById<PieChart>(R.id.pieChartArticulos)
        val articulosGraphs = ArticulosGraphs()
        val articulos = ArticulosSQLite(this).getAllArticulos()
        articulosGraphs.crearGraficoPastelCategorias(
            articulos,
            cardView1.findViewById(R.id.pieChartArticulos)
        )
        pieChartArticulos.legend.isEnabled = false


        val prestamosGraphs = PrestamosGraphs()
        val lineChartPrestamosPorMes =
            cardView3.findViewById<LineChart>(R.id.lineChartPrestamosPorMes)
        val pieChartPrestamosPorEstado =
            cardView3.findViewById<PieChart>(R.id.pieChartPrestamosPorEstado)

        val prestamos = PrestamosSQLite(this).getAllPrestamos()
        prestamosGraphs.crearGraficoLineasPrestamosPorMes(prestamos, lineChartPrestamosPorMes)
        lineChartPrestamosPorMes.legend.isEnabled = false
        lineChartPrestamosPorMes.description.isEnabled = true
        lineChartPrestamosPorMes.description.text = ""

        prestamosGraphs.crearGraficoPastelPrestamosPorEstado(prestamos, pieChartPrestamosPorEstado)
        pieChartPrestamosPorEstado.legend.isEnabled = false
        pieChartPrestamosPorEstado.description.isEnabled = true
        pieChartPrestamosPorEstado.description.text = ""


        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val btnArticulosGraphs: FloatingActionButton = findViewById(R.id.btnArticulosGraphs)
        btnArticulosGraphs.setOnClickListener {
            val intent = Intent(this, ArticulosGraphs::class.java)
            startActivity(intent)

        }
        val btnPrestamosGraphs: FloatingActionButton = findViewById(R.id.btnPrestamosGraphs)
        btnPrestamosGraphs.setOnClickListener {
            val intent = Intent(this, PrestamosGraphs::class.java)
            startActivity(intent)

        }

        val btnSocioGraphs: FloatingActionButton = findViewById(R.id.btnSociosGraphs)
        btnSocioGraphs.setOnClickListener {
            val intent = Intent(this, SociosGraphs::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        actualizarInformacion()
    }


    @SuppressLint("SetTextI18n")
    private fun actualizarInformacion() {
        val totalArticulos = dbArticulos.getAllArticulos().size
        val totalArticulosDispos =
            dbArticulos.getAllArticulos().count { it.estado == EstadoArticulo.DISPONIBLE }
        val totalArticulosPrestados =
            dbArticulos.getAllArticulos().count { it.estado == EstadoArticulo.PRESTADO }
        val totalArticulosNoDisponibles =
            dbArticulos.getAllArticulos().count { it.estado == EstadoArticulo.NO_DISPONIBLE }
        val totalSocios = dbSocios.getAllSocios().size
        val totalSociosPrestamosActivos = dbPrestamos.getAllPrestamos()
            .filter { it.estado == EstadoPrestamo.ACTIVO }
            .distinctBy { it.idSocio }
            .count()
        val sociosCumpleaneros = dbSocios.getSociosCumpleanosMes()
        val ultimoSocioRegistrado = dbSocios.getUltimoSocioRegistrado()

        val totalPrestamos = dbPrestamos.getAllPrestamos().size
        val totalPrestamosActivos =
            dbPrestamos.getAllPrestamos().count { it.estado == EstadoPrestamo.ACTIVO }
        val totalCerrados =
            dbPrestamos.getAllPrestamos().count { it.estado == EstadoPrestamo.CERRADO }

        val totalArticulosTextView = findViewById<TextView>(R.id.totalArticulosTextView)
        totalArticulosTextView.text = "\nTotal de artículos: $totalArticulos"

        val articulosDisponiblesTextView = findViewById<TextView>(R.id.articulosDisponiblesTextView)
        articulosDisponiblesTextView.text = "Disponibles: $totalArticulosDispos"
        val articulosPrestadosTextView = findViewById<TextView>(R.id.articulosPrestadosTextView)
        articulosPrestadosTextView.text = "Prestados: $totalArticulosPrestados"
        val articulosNoDisponiblesTextView =
            findViewById<TextView>(R.id.articulosNoDisponiblesTextView)
        articulosNoDisponiblesTextView.text = "No disponibles: $totalArticulosNoDisponibles"

        val totalSociosTextView = findViewById<TextView>(R.id.totalSociosTextView)
        totalSociosTextView.text = "\nNúmero total de Socios: $totalSocios"
        val cumpleanerosTextView = findViewById<TextView>(R.id.cumpleanerosTextView)
        cumpleanerosTextView.text =
            "\nCumpleañer@s del mes: \n${sociosCumpleaneros.joinToString("\n") { it.first }}"
        val ultimoRegistroTextView = findViewById<TextView>(R.id.ultimoRegistroTextView)
        ultimoRegistroTextView.text =
            "\nUltimo registro: ${ultimoSocioRegistrado?.nombre} ${ultimoSocioRegistrado?.apellido}  ${
                fechaUtils.dateFormat.format(ultimoSocioRegistrado?.fechaIngresoSocio)
            }"

        val totalPrestamosTextView = findViewById<TextView>(R.id.totalPrestamosTextView)
        totalPrestamosTextView.text = "\nPréstamos totales: $totalPrestamos"

        val prestamosActivosTextView = findViewById<TextView>(R.id.prestamosActivosTextView)
        prestamosActivosTextView.text = "Préstamos activos: $totalPrestamosActivos"
        val prestamosCerradosTextView = findViewById<TextView>(R.id.prestamosCerradosTextView)
        prestamosCerradosTextView.text = "Préstamos cerrados: $totalCerrados"
    }

}




