package br.com.easyshop.ui.products

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.easyshop.EasyShopApp
import br.com.easyshop.data.nosql.Product
import br.com.easyshop.domain.ProductService
import br.com.easyshop.domain.ValidationException
import br.com.easyshop.ui.FormMessage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Linha editável de atributo dinâmico (chave/valor) no formulário. */
data class AttributeRow(val id: Long, val key: String = "", val value: String = "")

/** Estado e ações da tela de Produtos. Conversa apenas com o serviço, nunca com o banco. */
class ProductViewModel(private val service: ProductService) : ViewModel() {

    var name by mutableStateOf("")
    var price by mutableStateOf("")
    var category by mutableStateOf("")
    val attributes = mutableStateListOf<AttributeRow>()
    var message by mutableStateOf<FormMessage?>(null)
        private set

    private var nextRowId = 0L

    val products: StateFlow<List<Product>> = service.observeProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addAttributeRow() {
        attributes.add(AttributeRow(id = nextRowId++))
    }

    fun updateAttribute(row: AttributeRow) {
        val index = attributes.indexOfFirst { it.id == row.id }
        if (index >= 0) attributes[index] = row
    }

    fun removeAttribute(row: AttributeRow) {
        attributes.removeAll { it.id == row.id }
    }

    fun save() {
        viewModelScope.launch {
            try {
                val saved = service.addProduct(
                    name = name,
                    priceText = price,
                    category = category,
                    attributes = attributes.map { it.key to it.value }
                )
                name = ""
                price = ""
                category = ""
                attributes.clear()
                val count = saved.attributes?.size ?: 0
                message = FormMessage(
                    "Documento \"${saved.name}\" salvo no ObjectBox (id ${saved.id}, $count atributo(s)).",
                    false
                )
            } catch (e: ValidationException) {
                message = FormMessage(e.message ?: "Erro de validação.", true)
            }
        }
    }

    fun delete(product: Product) {
        viewModelScope.launch {
            service.remove(product)
            message = FormMessage("Produto \"${product.name}\" removido.", false)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { ProductViewModel((this[APPLICATION_KEY] as EasyShopApp).productService) }
        }
    }
}
