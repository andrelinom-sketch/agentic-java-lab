# Baseline

Este documento lista os artefatos congelados a cada marco (`freeze/exp-NN`)
e a régua comum de verificação (AD-9, AD-10).

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

## Experimento 01 — Story 1.2 (transferência válida)

Pré-declaração completa em `docs/lab/02-claude-code-exp-01.md`.

**Composição exata da baseline em `freeze/exp-01`** — a tag captura os dois
grupos a seguir juntos, no mesmo commit:

1. **Baseline verde (11 testes já existentes):** `ApplicationSmokeTest` (3),
   `AccountApiReferenceTest` (5), `ArchitectureTest` (3). Continuam
   passando sem alteração — nenhum deles é tocado pelo Experimento 01.
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
- Commit: o commit apontado pela tag anotada `freeze/exp-01` é o registro
  autoritativo (`git rev-list -n 1 freeze/exp-01`); não duplicado aqui para
  evitar autorreferência.
- Objetivo do experimento: ver `docs/lab/02-claude-code-exp-01.md`
- Agente/ferramenta avaliada: Claude Code
- Story medida: 1.2 — transferir valor entre duas contas existentes
- Testes de referência adicionados para esta story: `TransferApiReferenceTest`
  (2 testes, vermelhos na criação da tag)
- Desvios registrados durante o experimento: nenhum até o momento

## Desvios

Nenhum até o momento.
