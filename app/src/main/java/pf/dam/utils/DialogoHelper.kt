package pf.dam.utils

import android.content.Context
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

class DialogoHelper(private val context: Context) {

    @Composable
    fun ShowEditConfirmationDialog(
        title: String,
        message: String,
        positiveButtonText: String = "Editar",
        negativeButtonText: String = "Cancelar",
        onPositiveButtonClick: () -> Unit,
        onDismissRequest: () -> Unit
    ) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text(text = title) },
            text = { Text(text = message) },
            confirmButton = {
                TextButton(onClick = onPositiveButtonClick) {
                    Text(text = positiveButtonText)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissRequest) {
                    Text(text = negativeButtonText)
                }
            }
        )
    }
}

@Composable
fun ShowDeleteConfirmationDialog(
    title: String,
    message: String,
    positiveButtonText: String = "Eliminar",
    negativeButtonText: String = "Cancelar",
    onPositiveButtonClick: () -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(onClick = onPositiveButtonClick) {
                Text(text = positiveButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = negativeButtonText)
            }
        }
    )
}