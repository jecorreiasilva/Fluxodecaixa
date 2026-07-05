package com.example.fluxodecaixa

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fluxodecaixa.data.AppDatabase
import com.example.fluxodecaixa.data.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FinanceViewModel(private val database: AppDatabase) : ViewModel() {

    val transacoes: Flow<List<Transaction>> = database.transactionDao().getAllTransactions()

    fun adicionarTransacao(descricao: String, valor: Double, tipo: String, data: String) {
        viewModelScope.launch {
            database.transactionDao().insert(
                Transaction(description = descricao, amount = valor, type = tipo, date = data)
            )
        }
    }
}