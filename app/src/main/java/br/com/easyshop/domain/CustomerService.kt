package br.com.easyshop.domain

import android.database.sqlite.SQLiteConstraintException
import br.com.easyshop.data.sql.Customer
import br.com.easyshop.data.sql.CustomerRepository
import kotlinx.coroutines.flow.Flow

/**
 * Regras de negócio do cadastro de clientes (dados estruturados → SQL).
 * Valida estritamente os dados antes de chegar ao banco relacional.
 */
class CustomerService(private val repository: CustomerRepository) {

    fun observeCustomers(): Flow<List<Customer>> = repository.observeAll()

    suspend fun register(fullName: String, email: String): Customer {
        val name = fullName.trim().replace(Regex("""\s+"""), " ")
        val normalizedEmail = email.trim().lowercase()

        if (name.isEmpty()) throw ValidationException("Informe o nome completo.")
        if (name.split(" ").size < 2) throw ValidationException("Informe nome e sobrenome.")
        if (name.length > 120) throw ValidationException("Nome muito longo (máx. 120 caracteres).")
        if (normalizedEmail.isEmpty()) throw ValidationException("Informe o e-mail.")
        if (!EMAIL_REGEX.matches(normalizedEmail)) throw ValidationException("E-mail inválido.")
        if (repository.existsByEmail(normalizedEmail)) {
            throw ValidationException("Já existe um cliente com este e-mail.")
        }

        val customer = Customer(fullName = name, email = normalizedEmail)
        return try {
            customer.copy(id = repository.insert(customer))
        } catch (e: SQLiteConstraintException) {
            // Segunda barreira: a restrição UNIQUE do próprio SQLite
            throw ValidationException("Já existe um cliente com este e-mail.")
        }
    }

    suspend fun remove(customer: Customer) = repository.delete(customer)

    private companion object {
        val EMAIL_REGEX = Regex("""^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$""")
    }
}
