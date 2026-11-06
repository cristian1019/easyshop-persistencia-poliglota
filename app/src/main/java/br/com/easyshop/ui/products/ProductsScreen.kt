package br.com.easyshop.ui.products

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.easyshop.data.nosql.Product
import br.com.easyshop.ui.FormMessageText
import br.com.easyshop.ui.toBrl

@Composable
fun ProductsScreen(
    modifier: Modifier = Modifier,
    viewModel: ProductViewModel = viewModel(factory = ProductViewModel.Factory)
) {
    val products by viewModel.products.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { ProductForm(viewModel) }
        item {
            Text(
                "Catálogo · ${products.size} documento(s) no ObjectBox",
                style = MaterialTheme.typography.titleMedium
            )
        }
        if (products.isEmpty()) {
            item { Text("Nenhum produto cadastrado ainda.", style = MaterialTheme.typography.bodyMedium) }
        }
        items(products, key = { it.id }) { product ->
            ProductCard(product, onDelete = { viewModel.delete(product) })
        }
    }
}

@Composable
private fun ProductForm(viewModel: ProductViewModel) {
    ElevatedCard(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Novo produto", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = viewModel.name,
                onValueChange = { viewModel.name = it },
                label = { Text("Nome *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = viewModel.price,
                    onValueChange = { viewModel.price = it },
                    label = { Text("Preço (R$) *") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = viewModel.category,
                    onValueChange = { viewModel.category = it },
                    label = { Text("Categoria") },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Text(
                "Atributos dinâmicos (opcionais, sem schema fixo)",
                style = MaterialTheme.typography.labelLarge
            )
            viewModel.attributes.forEach { row ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = row.key,
                        onValueChange = { viewModel.updateAttribute(row.copy(key = it)) },
                        label = { Text("Atributo") },
                        placeholder = { Text("ex.: cor") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = row.value,
                        onValueChange = { viewModel.updateAttribute(row.copy(value = it)) },
                        label = { Text("Valor") },
                        placeholder = { Text("ex.: azul") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { viewModel.removeAttribute(row) }) {
                        Icon(Icons.Filled.Close, contentDescription = "Remover atributo")
                    }
                }
            }
            TextButton(onClick = viewModel::addAttributeRow) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Text("Adicionar atributo")
            }

            Button(onClick = viewModel::save, modifier = Modifier.fillMaxWidth()) {
                Text("Salvar no banco NoSQL")
            }
            FormMessageText(viewModel.message)
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProductCard(product: Product, onDelete: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(start = 16.dp, top = 8.dp, end = 4.dp, bottom = 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text(
                        listOfNotNull(product.price.toBrl(), product.category).joinToString(" · "),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Filled.Delete, contentDescription = "Excluir produto")
                }
            }
            val attributes = product.attributes.orEmpty().toSortedMap()
            if (attributes.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(end = 12.dp)
                ) {
                    attributes.forEach { (key, value) ->
                        SuggestionChip(onClick = {}, label = { Text("$key: $value") })
                    }
                }
            }
            Text(
                "id #${product.id} · documento com ${attributes.size} atributo(s) dinâmico(s)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
