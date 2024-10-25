//package pf.dam.utils
//
//import android.content.Context
//import com.jjoe64.graphview.GraphView
//import com.jjoe64.graphview.series.BarGraphSeries
//import com.jjoe64.graphview.series.DataPoint
//
//class GraficoUtil {
//
//    fun generarGraficoBarrasGraphView(
//        context: Context,
//        graphView: GraphView,
//        etiquetas: List<String>,
//        valores: List<Int>,
//        titulo: String
//    ) {
//        val dataPoints = mutableListOf<com.google.android.gms.fitness.data.DataPoint>()
//        for (i in etiquetas.indices) {
//            dataPoints.add(DataPoint(i.toDouble(), valores[i].toDouble()))
//        }
//
//        val series = BarGraphSeries(dataPoints.toTypedArray())
//        graphView.addSeries(series)
//
//        // Personalización del gráfico (opcional)
//        graphView.title = titulo
//        graphView.viewport.isXAxisBoundsManual = true
//        graphView.viewport.setMinX(0.0)
//        graphView.viewport.setMaxX(etiquetas.size.toDouble() -1)
//        // ... otras opciones de personalización ...
//    }
//}