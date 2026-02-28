# 🧪 API Automation — DummyJSON

[![API Tests](https://github.com/seu-usuario/api-automation-dummyjson/actions/workflows/api-tests.yml/badge.svg)](https://github.com/seu-usuario/api-automation-dummyjson/actions/workflows/api-tests.yml)

Projeto de testes automatizados de API REST utilizando **RestAssured + Cucumber BDD + Extent Report**, com pipeline CI/CD via GitHub Actions. A API testada é a [DummyJSON](https://dummyjson.com) — uma API pública com autenticação JWT.

---

## Arquitetura

```
api-automation-dummyjson/
├── .github/
│   └── workflows/
│       └── api-tests.yml            # Pipeline CI/CD GitHub Actions
├── src/
│   └── test/
│       ├── java/
│       │   ├── hooks/
│       │   │   └── Hooks.java       # Before/After Cucumber: ciclo de vida
│       │   ├── models/
│       │   │   ├── AuthPayload.java    # POJO para payloads de Auth
│       │   │   └── ProductPayload.java # POJO para payloads de Product
│       │   ├── runner/
│       │   │   └── TestRunner.java  # Ponto de entrada JUnit4 + Cucumber
│       │   ├── services/
│       │   │   ├── AuthService.java    # Camada de requisições Auth
│       │   │   └── ProductService.java # Camada de requisições Product
│       │   ├── steps/
│       │   │   ├── AuthSteps.java   # Steps de autenticação
│       │   │   └── ProductsSteps.java # Steps de produtos
│       │   └── utils/
│       │       ├── ConfigurationManager.java # Gerenciador de propriedades (ambientes)
│       │       ├── CurlLogger.java       # Filtro RestAssured → gera cURL
│       │       ├── ExtentReportManager.java # Relatório HTML
│       │       ├── RequestBuilder.java   # Construção/envio de requisições
│       │       └── ScenarioContext.java  # Estado compartilhado entre steps
│       └── resources/
│           ├── features/
│           │   ├── auth.feature     # Cenários de autenticação
│           │   └── products.feature # Cenários de CRUD de produtos
│           ├── schemas/
│           │   ├── auth-login-schema.json  # Schema validation JSON
│           │   └── ... 
│           ├── config-hom.properties # Config de homologação
│           ├── config-prod.properties # Config de produção
│           ├── extent.properties    # Config do Extent Report
│           └── extent-config.xml    # Config XML do Extent Report
├── pom.xml
└── README.md
```

---

## Dependências

| Biblioteca                           | Versão  |
|--------------------------------------|---------|
| Java                                 | 17+     |
| RestAssured                          | 5.4.0   |
| Cucumber Java                        | 7.15.0  |
| Cucumber JUnit                       | 7.15.0  |
| Cucumber PicoContainer               | 7.15.0  |
| JUnit                                | 4.13.2  |
| ExtentReports                        | 5.1.1   |
| ExtentReports Cucumber7 Adapter      | 1.14.0  |
| Jackson Databind                     | 2.16.1  |
| Lombok                               | 1.18.30 |
| SLF4J Simple                         | 2.0.11  |

---

## Pré-requisitos

- Java 17 ou superior
- Maven 3.8+
- Git

Verifique as versões instaladas:
```bash
java -version
mvn -version
```

---

## Como executar

### 1. Clone o repositório
```bash
git clone https://github.com/seu-usuario/api-automation-dummyjson.git
cd api-automation-dummyjson
```

### 2. Configure o ambiente de execução

Os ambientes são gerenciados por arquivos de propriedades em `src/test/resources/`. O padrão é `hom` (homologação), mas você pode definir o ambiente passando o parâmetro `-Denv`:

* `config-hom.properties`: Ambiente de homologação
* `config-prod.properties`: Ambiente de produção

### 3. Execute todos os testes
```bash
mvn test -Denv=hom
```

### 4. Execute por tags
```bash
# Somente smoke tests
mvn test -Dcucumber.filter.tags="@smoke"

# Somente testes negativos
mvn test -Dcucumber.filter.tags="@negativo"

# Somente testes de autenticação
mvn test -Dcucumber.filter.tags="@auth"

# Somente testes de produtos
mvn test -Dcucumber.filter.tags="@products"

# Combinação de tags
mvn test -Dcucumber.filter.tags="@smoke and @products"
mvn test -Dcucumber.filter.tags="@negativo and not @auth"

# Todos os testes
mvn test
```

---

## Relatório

Após execução, o relatório HTML é gerado em:
```
target/extent-reports/report.html
```

Abra no navegador:
```bash
# Linux/macOS
open target/extent-reports/report.html

# Windows
start target/extent-reports/report.html
```

O relatório contém:
- ✅ Sumário executivo (total / passou / falhou)
- 🔗 cURL completo de cada requisição
- 📥 Resposta da API (status code + body formatado)
- 🏷️ Tags e categorias de cada cenário
- 🔎 Logs detalhados de falhas

---

## Tipos de Validação

Este framework está preparado para avaliar a qualidade e a conformidade das respostas da API de ponta a ponta:

1. **Validação de Status HTTP:** Confirma se o status code de resposta bate com o esperado (ex: `200 OK`, `201 Created`, `401 Unauthorized`, `400 Bad Request`, `404 Not Found`).
2. **Validação de Headers (Cabeçalhos):** Analisa parâmetros da resposta no nível de transporte, como `Content-Type: application/json`.
3. **Validação de Campos e Conteúdo (Body/Payload):** Assertions baseadas em JsonPath que verificam a existência de campos mandatórios, tipos de dados (maior/menor, igualdade absoluta) e formatação.
4. **Validação de Contrato (JSON Schema):** Garante que o contrato geral da resposta (estruturas, tipagens de nós e propriedades esperadas) está 100% de acordo com as especificações da API.

---

## CI/CD — GitHub Actions

O pipeline é disparado automaticamente em **push** e **pull_request** para as branches `main` e `develop`.

### Configurar segredos no GitHub
Vá em **Settings → Secrets and variables → Actions** e adicione:
- `API_USERNAME` → `emilys`
- `API_PASSWORD` → `emilyspass`

### Artefatos publicados
Após cada execução, os artefatos ficam disponíveis em **Actions → Workflow run → Artifacts**:
- `extent-report-{run_number}` — Relatório HTML completo
- `cucumber-report-{run_number}` — JSON/XML do Cucumber

---

## Tags disponíveis

| Tag          | Descrição                              |
|--------------|----------------------------------------|
| `@smoke`     | Testes críticos e rápidos              |
| `@positivo`  | Cenários com dados válidos             |
| `@negativo`  | Cenários de erro esperados             |
| `@auth`      | Testes de autenticação                 |
| `@products`  | Testes de CRUD de produtos             |

---

## Endpoints testados

| Método | Endpoint           | Descrição                     |
|--------|--------------------|-------------------------------|
| POST   | /auth/login        | Login e obtenção do JWT        |
| GET    | /auth/me           | Perfil do usuário autenticado  |
| GET    | /products          | Listar todos os produtos       |
| GET    | /products/{id}     | Buscar produto por ID          |
| POST   | /products/add      | Criar novo produto             |
| PATCH  | /products/{id}     | Atualizar produto parcialmente |
| DELETE | /products/{id}     | Remover produto                |

---

## Contribuindo

1. Crie uma branch: `git checkout -b feat/novo-cenario`
2. Adicione seus testes em `src/test/resources/features/`
3. Implemente os steps em `src/test/java/steps/`
4. Execute `mvn test` para validar
5. Abra um Pull Request para `develop`

---

> **Projeto gerado por:** Automação QA — RestAssured + Cucumber + Extent Report
