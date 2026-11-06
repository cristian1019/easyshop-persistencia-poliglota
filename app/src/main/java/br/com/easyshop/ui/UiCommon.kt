package br.com.easyshop.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import java.text.NumberFormat
import java.util.Locale

/** Mensagem de feedback do formulário (sucesso ou erro). */
data class FormMessage(val text: String, val isError: Boolean)

private val currency: NumberFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR"))

fun Double.toBrl(): String = currency.format(this)

@Composable
fun FormMessageText(message: FormMessage?) {
    if (message == null) return
    Text(
        text = message.text,
        color = if (message.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
        style = MaterialTheme.typography.bodyMedium
    )
}
