package pf.dam.utils.graficos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
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
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import db.PrestamosSQLite
import db.SociosSQLite
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

    private lateinit var textViewPrestamosPorSocio: TextView
    private lateinit var homeButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.graph_prestamos_activity)
        supportActionBar?.title = "RR - Gráficos préstamos"

        barChartPrestamosPorMes = findViewById(R.id.barChartPrestamosPorMes)
        lineChartPrestamosPorMes = findViewById(R.id.lineChartPrestamosPorMes)
        pieChartPrestamosPorEstado = findViewById(R.id.pieChartPrestamosPorEstado)
        textViewPrestamosPorSocio = findViewById(R.id.textViewPrestamosPorSocio)

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

        val prestamos = PrestamosSQLite(this).getAllPrestamos()
        val sociosSQLite = SociosSQLite(this)
        val prestamosPorSocio = prestamos.groupingBy { it.idSocio }.eachCount()
            .entries.sortedByDescending { it.value }
            .take(10)

        val textoPrestamosPorSocio = StringBuilder()
        prestamosPorSocio.forEach { entry ->
            val socio = sociosSQLite.getSocioById(entry.key)
            textoPrestamosPorSocio.append("Socio: \t${socio?.numeroSocio} ${socio?.nombre} ${socio?.apellido}: \t${entry.value} préstamos\n")
        }

        val textViewPrestamosPorSocio = findViewById<TextView>(R.id.textViewPrestamosPorSocio)
        textViewPrestamosPorSocio.text = textoPrestamosPorSocio.toString()
    }

    private fun configurarGraficos() {
        val prestamos = PrestamosSQLite(this).getAllPrestamos()
        crearGraficoLineasPrestamosPorMes(prestamos, lineChartPrestamosPorMes)
        crearGraficoPastelPrestamosPorEstado(prestamos, pieChartPrestamosPorEstado)
        crearGraficoBarrasApiladasPrestamosPorMesYEstado(prestamos, barChartPrestamosPorMes)
    }

    fun crearGraficoLineasPrestamosPorMes(prestamos: List<Prestamo>, lineChart: LineChart) {
        val prestamosPorMesL = prestamos.groupingBy {
            SimpleDateFormat("MM", Locale.getDefault()).format(it.fechaInicio)
        }.eachCount()

        val lineEntriesPrestamosPorMes = prestamosPorMesL.entries.mapIndexed { index, entry ->
            Entry(index.toFloat(), entry.value.toFloat(), entry.key)
        }

        val lineDataSetPrestamosPorMes =
            LineDataSet(lineEntriesPrestamosPorMes, "Cantidad de préstamos por mes")
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

        val pieDataSetPrestamosPorEstado =
            PieDataSet(pieEntriesPrestamosPorEstado, "Cantidad de préstamos por estado")
        pieDataSetPrestamosPorEstado.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val pieDataPrestamosPorEstado = PieData(pieDataSetPrestamosPorEstado)

        pieChart.data = pieDataPrestamosPorEstado
        pieChart.invalidate()
        pieChart.description.isEnabled = false
    }

    fun crearGraficoBarrasApiladasPrestamosPorMesYEstado(prestamos: List<Prestamo>, barChart: BarChart) {
        val prestamosPorMesYEstado = prestamos.groupBy {
            SimpleDateFormat("MM", Locale.getDefault()).format(it.fechaInicio)
        }.mapValues { (_, prestamosDelMes) ->
            prestamosDelMes.groupingBy { it.estado }.eachCount()
        }

        val meses = prestamosPorMesYEstado.keys.toList()
        val estados = prestamosPorMesYEstado.values.flatMap { it.keys }.distinct().toList()

        val barDataSets = mutableListOf<IBarDataSet>()

        meses.forEachIndexed { mesIndex, mes ->
            val valoresPorEstado = estados.map { estado ->
                prestamosPorMesYEstado[mes]?.get(estado)?.toFloat() ?: 0f
            }.toFloatArray()

            val barEntry = BarEntry(mesIndex.toFloat(), valoresPorEstado, mes)
            val barDataSet = BarDataSet(listOf(barEntry), mes)
            barDataSet.stackLabels = estados.map { it.toString() }.toTypedArray()
            barDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
            barDataSets.add(barDataSet)
        }

        val barData = BarData(barDataSets)
        barChart.data = barData

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(meses)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f

        barChart.description.isEnabled = false
        barChart.legend.isEnabled = true
        barChart.invalidate()
    }
}
