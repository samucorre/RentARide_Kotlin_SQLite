//package pf.dam.utils
//
//import android.os.Bundle
//import androidx.appcompat.app.AppCompatActivity
//import com.github.mikephil.charting.charts.BarChart
//import com.github.mikephil.charting.data.BarData
//import com.github.mikephil.charting.data.BarDataSet
//import com.github.mikephil.charting.data.BarEntry
//import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
//import com.github.mikephil.charting.utils.ColorTemplate
//import db.SociosSQLite
//import pf.dam.R
//import kotlin.collections.eachCount
//
//class SocioGraphs : AppCompatActivity() {
//
//    private lateinit var barChart: BarChart
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_socio_graphs)
//
//        barChart = findViewById(R.id.barChart)
//
//        configurarGrafico()
//    }
//
//    private fun configurarGrafico() {
//        val socios = SociosSQLite(this).obtenerSocios()
//
//        // Gráfico de barras para la cantidad de socios por inicial del apellido
//        val sociosPorInicialApellido = socios.groupingBy { it.apellido.firstOrNull() ?: ' ' }.eachCount()
//        val barEntries = sociosPorInicialApellido.entries.mapIndexed { index, entry ->
//            BarEntry(index.toFloat(), entry.value.toFloat(), entry.key.toString())
//        }
//
//        val barDataSet = BarDataSet(barEntries, "Cantidad de socios por inicial del apellido")
//        barDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
//        val barData = BarData(barDataSet)
//
//        barChart.data = barData
//        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(sociosPorInicialApellido.keys.map { it.toString() })
//        barChart.invalidate()
//    }
//}