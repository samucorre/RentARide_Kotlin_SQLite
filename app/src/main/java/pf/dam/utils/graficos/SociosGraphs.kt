package pf.dam.utils.graficos

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.SociosSQLite
import pf.dam.MainActivity
import pf.dam.R
import pf.dam.socios.Socio
import java.util.*
import kotlin.collections.eachCount

class SociosGraphs : AppCompatActivity() {

    private lateinit var pieChart: PieChart
    private lateinit var barChart: BarChart
    private lateinit var barChartHorizontal: BarChart
    private lateinit var homeButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.graphs_socios_activity)
        supportActionBar?.title = "RR - Gráficos socios"

        pieChart = findViewById(R.id.pieChartSocios)
        barChart = findViewById(R.id.barChartSocios)
        barChartHorizontal = findViewById(R.id.barChartHorizontal)


        homeButton = findViewById(R.id.homeButtonSocios)
        volverButton = findViewById(R.id.volverButtonSocios)
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
        val socios = SociosSQLite(this).getAllSocios()
        crearGraficoPastelGenero(socios)
        crearGraficoBarrasSociosPorAno(socios, barChart)
        crearGraficoBarrasHorizontalesAnoNacimiento(socios)

    }

    private fun crearGraficoPastelGenero(socios: List<Socio>) {
        val sociosPorGenero = socios.groupingBy { it.genero }.eachCount()
        val pieEntries =
            sociosPorGenero.entries.map { PieEntry(it.value.toFloat(), it.key.toString()) }
        val pieDataSet = PieDataSet(pieEntries, "Distribución de Género")
        pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.invalidate()
        pieChart.description.isEnabled = true
        pieChart.description.text = "Gráfico de género"
    }

    private fun obtenerAnoNacimiento(fechaNacimiento: Date?): Int? {
        return fechaNacimiento?.let {
            Calendar.getInstance().apply { time = it }.get(Calendar.YEAR)
        }
    }

    private fun crearGraficoBarrasHorizontalesAnoNacimiento(socios: List<Socio>) {
        val sociosPorAnoNacimiento =
            socios.groupingBy { obtenerAnoNacimiento(it.fechaNacimiento) }.eachCount()

        val entries = sociosPorAnoNacimiento.entries.sortedByDescending { it.value }
            .mapIndexed { index, entry ->
                BarEntry(index.toFloat(), entry.value.toFloat(), entry.key?.toString())
            }

        val dataSet = BarDataSet(entries, "Socios por Año de Nacimiento")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        dataSet.valueTextSize = 12f
        dataSet.valueTextColor = Color.BLACK

        val barData = BarData(dataSet)
        barChartHorizontal.data = barData
        val xAxis = barChartHorizontal.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return entries.getOrNull(value.toInt())?.data.toString() ?: ""
            }
        }
        xAxis.granularity = 1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        val yAxis = barChartHorizontal.axisLeft
        yAxis.axisMinimum = 0f
        yAxis.granularity = 1f

        barChartHorizontal.description.isEnabled = false
        barChartHorizontal.legend.isEnabled = true
        barChartHorizontal.invalidate()
    }

    fun crearGraficoBarrasSociosPorAno(socios: List<Socio>, barChart: BarChart) {
        val sociosPorAno =
            socios.groupingBy { obtenerAnoIngreso(it.fechaIngresoSocio) }.eachCount()
        val barEntries = sociosPorAno.entries.mapNotNull { entry ->
            entry.key?.let { BarEntry(it.toFloat(), entry.value.toFloat()) }
        }

        val barDataSet = BarDataSet(barEntries, "Socios Ingresados por Año")
        barDataSet.colors =
            ColorTemplate.COLORFUL_COLORS.toList()
        barDataSet.setDrawValues(true)

        val legendEntries = mutableListOf<LegendEntry>()
        sociosPorAno.keys.forEachIndexed { index, year ->
            year?.let {
                val legendEntry = LegendEntry()
                legendEntry.label = it.toString()
                legendEntry.formColor =
                    barDataSet.colors[index % barDataSet.colors.size]
                legendEntries.add(legendEntry)
            }
        }

        val legend = barChart.legend
        legend.setCustom(legendEntries)
        legend.isEnabled = true

        val xAxis = barChart.xAxis
        xAxis.granularity = 1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawLabels(false)
        barChart.axisRight.isEnabled = false

        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.description.isEnabled = false
        barChart.invalidate()
    }

    fun obtenerAnoIngreso(fechaIngreso: Date?): Int? {
        return fechaIngreso?.let {
            Calendar.getInstance().apply { time = it }.get(Calendar.YEAR)
        }
    }
}
