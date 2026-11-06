package br.com.easyshop.data.sql

import kotlinx.coroutines.flow.Flow

/** Repositório SQL: única porta de acesso da aplicação à tabela de clientes. */
class CustomerRepository(private val dao: CustomerDao) {

    fun observeAll(): Flow<List<Customer>> = dao.observeAll()

    suspend fun existsByEmail(email: String): Boolean = dao.countByEmail(email) > 0

    suspend fun insert(customer: Customer): Long = dao.insert(customer)

    suspend fun delete(customer: Customer) = dao.delete(customer)
}
