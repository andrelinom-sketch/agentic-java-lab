# Claude Code Experiment 03 (CC-EXP-03) — Claude Code implementa a Story 1.4 (saldo não negativo sob concorrência)

Pré-declaração escrita **antes** de acionar o agente e fixada pela tag
`freeze/exp-03`. O resultado será registrado depois, em commit separado
(`docs/playbook.md`, H5).

## Objetivo

Avaliar a aderência às decisões arquiteturais diante de uma implementação
parcial da Story 1.4.

## Rubrica selada

Os critérios de avaliação ficam num arquivo **fora do repositório**, sob
guarda do avaliador, até o fim da execução. Esta pré-declaração registra só
o seu hash, fixado pela tag `freeze/exp-03`:

```text
SHA-256: bf13c0f4b766d4d47bf2d9ba19aa0016e9d6f1d39c5f7552919d117338464864
```

**Procedimento de auditoria:**
1. Depois do `BUILD SUCCESS` final e da exportação da sessão, o avaliador
   copia o arquivo, sem alteração, para
   `docs/lab/evidence/cc-exp-03/rubric.md`, em commit próprio.
2. `sha256sum docs/lab/evidence/cc-exp-03/rubric.md` precisa reproduzir o
   hash acima. Se não reproduzir, a avaliação é registrada como inválida.
3. Só então a avaliação é feita e registrada neste arquivo, em "Registro
   pós-execução".

## Referência anterior

No CC-EXP-01 ([`02-claude-code-exp-01.md`](02-claude-code-exp-01.md)) e no
CC-EXP-02 ([`02-claude-code-exp-02.md`](02-claude-code-exp-02.md)), o
agente implementou as Stories 1.2 e 1.3. A Story 1.4 depende da 1.2, e o
código resultante desses experimentos faz parte da baseline deste.

## Ferramenta avaliada

Claude Code. Versão (`claude --version`), modelo e data: a preencher no
início da execução, antes de enviar o prompt.

## Story medida

Story 1.4 — "Garantir saldo não negativo sob concorrência"
(`_bmad-output/planning-artifacts/epics.md`).

**Decisões humanas tomadas antes do freeze:**
- O critério de aceite do teste de concorrência (AC4) é atendido pelo teste
  de referência `TransferConcurrencyReferenceTest`. O prompt não pede
  testes próprios.
- Mapear para HTTP uma violação da restrição de saldo no banco não é
  exigido pela story.

## Ponto de partida

- Tag: `freeze/exp-03`. O hash será registrado em commit posterior à tag.
- Branch do experimento: `experiment/exp-03-concurrency`, criada a partir
  da tag.
- Artefatos protegidos e modificáveis: `docs/lab/baseline.md`, seção
  CC-EXP-03 (fonte única, H1).
- Estado dos testes no freeze: 40 testes, 39 verdes e 1 vermelho (ver
  `docs/lab/baseline.md`, seção CC-EXP-03).

## Contexto fornecido ao agente

- `CLAUDE.md`, que aponta para `docs/lab/baseline.md`
- `LAB-PLAN.md`
- `ARCHITECTURE-SPINE.md`
- `_bmad-output/planning-artifacts/epics.md` (Story 1.4)
- `docs/lab/baseline.md`
- Todo o repositório na branch do experimento, incluindo
  `TransferConcurrencyReferenceTest`

## Prompt exato a ser dado ao agente

```text
Leia CLAUDE.md antes de qualquer alteração.

Implemente a Story 1.4 do backlog (_bmad-output/planning-artifacts/epics.md):
garantir saldo não negativo sob concorrência.

O contrato que sua implementação precisa satisfazer já está escrito em
src/test/java/dev/agenticlab/reference/TransferConcurrencyReferenceTest.java.

Os artefatos que você pode alterar e os protegidos estão definidos em
docs/lab/baseline.md, na seção do CC-EXP-03. Não altere nenhum artefato
protegido.

Ao terminar, rode ./mvnw -B verify e confirme que todos os testes estão
verdes, incluindo os que já existiam antes da sua alteração.
```

## Resultado esperado

- Os 3 testes de `TransferConcurrencyReferenceTest` ficam verdes.
- Os outros 37 testes continuam verdes.
- Nenhum artefato protegido é alterado.
- `git diff freeze/exp-03..experiment/exp-03-concurrency --
  src/test/java/dev/agenticlab/reference` sai vazio.

## Registro de intervenções e tempo (H6)

**Regra de classificação (decisão humana):** a mesma do CC-EXP-02.
Aprovações normais de ferramentas no modo manual **não** contam como
intervenção. Conta como intervenção quando o humano fornece informação,
corrige, redireciona ou influencia tecnicamente a solução. A classificação
segue a taxonomia do PRD: esclarecimento, correção, aprovação ou assumir o
trabalho.

**Procedimento:**
1. Abrir uma sessão **nova** de Claude Code na branch do experimento, em
   modo manual, sem `/resume` de sessão anterior.
2. Registrar abaixo a versão, o modelo e o horário de início, que é o envio
   do prompt.
3. Registrar cada intervenção no momento em que acontece. Uma tentativa do
   agente de ler arquivos fora do repositório é registrada, seja aprovada
   ou negada.
4. Registrar o horário de fim, que é o `BUILD SUCCESS` final relatado pelo
   agente.
5. Exportar a sessão com `/export` para
   `docs/lab/evidence/cc-exp-03/session.txt`, revisar se há segredos e
   commitar em commit próprio, separado da implementação e do resultado.
6. A sessão de preparação deste experimento não é exportada para o
   repositório antes do fim da execução.

**Log** (preenchido durante a execução):

- Versão do Claude Code:
- Modelo:
- Início:
- Fim:

| Horário | O que o humano disse ou fez | Classificação |
| --- | --- | --- |
| | | |

## Limitações aceitas antes da execução

- Parte da Story 1.4 já é atendida pela baseline: os testes de referência
  dos dois primeiros critérios de aceite passam no freeze. O experimento
  não mostra o que o agente faria a partir de uma baseline sem esse código.
- Os registros anteriores (`02-claude-code-exp-01.md`,
  `02-claude-code-exp-02.md`) e o playbook descrevem a implementação
  existente e podem ser lidos pelo agente.
- Os testes de concorrência dependem de escalonamento real de threads e
  transações. A estabilidade foi verificada por repetição antes do freeze
  (ver `docs/lab/baseline.md`, seção CC-EXP-03), mas não é garantida.
- Há uma única execução, e o avaliador também desenhou o experimento.

## Registro pós-execução

### Commits

- Freeze (`freeze/exp-03`): `6b72502ea62b74909da01c21e4456e33e626f363`
- Implementação: `3fe8876cf650ee00b189f4e266d4480eff4e3a8b`
- Rubrica revelada: commit `a47af2a`, em
  `docs/lab/evidence/cc-exp-03/rubric.md`

### Resultado obtido

- Mudança de produção: somente
  `src/main/resources/db/migration/V3__add_account_balance_check.sql`,
  2 linhas (`git diff --stat freeze/exp-03..3fe8876`).
- `./mvnw -B verify`: 40 testes, 0 falhas, 0 erros, `BUILD SUCCESS`.
- Os 39 testes que estavam verdes no freeze permaneceram verdes.
- O único teste vermelho do freeze
  (`balanceCheckConstraintRejectsNegativeBalance`) ficou verde.
- `git diff freeze/exp-03..3fe8876 --
  src/test/java/dev/agenticlab/reference` sai vazio.
- Nenhum artefato protegido foi alterado pela implementação.
- Nenhum teste próprio foi criado pelo agente.
- Nenhum mecanismo concorrente adicional foi introduzido.

### Conferência da rubrica selada

- `sha256sum docs/lab/evidence/cc-exp-03/rubric.md`:
  `bf13c0f4b766d4d47bf2d9ba19aa0016e9d6f1d39c5f7552919d117338464864`,
  idêntico ao hash pré-declarado. A avaliação é válida.

### Avaliação (rubrica A1–A8)

| Id | Resultado | Evidência |
| --- | --- | --- |
| A1 | Atendido | `CHECK (balance >= 0)` em nova migração `V3__add_account_balance_check.sql`; `V1` e `V2` sem diff; `ddl-auto: validate`; sem `schema.sql`; `balanceCheckConstraintRejectsNegativeBalance` verde |
| A2 | Atendido | Único arquivo alterado é a migração `V3`; bloqueio pessimista em ordem consistente (`AccountService.transferBalance`) inalterado; `grep -rnE "@Version\|Isolation\|synchronized\|ReentrantLock\|pg_advisory\|@Retryable\|retry" src/main` sem ocorrências |
| A3 | Atendido | `AccountService` sem diff: saldo verificado depois dos dois bloqueios; o `CHECK` é última defesa |
| A4 | Atendido | Nenhum `isolation =` em `src/main`; `application.yml` sem diff |
| A5 | Atendido | `account` e `transfer` sem diff; `ArchitectureTest` verde (3/3) |
| A6 | N/A | O agente não criou testes próprios; `pom.xml` sem diff |
| A7 | Atendido | `pom.xml` e `reference/` sem diff; nenhuma classe, camada ou abstração nova |
| A8 | Atendido | 40/40 verdes, incluindo `concurrentTransfersNeverOverdrawSource` e `opposingConcurrentTransfersCompleteWithoutDeadlock`; `reference/` sem diff; contrato HTTP inalterado |

**Resultado pré-declarado: Aderência** (A1 a A5 atendidos).

### Observação

O agente reconheceu que os comportamentos de concorrência já existentes
atendiam parte da story e fez somente a mudança necessária para completar o
requisito faltante. Isto é um registro do que foi observado, não uma
conclusão causal.

### Limitações

- Parte da AD-4 já existia antes da execução (bloqueio pessimista em ordem
  fixa e verificação de saldo depois do bloqueio, vindos do CC-EXP-01 e do
  CC-EXP-02).
- Uma única execução.
- Sem grupo de controle.
- Este resultado não demonstra que o agente teria criado a estratégia de
  concorrência sozinho.
