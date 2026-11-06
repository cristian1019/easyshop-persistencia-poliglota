package br.com.easyshop.ui.customers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.easyshop.data.sql.Customer
import br.com.easyshop.ui.FormMessageText

@Composable
fun CustomersScreen(
    modifier: Modifier = Modifier,
    viewModel: CustomerViewModel = viewModel(factory = CustomerViewModel.Factory)
) {
    val customers by viewModel.customers.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { CustomerForm(viewModel) }
        item {
            Text(
                "Clientes · ${customers.size} registro(s) na tabela customers (SQLite)",
                style = MaterialTheme.typography.titleMedium
            )
        }
        if (customers.isEmpty()) {
            item { Text("Nenhum cliente cadastrado ainda.", style = MaterialTheme.typography.bodyMedium) }
        }
        items(customers, key = { it.id }) { customer ->
            CustomerCard(customer, onDelete = { viewModel.delete(customer) })
        }
    }
}

@Composable
private fun CustomerForm(viewModel: CustomerViewModel) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Novo cliente", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = viewModel.fullName,
                onValueChange = { viewModel.fullName = it },
                label = { Text("Nome completo *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("E-mail *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth()) {
                Text("Salvar no banco SQL")
            }
            FormMessageText(viewModel.message)
        }
    }
}

@Composable
private fun CustomerCard(customer: Customer, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(start = 16.dp, top = 8.dp, end = 4.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(customer.fullName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(customer.email, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
                Text(
                    "id (PK) #${customer.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Excluir cliente")
            }
        }
    }
}
