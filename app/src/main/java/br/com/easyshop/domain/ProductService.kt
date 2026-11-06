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

        val price = parsePrice(priceText) ?: throw ValidationException("Preço inválido.")
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

    internal companion object {
        private val THOUSANDS_ONLY = Regex("""^\d{1,3}(\.\d{3})+$""")
        private val PLAIN_NUMBER = Regex("""^\d+(\.\d+)?$""")

        /**
         * Converte o texto digitado em preço, aceitando o formato brasileiro.
         * Exemplos: "3.999,00" → 3999.0 · "R$ 1.500" → 1500.0 · "19,90" → 19.9 · "19.90" → 19.9
         * Retorna null quando o texto não é um número válido.
         */
        fun parsePrice(text: String): Double? {
            // Remove "R$" e qualquer espaço (inclusive o espaço não separável usado na formatação de moeda)
            var s = text.replace("R$", "", ignoreCase = true)
                .replace(Regex("""[\s ]"""), "")

            s = when {
                // Formato brasileiro: ponto = milhar, vírgula = decimal
                s.contains(',') -> s.replace(".", "").replace(',', '.')
                // Só pontos agrupando milhares (ex.: "1.500", "1.234.567") → número inteiro
                THOUSANDS_ONLY.matches(s) -> s.replace(".", "")
                // Caso contrário, aceita ponto como separador decimal (ex.: "19.90")
                else -> s
            }

            // Só dígitos e no máximo um separador decimal (rejeita "NaN", "1e5", "1,2,3"...)
            if (!PLAIN_NUMBER.matches(s)) return null
            return s.toDoubleOrNull()
        }
    }
}
