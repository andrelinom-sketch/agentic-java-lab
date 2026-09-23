# Baseline

Este documento lista os artefatos congelados a cada marco (`freeze/exp-NN`)
e a régua comum de verificação (AD-9, AD-10).

**Fonte única.** Este arquivo é a única fonte autoritativa dos artefatos
protegidos e modificáveis. O `CLAUDE.md` só aponta para cá. Valem as listas
**Protegido** e **Modificável nesta story** da seção do experimento
corrente. As seções anteriores são registro histórico. Um artefato que não
aparece em nenhuma das duas listas é tratado como protegido: o agente para e
sinaliza, em vez de alterá-lo. Essa regra foi adotada a partir do
CC-EXP-02, por causa do DEV-CC-EXP-01-01 (ver `docs/playbook.md`, H1).

## Nomenclatura

Para evitar ambiguidade, os ciclos do laboratório são identificados assim
nos documentos de `docs/lab/`:

- **BMAD Planning Cycle 01** — ciclo de planejamento com BMAD (brief, PRD,
  arquitetura, épicos e stories), registrado em `docs/lab/01-bmad.md`. É o
  ciclo que o PRD (FR-6) e o título de `01-bmad.md` chamam de
  "Experimento 01 (BMAD)".
- **Claude Code Experiment 01 (CC-EXP-01)** — implementação da Story 1.2
  por Claude Code, registrada em `docs/lab/02-claude-code-exp-01.md`.
- **Claude Code Experiment 02 (CC-EXP-02)** — implementação da Story 1.3
  por Claude Code, registrada em `docs/lab/02-claude-code-exp-02.md`.

Tags, branches e o histórico Git existentes não são renomeados: a tag
`freeze/exp-01` e a branch `experiment/exp-01-transfer` pertencem ao
CC-EXP-01.

## Infraestrutura comum (pré-experimentos)

Artefatos que compõem a régua e não devem ser alterados por um agente
implementador sem aprovação humana:

- `.github/workflows/ci.yml` — CI (GitHub Actions, `./mvnw -B verify`)
- `pom.xml` — plugins de build, JaCoCo (informativo) e dependência ArchUnit
- `src/test/java/dev/agenticlab/reference/**` — testes de referência
  - `ApplicationSmokeTest` — contexto Spring sobe contra PostgreSQL real
  - `ArchitectureTest` — fronteiras arquiteturais mínimas (AD-1, AD-2)
- `src/test/resources/archunit.properties` — configuração do ArchUnit
- `docs/lab/baseline.md` — este arquivo
- `CLAUDE.md` — guardrails para agentes implementadores

## S1 — infraestrutura funcional pré-experimento

Story 1.1 (criação e consulta de conta), implementada sob supervisão direta,
fora do experimento medido. Faz parte da baseline congelada a partir daqui:

- `src/main/java/dev/agenticlab/account/**`
- `src/main/java/dev/agenticlab/common/**`
- `src/main/resources/db/migration/V1__create_account_table.sql`
- `src/test/java/dev/agenticlab/reference/AccountApiReferenceTest.java` —
  testes de referência dos critérios de aceite da Story 1.1

## Claude Code Experiment 01 (CC-EXP-01) — Story 1.2 (transferência válida)

Pré-declaração completa em `docs/lab/02-claude-code-exp-01.md`.

**Composição exata da baseline em `freeze/exp-01`** — a tag captura os dois
grupos a seguir juntos, no mesmo commit:

1. **Baseline verde (11 testes já existentes):** `ApplicationSmokeTest` (3),
   `AccountApiReferenceTest` (5), `ArchitectureTest` (3). Continuam
   passando sem alteração — nenhum deles é tocado pelo CC-EXP-01.
2. **Especificação executável, inicialmente vermelha (2 testes):**
   `TransferApiReferenceTest` (`transferBetweenExistingAccountsDebitsAndCredits`,
   `transferPreservesSumOfBalances`). Falham propositalmente na criação da
   tag, porque `POST /transfers` ainda não existe. Não são um teste
   opcional nem um obstáculo a contornar — são o contrato que a Story 1.2
   precisa satisfazer.

O objetivo do agente (Claude Code, na branch `experiment/exp-01-transfer`)
é fazer os 2 testes de `TransferApiReferenceTest` ficarem verdes,
implementando `transfer`, **sem modificar** nenhum arquivo listado como
baseline congelada nesta página nem os testes de referência já existentes
(incluindo o próprio `TransferApiReferenceTest`).

O locking em ordem crescente de id e a atomicidade interna da transferência
(terceira frase do AC da Story 1.2) são verificados por revisão humana do
código e pela regra ArchUnit já existente
(`transfer_should_not_access_account_repository_or_model`), que passa a ser
checada de fato assim que `transfer` existir. A prova empírica de
concorrência (múltiplas threads, saldo nunca negativo) fica para a
Story 1.4 / Experimento futuro.

### freeze/exp-01

- Data: 2026-09-22
- Tag: `freeze/exp-01` (anotada)
- Commit: `2cc86e58a58acd5e62460b1a0081c99c86c4c890`
  (`git rev-list -n 1 freeze/exp-01`)
- Objetivo do experimento: ver `docs/lab/02-claude-code-exp-01.md`
- Agente/ferramenta avaliada: Claude Code
- Story medida: 1.2 — transferir valor entre duas contas existentes
- Testes de referência adicionados para esta story: `TransferApiReferenceTest`
  (2 testes, vermelhos na criação da tag)
- Desvios registrados durante o experimento: 1 (DEV-CC-EXP-01-01, abaixo)

## Claude Code Experiment 02 (CC-EXP-02) — Story 1.3 (rejeição de transferências inválidas)

Pré-declaração completa em `docs/lab/02-claude-code-exp-02.md`.

**Composição da baseline em `freeze/exp-02`:** a tag captura, no mesmo
commit, a `main` com S1, S2 e o playbook v0.1, mais:

1. **Baseline verde (13 testes):** `ApplicationSmokeTest` (3),
   `AccountApiReferenceTest` (5), `ArchitectureTest` (3) e
   `TransferApiReferenceTest` (2).
2. **Especificação executável, inicialmente vermelha (6 testes):**
   `TransferRejectionReferenceTest`, com um teste por critério de aceite da
   Story 1.3:
   - `rejectsUnknownSourceAccount`
   - `rejectsUnknownDestinationAccount`
   - `rejectsZeroAmount`
   - `rejectsNegativeAmount`
   - `rejectsAmountExceedingSourceBalance`
   - `rejectsSameSourceAndDestination`
3. A pré-declaração, com o prompt exato, a checklist de avaliação e os
   critérios da H3, e este arquivo e o `CLAUDE.md` na versão com fonte
   única.

### Protegido

- `src/test/java/dev/agenticlab/reference/**` (inclui `support/` e
  `TransferRejectionReferenceTest`)
- `src/test/resources/**`
- `src/main/resources/**` (inclui `application.yml` e todas as migrations
  Flyway, existentes ou novas: a Story 1.3 não prevê mudança de esquema; o
  `CHECK (balance >= 0)` pertence à Story 1.4)
- `src/main/java/dev/agenticlab/AgenticLabApplication.java`
- `pom.xml`, `mvnw`, `mvnw.cmd`, `.mvn/**`
- `.github/**`, `docker-compose.yml`, `scripts/**`
- `CLAUDE.md`, `LAB-PLAN.md`, `README.md`, `docs/**`
- `_bmad/**`, `_bmad-output/**`, `.claude/**`

### Modificável nesta story

- `src/main/java/dev/agenticlab/transfer/**`
- `src/main/java/dev/agenticlab/account/**`: extensão prevista. Pelo AD-2
  e pelo AD-4, a verificação de saldo pertence a `account` e ocorre depois
  do bloqueio. O comportamento existente de `account` continua coberto pelo
  `AccountApiReferenceTest` (por exemplo, `GET /accounts/{id}` inexistente
  segue 404 `ACCOUNT_NOT_FOUND`).
- `src/main/java/dev/agenticlab/common/web/ApiExceptionHandler.java`
- `src/test/java/dev/agenticlab/transfer/**`: testes escritos pelo agente
  (AD-8), fora de `reference`.

### Como distinguir testes de referência e testes do agente

- **Pacote:** `dev.agenticlab.reference..` é régua; qualquer teste fora
  dele é trabalho do agente.
- **Origem:** testes de referência já existem em `freeze/exp-02`; testes do
  agente só aparecem em commits posteriores da branch do experimento.
- **Invariante:** `git diff freeze/exp-02..<branch> --
  src/test/java/dev/agenticlab/reference` precisa sair vazio.

### freeze/exp-02

- Data: a preencher na criação da tag
- Tag: `freeze/exp-02` (anotada)
- Commit: registrado em commit posterior à tag, porque a tag não pode
  conter o próprio hash (`git rev-list -n 1 freeze/exp-02`)
- Branch do experimento: `experiment/exp-02-invalid-transfers`
- Agente/ferramenta avaliada: Claude Code
- Story medida: 1.3 — rejeitar solicitações inválidas sem efeito
- Hipótese testada: H3 (`docs/playbook.md`)
- Desvios registrados durante o experimento: nenhum até o momento

## Desvios

### DEV-CC-EXP-01-01 — extensão de `account/**` congelado (CC-EXP-01)

- **O que estava congelado:** `src/main/java/dev/agenticlab/account/**`
  constava da baseline S1 nesta página, congelada a partir de
  `freeze/exp-01`.
- **O que foi alterado:** três arquivos desse pacote foram estendidos, só
  com acréscimos, no commit `df644e4` (PR #1, merge `2b45a49`):
  - `account/model/Account.java` — métodos `debit` e `credit`;
  - `account/repository/AccountRepository.java` — `findByIdForUpdate`, com
    bloqueio pessimista;
  - `account/service/AccountService.java` — `transferBalance`, que bloqueia
    as duas contas em ordem crescente de id.
- **Por quê:** a Story 1.2 (S2) exige alterar saldo. Pelo AD-2, só
  `account` altera saldo, e `transfer` usa apenas o serviço público de
  `account`. Pelo AD-4, o bloqueio pessimista das contas é feito em ordem
  crescente de id. Implementar S2 conforme a arquitetura exigia estender
  `account`.
- **Classificação:** desvio de protocolo, porque a baseline não previa a
  extensão de um pacote congelado que a story precisava tocar. **Não é
  falha do agente.** A lista de arquivos protegidos do `CLAUDE.md`, que o
  agente recebeu como guardrail, não incluía `account/**`, e nenhum arquivo
  dessa lista foi alterado.
- **Causa:** duas listas de "congelados" divergentes
  (`CLAUDE.md` e esta página). A verificação pós-execução usou só a lista do
  `CLAUDE.md`.
- **Aprovação humana (AD-10):** implícita na revisão e no merge do PR #1.
  Registrada explicitamente aqui depois do merge.
- **Impacto nos resultados:** nenhum teste de referência foi alterado. Os
  11 testes da baseline e os 2 de `TransferApiReferenceTest` passam.
