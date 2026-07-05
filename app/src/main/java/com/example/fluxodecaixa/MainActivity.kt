package com.example.fluxodecaixa

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fluxodecaixa.data.AppDatabase
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(applicationContext)
        val viewModel = FinanceViewModel(db)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainNavigation(viewModel)
                }
            }
        }
    }
}

@Composable
fun MainNavigation(viewModel: FinanceViewModel) {
    // 🔀 Controle de Navegação para separar as telas
    var telaAtual by remember { mutableStateOf("cadastro") }

    if (telaAtual == "cadastro") {
        TelaCadastro(viewModel = viewModel, aoNavegarParaExtrato = { telaAtual = "extrato" })
    } else {
        TelaExtrato(viewModel = viewModel, aoVoltar = { telaAtual = "cadastro" })
    }
}

// 1. TELA DE LANÇAMENTO (CADASTRO)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaCadastro(viewModel: FinanceViewModel, aoNavegarParaExtrato: () -> Unit) {
    var descricao by remember { mutableStateOf("") }
    var valorInput by remember { mutableStateOf("") }
    var tipoSelecionado by remember { mutableStateOf("ENTRADA") }

    // 📅 Lógica do DatePicker
    val contexto = LocalContext.current
    val calendario = Calendar.getInstance()
    var dataSelecionada by remember {
        mutableStateOf("%02d/%02d/%04d".format(calendario.get(Calendar.DAY_OF_MONTH), calendario.get(Calendar.MONTH) + 1, calendario.get(Calendar.YEAR)))
    }

    val datePickerDialog = DatePickerDialog(
        contexto,
        { _: DatePicker, ano: Int, mes: Int, dia: Int ->
            dataSelecionada = "%02d/%02d/%04d".format(dia, mes + 1, ano)
        },
        calendario.get(Calendar.YEAR),
        calendario.get(Calendar.MONTH),
        calendario.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Novo Lançamento", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = descricao,
                onValueChange = { descricao = it },
                label = { Text("Descrição (Ex: Salário, Mercado)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = valorInput,
                onValueChange = { valorInput = it },
                label = { Text("Valor (R$)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Botão do DatePicker
            OutlinedButton(
                onClick = { datePickerDialog.show() },
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = "Calendário")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Data Selecionada: $dataSelecionada", fontSize = 16.sp)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = tipoSelecionado == "ENTRADA", onClick = { tipoSelecionado = "ENTRADA" })
                    Text("Receita (Crédito)", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = tipoSelecionado == "SAÍDA", onClick = { tipoSelecionado = "SAÍDA" })
                    Text("Despesa (Débito)", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }

            Button(
                onClick = {
                    val valor = valorInput.toDoubleOrNull()
                    if (descricao.isNotBlank() && valor != null) {
                        viewModel.adicionarTransacao(descricao, valor, tipoSelecionado, dataSelecionada)
                        descricao = ""
                        valorInput = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Lançamento")
            }

            Spacer(modifier = Modifier.height(20.dp))

            OutlinedButton(
                onClick = aoNavegarParaExtrato,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Ver Extrato Completo ➡️")
            }
        }
    }
}


// 2. TELA DE EXTRATO (LISTAGEM)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaExtrato(viewModel: FinanceViewModel, aoVoltar: () -> Unit) {
    val transacoes by viewModel.transacoes.collectAsState(initial = emptyList())

    val totalEntradas = transacoes.filter { it.type == "ENTRADA" }.sumOf { it.amount }
    val totalSaidas = transacoes.filter { it.type == "SAÍDA" }.sumOf { it.amount }
    val saldoTotal = totalEntradas - totalSaidas

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Extrato Financeiro", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Saldo Consolidado", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        text = "R$ %.2f".format(saldoTotal),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (saldoTotal >= 0) Color(0xFF2E7D32) else Color.Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Histórico de Lançamentos", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(transacoes) { transacao ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(transacao.description, fontWeight = FontWeight.Medium, fontSize = 16.sp)
                                Text(transacao.date, fontSize = 12.sp, color = Color.Gray) // Exibe a data salva
                            }
                            Text(
                                text = "${if (transacao.type == "ENTRADA") "+" else "-"} R$ %.2f".format(transacao.amount),
                                fontWeight = FontWeight.Bold,
                                color = if (transacao.type == "ENTRADA") Color(0xFF2E7D32) else Color.Red
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = aoVoltar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("⬅️ Voltar para Cadastro")
            }
        }
    }
}