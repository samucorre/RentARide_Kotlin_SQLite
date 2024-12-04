package pf.dam.utils

import android.app.DatePickerDialog
import android.content.Context
import android.icu.util.Calendar
import android.widget.Button
import android.widget.EditText
import pf.dam.R
import java.text.SimpleDateFormat
import java.util.Locale


class FechaUtils (context: Context){
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    private val calendar = Calendar.getInstance()

    fun mostrarDatePickerEditText(context: Context, editText: EditText) {
        val datePicker = DatePickerDialog(
            context,
            R.style.MyDatePickerDialogTheme,
            { _, year, month, dayOfMonth ->
                calendar.set(java.util.Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                editText.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    fun mostrarDatePicker(context: Context, editText: Button) {
        val datePicker = DatePickerDialog(
            context,
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

    fun mostrarDatePickerPrestamos(context: Context, button: Button) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        R.style.MyDatePickerDialogTheme

        val datePickerDialog = DatePickerDialog(

            context,
            R.style.MyDatePickerDialogTheme,

            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }.time
                button.text = dateFormat.format(selectedDate)
            },
            year,
            month,
            day
        )

        val fechaActual = System.currentTimeMillis()
        datePickerDialog.datePicker.minDate = fechaActual
        datePickerDialog.datePicker.maxDate = fechaActual + (7 * 24 * 60 * 60 * 1000)

        datePickerDialog.show()
    }

    companion object {
        fun mostrarDatePicker(fechaInicioButton: Button?) {

        }
    }
}
