App de Fluxo de Caixa 📱📊

Desafio técnico de desenvolvimento Android nativo para controle financeiro pessoal, permitindo
o registro de transações (entradas e saídas) e monitoramento do saldo consolidado em tempo real.

---

1)Arquitetura e Tecnologias Utilizadas

Linguagem: Kotlin
Interface Gráfica: Jetpack Compose (UI Declarativa)
Arquitetura: MVVM (Model View ViewModel) para separação de responsabilidades.
Persistência de Dados: Room Database
Gerenciamento de Fluxos: Kotlin Coroutines & Flow para operações assíncronas e reativas com o banco de dados.

---

Requisitos Implementados (Checklist do Desafio)

- [x] Desenvolvimento da Tela Principal: Interface limpa para novos lançamentos.
- [x] Desenvolvimento da Tela de Listagem: Tela dedicada para o extrato financeiro.
- [x] Consistência de Campos: Validação do campo de valor numérico e restrição contra campos em branco.
- [x] Persistência de Dados: Uso do Room Database mantendo as transações salvas mesmo após fechar o app.
- [x] Navegabilidade entre Telas: Transição reativa de estados entre a tela de Cadastro e a tela de Extrato.
- [x] Organização do Código: Arquitetura estruturada em camadas seguindo o padrão **MVVM** (`FinanceViewModel`).
- [x] Apresentação de Dados Eficiente: Uso do `LazyColumn` (substituto moderno do RecyclerView) para renderização otimizada da lista.
- [x] [PLUS] Uso de DatePicker: Seleção de datas integrada via `DatePickerDialog` nativo do Android.
- [x] [PLUS] Diferenciação Visual: Indicação de Crédito/Débito com destaque visual em Verde (Entrada) e Vermelho (Saída).
- [x] [PLUS] Apresentação do Saldo: Card dinâmico com o cálculo do saldo consolidado no topo do extrato.

---

📂 Estrutura do Projeto

```text
com.example.fluxodecaixa/
│
├── data/                      # Camada de Dados (Banco Local)
│   ├── AppDatabase.kt         # Configuração e inicialização do Room
│   ├── Transaction.kt         # Entidade / Tabela do banco de dados
│   └── TransactionDao.kt      # Interface de acesso e queries (Insert/Select)
│
├── FinanceViewModel.kt        # Camada de Negócio (MVVM) - Gerencia os estados e fluxos
└── MainActivity.kt            # Camada de Visão (UI) - Telas e componentes em Jetpack Compose
