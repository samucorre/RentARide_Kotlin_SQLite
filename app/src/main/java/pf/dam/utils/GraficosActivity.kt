package pf.dam.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
//import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.compose.ui.input.key.key
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.intl.Locale
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BubbleChart
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.BubbleDataSet
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
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
import pf.dam.articulos.ArticulosActivity
import pf.dam.articulos.EstadoArticulo
import pf.dam.prestamos.EstadoPrestamo
import pf.dam.prestamos.PrestamosActivity
import pf.dam.socios.SociosActivity
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.collections.eachCount
import kotlin.text.format
import kotlin.text.toFloat

class GraficosActivity : AppCompatActivity() {
    private lateinit var dbArticulos: ArticulosSQLite
    private lateinit var dbSocios: SociosSQLite
    private lateinit var dbPrestamos: PrestamosSQLite
    private lateinit var dateUtil: DateUtil
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
        setContentView(R.layout.activity_graficos)
        supportActionBar?.title = "RR - Informes Gráficos"
        dbArticulos = ArticulosSQLite(this)
        dbSocios = SociosSQLite(this)
        dbPrestamos = PrestamosSQLite(this)
        dateUtil = DateUtil(this)

        zona1TextView = findViewById(R.id.zona1)
        zona2TextView = findViewById(R.id.zona2)
        zona3TextView = findViewById(R.id.zona3)
        homeButton= findViewById(R.id.homeButton)
        val cardView1 = findViewById<CardView>(R.id.cardView1)
        val cardView2 = findViewById<CardView>(R.id.cardView2)
        val cardView3 = findViewById<CardView>(R.id.cardView3)
        val pieChartArticulos = cardView1.findViewById<PieChart>(R.id.pieChartArticulos)
        val articulosGraphs = ArticulosGraphs()
        val articulos = ArticulosSQLite(this).obtenerArticulos()
        articulosGraphs.crearGraficoPastelCategorias(articulos, cardView1.findViewById(R.id.pieChartArticulos))
        pieChartArticulos.legend.isEnabled = false



        val prestamosGraphs = PrestamosGraphs()
        val lineChartPrestamosPorMes = cardView3.findViewById<LineChart>(R.id.lineChartPrestamosPorMes)
        val pieChartPrestamosPorEstado = cardView3.findViewById<PieChart>(R.id.pieChartPrestamosPorEstado)

        val prestamos = PrestamosSQLite(this).obtenerPrestamos()
        prestamosGraphs.crearGraficoLineasPrestamosPorMes(prestamos,lineChartPrestamosPorMes)
        lineChartPrestamosPorMes.legend.isEnabled = false
        lineChartPrestamosPorMes.description.isEnabled = true
        lineChartPrestamosPorMes.description.text = ""

        prestamosGraphs.crearGraficoPastelPrestamosPorEstado(prestamos,pieChartPrestamosPorEstado)
        pieChartPrestamosPorEstado.legend.isEnabled = false
        pieChartPrestamosPorEstado.description.isEnabled = true
        pieChartPrestamosPorEstado.description.text = ""


        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
//        val btnEjemploGraficos: Button = findViewById(R.id.btnEjemploGraficos)
//        btnEjemploGraficos.setOnClickListener {
//            val intent = Intent(this, EjemploGraficos::class.java)
//            startActivity(intent)
//
//        }

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


    @SuppressLint("SetTextI18n")
    private fun actualizarInformacion() {
        // Actualizar los TextViews con la información actualizada
        val totalArticulos = dbArticulos.obtenerArticulos().size
        val totalArticulosDispos =
            dbArticulos.obtenerArticulos().count { it.estado == EstadoArticulo.DISPONIBLE }
        val totalArticulosPrestados =
            dbArticulos.obtenerArticulos().count { it.estado == EstadoArticulo.PRESTADO }
        val totalArticulosNoDisponibles =
            dbArticulos.obtenerArticulos().count { it.estado == EstadoArticulo.NO_DISPONIBLE }
        // Actualizar los TextViews
        val totalSocios = dbSocios.obtenerSocios().size
        val totalSociosPrestamosActivos = dbPrestamos.obtenerPrestamos()
            .filter { it.estado == EstadoPrestamo.ACTIVO }
            .distinctBy { it.idSocio }
            .count()
        val sociosCumpleaneros = dbSocios.obtenerSociosCumplenAnosMesActual()
        val ultimoSocioRegistrado = dbSocios.obtenerUltimoSocioRegistrado()

        // Actualizar los TextViews
        val totalPrestamos = dbPrestamos.obtenerPrestamos().size
        val totalPrestamosActivos =
            dbPrestamos.obtenerPrestamos().count { it.estado == EstadoPrestamo.ACTIVO }
        val totalCerrados =
            dbPrestamos.obtenerPrestamos().count { it.estado == EstadoPrestamo.CERRADO }

//        zona1TextView.text = "\t\tARTICULOS\n" +
//                "Total de artículos: $totalArticulos\n" +
//                "Disponibles: $totalArticulosDispos\n" +
//                "Prestados: $totalArticulosPrestados\n" +
//                "No disponibles: $totalArticulosNoDisponibles"
//
//

        val totalArticulosTextView = findViewById<TextView>(R.id.totalArticulosTextView)
        totalArticulosTextView.text = "\nTotal de artículos: $totalArticulos"

        val articulosDisponiblesTextView = findViewById<TextView>(R.id.articulosDisponiblesTextView)
        articulosDisponiblesTextView.text = "Disponibles: $totalArticulosDispos"
        val articulosPrestadosTextView = findViewById<TextView>(R.id.articulosPrestadosTextView)
        articulosPrestadosTextView.text = "Prestados: $totalArticulosPrestados"
        val articulosNoDisponiblesTextView = findViewById<TextView>(R.id.articulosNoDisponiblesTextView)
        articulosNoDisponiblesTextView.text = "No disponibles: $totalArticulosNoDisponibles"


        // ... (establece los valores de los demás TextView de artículos) ...

        // ... (establece los valores de los demás TextView de préstamos) ...



        val totalSociosTextView = findViewById<TextView>(R.id.totalSociosTextView)
        totalSociosTextView.text = "\nNúmero total de Socios: $totalSocios"
        val cumpleanerosTextView = findViewById<TextView>(R.id.cumpleanerosTextView)
        cumpleanerosTextView.text = "\nCumpleañer@s del mes: \n${sociosCumpleaneros.joinToString("\n") { it.first }}"
        val ultimoRegistroTextView = findViewById<TextView>(R.id.ultimoRegistroTextView)
        ultimoRegistroTextView.text = "\nUltimo registro: ${ultimoSocioRegistrado?.nombre} ${ultimoSocioRegistrado?.apellido}  ${dateUtil.dateFormat.format(ultimoSocioRegistrado?.fechaIngresoSocio)}"



        val totalPrestamosTextView = findViewById<TextView>(R.id.totalPrestamosTextView)
        totalPrestamosTextView.text = "\nPréstamos totales: $totalPrestamos"

        val prestamosActivosTextView = findViewById<TextView>(R.id.prestamosActivosTextView)
        prestamosActivosTextView.text = "Préstamos activos: $totalPrestamosActivos"
        val prestamosCerradosTextView = findViewById<TextView>(R.id.prestamosCerradosTextView)
        prestamosCerradosTextView.text = "Préstamos cerrados: $totalCerrados"

//        zona3TextView.text ="\t\tPRESTAMOS\n" +
//            "Préstamos totales: $totalPrestamos\n" +
//                "Préstamos activos: $totalPrestamosActivos\n" +
//                "Préstamos Cerrados: $totalCerrados\n"
    }

}




