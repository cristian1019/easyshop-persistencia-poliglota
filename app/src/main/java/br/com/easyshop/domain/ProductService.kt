package br.com.easyshop.domain

import br.com.easyshop.data.nosql.Product
import br.com.easyshop.data.nosql.ProductRepository
import kotlinx.coroutines.flow.Flow

/**
 * Regras de negócio do catálogo (dados dinâmicos → NoSQL).
 * Nome e preço são obrigatórios; categoria e atributos são livres.
 */
class ProductService(private val repository: ProductRepository) {

    fun observeProducts(): Flow<List<Product>> = repository.observeAll()

    suspend fun addProduct(
        name: String,
        priceText: String,
        category: String,
        attributes: List<Pair<String, String>>
    ): Product {
        val cleanName = name.trim()
        if (cleanName.isEmpty()) throw ValidationException("Informe o nome do produto.")

        val price = priceText.trim().replace(",", ".").toDoubleOrNull()
            ?: throw ValidationException("Preço inválido.")
        if (price <= 0) throw ValidationException("O preço deve ser maior que zero.")

        // Atributos dinâmicos: ignora linhas vazias; chave repetida → vale a última
        val dynamicAttributes = linkedMapOf<String, Any>()
        for ((key, value) in attributes) {
            val k = key.trim()
            val v = value.trim()
            if (k.isEmpty() && v.isEmpty()) continue
            if (k.isEmpty()) throw ValidationException("Atributo sem nome para o valor \"$v\".")
            dynamicAttributes[k] = v
        }

        val product = Product(
            name = cleanName,
            price = price,
            category = category.trim().ifEmpty { null },
            attributes = dynamicAttributes.ifEmpty { null }
        )
        product.id = repository.insert(product)
        return product
    }

    suspend fun remove(product: Product) = repository.delete(product.id)
}
