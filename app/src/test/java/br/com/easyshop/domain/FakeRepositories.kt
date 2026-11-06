package br.com.easyshop.domain

import br.com.easyshop.data.nosql.Product
import br.com.easyshop.data.nosql.ProductRepository
import br.com.easyshop.data.sql.Customer
import br.com.easyshop.data.sql.CustomerRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** Repositório de produtos em memória, sem ObjectBox — só para testes. */
class FakeProductRepository : ProductRepository {
    val products = MutableStateFlow<List<Product>>(emptyList())
    private var nextId = 1L

    override fun observeAll(): Flow<List<Product>> = products

    override suspend fun insert(product: Product): Long {
        val id = nextId++
        products.value = products.value + product.copy(id = id)
        return id
    }

    override suspend fun delete(id: Long): Boolean {
        val before = products.value.size
        products.value = products.value.filterNot { it.id == id }
        return products.value.size < before
    }
}

/** Repositório de clientes em memória, sem Room/SQLite — só para testes. */
class FakeCustomerRepository : CustomerRepository {
    val customers = MutableStateFlow<List<Customer>>(emptyList())
    private var nextId = 1L

    override fun observeAll(): Flow<List<Customer>> = customers

    override suspend fun existsByEmail(email: String): Boolean =
        customers.value.any { it.email == email }

    override suspend fun insert(customer: Customer): Long {
        val id = nextId++
        customers.value = customers.value + customer.copy(id = id)
        return id
    }

    override suspend fun delete(customer: Customer) {
        customers.value = customers.value.filterNot { it.id == customer.id }
    }
}
