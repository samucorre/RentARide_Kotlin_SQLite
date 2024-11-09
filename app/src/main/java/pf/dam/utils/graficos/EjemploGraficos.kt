package pf.dam.utils.graficos

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.BubbleChart
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.RadarChart
import com.github.mikephil.charting.charts.ScatterChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BubbleData
import com.github.mikephil.charting.data.BubbleDataSet
import com.github.mikephil.charting.data.BubbleEntry
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.RadarData
import com.github.mikephil.charting.data.RadarDataSet
import com.github.mikephil.charting.data.RadarEntry
import com.github.mikephil.charting.data.ScatterData
import com.github.mikephil.charting.data.ScatterDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import pf.dam.R

class EjemploGraficos: AppCompatActivity() {

    private lateinit var chartE: LineChart
    private lateinit var barChartE: BarChart
    private lateinit var radarChartE: RadarChart
    private lateinit var pieChartE: PieChart
    private lateinit var scatterChartE: ScatterChart
    private lateinit var bubbleChartE: BubbleChart
    private lateinit var candleStickChartE: CandleStickChart

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graficos_ejemplo)

        chartE = findViewById(R.id.chartE)
        barChartE = findViewById(R.id.barChartE)
        radarChartE = findViewById(R.id.radarChartE)
        pieChartE = findViewById(R.id.pieChartE)
        scatterChartE = findViewById(R.id.scatterChartE)
        bubbleChartE = findViewById(R.id.bubbleChartE)
        candleStickChartE = findViewById(R.id.candleStickChartE)

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
        chartE.data = lineData
        chartE.invalidate()

// Gráfico de barras
        val barEntries = listOf(
            BarEntry(0f, 30f),
            BarEntry(1f, 80f),
            BarEntry(2f, 60f)
        )
        val barDataSet = BarDataSet(barEntries, "Datos de barras")
        barDataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()
        val barData = BarData(barDataSet)
        barChartE.data = barData
        barChartE.invalidate()

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
        radarChartE.data = radarData
        radarChartE.invalidate()

// Gráfico de pastel
        val pieEntries = listOf(
            PieEntry(30f, "Categoría A"),
            PieEntry(80f, "Categoría B"),
            PieEntry(60f, "Categoría C")
        )
        val pieDataSet = PieDataSet(pieEntries, "Datos de pastel")
        pieDataSet.colors = ColorTemplate.COLORFUL_COLORS.toList()
        val pieData = PieData(pieDataSet)
        pieChartE.data = pieData
        pieChartE.invalidate()

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
        scatterChartE.data = scatterData
        scatterChartE.invalidate()

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
        bubbleChartE.data = bubbleData
        bubbleChartE.invalidate()

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
        candleStickChartE.data = candleData
        candleStickChartE.invalidate()
    }
    }