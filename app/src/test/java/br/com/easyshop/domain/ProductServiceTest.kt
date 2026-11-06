package br.com.easyshop.domain

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class ProductServiceTest {

    private lateinit var repository: FakeProductRepository
    private lateinit var service: ProductService

    @Before
    fun setUp() {
        repository = FakeProductRepository()
        service = ProductService(repository)
    }

    private fun add(price: String, name: String = "Notebook") = runBlocking {
        service.addProduct(name, price, category = "", attributes = emptyList())
    }

    private fun assertValidationError(expectedMessage: String, block: () -> Unit) {
        try {
            block()
            fail("Era esperada ValidationException: $expectedMessage")
        } catch (e: ValidationException) {
            assertEquals(expectedMessage, e.message)
        }
    }

    // ---------- parsing do preço ----------

    @Test
    fun `preco no formato brasileiro com milhar e centavos`() {
        assertEquals(3999.0, add("3.999,00").price, 0.0001)
    }

    @Test
    fun `preco so com ponto de milhar vira numero inteiro`() {
        assertEquals(1500.0, add("1.500").price, 0.0001)
        assertEquals(1_234_567.0, ProductService.parsePrice("1.234.567")!!, 0.0001)
    }

    @Test
    fun `preco com virgula decimal`() {
        assertEquals(19.9, add("19,90").price, 0.0001)
    }

    @Test
    fun `preco com ponto decimal continua aceito`() {
        assertEquals(19.9, add("19.90").price, 0.0001)
        assertEquals(1.5, ProductService.parsePrice("1.5")!!, 0.0001)
    }

    @Test
    fun `preco com R$ e espacos`() {
        assertEquals(1500.0, add("R$ 1.500,00").price, 0.0001)
        assertEquals(2.5, add("  R$2,50  ").price, 0.0001)
        assertEquals(10.0, ProductService.parsePrice("R$ 10,00")!!, 0.0001) // espaço não separável
    }

    @Test
    fun `textos que nao sao preco retornam null`() {
        for (text in listOf("", "abc", "R$", "1,2,3", "NaN", "Infinity", "1e5", "-5", "12,3x")) {
            assertNull("\"$text\" deveria ser inválido", ProductService.parsePrice(text))
        }
    }

    @Test
    fun `preco invalido gera erro de validacao`() {
        assertValidationError("Preço inválido.") { add("abc") }
    }

    @Test
    fun `preco zero gera erro de validacao`() {
        assertValidationError("O preço deve ser maior que zero.") { add("0,00") }
    }

    // ---------- outras regras ----------

    @Test
    fun `nome vazio gera erro de validacao`() {
        assertValidationError("Informe o nome do produto.") { add("10,00", name = "   ") }
    }

    @Test
    fun `produto valido e salvo com atributos dinamicos`() = runBlocking {
        val saved = service.addProduct(
            name = " Camiseta ",
            priceText = "49,90",
            category = "Roupas",
            attributes = listOf("cor" to "azul", "" to "", "tamanho" to "M")
        )
        assertEquals("Camiseta", saved.name)
        assertEquals(mapOf("cor" to "azul", "tamanho" to "M"), saved.attributes)
        assertTrue(saved.id > 0)
        assertEquals(1, repository.products.value.size)
    }
}
