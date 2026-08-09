# Rest Generator Shell

Aplicação em linha de comando (CLI) construída com **Spring Shell** e **Spring Boot** para orquestrar requisições HTTP e consultas SQL a partir de arquivos XML. Permite gerar dados dinâmicos, reutilizar valores entre requisições, importar massas de dados de CSV e exportar resultados para CSV.

## Tecnologias

- Java 25
- Spring Boot 4.1.0
- Spring Shell 4.0.3
- Spring Web (RestClient)
- Spring Data JDBC
- Dom4j (leitura de XML)
- OpenCSV
- DataFaker (dados aleatórios)
- Bancos de dados suportados: PostgreSQL, MySQL, SQL Server, Oracle, H2

## Funcionalidades

- Executar requisições HTTP `GET`, `POST`, `PUT`, `DELETE`, `HEAD`, `OPTIONS`, `PATCH`, `TRACE`, `CONNECT`.
- Definir variáveis de ambiente estáticas, aleatórias ou lidas de arquivo.
- Substituir placeholders `{{variavel}}` em URL, headers e corpo da requisição.
- Capturar valores de respostas JSON e reutilizá-los em requisições posteriores.
- Executar consultas `SELECT` em bancos de dados e armazenar colunas como variáveis.
- Importar dados de arquivos CSV para parametrizar requisições.
- Exportar variáveis para um arquivo CSV ao final de cada execução.
- Repetição automática de requisições com limite máximo de 100.000 iterações.
- Retentativa automática em caso de falhas HTTP (até 3 tentativas).

## Pré-requisitos

- JDK 25 instalado.
- Maven 3.9+ ou o wrapper `./mvnw` (`mvnw.cmd` no Windows).

## Build

```bash
# Linux / macOS
./mvnw clean package

# Windows
mvnw.cmd clean package
```

O JAR executável será gerado em `target/rest-generator-shell-2.0.0.jar`.

## Execução

### Modo interativo

```bash
java -jar target/rest-generator-shell-2.0.0.jar
```

Dentro do shell, execute os comandos disponíveis.

### Comandos disponíveis

#### `request start`

Executa um arquivo XML de configuração.

```shell
request start --file requests.xml --repeat 1
```

Opções:

- `-f, --file`: caminho do arquivo XML (padrão: `requests.xml`).
- `-r, --repeat`: quantidade de repetições, de 1 a 100.000 (padrão: `1`).

#### `request start csv`

Executa um arquivo XML utilizando dados de um CSV como variáveis de ambiente.

```shell
request start csv --file requests.xml --csvFile dados.csv --delimiter ","
```

Opções:

- `-f, --file`: caminho do arquivo XML (padrão: `requests.xml`).
- `--csvFile`: caminho do arquivo CSV com os dados.
- `-d, --delimiter`: delimitador do CSV (padrão: `,`).

> Ao usar CSV, cada linha do arquivo gera uma execução completa do XML. O número de repetições é definido pela quantidade de linhas de dados.

## Estrutura do XML

O arquivo XML raiz deve se chamar `restGenerator` e conter três seções principais:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<restGenerator>
    <environments>
        <!-- variáveis de ambiente -->
    </environments>

    <requests>
        <!-- requisições HTTP e/ou consultas SQL -->
    </requests>

    <export export-variables="id,nome" />
</restGenerator>
```

### 1. Variáveis de ambiente (`<environments>`)

Cada variável é declarada com o elemento `environment`.

```xml
<environments>
    <!-- Valor fixo -->
    <environment variable-name="baseUrl">http://localhost:8080</environment>

    <!-- Valor aleatório -->
    <environment variable-name="nome" random-name="FULL_NAME" />
    <environment variable-name="idade" random-name="INT" random-min="18" random-max="99" number="true" />
    <environment variable-name="cpf" random-name="CPF" />

    <!-- Leitura de arquivo -->
    <environment variable-name="payload" read-file="payload.json" />

    <!-- Escolha aleatória entre opções -->
    <environment variable-name="perfil">[admin,usuario,convidado]</environment>
</environments>
```

Atributos aceitos:

| Atributo | Descrição |
|----------|-----------|
| `variable-name` | Nome da variável. |
| `random-name` | Tipo de dado aleatório a ser gerado. |
| `random-min` | Valor mínimo para `INT` e `DOUBLE`. |
| `random-max` | Valor máximo para `INT` e `DOUBLE`. |
| `read-file` | Caminho de arquivo cujo conteúdo será atribuído à variável. |
| `number` | `true` indica que o campo é numérico. |

Geradores aleatórios disponíveis (`random-name`):

- `FULL_NAME`
- `FIRST_NAME`
- `LAST_NAME`
- `CELLPHONE`
- `TELEPHONE`
- `INT`
- `DOUBLE`
- `DATE`
- `PAST_DATE`
- `FUTURE_DATE`
- `BIRTHDAY`
- `CPF`
- `CNPJ`

### 2. Requisições (`<requests>`)

#### Requisição HTTP

```xml
<requests>
    <request request-name="criar-usuario"
             request-url="{{baseUrl}}/api/users"
             request-type="POST"
             response-save="id,email"
             response-save-custom="endereco.cep"
             request-wait="1">
        <body>
            {
                "nome": "{{nome}}",
                "cpf": "{{cpf}}",
                "perfil": "{{perfil}}"
            }
        </body>
        <headers>
            <header header-key="Authorization">Bearer {{token}}</header>
            <header header-key="Content-Type">application/json</header>
        </headers>
    </request>

    <request request-name="buscar-usuario"
             request-url="{{baseUrl}}/api/users/{{id}}"
             request-type="GET"
             response-save="nome" />

    <request request-name="atualizar-usuario"
             request-url="{{baseUrl}}/api/users/{{id}}"
             request-type="PUT">
        <body>{"nome": "{{nome}} Atualizado"}</body>
    </request>

    <request request-name="remover-usuario"
             request-url="{{baseUrl}}/api/users/{{id}}"
             request-type="DELETE" />
</requests>
```

Atributos da requisição:

| Atributo | Descrição |
|----------|-----------|
| `request-name` | Nome identificador da requisição. |
| `request-url` | URL completa, pode conter placeholders. |
| `request-type` | Método HTTP: `GET`, `POST`, `PUT`, `DELETE`. |
| `response-save` | Lista de nomes de campos JSON a serem salvos automaticamente (separados por vírgula). |
| `response-save-custom` | Caminhos JSON específicos, como `usuario.endereco.cep` ou `itens[0].id`. |
| `request-wait` | Tempo de espera em segundos entre retentativas (padrão: `1`). |

#### Consulta SQL

```xml
<requests>
    <sql sgbd="POSTGRES"
         host="localhost"
         port="5432"
         database="meubanco"
         userName="usuario"
         password="senha">
        SELECT id, email FROM cliente WHERE ativo = true LIMIT 1
    </sql>
</requests>
```

Atributos da consulta SQL:

| Atributo | Descrição |
|----------|-----------|
| `sgbd` | Banco de dados: `POSTGRES`, `MYSQL`, `SQL_SERVER`, `ORACLE`, `H2`. |
| `host` | Host do banco. |
| `port` | Porta do banco. |
| `database` | Nome do banco/esquema. |
| `userName` | Usuário. |
| `password` | Senha. |
| `url` | URL JDBC completa (opcional, substitui a montagem automática). |

> **Atenção:** apenas consultas `SELECT` são permitidas. Comandos `UPDATE` e `DELETE` são rejeitados.

### 3. Exportação (`<export>`)

Define quais variáveis serão salvas em um arquivo CSV ao final de cada execução.

```xml
<export export-variables="id,nome,cpf,email" />
```

O arquivo CSV será criado no diretório de execução com nome gerado automaticamente, por exemplo `rest-generator123456.csv`.

## Exemplo completo

Arquivo `requests.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<restGenerator>
    <environments>
        <environment variable-name="baseUrl">http://localhost:8080</environment>
        <environment variable-name="nome" random-name="FULL_NAME" />
        <environment variable-name="cpf" random-name="CPF" />
    </environments>

    <requests>
        <request request-name="criar-cliente"
                 request-url="{{baseUrl}}/api/clientes"
                 request-type="POST"
                 response-save="id">
            <body>
                {
                    "nome": "{{nome}}",
                    "cpf": "{{cpf}}"
                }
            </body>
        </request>

        <request request-name="consultar-cliente"
                 request-url="{{baseUrl}}/api/clientes/{{id}}"
                 request-type="GET" />
    </requests>

    <export export-variables="id,nome,cpf" />
</restGenerator>
```

Execução:

```shell
request start --file requests.xml --repeat 10
```

## Exemplo com CSV

Arquivo `clientes.csv`:

```csv
nome,cpf
João Silva,12345678901
Maria Souza,98765432109
```

Arquivo `requests.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<restGenerator>
    <requests>
        <request request-name="criar-cliente"
                 request-url="http://localhost:8080/api/clientes"
                 request-type="POST"
                 response-save="id">
            <body>
                {
                    "nome": "{{nome}}",
                    "cpf": "{{cpf}}"
                }
            </body>
        </request>
    </requests>

    <export export-variables="id,nome,cpf" />
</restGenerator>
```

Execução:

```shell
request start csv --file requests.xml --csvFile clientes.csv --delimiter ","
```

As colunas `nome` e `cpf` do CSV serão injetadas automaticamente como variáveis de ambiente.

## Tratamento de erros

- Requisições com falha recebem até 3 tentativas antes de interromper a execução.
- Erros de XML, JSON, CSV, SQL ou resposta HTTP são exibidos no console.
- O número de repetições é limitado a 100.000 para evitar execuções acidentais em massa.

## Testes

```bash
# Linux / macOS
./mvnw test

# Windows
mvnw.cmd test
```

Os testes cobrem execução de requisições HTTP, caminhamento de respostas JSON e execução de consultas SQL com banco H2 em memória.

## Licença

Este projeto é de uso interno e não possui uma licença pública definida.
