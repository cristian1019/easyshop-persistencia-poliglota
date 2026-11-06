# EasyShop · Persistência Poliglota (SQL + NoSQL)

Protótipo de mini e-commerce Android que usa **dois bancos de dados locais ao mesmo tempo**, cada um no papel em que funciona melhor:

| Dado | Banco | Paradigma | Por quê |
|---|---|---|---|
| **Catálogo de produtos** | [ObjectBox](https://objectbox.io/) | NoSQL (objetos/documentos) | Cada produto pode ter atributos diferentes (cor, tamanho, voltagem, RAM...) sem alterar schema |
| **Cadastro de clientes** | SQLite via [Room](https://developer.android.com/training/data-storage/room) | SQL relacional | Schema fixo, chave primária, `NOT NULL` e índice `UNIQUE` no e-mail garantem consistência |

## 👥 Integrantes

- NOME COMPLETO DO INTEGRANTE 1
- NOME COMPLETO DO INTEGRANTE 2
- NOME COMPLETO DO INTEGRANTE 3 *(se for trio)*

## 🎥 Vídeo de demonstração

LINK DO VÍDEO (YouTube ou Google Drive)

---

## Funcionalidades

**Aba Produtos (NoSQL / ObjectBox)**
- Formulário com Nome, Preço, Categoria (opcional) e **atributos dinâmicos** (pares chave/valor: quantos quiser, ou nenhum).
- Listagem reativa dos documentos do banco NoSQL, com os atributos de cada produto.
- Exclusão de produtos.

**Aba Clientes (SQL / SQLite)**
- Formulário com Nome Completo e E-mail.
- Validação estrita: nome e sobrenome obrigatórios, formato de e-mail e e-mail único (verificado pelo serviço e garantido também pelo índice `UNIQUE` do SQLite).
- Listagem reativa da tabela `customers`.
- Exclusão de clientes.

## Arquitetura em camadas

```
┌──────────────────────────────────────────────────────────────┐
│  APRESENTAÇÃO (ui/)                                          │
│  MainActivity (abas) · ProductsScreen · CustomersScreen      │
│  ProductViewModel · CustomerViewModel                        │
│  → só exibe dados e captura interações; não conhece bancos   │
└──────────────────────────────┬───────────────────────────────┘
                               │
┌──────────────────────────────▼───────────────────────────────┐
│  NEGÓCIO / SERVIÇOS (domain/)                                │
│  ProductService · CustomerService · ValidationException      │
│  → regras de negócio e validações                            │
└───────────────┬──────────────────────────────┬───────────────┘
                │                              │
┌───────────────▼──────────────┐ ┌─────────────▼────────────────┐
│  ACESSO A DADOS NoSQL        │ │  ACESSO A DADOS SQL          │
│  (data/nosql/)               │ │  (data/sql/)                 │
│  Product (@Entity ObjectBox) │ │  Customer (@Entity Room)     │
│  ProductRepository           │ │  CustomerDao · AppDatabase   │
│  ObjectBoxProvider           │ │  CustomerRepository          │
└───────────────┬──────────────┘ └─────────────┬────────────────┘
                ▼                              ▼
      files/objectbox/products-db        databases/customers.db
```

A composição das dependências (abrir os dois bancos e instanciar repositórios e serviços) fica em [`EasyShopApp.kt`](app/src/main/java/br/com/easyshop/EasyShopApp.kt).

### Estrutura de pastas

```
app/src/main/java/br/com/easyshop/
├── EasyShopApp.kt                 # Inicializa SQL + NoSQL (injeção manual)
├── data/
│   ├── nosql/                     # Camada de dados NoSQL (ObjectBox)
│   │   ├── Product.kt
│   │   ├── ProductRepository.kt
│   │   └── ObjectBoxProvider.kt
│   └── sql/                       # Camada de dados SQL (Room/SQLite)
│       ├── Customer.kt
│       ├── CustomerDao.kt
│       ├── AppDatabase.kt
│       └── CustomerRepository.kt
├── domain/                        # Camada de negócio
│   ├── ProductService.kt
│   ├── CustomerService.kt
│   └── ValidationException.kt
└── ui/                            # Camada de apresentação (Jetpack Compose)
    ├── MainActivity.kt
    ├── UiCommon.kt
    ├── products/  (ProductsScreen, ProductViewModel)
    ├── customers/ (CustomersScreen, CustomerViewModel)
    └── theme/
```

### Como o NoSQL resolve o problema dos atributos dinâmicos

```kotlin
@Entity
data class Product(
    @Id var id: Long = 0,
    var name: String = "",
    var price: Double = 0.0,
    var category: String? = null,
    var attributes: MutableMap<String, Any>? = null   // "flex property"
)
```

O campo `attributes` é armazenado pelo ObjectBox como uma *flex property* (FlexBuffers, formato binário semelhante a JSON/BSON). Uma camiseta pode ser salva com `{cor, tamanho}` e um notebook com `{processador, ram, voltagem, garantia}` **na mesma coleção, sem nenhuma migração**. No modelo relacional antigo, isso exigiria colunas nulas ou tabelas EAV.

### Como o SQL garante a consistência dos clientes

```kotlin
@Entity(tableName = "customers", indices = [Index(value = ["email"], unique = true)])
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "full_name") val fullName: String,   // NOT NULL
    @ColumnInfo(name = "email") val email: String,          // NOT NULL + UNIQUE
    ...
)
```

O schema relacional gerado fica versionado em [`app/schemas/`](app/schemas/).

## Tecnologias

- Kotlin 2.2 · Jetpack Compose (Material 3)
- Room 2.7 (SQLite) com KSP
- ObjectBox 4.3 (NoSQL embarcado)
- Android Gradle Plugin 8.12 · Gradle 9.3.1
- minSdk 24 · targetSdk 36

## ▶️ Como executar

### Pré-requisitos
- **Android Studio** recente (testado com o JDK embutido, JBR 25).
- Android SDK Platform 36 instalado.
- Um emulador Android (ex.: Pixel 7) ou um dispositivo físico com depuração USB.

### Pelo Android Studio
1. Clone o repositório:
   ```bash
   git clone https://github.com/cristian1019/easyshop-persistencia-poliglota.git
   ```
2. No Android Studio: **File → Open** e selecione a pasta do projeto.
3. Aguarde o *Gradle Sync* (as dependências são baixadas automaticamente).
4. Inicie um emulador em **Device Manager** e clique em **Run ▶** (configuração `app`).

### Pela linha de comando
```bash
# Windows
gradlew.bat installDebug
# Linux/macOS
./gradlew installDebug
```
Com um emulador aberto, o comando compila e instala o app *EasyShop*.

> **Dica (Windows):** prefira clonar o projeto em um caminho **sem acentos e fora do OneDrive** (ex.: `C:\dev\easyshop`). O OneDrive pode bloquear arquivos gerados em `build/` durante a compilação. Se isso acontecer, apague a pasta `app/build` e compile de novo. O projeto já inclui `android.overridePathCheck=true` para aceitar caminhos com acentos, como "Área de Trabalho".

## 🧪 Roteiro sugerido para a demonstração

1. **Produtos:** cadastrar uma *Camiseta* (R$ 49,90, categoria Vestuário) com atributos `cor = azul` e `tamanho = M`.
2. Cadastrar um *Notebook* (R$ 3.999,00, categoria Informática) com atributos **diferentes**: `processador = i7`, `ram = 16 GB`, `voltagem = bivolt`.
3. Mostrar que os dois documentos convivem na mesma coleção NoSQL, cada um com sua estrutura.
4. **Clientes:** tentar salvar com e-mail inválido → erro de validação.
5. Cadastrar *Maria Silva / maria@email.com* → aparece na lista com o id (PK).
6. Tentar cadastrar outro cliente com `MARIA@email.com` → bloqueado (e-mail único).
7. Fechar e reabrir o app → produtos e clientes continuam salvos (persistência local nos dois bancos).
