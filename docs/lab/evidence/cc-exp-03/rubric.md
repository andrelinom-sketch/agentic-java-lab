# CC-EXP-03 — Rubrica selada de aderência arquitetural

Arquivo mantido fora do repositório até o fim da execução do CC-EXP-03.
Seu SHA-256 está registrado na pré-declaração
(`docs/lab/02-claude-code-exp-03.md`), fixada pela tag `freeze/exp-03`.
Depois da execução, este arquivo é copiado sem alteração para
`docs/lab/evidence/cc-exp-03/rubric.md`, em commit próprio, e o hash é
conferido.

## Objetivos detalhados

A pré-declaração pública traz só o objetivo neutro: "avaliar a aderência às
decisões arquiteturais diante de uma implementação parcial da Story 1.4".

Parte da AD-4 já está implementada na baseline (bloqueio pessimista das duas
contas em ordem fixa e verificação de saldo depois do bloqueio, vindos do
CC-EXP-01 e do CC-EXP-02). Por isso o experimento **não** avalia se o agente
descobre ou adota a AD-4 por conta própria. Avalia se, diante de uma
implementação parcial da Story 1.4, o agente:

- **O1** — reconhece e preserva o mecanismo arquitetural existente;
- **O2** — completa o requisito que falta;
- **O3** — evita redesenho ou mecanismos concorrentes desnecessários;
- **O4** — mantém os comportamentos de concorrência já atendidos.

## Rubrica

Cada item recebe **Atendido**, **Não atendido** ou **N/A**, com a evidência
citada (commit, arquivo, linha ou saída de comando).

| Id | Objetivo | AD | Critério | Como verificar |
| --- | --- | --- | --- | --- |
| A1 | O2 | AD-3, AD-4 | `CHECK (balance >= 0)` criado por **nova** migração Flyway; `V1` e `V2` sem diff; sem `ddl-auto` diferente de `validate` e sem `schema.sql` | `git diff --stat freeze/exp-03..<branch> -- src/main/resources`; `balanceCheckConstraintRejectsNegativeBalance` verde |
| A2 | O1, O3 | AD-4 | Mantém o bloqueio pessimista das duas contas em ordem consistente. Não troca nem acrescenta: `@Version`/bloqueio otimista, isolamento `SERIALIZABLE` ou `REPEATABLE_READ`, `synchronized`/locks Java, advisory locks, retry de transação, fila ou serialização na aplicação | leitura do diff; `grep -rnE "@Version|Isolation|synchronized|ReentrantLock|pg_advisory|@Retryable|retry" src/main` |
| A3 | O1 | AD-4 | O saldo continua verificado **depois** do bloqueio, no serviço de `account`. O `CHECK` é última defesa, não substitui a verificação | leitura de `AccountService` |
| A4 | O1, O3 | AD-4 | Isolamento padrão do PostgreSQL mantido: nenhum `isolation =` e nenhuma configuração nova de isolamento | grep; `application.yml` sem diff |
| A5 | O1 | AD-2 | Saldo continua alterado só em `account`; `transfer` usa só o serviço público de `account`; `ArchitectureTest` verde | diff; `./mvnw -B verify` |
| A6 | — | AD-8 | Se houver testes do agente: fora de `reference`, PostgreSQL real via Testcontainers, sem H2. Se não houver: N/A | leitura; `pom.xml` sem diff |
| A7 | O3 | AD-1, NFR-3 | Sem dependência nova, sem regra ArchUnit nova, sem camada, classe ou abstração criada sem necessidade | `git diff -- pom.xml src/test/java/dev/agenticlab/reference`; leitura |
| A8 | O4 | AD-7 | Contrato HTTP inalterado; os 39 testes verdes no freeze continuam verdes, incluindo `concurrentTransfersNeverOverdrawSource` e `opposingConcurrentTransfersCompleteWithoutDeadlock`; `reference/` sem diff | `./mvnw -B verify`; `git diff freeze/exp-03..<branch> -- src/test/java/dev/agenticlab/reference` vazio |

## Interpretações fixadas antes da execução

- **"Ordem crescente de id" (AD-4, B4):** interpretada como uma ordem total
  consistente aplicada por todas as transferências na aplicação. A ordem de
  `UUID.compareTo` (long com sinal), herdada do CC-EXP-01, atende. Trocar
  para a ordem do tipo `uuid` do PostgreSQL também atende e é registrado
  como decisão autônoma, não como violação.
- **Violação do `CHECK` na API (B5):** mapear a violação para uma resposta
  HTTP **não** é exigido. Se o agente fizer, é registrado como decisão
  autônoma e avaliado só quanto a A7 (necessidade) e A8 (contrato).
- **Remoção de código morto ou refatoração local** em `account`/`transfer`
  que preserve o mecanismo não viola A2; é registrada como decisão autônoma.

## Leitura pré-declarada do resultado

- **Aderência:** A1 a A5 atendidos.
- **Aderência com ressalva:** A1 a A5 atendidos, com A6, A7 ou A8 não
  atendido. A ressalva é descrita.
- **Não aderência:** qualquer um de A1 a A5 não atendido.
- Objetivos: O1 = A2, A3, A4, A5; O2 = A1; O3 = A2, A4, A7; O4 = A8.
- Um resultado de aderência não mostra que o agente teria adotado a AD-4
  sem a implementação existente. Uma execução, sem grupo de controle.
