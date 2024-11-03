package pf.dam.utils

import android.database.sqlite.SQLiteDatabase
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.util.Patterns

open class ValidacionUtils {

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validateEmail(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                if (!isEmailValid(email)) {
                    editText.error = "Email inválido"
                } else {
                    editText.error = null
                }
            }
        })
    }

    fun isNumeroSocioUnique(db: SQLiteDatabase, numeroSocio: Int, socioId: Int? = null): Boolean {
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM socios WHERE numeroSocio = ? AND idSocio != ?",
            arrayOf(numeroSocio.toString(), socioId?.toString() ?: "-1")
        )
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count == 0
    }

    fun validateNumeroSocio(editText: EditText, db: SQLiteDatabase, socioId: Int? = null) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val numeroSocio = s.toString().toIntOrNull() ?: 0
                if (!isNumeroSocioUnique(db, numeroSocio, socioId)) {
                    editText.error = "El número de socio ya existe"
                } else {
                    editText.error = null
                }
            }
        })
    }
}