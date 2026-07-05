package com.example.fluxodecaixa.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val description: String,
    val amount: Double,
    val type: String, // "ENTRADA" ou "SAÍDA"
    val date: String  // 📅 Campo de data adicionado para o checklist!
)