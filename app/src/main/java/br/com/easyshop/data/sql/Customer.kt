package br.com.easyshop.data.sql

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Tabela relacional "customers" (SQLite via Room).
 * Schema fixo e estrito: colunas NOT NULL, chave primária autoincremento
 * e índice UNIQUE no e-mail (o próprio banco impede e-mails duplicados).
 */
@Entity(
    tableName = "customers",
    indices = [Index(value = ["email"], unique = true)]
)
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
