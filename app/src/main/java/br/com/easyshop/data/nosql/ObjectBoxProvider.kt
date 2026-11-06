package br.com.easyshop.data.nosql

import android.content.Context
import io.objectbox.BoxStore

/** Abre o banco NoSQL local (diretório "products-db" dentro dos arquivos do app). */
object ObjectBoxProvider {
    fun create(context: Context): BoxStore =
        MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .name("products-db")
            .build()
}
