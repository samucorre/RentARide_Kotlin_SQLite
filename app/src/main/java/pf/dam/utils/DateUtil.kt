package pf.dam.utils

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.widget.Button
import pf.dam.R
import java.text.SimpleDateFormat
import java.util.Locale


class DateUtil (context: Context){
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val calendar = Calendar.getInstance()

    fun mostrarDatePicker(context: Context, editText: Button) {
        val datePicker = DatePickerDialog(
            context, // Usa el contexto pasado como parámetro
            R.style.MyDatePickerDialogTheme,
            { _, year, month, dayOfMonth ->
                calendar.set(java.util.Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                editText.text = dateFormat.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    companion object {
        fun mostrarDatePicker(fechaInicioButton: Button?) {

        }
    }
}
