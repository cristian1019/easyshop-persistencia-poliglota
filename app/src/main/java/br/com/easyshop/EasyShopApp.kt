package br.com.easyshop

import android.app.Application
import br.com.easyshop.data.nosql.ObjectBoxProvider
import br.com.easyshop.data.nosql.ProductRepository
import br.com.easyshop.data.sql.AppDatabase
import br.com.easyshop.data.sql.CustomerRepository
import br.com.easyshop.domain.CustomerService
import br.com.easyshop.domain.ProductService

/**
 * Ponto de composição da aplicação (injeção de dependências manual).
 * Aqui os DOIS bancos são abertos lado a lado no mesmo app:
 *   - SQLite (Room)  → clientes
 *   - ObjectBox      → produtos
 */
class EasyShopApp : Application() {

    lateinit var productService: ProductService
        private set
    lateinit var customerService: CustomerService
        private set

    override fun onCreate() {
        super.onCreate()

        // Persistência NoSQL
        val boxStore = ObjectBoxProvider.create(this)
        productService = ProductService(ProductRepository(boxStore))

        // Persistência SQL
        val sqlDatabase = AppDatabase.create(this)
        customerService = CustomerService(CustomerRepository(sqlDatabase.customerDao()))
    }
}
