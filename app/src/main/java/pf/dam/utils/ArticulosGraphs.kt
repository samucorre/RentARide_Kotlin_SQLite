package pf.dam.utils

import android.content.Intent
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
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.ArticulosSQLite
import db.PrestamosSQLite
import pf.dam.MainActivity
import pf.dam.R
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
        setContentView(R.layout.activity_articulos_graphs)

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
        val articulos = ArticulosSQLite(this).obtenerArticulos()
        val prestamos = PrestamosSQLite(this).obtenerPrestamos()

        // Gráfico de pastel para categorías con más préstamos
        val prestamosPorCategoria = prestamos.groupingBy {
            PrestamosSQLite(this).obtenerCategoriaPrestamoId(it.idArticulo) // Obtener nombre de categoría
        }.eachCount()
        val pieEntries2 = prestamosPorCategoria.entries.map { PieEntry(it.value.toFloat(), it.key) }
        val pieDataSet2 = PieDataSet(pieEntries2, "Categorías con más préstamos")
        pieDataSet2.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val pieData2 = PieData(pieDataSet2)
        pieChart2.data = pieData2
        pieChart2.invalidate()

        // Gráfico de barras para artículos más prestados
        val prestamosPorArticulo = prestamos.groupingBy {
            PrestamosSQLite(this).obtenerArticuloPrestamoId(it.idArticulo)?.nombre // Obtener nombre de artículo
        }.eachCount()
        val barEntries2 = prestamosPorArticulo.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }
        val barDataSet2 = BarDataSet(barEntries2, "Artículos más prestados")
        barDataSet2.colors = ColorTemplate.MATERIAL_COLORS.toList()
        val barData2 = BarData(barDataSet2)
        barChart2.data = barData2
        barChart2.invalidate()
        // Gráfico de pastel para categorías
        val articulosPorCategoria = articulos.groupingBy { it.categoria }.eachCount()
        val pieEntries = articulosPorCategoria.entries.map { PieEntry(it.value.toFloat(), it.key) }
        val pieDataSet = PieDataSet(pieEntries, "Categorías de artículos")
        pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.invalidate()

        // Gráfico de barras para estados
        val articulosPorEstado = articulos.groupingBy { it.estado }.eachCount()
        val barEntries = articulosPorEstado.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat())
        }
        val barDataSet = BarDataSet(barEntries, "Estados de artículos")
        barDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.invalidate()
    }

}