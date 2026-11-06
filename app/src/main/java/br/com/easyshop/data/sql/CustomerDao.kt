package br.com.easyshop.data.sql

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/** DAO: consultas SQL sobre a tabela customers. */
@Dao
interface CustomerDao {

    @Query("SELECT * FROM customers ORDER BY full_name COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<Customer>>

    @Query("SELECT COUNT(*) FROM customers WHERE email = :email")
    suspend fun countByEmail(email: String): Int

    /** ABORT: se violar o UNIQUE do e-mail, o SQLite lança SQLiteConstraintException. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(customer: Customer): Long

    @Delete
    suspend fun delete(customer: Customer)
}
