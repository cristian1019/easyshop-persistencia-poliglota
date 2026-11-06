package br.com.easyshop.data.nosql

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

/**
 * Documento de produto armazenado no ObjectBox (NoSQL local).
 *
 * O campo [attributes] é uma "flex property": um mapa chave/valor serializado
 * em FlexBuffers. Cada produto pode ter atributos totalmente diferentes
 * (ex.: camisa → tamanho/cor; notebook → processador/RAM/voltagem) sem
 * nenhuma alteração de schema, coluna nula ou tabela EAV.
 */
@Entity
data class Product(
    @Id var id: Long = 0,
    var name: String = "",
    var price: Double = 0.0,
    var category: String? = null,
    var attributes: MutableMap<String, Any>? = null,
    var createdAt: Long = System.currentTimeMillis()
)
