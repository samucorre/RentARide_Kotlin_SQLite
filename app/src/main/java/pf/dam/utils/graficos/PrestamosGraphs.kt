package pf.dam.utils.graficos

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.add
import androidx.compose.ui.semantics.text
import androidx.compose.ui.tooling.data.position
import androidx.core.text.color
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.ScatterChart
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
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
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
    private lateinit var barChartSociosPrestamos: BarChart
    private lateinit var barChartArticulosPrestamos: BarChart
   // private lateinit var lineChartPrestamosPorMes2: LineChart
    //private lateinit var scatterChartPrestamosPorMesYEstado: ScatterChart
    private lateinit var textViewPrestamosPorSocio: TextView
    private lateinit var homeButton: FloatingActionButton
    private lateinit var volverButton: FloatingActionButton

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_prestamos_graph)
        supportActionBar?.title = "RR - Gráficos préstamos"

        barChartPrestamosPorMes = findViewById(R.id.barChartPrestamosPorMes)
        lineChartPrestamosPorMes = findViewById(R.id.lineChartPrestamosPorMes)
    //    lineChartPrestamosPorMes2 = findViewById(R.id.lineChartPrestamosPorMes2)
        pieChartPrestamosPorEstado = findViewById(R.id.pieChartPrestamosPorEstado)
      //  scatterChartPrestamosPorMesYEstado = findViewById(R.id.scatterChartPrestamosPorMesYEstado)
        textViewPrestamosPorSocio = findViewById(R.id.textViewPrestamosPorSocio)
        barChartSociosPrestamos = findViewById(R.id.barChartSociosPrestamos) // Usa el ID que asignaste en el layout
        barChartArticulosPrestamos = findViewById(R.id.barChartArticulosPrestamos) // Usa el ID que asignaste en el layout

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

        val prestamos = PrestamosSQLite(this).obtenerPrestamos()
        val sociosSQLite = SociosSQLite(this)
        val prestamosPorSocio = prestamos.groupingBy { it.idSocio }.eachCount()
            .entries.sortedByDescending { it.value } // Ordenar por cantidad de préstamos (descendente)
            .take(10) // Tomar los 10 primeros

        val textoPrestamosPorSocio = StringBuilder()
        prestamosPorSocio.forEach { entry ->
            val socio = sociosSQLite.obtenerSocioPorId(entry.key) // Obtener el socio por ID
            textoPrestamosPorSocio.append("Socio: \t${socio?.numeroSocio} ${socio?.nombre} ${socio?.apellido}: \t${entry.value} préstamos\n")
        }

        val textViewPrestamosPorSocio = findViewById<TextView>(R.id.textViewPrestamosPorSocio)
        textViewPrestamosPorSocio.text = textoPrestamosPorSocio.toString()
    }

    private fun configurarGraficos() {
        val prestamos = PrestamosSQLite(this).obtenerPrestamos()
      //  crearGraficoBarrasPrestamosPorMes(prestamos, barChartPrestamosPorMes)
        crearGraficoLineasPrestamosPorMes(prestamos, lineChartPrestamosPorMes)
        crearGraficoPastelPrestamosPorEstado(prestamos, pieChartPrestamosPorEstado)
    //    crearGraficoLineasAgrupadasPrestamosPorMesYEstado(prestamos, lineChartPrestamosPorMes2)
     //   crearGraficoDispersionPrestamosPorMesYEstado(prestamos, scatterChartPrestamosPorMesYEstado)
       crearGraficoBarrasApiladasPrestamosPorMesYEstado(prestamos, barChartPrestamosPorMes)
        crearGraficoBarrasSocios(prestamos, barChartSociosPrestamos)
        crearGraficoBarrasArticulos(prestamos, barChartArticulosPrestamos)
    }
//
//    fun crearGraficoBarrasPrestamosPorMes(prestamos: List<Prestamo>, barChart: BarChart) {
//        val prestamosPorMes = prestamos.groupingBy {
//            SimpleDateFormat("MM", Locale.getDefault()).format(it.fechaInicio)
//        }.eachCount()
//        val barEntriesPrestamosPorMes = prestamosPorMes.entries.mapIndexed { index, entry ->
//            BarEntry(index.toFloat(), entry.value.toFloat(), entry.key)
//        }
//        val barDataSetPrestamosPorMes = BarDataSet(barEntriesPrestamosPorMes, "Cantidad de préstamos por mes")
//        barDataSetPrestamosPorMes.colors = ColorTemplate.COLORFUL_COLORS.toList()
//        val barDataPrestamosPorMes = BarData(barDataSetPrestamosPorMes)
//        barChart.data = barDataPrestamosPorMes
//        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(prestamosPorMes.keys.toList())
//        barChart.invalidate()
//        barChart.description.isEnabled = false
//        barChart.legend.isEnabled = false
//    }

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
    private fun crearGraficoBarrasSocios(prestamos: List<Prestamo>, barChart: BarChart) {
        val prestamosPorSocio = prestamos.groupingBy { it.idSocio }.eachCount()
        val barEntries = prestamosPorSocio.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat(), entry.key.toString())
        }
        val barDataSet = BarDataSet(barEntries, "Número de préstamos por socio")
        barDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(prestamosPorSocio.keys.map { it.toString() })
        barChart.invalidate()
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
    }

    private fun crearGraficoBarrasArticulos(prestamos: List<Prestamo>, barChart: BarChart) {
        val prestamosPorArticulo = prestamos.groupingBy { it.idArticulo }.eachCount()
        val barEntries = prestamosPorArticulo.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat(), entry.key.toString())
        }
        val barDataSet = BarDataSet(barEntries, "Número de préstamos por artículo")
        barDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(prestamosPorArticulo.keys.map { it.toString() })
        barChart.invalidate()
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false
    }
//    fun crearGraficoLineasAgrupadasPrestamosPorMesYEstado(prestamos: List<Prestamo>, lineChart: LineChart) {
//        val prestamosPorMesYEstado = prestamos.groupBy {
//            SimpleDateFormat("MM", Locale.getDefault()).format(it.fechaInicio)
//        }.mapValues { (_, prestamosDelMes) ->
//            prestamosDelMes.groupingBy { it.estado }.eachCount()
//        }
//
//        val meses = prestamosPorMesYEstado.keys.toList()
//        val estados = prestamosPorMesYEstado.values.flatMap { it.keys }.distinct().toList()
//
//        val lineDataSets = mutableListOf<ILineDataSet>()
//
//        estados.forEachIndexed { estadoIndex, estado ->
//            val entries = meses.mapIndexed { mesIndex, mes ->
//                Entry(mesIndex.toFloat(), prestamosPorMesYEstado[mes]?.get(estado)?.toFloat() ?: 0f)
//            }
//            val lineDataSet = LineDataSet(entries, estado.toString())
//            lineDataSet.color = ColorTemplate.COLORFUL_COLORS[estadoIndex % ColorTemplate.COLORFUL_COLORS.size]
//            lineDataSets.add(lineDataSet)
//        }
//
//        val lineData = LineData(lineDataSets)
//        lineChart.data = lineData
//        lineChart.xAxis.valueFormatter = IndexAxisValueFormatter(meses)
//        lineChart.invalidate()
//        lineChart.description.isEnabled = false
//        lineChart.legend.isEnabled = true // Habilitar la leyenda para mostrar los estados
//    }
//    fun crearGraficoDispersionPrestamosPorMesYEstado(prestamos: List<Prestamo>, scatterChart: ScatterChart) {
//        val prestamosPorMesYEstado = prestamos.groupBy {
//            SimpleDateFormat("MM", Locale.getDefault()).format(it.fechaInicio)
//        }.mapValues { (_, prestamosDelMes) ->
//            prestamosDelMes.groupingBy { it.estado }.eachCount()
//        }
//
//        val meses = prestamosPorMesYEstado.keys.toList()
//        val estados = prestamosPorMesYEstado.values.flatMap { it.keys }.distinct().toList()
//
//        val entries = mutableListOf<Entry>()
//
//        meses.forEachIndexed { mesIndex, mes ->
//            estados.forEachIndexed { estadoIndex, estado ->
//                val cantidad = prestamosPorMesYEstado[mes]?.get(estado)?.toFloat() ?: 0f
//                if (cantidad > 0) { // Solo agregar entradas con cantidad mayor a 0
//                    entries.add(Entry(mesIndex.toFloat(), estadoIndex.toFloat(), cantidad))
//                }
//            }
//        }
//
//        val scatterDataSet = ScatterDataSet(entries, "Préstamos por mes y estado")
//        scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE) // Usar círculos como forma de los puntos
//        scatterDataSet.scatterShapeSize = 10f // Ajustar el tamaño de los puntos
//        // Puedes ajustar el tamaño de los puntos en función de la cantidad de préstamos
//        // utilizando scatterDataSet.setScatterShapeSizes(sizes)
//
//        val scatterData = ScatterData(scatterDataSet)
//        scatterChart.data = scatterData
//        scatterChart.xAxis.valueFormatter = IndexAxisValueFormatter(meses)
//        scatterChart.axisLeft.valueFormatter = IndexAxisValueFormatter(estados.map { it.toString() })
//        scatterChart.invalidate()
//        scatterChart.description.isEnabled = false
//        scatterChart.legend.isEnabled = true // Habilitar la leyenda para mostrar los estados
//    }

//    fun crearGraficoBarrasApiladasPrestamosPorMesYEstado(prestamos: List<Prestamo>, barChart: BarChart) {
//        val prestamosPorMesYEstado = prestamos.groupBy {
//            SimpleDateFormat("MM", Locale.getDefault()).format(it.fechaInicio)
//        }.mapValues { (_, prestamosDelMes) ->
//            prestamosDelMes.groupingBy { it.estado }.eachCount()
//        }
//
//        val meses = prestamosPorMesYEstado.keys.toList()
//        val estados = prestamosPorMesYEstado.values.flatMap { it.keys }.distinct().toList()
//
//        val barEntries = mutableListOf<BarEntry>()
//        val barDataSets = mutableListOf<IBarDataSet>()
//
//        estados.forEachIndexed { estadoIndex, estado ->
//            val valores = meses.mapIndexed { mesIndex, mes ->
//                prestamosPorMesYEstado[mes]?.get(estado)?.toFloat() ?: 0f
//            }
//            val barEntry = BarEntry(estadoIndex.toFloat(), valores.toFloatArray(), estado.toString()) // Cambiado mesIndex por estadoIndex
//            barEntries.add(barEntry)
//
//            val barDataSet = BarDataSet(listOf(barEntry), estado.toString())
//            barDataSet.stackLabels =
//                arrayOf(estados.toTypedArray().toString()) // Asegúrate de que stackLabels esté configurado correctamente
//            barDataSet.colors = listOf(ColorTemplate.COLORFUL_COLORS[estadoIndex % ColorTemplate.COLORFUL_COLORS.size])
//            barDataSets.add(barDataSet)
//        }
//
//        val barData = BarData(barDataSets)
//        barChart.data = barData
//        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(meses) // Asegúrate de que el formateador del eje X esté configurado correctamente
//        barChart.invalidate()
//        barChart.description.isEnabled = false
//        barChart.legend.isEnabled = true // Habilitar la leyenda para mostrar los estados
//    }
    }
