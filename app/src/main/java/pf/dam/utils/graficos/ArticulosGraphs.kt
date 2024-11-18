package pf.dam.utils.graficos

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import db.PrestamosSQLite
import pf.dam.MainActivity
import pf.dam.R
import pf.dam.articulos.Articulo
import pf.dam.articulos.EstadoArticulo
import pf.dam.prestamos.Prestamo
import kotlin.collections.eachCount

class ArticulosGraphs : AppCompatActivity() {

    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart

    private lateinit var pieChart2: PieChart
    private lateinit var barChart2: BarChart
    private lateinit var homeButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.graphs_articulos_activity)
        supportActionBar?.title = "RR - Gráficos artículos"

        pieChart = findViewById(R.id.pieChart)
        barChart = findViewById(R.id.barChart)
        pieChart2 = findViewById(R.id.pieChart2)
        barChart2 = findViewById(R.id.barChart2)

        homeButton = findViewById(R.id.homeButton)
        volverButton = findViewById(R.id.volverButton)

        configurarGraficos()
        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        volverButton.setOnClickListener {
            finish()
        }
    }

    private fun configurarGraficos() {
        val articulos = ArticulosSQLite(this).getAllArticulos()
        val prestamos = PrestamosSQLite(this).getAllPrestamos()

        crearGraficoPastelCategorias(articulos, pieChart)
        crearGraficoBarrasEstados(articulos, barChart)
        crearGraficoPastelCategoriasPrestamos(prestamos)
        crearGraficoBarrasArticulosPrestados(prestamos)
    }

    fun crearGraficoPastelCategorias(articulos: List<Articulo>, pieChart: PieChart) {
        val articulosPorCategoria = articulos.groupingBy { it.categoria }.eachCount()
        val pieEntries = articulosPorCategoria.entries.map { PieEntry(it.value.toFloat(), it.key) }
        val pieDataSet = PieDataSet(pieEntries, "Categorías de artículos")
        pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.invalidate()
        pieChart.description.isEnabled = true
        pieChart.description.text = "Gráfico de categorías"
    }
    fun crearGraficoBarrasEstados(articulos: List<Articulo>, barChart: BarChart) {
        val articulosPorEstado = articulos.groupingBy { it.estado }.eachCount()
        val barEntries = articulosPorEstado.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }
        val barDataSet = BarDataSet(barEntries, "Estados de artículos")
        barDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        barDataSet.setDrawValues(false)
        barDataSet.setValueTextColors(listOf(Color.TRANSPARENT))

        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.legend.isEnabled = false
        barChart.description.isEnabled = false
        barChart.description.text = "Gráfico de estados"

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = object : IndexAxisValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val index = value.toInt()
                val estado = articulosPorEstado.keys.toList()[index]

                return when (estado) {
                    EstadoArticulo.DISPONIBLE -> "Disponible"
                    EstadoArticulo.PRESTADO -> "Prestado"
                    EstadoArticulo.NO_DISPONIBLE -> "No disponible"
                    else -> estado.toString()
                }
            }
        }
        xAxis.granularity = 1f
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM

        barChart.invalidate()
    }
    fun crearGraficoPastelCategoriasPrestamos(prestamos: List<Prestamo>) {
        val prestamosPorCategoria = prestamos.groupingBy {
            PrestamosSQLite(this).getPrestamoByCategoria(it.idArticulo)
        }.eachCount()
        val pieEntries2 = prestamosPorCategoria.entries.map { PieEntry(it.value.toFloat(), it.key) }
        val pieDataSet2 = PieDataSet(pieEntries2, "Categorías con más préstamos")
        pieDataSet2.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val pieData2 = PieData(pieDataSet2)
        pieChart2.data = pieData2
        pieChart2.invalidate()
        pieChart2.legend.isEnabled = false
        pieChart2.description.isEnabled = true
        pieChart2.description.text = "Categorías con más préstamos"
    }
   fun crearGraficoBarrasArticulosPrestados(prestamos: List<Prestamo>) {
        val prestamosPorArticulo = prestamos.groupingBy {
            PrestamosSQLite(this).getPrestamoByIdArticulo(it.idArticulo)?.nombre
        }.eachCount()
        val barEntries2 = prestamosPorArticulo.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }
        val barDataSet2 = BarDataSet(barEntries2, "Artículos más prestados")
        barDataSet2.colors = ColorTemplate.MATERIAL_COLORS.toList()
        val barData2 = BarData(barDataSet2)
        barChart2.data = barData2
       val xAxis = barChart2.xAxis
       xAxis.valueFormatter = object : IndexAxisValueFormatter() {
           override fun getFormattedValue(value: Float): String {
               val index = value.toInt()
               val nombreArticulo = prestamosPorArticulo.keys.toList()[index]
               return nombreArticulo ?: ""
           }
       }
       xAxis.granularity = 1f
       xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM

       barChart2.invalidate()
       barChart2.legend.isEnabled = false
       barChart2.description.isEnabled = false

    }
}