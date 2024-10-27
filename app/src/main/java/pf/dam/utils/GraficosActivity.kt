package pf.dam.utils

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.color
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.utils.ColorTemplate
import pf.dam.R

class GraficosActivity : AppCompatActivity() {
    private lateinit var chart: LineChart
    private lateinit var barChart: BarChart
    private lateinit var radarChart: RadarChart
    private lateinit var pieChart: PieChart
    private lateinit var scatterChart: ScatterChart
    private lateinit var bubbleChart: BubbleChart
    private lateinit var candleStickChart: CandleStickChart

    override fun onContentChanged() {
        super.onContentChanged()

        chart = findViewById(R.id.chart)
        barChart = findViewById(R.id.barChart)
        radarChart = findViewById(R.id.radarChart)
        pieChart = findViewById(R.id.pieChart)
        scatterChart = findViewById(R.id.scatterChart)
        bubbleChart = findViewById(R.id.bubbleChart)
        candleStickChart = findViewById(R.id.candleStickChart)



        // Gráfico de líneas
        val entries = mutableListOf<Entry>()
        entries.add(Entry(1f, 2f))
        entries.add(Entry(2f, 4f))
        entries.add(Entry(3f, 1f))
        entries.add(Entry(4f, 3f))
        val dataSet = LineDataSet(entries, "Muestras")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK
        val lineData = LineData(dataSet)
        chart.data = lineData
        chart.invalidate()

        // Gráfico de barras
        val barEntries = listOf(
            BarEntry(0f, 30f),
            BarEntry(1f, 80f),
            BarEntry(2f, 60f)
        )
        val barDataSet = BarDataSet(barEntries, "Datos de barras")
        barDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        val barData = BarData(barDataSet)
        barChart.data = barData
        barChart.invalidate()

        // Gráfico de radar
        val radarEntries = listOf(
            RadarEntry(30f),
            RadarEntry(80f),
            RadarEntry(60f),
            RadarEntry(40f),
            RadarEntry(50f)
        )
        val radarDataSet = RadarDataSet(radarEntries, "Datos de radar")
        radarDataSet.color = Color.RED
        radarDataSet.fillColor = Color.RED
        radarDataSet.setDrawFilled(true)
        val radarData = RadarData(radarDataSet)
        radarChart.data = radarData
        radarChart.invalidate()

        // Gráfico de pastel
        val pieEntries = listOf(
            PieEntry(30f, "Categoría A"),
            PieEntry(80f, "Categoría B"),
            PieEntry(60f, "Categoría C")
        )
        val pieDataSet = PieDataSet(pieEntries, "Datos de pastel")
        pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val pieData = PieData(pieDataSet)
        pieChart.data = pieData
        pieChart.invalidate()

        // Gráfico de dispersión
        val scatterEntries = listOf(
            Entry(1f, 2f),
            Entry(2f, 4f),
            Entry(3f, 1f),
            Entry(4f, 3f)
        )
        val scatterDataSet = ScatterDataSet(scatterEntries, "Datos de dispersión")
        scatterDataSet.color = Color.GREEN
        val scatterData = ScatterData(scatterDataSet)
        scatterChart.data = scatterData
        scatterChart.invalidate()

        // Gráfico de burbujas
        val bubbleEntries = listOf(
            BubbleEntry(1f, 2f, 10f),
            BubbleEntry(2f, 4f, 15f),
            BubbleEntry(3f, 1f, 5f),
            BubbleEntry(4f, 3f, 20f)
        )
        val bubbleDataSet = BubbleDataSet(bubbleEntries, "Datos de burbujas")
        bubbleDataSet.colors = ColorTemplate.JOYFUL_COLORS.toList()
        val bubbleData = BubbleData(bubbleDataSet)
        bubbleChart.data = bubbleData
        bubbleChart.invalidate()

        // Gráfico de velas
        val candleEntries = listOf(
            CandleEntry(0f, 10f, 5f, 7f, 8f),
            CandleEntry(1f, 12f, 7f, 9f, 10f),
            CandleEntry(2f, 15f, 9f, 11f, 13f)
        )
        val candleDataSet = CandleDataSet(candleEntries, "Datos de velas")
        candleDataSet.color = Color.RED
        candleDataSet.shadowColor = Color.DKGRAY
        val candleData = CandleData(candleDataSet)
        candleStickChart.data = candleData
        candleStickChart.invalidate()
    }
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_graficos)
            supportActionBar?.title = "RR - Informes Gráficos"

            val btnArticulosGraphs: Button = findViewById(R.id.btnArticulosGraphs)
            btnArticulosGraphs.setOnClickListener {
                val intent = Intent(this, ArticulosGraphs::class.java)
                startActivity(intent)

            }
            val btnPrestamosGraphs: Button = findViewById(R.id.btnPrestamosGraphs)
            btnPrestamosGraphs.setOnClickListener {
                val intent = Intent(this, PrestamosGraphs::class.java)
                startActivity(intent)

            }

            val btnSocioGraphs: Button = findViewById(R.id.btnSocioGraphs)
            btnSocioGraphs.setOnClickListener {
                val intent = Intent(this, SocioGraphs::class.java)
                startActivity(intent)
            }

        }
    }