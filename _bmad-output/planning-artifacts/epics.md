---
stepsCompleted: ['backlog aprovado por Andrelino em 2026-09-21, materializado sem executar as etapas interativas nem a validação final da skill']
inputDocuments:
  - _bmad-output/planning-artifacts/prds/prd-agentic-java-lab-2026-09-21/prd.md
  - _bmad-output/planning-artifacts/architecture/architecture-agentic-java-lab-2026-09-21/ARCHITECTURE-SPINE.md
---

# agentic-java-lab - Epic Breakdown

## Overview

Este documento reúne o backlog da V1 do Agentic Java Lab: o epic e as cinco stories que decompõem os requisitos funcionais da API (FR-13 a FR-17) do PRD, sob as decisões do Architecture Spine. O backlog foi aprovado por Andrelino em 2026-09-21.

**Decisões em aberto (não tomadas aqui):**
- Qual das stories será selecionada como experimento medido com Claude Code.
- Quem implementa as demais stories e sob qual regime.

**Pré-requisitos fora deste backlog** (não medidos, sob supervisão humana direta): criação do projeto (versões, `pom.xml`, Docker Compose, pacote raiz e primeira migração) e story do CI comum (FR-4). Os testes de referência da story medida são preparados antes do Marco de congelamento (FR-4, AD-10).

## Requirements Inventory

### Functional Requirements

- **FR-13:** o Cliente da API pode criar uma Conta informando o saldo inicial. Saldo inicial maior ou igual a zero é aceito, menor que zero é rejeitado. Sem depósito nem saque.
- **FR-14:** o Cliente da API pode consultar uma Conta por identificador e obter seu saldo. Conta inexistente é tratada como não encontrada.
- **FR-15:** o Cliente da API pode criar uma Transferência de uma Conta de origem para uma Conta de destino. É rejeitada quando a origem não existe, o destino não existe, o valor não é maior que zero, o saldo da origem é insuficiente ou origem e destino são a mesma Conta. Uma solicitação rejeitada não cria Transferência e não gera identificador consultável.
- **FR-16:** uma Transferência efetivada debita a origem e credita o destino pelo mesmo valor, preservando a soma dos saldos. Uma solicitação rejeitada não altera nenhum saldo. Nenhuma Transferência produz saldo negativo por insuficiência de fundos, inclusive sob operações concorrentes.
- **FR-17:** cada Transferência possui identificador único e status e pode ser consultada por identificador. Transferência inexistente é tratada como não encontrada. O identificador único não implica idempotência.

### NonFunctional Requirements

- **NFR-1:** publicação segura, sem credenciais, tokens, dados pessoais ou caminhos locais desnecessários.
- **NFR-3:** decisões arquiteturais significativas não são tomadas em silêncio por agentes: são justificadas, registradas quando apropriado e aprovadas por um humano.

### Additional Requirements

Decisões do Architecture Spine que vinculam todas as stories:

- **AD-1:** monólito de um módulo Maven, pacote por funcionalidade (`account`, `transfer`), código e API em inglês.
- **AD-2:** `account` é o único dono do saldo. `transfer` usa só o serviço público de `account`. `web` nunca acessa `repository`.
- **AD-3:** Spring Data JPA com PostgreSQL. Esquema só por migração Flyway. `@Transactional` no serviço. Uma Transferência é uma única transação atômica.
- **AD-4:** bloqueio pessimista das duas contas em ordem crescente de id, saldo verificado depois do bloqueio, `CHECK (balance >= 0)` no banco.
- **AD-5:** `BigDecimal` e `NUMERIC(19,2)`, BRL implícita, mais de 2 casas decimais rejeitado sem arredondar.
- **AD-6:** UUID v4 gerado pela aplicação. Status único `COMPLETED`, guardado como texto.
- **AD-7:** contrato HTTP: 201 com `Location` nos `POST`, 200 ou 404 nos `GET`, 400 para entrada inválida, 422 para regra de negócio, corpo Problem Details com código estável.
- **AD-8:** testes com JUnit 5 e PostgreSQL real via Testcontainers. Testes de referência separados dos do agente.

### UX Design Requirements

Não se aplica. A V1 não tem interface de usuário.

### FR Coverage Map

| FR | Stories |
| --- | --- |
| FR-13 | 1.1 |
| FR-14 | 1.1 |
| FR-15 | 1.2 (criação), 1.3 (rejeições) |
| FR-16 | 1.2 (débito e crédito), 1.3 (rejeição não altera saldos), 1.4 (concorrência) |
| FR-17 | 1.2 (id e status na criação), 1.5 (consulta) |

## Epic List

**Epic 1: API de contas e transferências (V1).** Um Cliente da API cria contas, transfere valor entre elas com regras de negócio e integridade de saldo, e consulta contas e transferências.

Ordem e dependências: 1.1 → 1.2 → 1.3, 1.4 e 1.5 (cada uma depende só de stories anteriores).

## Epic 1: API de contas e transferências (V1)

Entregar a API da V1 como ambiente controlado do experimento: contas com saldo, transferências atômicas e seguras sob concorrência, e consultas. Cobre FR-13 a FR-17.

### Story 1.1: Criar conta com saldo inicial e consultá-la

**Depende de:** nenhuma story anterior (o projeto e o CI são pré-requisitos fora do backlog).

Como Cliente da API,
quero criar uma Conta com saldo inicial e consultá-la,
para ter contas com saldo observável pela própria API.

**Acceptance Criteria:**

**Dado** que informo um saldo inicial maior ou igual a zero
**Quando** faço `POST /accounts`
**Então** recebo 201 com `Location` e corpo `{id, balance}`
**E** o `id` é um UUID gerado pela aplicação
**E** a Conta foi persistida por um esquema criado só por migração Flyway.

**Dado** uma Conta criada
**Quando** faço `GET /accounts/{id}`
**Então** recebo 200 com o saldo informado na criação.

**Dado** que informo um saldo inicial menor que zero
**Quando** faço `POST /accounts`
**Então** recebo 400 no formato Problem Details com código estável
**E** nenhuma Conta é criada.

**Dado** que informo um saldo inicial com mais de 2 casas decimais
**Quando** faço `POST /accounts`
**Então** o valor é rejeitado com 400, sem arredondamento.

**Dado** um identificador de Conta inexistente
**Quando** faço `GET /accounts/{id}`
**Então** recebo 404.

### Story 1.2: Transferir valor entre duas contas existentes

**Depende de:** Story 1.1.

Como Cliente da API,
quero transferir valor de uma Conta para outra,
para movimentar saldo entre contas de forma atômica.

**Acceptance Criteria:**

**Dado** duas Contas existentes, A com saldo 1000.00 e B com saldo 500.00
**Quando** faço `POST /transfers` de A para B com valor 100.00
**Então** recebo 201 com `Location` e corpo `{id, sourceAccountId, destinationAccountId, amount, status, createdAt}`
**E** o `id` é um UUID único e o `status` é `COMPLETED`
**E** `GET /accounts/{id}` mostra A com 900.00 e B com 600.00.

**Dado** uma Transferência efetivada
**Quando** somo os saldos das duas Contas antes e depois
**Então** a soma é a mesma.

**Dado** o processamento de uma Transferência
**Quando** ele acontece
**Então** débito, crédito e registro ocorrem em uma única transação atômica
**E** as duas Contas são bloqueadas em ordem crescente de id
**E** só o serviço de `account` altera saldo, e `transfer` usa apenas o serviço público de `account`.

### Story 1.3: Rejeitar solicitações inválidas sem efeito

**Depende de:** Story 1.2.

Como Cliente da API,
quero que uma solicitação inválida seja rejeitada sem efeito,
para que erros meus não criem Transferências nem alterem saldos.

**Acceptance Criteria:**

**Dado** uma solicitação de Transferência cuja Conta de origem não existe
**Quando** faço `POST /transfers`
**Então** recebo 422 no formato Problem Details com código estável.

**Dado** uma solicitação cuja Conta de destino não existe
**Quando** faço `POST /transfers`
**Então** recebo 422 no formato Problem Details com código estável.

**Dado** uma solicitação com valor menor ou igual a zero
**Quando** faço `POST /transfers`
**Então** recebo 400 no formato Problem Details com código estável.

**Dado** uma solicitação cujo valor excede o saldo da Conta de origem
**Quando** faço `POST /transfers`
**Então** recebo 422 no formato Problem Details com código estável.

**Dado** uma solicitação com origem e destino na mesma Conta
**Quando** faço `POST /transfers`
**Então** recebo 422 no formato Problem Details com código estável.

**Dado** qualquer uma das solicitações rejeitadas acima
**Quando** ela é rejeitada
**Então** nenhuma Transferência é criada, nenhum ID é gerado e nenhum saldo muda.

### Story 1.4: Garantir saldo não negativo sob concorrência

**Depende de:** Story 1.2.

Como Cliente da API,
quero que nenhuma Transferência deixe um saldo negativo, mesmo com pedidos simultâneos,
para confiar na integridade dos saldos.

**Acceptance Criteria:**

**Dado** uma Conta com saldo X e várias Transferências simultâneas cuja soma excederia X
**Quando** elas são executadas concorrentemente
**Então** o saldo da Conta nunca fica negativo
**E** a soma dos valores das Transferências aceitas não excede X.

**Dado** Transferências simultâneas em sentidos opostos entre as mesmas duas Contas
**Quando** elas são executadas concorrentemente
**Então** todas terminam sem deadlock.

**Dado** o banco de dados
**Quando** inspeciono o esquema
**Então** existe `CHECK (balance >= 0)`, adicionado por migração Flyway.

**Dado** a suíte de testes
**Quando** ela roda
**Então** um teste de concorrência com várias threads, contra PostgreSQL real via Testcontainers, verifica os critérios acima.

### Story 1.5: Consultar uma transferência pelo identificador

**Depende de:** Story 1.2.

Como Cliente da API,
quero consultar uma Transferência pelo identificador,
para saber o estado atual de uma operação que fiz.

**Acceptance Criteria:**

**Dado** uma Transferência efetivada
**Quando** faço `GET /transfers/{id}`
**Então** recebo 200 com a Transferência e seu status atual.

**Dado** um identificador de Transferência inexistente
**Quando** faço `GET /transfers/{id}`
**Então** recebo 404.

**Dado** duas Transferências efetivadas
**Quando** comparo seus identificadores
**Então** eles são diferentes.
