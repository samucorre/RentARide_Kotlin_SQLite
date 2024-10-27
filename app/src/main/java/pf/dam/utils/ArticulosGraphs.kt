package pf.dam.utils

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
import db.ArticulosSQLite
import pf.dam.R

class ArticulosGraphs : AppCompatActivity() {

    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_articulos_graphs)

        pieChart = findViewById(R.id.pieChart)
        barChart = findViewById(R.id.barChart)

        configurarGraficos() // Llama al método para configurar los gráficos
    }

    private fun configurarGraficos() {
        val articulos = ArticulosSQLite(this).obtenerArticulos()

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