package br.com.easyshop.domain

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class CustomerServiceTest {

    private lateinit var repository: FakeCustomerRepository
    private lateinit var service: CustomerService

    @Before
    fun setUp() {
        repository = FakeCustomerRepository()
        service = CustomerService(repository)
    }

    private fun register(name: String, email: String) = runBlocking { service.register(name, email) }

    private fun assertValidationError(expectedMessage: String, block: () -> Unit) {
        try {
            block()
            fail("Era esperada ValidationException: $expectedMessage")
        } catch (e: ValidationException) {
            assertEquals(expectedMessage, e.message)
        }
    }

    @Test
    fun `cliente valido e salvo com nome e email normalizados`() {
        val saved = register("  Maria   da  Silva ", "  Maria.Silva@Email.COM ")
        assertEquals("Maria da Silva", saved.fullName)
        assertEquals("maria.silva@email.com", saved.email)
        assertTrue(saved.id > 0)
        assertEquals(1, repository.customers.value.size)
    }

    @Test
    fun `nome sem sobrenome gera erro`() {
        assertValidationError("Informe nome e sobrenome.") { register("Maria", "maria@email.com") }
    }

    @Test
    fun `nome vazio gera erro`() {
        assertValidationError("Informe o nome completo.") { register("   ", "maria@email.com") }
    }

    @Test
    fun `email com formato invalido gera erro`() {
        for (email in listOf("maria", "maria@", "maria@email", "@email.com", "maria silva@email.com")) {
            assertValidationError("E-mail inválido.") { register("Maria Silva", email) }
        }
    }

    @Test
    fun `email vazio gera erro`() {
        assertValidationError("Informe o e-mail.") { register("Maria Silva", "  ") }
    }

    @Test
    fun `email duplicado gera erro mesmo com maiusculas diferentes`() {
        register("Maria Silva", "maria@email.com")
        assertValidationError("Já existe um cliente com este e-mail.") {
            register("Maria Souza", "MARIA@email.com")
        }
        assertEquals(1, repository.customers.value.size)
    }
}
