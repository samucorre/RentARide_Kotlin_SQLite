package pf.dam.utils

import android.content.Context
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class DialogoUtil( context: Context) {

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
            TextButton(
                onClick = onPositiveButtonClick,
                modifier = Modifier,
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Red, // Color de fondo rojo
                    contentColor = MaterialTheme.colorScheme.onError  // Color del texto en contraste
                )
            ) {
                Text(text = positiveButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = negativeButtonText)
            }
        },
        shape = RoundedCornerShape(8.dp), // Forma redondeada (opcional)
        containerColor =  Color(0xFFFFF2CC)// Color de fondo del diálogo
    )
}