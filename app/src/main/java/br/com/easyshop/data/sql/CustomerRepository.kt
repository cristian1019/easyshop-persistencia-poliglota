package br.com.easyshop.data.sql

import kotlinx.coroutines.flow.Flow

/**
 * Repositório SQL: única porta de acesso da aplicação à tabela de clientes.
 * É uma interface para que a camada de negócio possa ser testada com um repositório falso.
 */
interface CustomerRepository {
    fun observeAll(): Flow<List<Customer>>
    suspend fun existsByEmail(email: String): Boolean
    suspend fun insert(customer: Customer): Long
    suspend fun delete(customer: Customer)
}

/** Implementação real, sobre o Room/SQLite. */
class RoomCustomerRepository(private val dao: CustomerDao) : CustomerRepository {

    override fun observeAll(): Flow<List<Customer>> = dao.observeAll()

    override suspend fun existsByEmail(email: String): Boolean = dao.countByEmail(email) > 0

    override suspend fun insert(customer: Customer): Long = dao.insert(customer)

    override suspend fun delete(customer: Customer) {
        dao.delete(customer)
    }
}
