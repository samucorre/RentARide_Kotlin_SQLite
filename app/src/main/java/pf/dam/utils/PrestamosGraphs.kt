package pf.dam.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.PrestamosSQLite
import pf.dam.MainActivity
import pf.dam.R
import pf.dam.prestamos.Prestamo
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.collections.eachCount

class PrestamosGraphs : AppCompatActivity() {

    private lateinit var barChartPrestamosPorMes: BarChart
    private lateinit var pieChartPrestamosPorEstado: PieChart
    private lateinit var lineChartPrestamosPorMes: LineChart
    private lateinit var homeButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prestamos_graph)
        supportActionBar?.title = "RR - Gráficos préstamos"

        barChartPrestamosPorMes = findViewById(R.id.barChartPrestamosPorMes)
        lineChartPrestamosPorMes = findViewById(R.id.lineChartPrestamosPorMes)
        pieChartPrestamosPorEstado = findViewById(R.id.pieChartPrestamosPorEstado)
        homeButton = findViewById(R.id.homeButton)
        volverButton = findViewById(R.id.volverButton)


        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        volverButton.setOnClickListener {
            finish()
        }

        configurarGraficos()
    }
    private fun configurarGraficos() {
        val prestamos = PrestamosSQLite(this).obtenerPrestamos()

        crearGraficoBarrasPrestamosPorMes(prestamos, barChartPrestamosPorMes)
        crearGraficoLineasPrestamosPorMes(prestamos, lineChartPrestamosPorMes)
        crearGraficoPastelPrestamosPorEstado(prestamos, pieChartPrestamosPorEstado)
    }

    fun crearGraficoBarrasPrestamosPorMes(prestamos: List<Prestamo>, barChart: BarChart) {
        val prestamosPorMes = prestamos.groupingBy {
            SimpleDateFormat("MM", Locale.getDefault()).format(it.fechaInicio)
        }.eachCount()

        val barEntriesPrestamosPorMes = prestamosPorMes.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat(), entry.key)
        }

        val barDataSetPrestamosPorMes = BarDataSet(barEntriesPrestamosPorMes, "Cantidad de préstamos por mes")
        barDataSetPrestamosPorMes.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val barDataPrestamosPorMes = BarData(barDataSetPrestamosPorMes)

        barChart.data = barDataPrestamosPorMes
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(prestamosPorMes.keys.toList())
        barChart.invalidate()
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
    }

    fun crearGraficoLineasPrestamosPorMes(prestamos: List<Prestamo>, lineChart: LineChart) {
        val prestamosPorMesL = prestamos.groupingBy {
            SimpleDateFormat("MM", Locale.getDefault()).format(it.fechaInicio)
        }.eachCount()

        val lineEntriesPrestamosPorMes = prestamosPorMesL.entries.mapIndexed { index, entry ->
            Entry(index.toFloat(), entry.value.toFloat(), entry.key)
        }

        val lineDataSetPrestamosPorMes = LineDataSet(lineEntriesPrestamosPorMes, "Cantidad de préstamos por mes")
        lineDataSetPrestamosPorMes.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val lineDataPrestamosPorMes = LineData(lineDataSetPrestamosPorMes)

        lineChart.data = lineDataPrestamosPorMes
        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(prestamosPorMesL.keys.toList())
        lineChart.invalidate()
        lineChart.description.isEnabled = false
        lineChart.legend.isEnabled = false
        lineChart.axisLeft.isEnabled = false
    }

    fun crearGraficoPastelPrestamosPorEstado(prestamos: List<Prestamo>, pieChart: PieChart) {
        val prestamosPorEstado = prestamos.groupingBy { it.estado }.eachCount()

        val pieEntriesPrestamosPorEstado = prestamosPorEstado.entries.map {
            PieEntry(it.value.toFloat(), it.key.toString())
        }

        val pieDataSetPrestamosPorEstado = PieDataSet(pieEntriesPrestamosPorEstado, "Cantidad de préstamos por estado")
        pieDataSetPrestamosPorEstado.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val pieDataPrestamosPorEstado = PieData(pieDataSetPrestamosPorEstado)

        pieChart.data = pieDataPrestamosPorEstado
        pieChart.invalidate()
        pieChart.description.isEnabled = false
    }
}