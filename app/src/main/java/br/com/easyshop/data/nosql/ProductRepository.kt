package br.com.easyshop.data.nosql

import android.util.Log
import io.objectbox.BoxStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Repositório NoSQL: única porta de acesso da aplicação à coleção de produtos.
 * É uma interface para que a camada de negócio possa ser testada com um repositório falso.
 */
interface ProductRepository {
    fun observeAll(): Flow<List<Product>>
    suspend fun insert(product: Product): Long
    suspend fun delete(id: Long): Boolean
}

/** Implementação real, sobre o ObjectBox. */
class ObjectBoxProductRepository(store: BoxStore) : ProductRepository {

    private val box = store.boxFor(Product::class.java)

    /**
     * Consulta reativa: emite a lista atual e, depois, a cada alteração na coleção.
     * A leitura inicial é feita explicitamente com find(); a assinatura do ObjectBox
     * fica responsável apenas pelas mudanças posteriores.
     */
    override fun observeAll(): Flow<List<Product>> = callbackFlow {
        val query = box.query().order(Product_.name).build()
        val subscription = query.subscribe()
            .onlyChanges()
            .onError { error ->
                Log.e(TAG, "Erro na consulta NoSQL", error)
                close(error)
            }
            .observer { products -> trySend(products) }

        send(query.find())

        awaitClose {
            subscription.cancel()
            query.close()
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun insert(product: Product): Long = withContext(Dispatchers.IO) { box.put(product) }

    override suspend fun delete(id: Long): Boolean = withContext(Dispatchers.IO) { box.remove(id) }

    private companion object {
        const val TAG = "EasyShop"
    }
}
