package br.com.easyshop.ui.customers

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import br.com.easyshop.EasyShopApp
import br.com.easyshop.data.sql.Customer
import br.com.easyshop.domain.CustomerService
import br.com.easyshop.domain.ValidationException
import br.com.easyshop.ui.FormMessage
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Estado e ações da tela de Clientes. Conversa apenas com o serviço, nunca com o banco. */
class CustomerViewModel(private val service: CustomerService) : ViewModel() {

    var fullName by mutableStateOf("")
    var email by mutableStateOf("")
    var message by mutableStateOf<FormMessage?>(null)
        private set

    val customers: StateFlow<List<Customer>> = service.observeCustomers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun save() {
        viewModelScope.launch {
            try {
                val saved = service.register(fullName, email)
                fullName = ""
                email = ""
                message = FormMessage("Cliente \"${saved.fullName}\" salvo no SQLite (id ${saved.id}).", false)
            } catch (e: ValidationException) {
                message = FormMessage(e.message ?: "Erro de validação.", true)
            }
        }
    }

    fun delete(customer: Customer) {
        viewModelScope.launch {
            service.remove(customer)
            message = FormMessage("Cliente \"${customer.fullName}\" removido.", false)
        }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer { CustomerViewModel((this[APPLICATION_KEY] as EasyShopApp).customerService) }
        }
    }
}
