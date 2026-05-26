# CLAUDE.md — FixFlow Backend (Kotlin + Spring Boot)

## 📌 Contexto do Projeto
FixFlow é uma plataforma backend orientada a eventos para gestão de chamados técnicos residenciais e comerciais, utilizando **Arquitetura Hexagonal**, **Kotlin 17+**, **Spring Boot 3.x** e **Gradle (Kotlin DSL)**.

---

## 🏗️ Restrições Arquiteturais (Hexagonal Estrita)
O código deve ser estritamente dividido em `core` (puro, isolado, sem frameworks) e `infrastructure` (Spring, JPA, etc).

### 📁 Estrutura de Pastas Obrigatória
```text
src/main/kotlin/com/periclao/fixflow/
 ├── core/                  # Domínio Puro (Sem anotações Spring, JPA, Jackson ou validações Jakarta)
 │    ├── model/            # Data classes de domínio, Enums, Regras de negócio puras
 │    ├── usecase/          # Serviços/Casos de uso (Interfaces e Implementações)
 │    ├── repository/       # Ports de Saída (Interfaces de persistência)
 │    ├── event/            # Ports de Saída (Interfaces de mensageria/eventos)
 │    └── exception/        # Exceções de negócio puras
 ├── infrastructure/        # Adapters de SAÍDA (Spring Data JPA, RabbitMQ, etc.)
 │    ├── entity/           # Entidades JPA (@Entity, @Table)
 │    ├── repository/       # Spring Data JPA + Implementações dos Ports
 │    ├── mapper/           # Mappers explícitos (Core Model <-> Entity)
 │    ├── config/           # Beans do Spring (@Configuration, instanciação manual de UseCases)
 │    └── integration/      # Adapters de Saída (RabbitMQ/Kafka, Webhooks, SQS)
 └── api/                   # Adapters de ENTRADA (HTTP — REST Controllers)
      ├── cliente/
      │    ├── request/     # DTOs de entrada com validações Jakarta
      │    ├── response/    # DTOs de saída
      │    └── ClienteController.kt
      └── endereco/
           ├── request/
           ├── response/
           └── EnderecoController.kt
```

---

## 🧼 Clean Code & Melhores Práticas (Kotlin + Spring)
- **Nomes Significativos:** Funções devem ser verbos descritivos. Classes devem ser substantivos. Evite abreviações.
- **Funções Pequenas:** Cada função deve fazer apenas uma coisa (SRP) e ter poucas linhas.
- **Imutabilidade por Padrão:** Use `val` sempre. Use `var` apenas se estritamente necessário.
- **Null Safety Nativo:** Use tipos anuláveis do Kotlin (`?`) e operadores como `?:` (Elvis). Nunca use `Optional<T>`.
- **Injeção de Dependência:** Via construtor clássico do Kotlin. É expressamente proibido o uso de `@Autowired`.
- **Tratamento de Erros Limpo:** O `core` lança exceções de negócio específicas. A `infrastructure` captura globalmente via `@RestControllerAdvice`.
- **Validações na Borda:** Valide payloads HTTP com anotações do Jakarta Validation nos DTOs da camada `infrastructure`, nunca nos modelos do `core`.
- **Instanciação do Core:** Registre os UseCases manualmente na camada `infrastructure.config.UseCaseConfig` usando `@Bean`.

---

## ⚖️ Regras de Negócio Estritas (Invariantes do Domínio)

### 👥 Clientes e Endereços
- **Exclusão Lógica:** Clientes nunca são deletados fisicamente do banco de dados. Deve existir uma flag `ativo: Boolean` ou `deletadoEm: LocalDateTime?`. Clientes inativos não podem abrir novos chamados.
- **Vínculo de Endereço:** Um endereço pertence obrigatoriamente a um único cliente. Um cliente pode ter múltiplos endereços.

### 📝 Ciclo de Vida e Transições do Chamado
O chamado segue uma máquina de estados rígida baseada no enum `StatusChamado`:
- **ABERTO:** Estado inicial quando criado e categorizado.
- **EM_ANALISE:** Quando o suporte está avaliando o problema ou buscando técnicos elegíveis.
- **TECNICO_ATRIBUIDO:** Quando um técnico disponível aceita ou recebe o chamado.
- **EM_EXECUCAO:** Quando o técnico inicia o atendimento no endereço.
- **CONCLUIDO:** Estado final de sucesso. Exige uma descrição técnica do encerramento.
- **CANCELADO:** Estado final de interrupção. Pode ocorrer a partir de `ABERTO`, `EM_ANALISE` ou `TECNICO_ATRIBUIDO`. Não pode ser cancelado se estiver `EM_EXECUCAO` ou `CONCLUIDO`.

### 🗂️ Categorização Automática
- Na abertura, o sistema analisa a descrição do chamado usando um motor simples de palavras-chave do Core (ex: "vazamento", "cano", "infiltração" -> Categoria: *HIDRAULICA*; "curto", "tomada", "fio" -> Categoria: *ELETRICA*).
- Se nenhuma palavra-chave mapeada for encontrada, o chamado é obrigatoriamente marcado com a flag ou status secundário `PENDENTE_CATEGORIZACAO`.

### 🪝 Mecanismo de Webhooks
- O disparo de notificações HTTP para parceiros deve ser **assíncrono** (para não travar a requisição principal do usuário).
- Em caso de falha de rede (HTTP status diferente de 2xx ou timeout), o sistema deve agendar um **Retry com Backoff Exponencial** (ex: tentar novamente em 5m, 15m, 30m) até um limite de 5 tentativas. Se falhar em definitivo, registra o log de falha na entidade de auditoria.

---

## 🧭 Estratégia de Desenvolvimento por Tasks
Desenvolva em micro-atividades focadas para economizar tokens e evitar alucinações.
- **Abordagem Inside-Out:** Sempre implemente primeiro o `core` e só depois a `infrastructure`.
- **Fluxo da Task:** O Claude deve sugerir o código da task atual, pedir aprovação e, após o feedback do usuário, **atualizar este arquivo mudando `[ ]` para `[x]`** antes de ir para a próxima.

---

## 🤖 Regras de Resposta do Claude (System Prompts)
1. **Foco e Fatiamento:** Nunca tente fazer a feature inteira de uma vez. Olhe o Backlog abaixo, execute a primeira task pendente `[ ]` e peça autorização para prosseguir.
2. **Economia Extrema de Tokens:** Não reescreva arquivos inteiros para pequenas alterações. Mostre apenas o trecho modificado utilizando comentários `// ...` para omitir código inalterado.
3. **Sem Poluição de Framework no Core:** Se você sugerir uma anotação de framework (Spring, JPA, Jackson) dentro do pacote `core`, a resposta estará errada.
4. **Respeito às Regras de Negócio:** Valide sempre se as transições de estado ou exclusões respeitam estritamente a seção `⚖️ Regras de Negócio Estritas`.
5. **Objetividade:** Vá direto ao ponto e ao código. Sem introduções ou conclusões longas.

---

## 📋 Backlog de Desenvolvimento (Checklist de Progresso)

### 🧱 Fase 0: Setup Inicial
- [x] Task 0.1: Configuração do `build.gradle.kts` (Dependências Kotlin, Spring Boot, JPA, Postgres, Testes).
- [x] Task 0.2: Criação do arquivo `Dockerfile`, `docker-compose.yml` (Postgres + Message Broker) e `application.yml`.

### 👥 Fase 1: Cadastro de Clientes e Endereços
- [x] Task 1.1: Core - Modelos (`Cliente`, `Endereco`), Enums e Exceções de negócio (com suporte à exclusão lógica).
- [x] Task 1.2: Core - Ports de Persistência (`ClienteRepositoryPort`, `EnderecoRepositoryPort`).
- [x] Task 1.3: Core - UseCases (Criação, Atualização, Consulta, Exclusão Lógica) + Testes Unitários Puros.
- [x] Task 1.4: Infra - Entidades JPA, Mappers e Spring Data Repositories.
- [x] Task 1.5: Infra - DTOs de Entrada/Saída, Validações Jakarta e REST Controllers.
- [x] Task 1.6: Infra - Configuração de Beans (`UseCaseConfig`) e Tratamento Global de Exceções.

### 🛠️ Fase 2: Gestão de Técnicos
- [ ] Task 2.1: Core - Modelo (`Tecnico`) e Port de Persistência.
- [ ] Task 2.2: Core - UseCases (Cadastro, Consulta, Listagem de Chamados do Técnico) + Testes Unitários.
- [ ] Task 2.3: Infra - Mapeamento JPA, Repositories, DTOs e REST Controller.

### 📝 Fase 3: Gestão de Chamados (Core Business)
- [ ] Task 3.1: Core - Modelo (`Chamado`), Enum de Status com validação de transição interna e Ports de Persistência.
- [ ] Task 3.2: Core - Mecanismo de Categorização Automática por palavras-chave e tratamento de `PENDENTE_CATEGORIZACAO`.
- [ ] Task 3.3: Core - UseCases (Abertura, Atribuição de Técnico, Alteração de Status, Cancelamento, Conclusão) + Testes Unitários de transição de estado.
- [ ] Task 3.4: Infra - Modelagem de Banco, Mappers, DTOs e REST Controllers para o fluxo de Chamados.

### 📣 Fase 4: Eventos e Mensageria (Arquitetura Orientada a Eventos)
- [ ] Task 4.1: Core - Modelos de Eventos de Domínio e Ports de Saída de Mensageria (`EventPublisherPort`).
- [ ] Task 4.2: Core - Atualizar UseCases de Chamados para disparar eventos em mudanças de estado.
- [ ] Task 4.3: Infra - Configuração do Broker (RabbitMQ/Kafka) e Implementação do Port de Publicação.

### 🗝 Fase 5: Sistema de Webhooks
- [ ] Task 5.1: Core - Modelo (`WebhookSubscription`) e UseCase de gerenciamento de inscrições de parceiros.
- [ ] Task 5.2: Infra - Motor de disparo HTTP assíncrono com mecanismo de Retry Exponencial e Log de falhas.

### 🔍 Fase 6: Observabilidade e Diferenciais (Polimento)
- [ ] Task 6.1: Configuração de Logs Estruturados e Rastreamento de Requisições (MDC/Trace ID).
- [ ] Task 6.2: Implementação de Idempotência no consumo de eventos e Rate Limiting nas APIs REST.

---

## ⚡ Comandos Úteis
- Compilar o projeto: `./gradlew compileKotlin`
- Executar testes: `./gradlew test`
- Rodar a aplicação localmente: `./gradlew bootRun`
