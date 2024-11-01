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
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.PrestamosSQLite
import pf.dam.MainActivity
import pf.dam.R
import java.text.SimpleDateFormat
import java.util.Locale

class PrestamosGraphs : AppCompatActivity() {

    private lateinit var barChartPrestamosPorMes: BarChart
    private lateinit var pieChartPrestamosPorEstado: PieChart
    private lateinit var homeButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prestamos_graph)

        barChartPrestamosPorMes = findViewById(R.id.barChartPrestamosPorMes)
        pieChartPrestamosPorEstado = findViewById(R.id.pieChartPrestamosPorEstado)
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

        configurarGraficos()
    }

    private fun configurarGraficos() {
        val prestamos = PrestamosSQLite(this).obtenerPrestamos()

        // Gráfico de barras para la cantidad de préstamos por mes
        val prestamosPorMes = prestamos.groupingBy {
            SimpleDateFormat("MM", Locale.getDefault()).format(it.fechaInicio)
        }.eachCount()

        val barEntriesPrestamosPorMes = prestamosPorMes.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat(), entry.key)
        }

        val barDataSetPrestamosPorMes = BarDataSet(barEntriesPrestamosPorMes, "Cantidad de préstamos por mes")
        barDataSetPrestamosPorMes.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val barDataPrestamosPorMes = BarData(barDataSetPrestamosPorMes)

        barChartPrestamosPorMes.data = barDataPrestamosPorMes
        barChartPrestamosPorMes.xAxis.valueFormatter = IndexAxisValueFormatter(prestamosPorMes.keys.toList())
        barChartPrestamosPorMes.invalidate()

        // Gráfico de pastel para la cantidad de préstamos por estado
        val prestamosPorEstado = prestamos.groupingBy { it.estado }.eachCount()

        val pieEntriesPrestamosPorEstado = prestamosPorEstado.entries.map {
            PieEntry(it.value.toFloat(), it.key.toString())
        }

        val pieDataSetPrestamosPorEstado = PieDataSet(pieEntriesPrestamosPorEstado, "Cantidad de préstamos por estado")
        pieDataSetPrestamosPorEstado.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val pieDataPrestamosPorEstado = PieData(pieDataSetPrestamosPorEstado)

        pieChartPrestamosPorEstado.data = pieDataPrestamosPorEstado
        pieChartPrestamosPorEstado.invalidate()
    }
}