package pf.dam.utils

import android.annotation.SuppressLint
import android.content.Intent
//import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.compose.ui.input.key.key
import androidx.compose.ui.text.intl.Locale
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
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
                val btnArticulosGraphs: Button = findViewById(R.id.btnArticulosGraphs)
        btnArticulosGraphs.setOnClickListener {
            val intent = Intent(this, ArticulosGraphs::class.java)
            startActivity(intent)

        }
        val btnPrestamosGraphs: Button = findViewById(R.id.btnPrestamosGraphs)
        btnPrestamosGraphs.setOnClickListener {
            val intent = Intent(this, PrestamosGraphs::class.java)
            startActivity(intent)

        }

        val btnSocioGraphs: Button = findViewById(R.id.btnSocioGraphs)
        btnSocioGraphs.setOnClickListener {
            val intent = Intent(this, SociosActivity::class.java)
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

        zona1TextView.text = "\t\tARTICULOS\n" +
                "Total de artículos: $totalArticulos\n" +
                "Disponibles: $totalArticulosDispos\n" +
                "Prestados: $totalArticulosPrestados\n" +
                "No disponibles: $totalArticulosNoDisponibles"
        zona2TextView.text = "\t\tSOCIOS\n" +
            "Socios: $totalSocios\n" +
                "Socios con préstamos activos: $totalSociosPrestamosActivos"
        zona3TextView.text ="\t\tPRESTAMOS\n" +
            "Préstamos totales: $totalPrestamos\n" +
                "Préstamos activos: $totalPrestamosActivos\n" +
                "Préstamos Cerrados: $totalCerrados\n"
    }

}

//

//    private lateinit var chart: LineChart
//    private lateinit var barChart: BarChart
//    private lateinit var radarChart: RadarChart
//    private lateinit var pieChart: PieChart
//    private lateinit var scatterChart: ScatterChart
//    private lateinit var bubbleChart: BubbleChart
//    private lateinit var candleStickChart: CandleStickChart

//        chart = findViewById(R.id.chart)
//        barChart = findViewById(R.id.barChart)
//        radarChart = findViewById(R.id.radarChart)
//        pieChart = findViewById(R.id.pieChart)
//        scatterChart = findViewById(R.id.scatterChart)
//        bubbleChart = findViewById(R.id.bubbleChart)
//        candleStickChart = findViewById(R.id.candleStickChart)
//
//
//
//        // Gráfico de líneas
//        val entries = mutableListOf<Entry>()
//        entries.add(Entry(1f, 2f))
//        entries.add(Entry(2f, 4f))
//        entries.add(Entry(3f, 1f))
//        entries.add(Entry(4f, 3f))
//        val dataSet = LineDataSet(entries, "Muestras")
//        dataSet.color = Color.BLUE
//        dataSet.valueTextColor = Color.BLACK
//        val lineData = LineData(dataSet)
//        chart.data = lineData
//        chart.invalidate()
//
//        // Gráfico de barras
//        val barEntries = listOf(
//            BarEntry(0f, 30f),
//            BarEntry(1f, 80f),
//            BarEntry(2f, 60f)
//        )
//        val barDataSet = BarDataSet(barEntries, "Datos de barras")
//        barDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
//        val barData = BarData(barDataSet)
//        barChart.data = barData
//        barChart.invalidate()
//
//        // Gráfico de radar
//        val radarEntries = listOf(
//            RadarEntry(30f),
//            RadarEntry(80f),
//            RadarEntry(60f),
//            RadarEntry(40f),
//            RadarEntry(50f)
//        )
//        val radarDataSet = RadarDataSet(radarEntries, "Datos de radar")
//        radarDataSet.color = Color.RED
//        radarDataSet.fillColor = Color.RED
//        radarDataSet.setDrawFilled(true)
//        val radarData = RadarData(radarDataSet)
//        radarChart.data = radarData
//        radarChart.invalidate()
//
//        // Gráfico de pastel
//        val pieEntries = listOf(
//            PieEntry(30f, "Categoría A"),
//            PieEntry(80f, "Categoría B"),
//            PieEntry(60f, "Categoría C")
//        )
//        val pieDataSet = PieDataSet(pieEntries, "Datos de pastel")
//        pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
//        val pieData = PieData(pieDataSet)
//        pieChart.data = pieData
//        pieChart.invalidate()
//
//        // Gráfico de dispersión
//        val scatterEntries = listOf(
//            Entry(1f, 2f),
//            Entry(2f, 4f),
//            Entry(3f, 1f),
//            Entry(4f, 3f)
//        )
//        val scatterDataSet = ScatterDataSet(scatterEntries, "Datos de dispersión")
//        scatterDataSet.color = Color.GREEN
//        val scatterData = ScatterData(scatterDataSet)
//        scatterChart.data = scatterData
//        scatterChart.invalidate()
//
//        // Gráfico de burbujas
//        val bubbleEntries = listOf(
//            BubbleEntry(1f, 2f, 10f),
//            BubbleEntry(2f, 4f, 15f),
//            BubbleEntry(3f, 1f, 5f),
//            BubbleEntry(4f, 3f, 20f)
//        )
//        val bubbleDataSet = BubbleDataSet(bubbleEntries, "Datos de burbujas")
//        bubbleDataSet.colors = ColorTemplate.JOYFUL_COLORS.toList()
//        val bubbleData = BubbleData(bubbleDataSet)
//        bubbleChart.data = bubbleData
//        bubbleChart.invalidate()
//
//        // Gráfico de velas
//        val candleEntries = listOf(
//            CandleEntry(0f, 10f, 5f, 7f, 8f),
//            CandleEntry(1f, 12f, 7f, 9f, 10f),
//            CandleEntry(2f, 15f, 9f, 11f, 13f)
//        )
//        val candleDataSet = CandleDataSet(candleEntries, "Datos de velas")
//        candleDataSet.color = Color.RED
//        candleDataSet.shadowColor = Color.DKGRAY
//        val candleData = CandleData(candleDataSet)
//        candleStickChart.data = candleData
//        candleStickChart.invalidate()