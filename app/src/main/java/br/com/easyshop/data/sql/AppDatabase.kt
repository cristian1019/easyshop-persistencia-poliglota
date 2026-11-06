package br.com.easyshop.data.sql

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/** Banco relacional local (arquivo SQLite "customers.db" no diretório do app). */
@Database(entities = [Customer::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao

    companion object {
        fun create(context: Context): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "customers.db").build()
    }
}
