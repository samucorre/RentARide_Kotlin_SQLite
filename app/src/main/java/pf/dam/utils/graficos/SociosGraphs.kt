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
    //  private lateinit var bubbleChart: BubbleChart
    //  private lateinit var lineChart: LineChart
    private lateinit var barChart: BarChart
    //    private lateinit var scatterChart: ScatterChart
//private lateinit var lineChart: LineChart
    private lateinit var barChartHorizontal: BarChart
    private lateinit var homeButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.graphs_socios_activity)
        supportActionBar?.title = "RR - Gráficos socios"

        pieChart = findViewById(R.id.pieChartSocios)
        //   bubbleChart = findViewById(R.id.bubbleChartSocios)
        //      lineChart = findViewById(R.id.lineChartSocios)
        barChart = findViewById(R.id.barChartSocios)
//        barChartSociosPorAno = findViewById(R.id.barChartSociosPorAno)
//        scatterChart = findViewById(R.id.scatterChartSocios)
//        lineChart = findViewById(R.id.lineChartSocios)
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
        //   crearGraficoBurbujasAnoNacimiento(socios)
        crearGraficoBarrasSociosPorAno(socios, barChart)
//        crearGraficoLineasAnoNacimiento(socios)
//        crearGraficoDispersionAnoNacimiento(socios)
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

        // Configura el eje X para mostrar los años de nacimiento
        val xAxis = barChartHorizontal.xAxis
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return entries.getOrNull(value.toInt())?.data.toString() ?: ""
            }
        }
        xAxis.granularity = 1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM

        // Configura el eje Y para mostrar el número de socios
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
            ColorTemplate.COLORFUL_COLORS.toList() // Puedes usar otros colores si lo prefieres
        barDataSet.setDrawValues(true) // Mostrar valores en las barras

        // Crear entradas de leyenda para cada año
        val legendEntries = mutableListOf<LegendEntry>()
        sociosPorAno.keys.forEachIndexed { index, year ->
            year?.let {
                val legendEntry = LegendEntry()
                legendEntry.label = it.toString()
                legendEntry.formColor =
                    barDataSet.colors[index % barDataSet.colors.size] // Asignar color correspondiente
                legendEntries.add(legendEntry)
            }
        }

        // Configurar la leyenda del gráfico
        val legend = barChart.legend
        legend.setCustom(legendEntries)
        legend.isEnabled = true // Asegurarse de que la leyenda esté habilitada

        // Configurar el eje X (sin mostrar los años como etiquetas)
        val xAxis = barChart.xAxis
        xAxis.granularity = 1f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawLabels(false) // Ocultar etiquetas del eje X

        // Ocultar el eje derecho
        barChart.axisRight.isEnabled = false

        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.description.isEnabled = false // Ocultar la descripción del gráfico
        barChart.invalidate() // Actualizar el gráfico
    }

    fun obtenerAnoIngreso(fechaIngreso: Date?): Int? {
        return fechaIngreso?.let {
            Calendar.getInstance().apply { time = it }.get(Calendar.YEAR)
        }
    }
}

//    fun crearGraficoDispersionSociosPorAnoNacimiento(socios: List<Socio>) {
//        val sociosPorAnoNacimiento = socios.groupingBy { obtenerAnoNacimiento(it.fechaNacimiento) }.eachCount()
//        val scatterEntries = sociosPorAnoNacimiento.entries.mapNotNull { entry ->
//            entry.key?.let { Entry(it.toFloat(), entry.value.toFloat()) }
//        }
//
//        val scatterDataSet = ScatterDataSet(scatterEntries, "Socios por Año de Nacimiento")
//        scatterDataSet.color = ColorTemplate.MATERIAL_COLORS[0]
//        scatterDataSet.scatterShapeSize = 10f
//
//        val scatterData = ScatterData(scatterDataSet)
//        scatterChart.data = scatterData
//        scatterChart.xAxis.granularity = 1f
//        scatterChart.xAxis.valueFormatter = IndexAxisValueFormatter(sociosPorAnoNacimiento.keys.toList().mapNotNull { it?.toString() })
//        scatterChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//        scatterChart.description.isEnabled = false
//        scatterChart.legend.isEnabled = true
//        scatterChart.invalidate()
//    }
//
//    fun obtenerAnoNacimiento(fechaNacimiento: Date?): Int? {
//        return fechaNacimiento?.let {
//            Calendar.getInstance().apply { time = it }.get(Calendar.YEAR)
//        }
//    }


//    private fun configurarGraficos() {
//        val socios = SociosSQLite(this).obtenerSocios()
//        crearGraficoPastelGenero(socios)
//        crearGraficoBarrasRangoEdad(socios)
//        crearGraficoBarrasSociosPorAno(socios)
//        crearGraficoDispersionSociosPorAnoNacimiento(socios)
//    }
//
//    private fun crearGraficoPastelGenero(socios: List<Socio>) {
//        val sociosPorGenero = socios.groupingBy { it.genero }.eachCount()
//        val pieEntries =
//            sociosPorGenero.entries.map { PieEntry(it.value.toFloat(), it.key.toString()) }
//        val pieDataSet = PieDataSet(pieEntries, "Distribución de Género")
//        pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
//        val pieData = PieData(pieDataSet)
//        pieChart.data = pieData
//        pieChart.invalidate()
//        pieChart.description.isEnabled = true
//        pieChart.description.text = "Gráfico de género"
//    }
//
//    private fun crearGraficoBarrasRangoEdad(socios: List<Socio>) {
//        val sociosPorRangoEdad = socios.groupingBy {
//            it.fechaNacimiento?.let { it1 ->
//                calcularRangoEdad(
//                    it1
//                )
//            }
//        }.eachCount()
//        val barEntries = sociosPorRangoEdad.entries.mapIndexed { index, entry ->
//            BarEntry(index.toFloat(), entry.value.toFloat())
//        }
//        val barDataSet = BarDataSet(barEntries, "Socios por Rango de Edad")
//        barDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
//        barDataSet.setDrawValues(false)
//
//        barDataSet.setValueTextColors(listOf(Color.TRANSPARENT))
//
//        val barData = BarData(barDataSet)
//        barChart.data = barData
//        barChart.legend.isEnabled = false
//        barChart.description.isEnabled = false
//        barChart.description.text = "Gráfico de rango de edad"
//
//        val xAxis = barChart.xAxis
//        xAxis.valueFormatter = object : IndexAxisValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                val index = value.toInt()
//                return when (index) {
//                    0 -> "< 25"
//                    1 -> "25-34"
//                    2 -> "35-44"
//                    3 -> "45-54"
//                    4 -> "55-64"
//                    else -> "65+"
//                }
//            }
//        }
//        xAxis.granularity = 1f
//        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
//
//        barChart.invalidate()
//    }
//
//    private fun calcularRangoEdad(fechaNacimiento: Date): Int {
//        val calendar = Calendar.getInstance()
//        val currentYear = calendar.get(Calendar.YEAR)
//        calendar.time = fechaNacimiento
//        val birthYear = calendar.get(Calendar.YEAR)
//        val edad = currentYear - birthYear
//
//        return when {
//            edad < 25 -> 0
//            edad in 25..34 -> 1
//            edad in 35..44 -> 2
//            edad in 45..54 -> 3
//            edad in 55..64 -> 4
//            else -> 5
//        }
//    }
//

//
//    fun crearGraficoDispersionSociosPorAnoNacimiento(socios: List<Socio>) {
//        val sociosPorAnoNacimiento = socios.groupingBy { obtenerAnoNacimiento(it.fechaNacimiento) }.eachCount()
//        val scatterEntries = sociosPorAnoNacimiento.entries.mapNotNull { entry ->
//            entry.key?.let { Entry(it.toFloat(), entry.value.toFloat()) }
//        }
//
//        val scatterDataSet = ScatterDataSet(scatterEntries, "Socios por Año de Nacimiento")
//        scatterDataSet.color = ColorTemplate.MATERIAL_COLORS[0]
//        scatterDataSet.scatterShapeSize = 10f
//
//        val scatterData = ScatterData(scatterDataSet)
//        scatterChart.data = scatterData
//        scatterChart.xAxis.granularity = 1f
//        scatterChart.xAxis.valueFormatter = IndexAxisValueFormatter(sociosPorAnoNacimiento.keys.toList().mapNotNull { it?.toString() })
//        scatterChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//        scatterChart.description.isEnabled = false
//        scatterChart.legend.isEnabled = true
//        scatterChart.invalidate()
//    }
//
//    fun obtenerAnoNacimiento(fechaNacimiento: Date?): Int? {
//        return fechaNacimiento?.let {
//            Calendar.getInstance().apply { time = it }.get(Calendar.YEAR)
//        }
//    }
//}
// private fun crearGraficoLineasAnoNacimiento(socios: List<Socio>) {
//        val sociosPorAnoNacimiento = socios.groupingBy { obtenerAnoNacimiento(it.fechaNacimiento) }.eachCount()
//
//        val entries = sociosPorAnoNacimiento.entries.sortedBy { it.key }.mapIndexed { index, entry ->
//            Entry(index.toFloat(), entry.value.toFloat(), entry.key?.toString())
//        }
//
//        val dataSet = LineDataSet(entries, "Socios por Año de Nacimiento")
//        dataSet.color = Color.BLUE
//        dataSet.setCircleColor(Color.RED)
//        dataSet.lineWidth = 2f
//        dataSet.circleRadius = 4f
//        dataSet.setDrawCircleHole(false)
//        dataSet.valueTextSize = 12f
//        dataSet.valueTextColor = Color.BLACK
//
//        val lineData = LineData(dataSet)
//        lineChart.data = lineData
//
//        val xAxis = lineChart.xAxis
//        xAxis.valueFormatter = object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                return entries.getOrNull(value.toInt())?.data.toString() ?: ""
//            }
//        }
//        xAxis.granularity = 1f
//        xAxis.position = XAxis.XAxisPosition.BOTTOM
//
//        lineChart.axisLeft.axisMinimum = 0f
//        lineChart.description.isEnabled = false
//        lineChart.legend.isEnabled = true
//        lineChart.axisLeft.isEnabled = false
//        lineChart.axisRight.isEnabled = true
//        lineChart.axisRight.valueFormatter = object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                return value.toInt().toString()
//            }
//        }
//
//
//        lineChart.invalidate()
//    }
//
//    private fun crearGraficoDispersionAnoNacimiento(socios: List<Socio>) {
//        val sociosPorAnoNacimiento = socios.groupingBy { obtenerAnoNacimiento(it.fechaNacimiento) }.eachCount()
//
//        val entries = sociosPorAnoNacimiento.entries.sortedBy { it.key }.mapIndexed { index, entry ->
//            Entry(index.toFloat(), entry.value.toFloat(), entry.key?.toString())
//        }
//
//        val dataSet = ScatterDataSet(entries, "Socios por Año de Nacimiento")
//        dataSet.color = Color.BLUE
//        dataSet.scatterShapeSize = 10f // Ajusta el tamaño de los puntos
//        dataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE) // Puedes cambiar la forma de los puntos
//
//        val scatterData = ScatterData(dataSet)
//        scatterChart.data = scatterData
//
//        val xAxis = scatterChart.xAxis
//        xAxis.valueFormatter = object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                return entries.getOrNull(value.toInt())?.data.toString() ?: ""
//            }
//        }
//        xAxis.granularity = 1f
//        xAxis.position = XAxis.XAxisPosition.BOTTOM
//
//        scatterChart.axisLeft.axisMinimum = 0f
//        scatterChart.description.isEnabled = false
//        scatterChart.legend.isEnabled = true
//        scatterChart.axisLeft.isEnabled = false
//        scatterChart.axisRight.isEnabled = true
//        scatterChart.axisRight.valueFormatter = object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                return value.toInt().toString()
//            }
//        }
//
//        scatterChart.invalidate()
//    }
//    private fun crearGraficoBurbujasAnoNacimiento(socios: List<Socio>) {
//        val sociosPorAnoNacimiento = socios.groupingBy { obtenerAnoNacimiento(it.fechaNacimiento) }.eachCount()
//        val bubbleEntries = sociosPorAnoNacimiento.entries.mapNotNull { entry ->
//            entry.key?.let { BubbleEntry(it.toFloat(), entry.value.toFloat(), entry.value.toFloat() / 10) }
//        }
//
//        val bubbleDataSet = BubbleDataSet(bubbleEntries, "Socios por Año de Nacimiento")
//        bubbleDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
//        bubbleDataSet.setDrawValues(true)
//        bubbleDataSet.valueTextSize = 12f
//        bubbleDataSet.valueTextColor = Color.WHITE
//
//        val bubbleData = BubbleData(bubbleDataSet)
//        bubbleChart.data = bubbleData
//        bubbleChart.legend.isEnabled = true
//        bubbleChart.axisLeft.isEnabled = false
//        bubbleChart.description.isEnabled = false
//        bubbleChart.xAxis.granularity = 5f
//        bubbleChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//        bubbleChart.invalidate()
//    }
//
//
//    private fun obtenerAnoNacimiento(fechaNacimiento: Date?): Int? {
//        return fechaNacimiento?.let {
//            Calendar.getInstance().apply { time = it }.get(Calendar.YEAR)
//        }
//    }
