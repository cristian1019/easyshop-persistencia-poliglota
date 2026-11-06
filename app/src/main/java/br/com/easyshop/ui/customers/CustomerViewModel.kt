package br.com.easyshop.ui.customers

import android.util.Log
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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
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
        .catch { e ->
            Log.e(TAG, "Erro ao carregar clientes", e)
            message = FormMessage("Não foi possível carregar os clientes.", true)
            emit(emptyList())
        }
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
            } catch (e: CancellationException) {
                throw e // cancelamento normal da coroutine (ex.: tela fechada) não é erro
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao salvar", e)
                message = FormMessage("Não foi possível salvar. Tente novamente.", true)
            }
        }
    }

    fun delete(customer: Customer) {
        viewModelScope.launch {
            try {
                service.remove(customer)
                message = FormMessage("Cliente \"${customer.fullName}\" removido.", false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Erro ao remover", e)
                message = FormMessage("Não foi possível remover. Tente novamente.", true)
            }
        }
    }

    companion object {
        private const val TAG = "EasyShop"

        val Factory = viewModelFactory {
            initializer { CustomerViewModel((this[APPLICATION_KEY] as EasyShopApp).customerService) }
        }
    }
}
