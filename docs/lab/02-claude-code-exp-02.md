# Claude Code Experiment 02 (CC-EXP-02) — Claude Code implementa a Story 1.3 (rejeição de transferências inválidas)

Pré-declaração escrita **antes** de acionar o agente e fixada pela tag
`freeze/exp-02`. O resultado será registrado depois, em commit separado
(`docs/playbook.md`, H5).

## Objetivo

Testar a hipótese **H3** de [`docs/playbook.md`](../playbook.md): quando se
quer que o agente escreva testes próprios, isso deve estar explícito no
prompt, junto com o lugar onde os testes ficam.

## Pergunta experimental

Quando o prompt pede explicitamente testes do agente e diz onde ficam, o
agente escreve testes próprios fora de `reference`, no estilo do AD-8? E
esses testes cobrem algo além do contrato de referência?

## Referência anterior

No CC-EXP-01 ([`02-claude-code-exp-01.md`](02-claude-code-exp-01.md)), o
prompt não pedia testes, e o commit de implementação (`df644e4`) não tocou
`src/test`. Aqui o prompt pede os testes. A story também é outra, então a
comparação não é um A/B.

## Ferramenta avaliada

Claude Code. Versão (`claude --version`), modelo e data: a preencher no
início da execução, antes de enviar o prompt.

## Story medida

Story 1.3 — "Rejeitar solicitações inválidas sem efeito"
(`_bmad-output/planning-artifacts/epics.md`).

**Decisões humanas tomadas antes do freeze** (a story só dizia "código
estável"):

| Caso | HTTP | `code` |
| --- | --- | --- |
| Conta de origem inexistente | 422 | `SOURCE_ACCOUNT_NOT_FOUND` |
| Conta de destino inexistente | 422 | `DESTINATION_ACCOUNT_NOT_FOUND` |
| `amount <= 0` | 400 | `VALIDATION_ERROR` |
| `amount` maior que o saldo da origem | 422 | `INSUFFICIENT_FUNDS` |
| Origem igual ao destino | 422 | `SAME_ACCOUNT` |

"Nenhum ID é gerado" é observado assim: nenhuma Transferência persistida e
resposta sem `id` e sem header `Location`.

## Ponto de partida

- Tag: `freeze/exp-02`. O hash será registrado em commit posterior à tag.
- Branch do experimento: `experiment/exp-02-invalid-transfers`, criada a
  partir da tag.
- Artefatos protegidos e modificáveis: `docs/lab/baseline.md`, seção
  CC-EXP-02 (fonte única, H1).

## Contexto fornecido ao agente

- `CLAUDE.md`, que aponta para `docs/lab/baseline.md`
- `LAB-PLAN.md`
- `ARCHITECTURE-SPINE.md`
- `_bmad-output/planning-artifacts/epics.md` (Story 1.3)
- `docs/lab/baseline.md`
- Todo o repositório na branch do experimento, incluindo
  `TransferRejectionReferenceTest` já vermelho

## Prompt exato a ser dado ao agente

```text
Leia CLAUDE.md antes de qualquer alteração.

Implemente a Story 1.3 do backlog (_bmad-output/planning-artifacts/epics.md):
rejeitar solicitações de transferência inválidas sem efeito.

O contrato que sua implementação precisa satisfazer já está escrito em
src/test/java/dev/agenticlab/reference/TransferRejectionReferenceTest.java.
Esses testes estão vermelhos porque as rejeições ainda não existem.

Os artefatos que você pode alterar e os protegidos estão definidos em
docs/lab/baseline.md, na seção do CC-EXP-02. Não altere nenhum artefato
protegido.

Além de fazer os testes de referência passarem, escreva seus próprios
testes para a Story 1.3 em src/test/java/dev/agenticlab/transfer/**, fora
do pacote reference. Siga o AD-8: testes HTTP contra PostgreSQL real via
Testcontainers (pode reutilizar
dev.agenticlab.reference.support.PostgresContainerConfig, sem alterá-lo) e
testes unitários só para lógica pura. Não duplique os testes de
referência. Cubra os casos que você julgar relevantes além deles.

Ao terminar, rode ./mvnw -B verify e confirme que todos os testes estão
verdes, incluindo os que já existiam antes da sua alteração.
```

## Resultado esperado

- Os 6 testes de `TransferRejectionReferenceTest` passam a verdes.
- Os 13 testes anteriores continuam verdes.
- Há pelo menos um teste do agente em `src/test/java/dev/agenticlab/transfer/**`.
- Nenhum artefato protegido é alterado.
- `git diff freeze/exp-02..experiment/exp-02-invalid-transfers --
  src/test/java/dev/agenticlab/reference` sai vazio.

## Critérios de avaliação da H3

Esta é a rubrica deste experimento. A versão fica fixada pelo commit de
`freeze/exp-02`.

| Id | Critério | Como verificar |
| --- | --- | --- |
| H3-a | O agente escreveu testes próprios | `git diff --stat freeze/exp-02..<branch> -- src/test` |
| H3-b | Todos estão em `src/test/java/dev/agenticlab/transfer/**` | idem |
| H3-c | Seguem o AD-8: HTTP com Testcontainers; unitários só para lógica pura; sem H2; sem dependência nova | leitura do diff; `pom.xml` sem diff |
| H3-d | Não duplicam os testes de referência | leitura: cada teste afirma algo que a referência não afirma |
| H3-e | Cobertura da checklist abaixo | contagem de C1–C5 cobertos |
| H3-f | Utilidade: o teste falharia contra uma implementação errada plausível | leitura, com justificativa registrada por teste |
| H3-g | Os testes de referência continuam intactos | invariante do `git diff` acima |

**Leitura pré-declarada do resultado:**
- Se H3-a e H3-b forem atendidos, a H3 **não é refutada neste caso**. Ela
  continua *hipótese*: uma execução, sem grupo de controle.
- Se H3-a for atendido e H3-b não, a H3 é candidata a **revisão** na parte
  sobre localização.
- Se H3-a não for atendido, é **evidência contrária** à H3 neste caso.
- H3-c a H3-f descrevem a qualidade dos testes. Eles não confirmam nem
  refutam a H3.

## Checklist de avaliação (casos além da referência)

Usada só na avaliação. O prompt não lista esses casos. Ela fica versionada
no repositório antes do freeze, por decisão humana. O agente pode lê-la; se
isso acontecer, fica visível no transcript e é registrado como limitação.

- **C1** — `amount` igual ao saldo da origem é aceito (201), e o saldo da
  origem fica 0.
- **C2** — `amount` com mais de 2 casas decimais é rejeitado com 400, sem
  arredondar (AD-5).
- **C3** — campo ausente ou corpo malformado é rejeitado com 400 (AD-7).
- **C4** — solicitação com mais de uma violação tem resposta determinística
  e sem efeito.
- **C5** — depois de uma rejeição, uma transferência válida entre as mesmas
  contas funciona normalmente.

## Registro de intervenções e tempo (H6)

**Regra de classificação (decisão humana):** aprovações normais de
ferramentas no modo manual **não** contam como intervenção. Conta como
intervenção quando o humano fornece informação, corrige, redireciona ou
influencia tecnicamente a solução. A classificação segue a taxonomia do
PRD: esclarecimento, correção, aprovação ou assumir o trabalho.

**Procedimento:**
1. Abrir uma sessão nova de Claude Code na branch do experimento, em modo
   manual.
2. Registrar abaixo a versão, o modelo e o horário de início, que é o envio
   do prompt.
3. Registrar cada intervenção no momento em que acontece.
4. Registrar o horário de fim, que é o `BUILD SUCCESS` final relatado pelo
   agente.
5. Exportar a sessão com `/export` para
   `docs/lab/evidence/cc-exp-02/session.txt`, revisar se há segredos e
   commitar em commit próprio, separado da implementação e do resultado.

**Log** (preenchido durante a execução):

- Versão do Claude Code:
- Modelo:
- Início:
- Fim:

| Horário | O que o humano disse ou fez | Classificação |
| --- | --- | --- |
| | | |

## Limitações aceitas antes da execução

- A H1 (fonte única de protegidos) e a H3 (pedido explícito de testes)
  mudam ao mesmo tempo em relação ao CC-EXP-01. A H1 é uma correção do
  protocolo experimental, e essa sobreposição foi aceita por decisão
  humana.
- A story é diferente da do CC-EXP-01, então a diferença no resultado não
  pode ser atribuída só ao prompt.
- Há uma única execução, e o avaliador também desenhou o experimento.
- A checklist está no repositório e pode ser lida pelo agente.

## Registro pós-execução

### Evidências

- Freeze: `freeze/exp-02` → `561944f3f491e81750237d550323d2622ec47788`
- Commit de implementação: `d6a7d9e76a197ef2498007ff3c02133087b26402`
  (`feat: implement invalid transfer rejection experiment 02`)
- PR: pendente (ainda sem push)
- CI: pendente (ainda sem push)
- Transcript: pendente (`docs/lab/evidence/cc-exp-02/session.txt`)
- Modelo informado pela sessão: Claude Opus 5.5 (`claude-opus-5-5`)

### Fatos observados

**Resultado da verificação**

- `./mvnw -B verify` final: **BUILD SUCCESS, 37 testes, 0 falhas**, 0 erros,
  0 ignorados.
- Os 6 testes de `TransferRejectionReferenceTest` passaram de vermelho para
  verde.
- Os 13 testes da baseline continuam verdes: `ApplicationSmokeTest` (3),
  `AccountApiReferenceTest` (5), `ArchitectureTest` (3) e
  `TransferApiReferenceTest` (2).
- O agente escreveu 18 testes adicionais, todos em
  `src/test/java/dev/agenticlab/transfer/TransferRejectionApiTest.java`.

**Integridade da baseline**

- Nenhum teste de referência foi alterado:
  `git diff freeze/exp-02 -- src/test/java/dev/agenticlab/reference` sai
  vazio, e não há arquivo novo nessa pasta.
- Nenhum artefato protegido foi alterado. Os 9 arquivos do commit estão
  todos na lista "Modificável nesta story" de `docs/lab/baseline.md`:
  - `account/service/AccountService.java` (alterado)
  - `account/service/SourceAccountNotFoundException.java` (novo)
  - `account/service/DestinationAccountNotFoundException.java` (novo)
  - `account/service/InsufficientFundsException.java` (novo)
  - `common/web/ApiExceptionHandler.java` (alterado)
  - `transfer/service/TransferService.java` (alterado)
  - `transfer/service/SameAccountException.java` (novo)
  - `transfer/web/CreateTransferRequest.java` (alterado)
  - `src/test/java/dev/agenticlab/transfer/TransferRejectionApiTest.java` (novo)
- `pom.xml` sem diff. Nenhuma dependência nova.

**Autoria do commit**

O commit de implementação contém o trailer
`Co-Authored-By: Claude Opus 5.5 <noreply@anthropic.com>`. O agente o
adicionou por conta própria: o prompt do commit não pedia essa linha. O
agente informou o fato ao entregar o commit.

**Tempos**

A implementação inicial levou cerca de **3m56s** e a correção do teste,
cerca de **1m21s**. São tempos informados pela sessão. Não são uma medição
completa de esforço, porque não incluem leitura humana, revisão nem as
etapas de evidência.

**Decisões autônomas declaradas pelo agente**

Decisões que nem os critérios de aceite nem o Architecture Spine
determinavam:

1. **Ordem de prioridade entre violações:** entrada inválida (400) →
   `SAME_ACCOUNT` → `SOURCE_ACCOUNT_NOT_FOUND` →
   `DESTINATION_ACCOUNT_NOT_FOUND` → `INSUFFICIENT_FUNDS` (422).
2. **Mesma conta verificada antes da existência:** a mesma conta inexistente
   como origem e destino responde `SAME_ACCOUNT`.
3. **Os dois bloqueios antes das verificações:**
   - a existência é verificada depois dos dois `SELECT … FOR UPDATE`, origem
     antes de destino;
   - o objetivo é o código de erro não depender da ordem dos UUIDs;
   - o custo é bloquear a conta existente até o rollback e fazer um
     `SELECT … FOR UPDATE` a mais nos casos de erro;
   - o helper `lockById`, do CC-EXP-01, foi removido;
   - a revisão humana manteve esta decisão, registrada como decisão
     autônoma/trade-off.
4. **Onde fica cada regra:**
   - saldo e existência em `AccountService`;
   - mesma conta em `TransferService`;
   - `amount > 0` no DTO, com `@DecimalMin(value = "0.00", inclusive = false)`.
5. **Hierarquia de exceções:** `Source…` e
   `DestinationAccountNotFoundException` estendem
   `AccountNotFoundException`. `GET /accounts/{id}` continua respondendo 404
   `ACCOUNT_NOT_FOUND` porque o Spring escolhe o handler mais específico.
6. **Saldo verificado no serviço:** `Account.debit` continua sem proteção
   própria.
7. **Nova dependência de `common` para `transfer`:** para
   `SameAccountException`. O ArchUnit não detectou ciclo.
8. **`detail` do Problem Details:** usa a mensagem da exceção, que inclui o
   UUID da conta. Não há `type` nem `instance` personalizados.
9. **Saldos ajustados direto no banco num teste:**
   `insufficientFundsIsCheckedOnSourceWhateverItsLockPosition` usa `UPDATE`
   via `JdbcTemplate`.
10. **Nenhum teste unitário:** o agente declarou que não havia lógica pura a
    isolar.

**Observação feita na revisão, sem alteração**

A ordem de bloqueio usa `UUID.compareTo`, que compara os bits como `long`
com sinal. Essa ordem é diferente da ordem de `uuid` no PostgreSQL, que
compara byte a byte, sem sinal. O critério já vinha do CC-EXP-01 e não foi
alterado neste experimento.

**Checklist lida pelo agente (limitação)**

O agente leu `docs/lab/02-claude-code-exp-02.md` inteiro, inclusive a
checklist C1–C5 e a rubrica da H3, **antes** de escrever os testes. O
próprio agente declarou isso no relatório de entrega. É a limitação prevista
em "Limitações aceitas antes da execução".

### Intervenções humanas

| Momento | O que o humano disse ou fez | Classificação |
| --- | --- | --- |
| Revisão, antes do commit | Pediu ao agente o conteúdo dos arquivos e uma explicação de `transferBalance`, sem pedir mudança. Na resposta, o agente apontou que `unknownDestinationIsReportedWhateverItsLockPosition` não garantia as duas posições de bloqueio declaradas. | Não classificada como intervenção: pedido de evidência, sem direcionamento técnico. |
| Revisão, antes do commit | Pediu exclusivamente a correção desse teste, proibindo alterar código de produção, testes de referência e documentação. | Correção |

- **Código de produção:** nenhum arquivo foi alterado nessa intervenção.
- **O que mudou no teste:** os destinos inexistentes passaram de
  `new UUID(0L, 1L)` e `new UUID(-1L, -1L)` para
  `new UUID(Long.MIN_VALUE, Long.MIN_VALUE)` e
  `new UUID(Long.MAX_VALUE, Long.MAX_VALUE)`. Também foram acrescentadas duas
  asserções sobre `compareTo` em relação à origem.
- **Resultado depois da correção:** 37 testes, 0 falhas.
- **Aprovações não contadas:** as aprovações de ferramenta, a aprovação da
  implementação e o pedido de commit não contam como intervenção, pela regra
  de classificação pré-declarada.

### Avaliação da H3 (rubrica pré-declarada)

| Id | Resultado | Base |
| --- | --- | --- |
| H3-a | Atendido | O commit `d6a7d9e` adiciona `TransferRejectionApiTest` (18 testes). |
| H3-b | Atendido | O único arquivo de teste novo está em `src/test/java/dev/agenticlab/transfer/`. |
| H3-c | Atendido | Testes HTTP com Testcontainers, reutilizando `PostgresContainerConfig`. Sem H2, sem teste unitário, `pom.xml` sem diff. Um teste ajusta saldos por SQL direto (decisão 9). |
| H3-d | Atendido, com sobreposição parcial em 2 testes (3 e 18) | Ver a coluna "Sobreposição" abaixo. |
| H3-e | 5 de 5 (C1–C5), com a ressalva de que a checklist foi lida antes | C1: teste 1. C2: teste 4. C3: testes 5–9. C4: testes 10–16. C5: teste 17. |
| H3-f | 15 com utilidade clara, 3 com utilidade fraca | Justificativa por teste abaixo. |
| H3-g | Atendido | `git diff freeze/exp-02 -- src/test/java/dev/agenticlab/reference` vazio. |

**H3-d e H3-f por teste.** Coluna "Falharia contra": implementação errada
plausível que o teste detecta.

| # | Teste | Falharia contra | Sobreposição com a referência |
| --- | --- | --- | --- |
| 1 | `acceptsAmountEqualToSourceBalanceLeavingZero` | verificação de saldo com `<=` em vez de `<` | não |
| 2 | `acceptsSmallestPositiveAmount` | limite mínimo errado (por exemplo `@Min(1)`) | não |
| 3 | `rejectsAnyAmountFromZeroBalanceAccount` | quase nenhuma além da que a referência já pega; **utilidade fraca** | **parcial** (`rejectsAmountExceedingSourceBalance`) |
| 4 | `rejectsAmountWithMoreThanTwoDecimalPlacesWithoutRounding` | remoção de `@Digits` ou arredondamento | não |
| 5 | `rejectsMissingAmount` | remoção de `@NotNull` (NPE → 500) | não |
| 6 | `rejectsMissingSourceAccountId` | idem (NPE em `equals` → 500) | não |
| 7 | `rejectsMissingDestinationAccountId` | idem | não |
| 8 | `rejectsNonUuidAccountId` | tratamento de corpo ilegível diferente de 400 | não |
| 9 | `rejectsMalformedJsonBody` | idem | não |
| 10 | `invalidInputTakesPrecedenceOverBusinessRules` | validação de valor movida para depois da checagem de mesma conta | não |
| 11 | `sameAccountTakesPrecedenceOverUnknownAccount` | existência verificada antes de mesma conta | não |
| 12 | `unknownSourceIsReportedFirstWhenBothAccountsAreUnknownRegardlessOfIdOrder` | falhar no primeiro bloqueio, como o `lockById` anterior | não |
| 13 | `unknownDestinationTakesPrecedenceOverInsufficientFunds` | saldo verificado antes da existência do destino | não |
| 14 | `unknownSourceTakesPrecedenceOverInsufficientFunds` | só uma implementação improvável, já que sem origem não há saldo a verificar; **utilidade fraca** | não |
| 15 | `insufficientFundsIsCheckedOnSourceWhateverItsLockPosition` | saldo verificado na primeira conta bloqueada, em vez da origem | não |
| 16 | `unknownDestinationIsReportedWhateverItsLockPosition` | exceção escolhida pela posição do bloqueio, em vez do papel da conta (garantido só depois da correção) | não |
| 17 | `validTransferSucceedsAfterRejectionBetweenSameAccounts` | rejeição que deixa efeito parcial (débito sem rollback) | não |
| 18 | `unknownAccountInTransferDoesNotChangeAccountLookupContract` | handler de conta inexistente mudado para 422 de forma global; **utilidade fraca** | **parcial** (`AccountApiReferenceTest.returnsNotFoundForUnknownAccountId`) |

### Interpretação

- **Leitura pré-declarada:** H3-a e H3-b foram atendidos. Portanto, a H3
  **não é refutada neste caso**. Ela continua hipótese: é uma única
  execução, sem grupo de controle, e o avaliador também desenhou o
  experimento.
- **H3-e é fraco como evidência:** o agente conhecia a checklist, então os
  5/5 não mostram que ele encontrou esses casos sozinho.
- **Qualidade dos testes:** a falha do teste 16 não foi detectada pelo build
  verde. Apareceu quando o próprio agente explicou o código a pedido da
  revisão humana. Isso sugere que testes de ordenação e concorrência escritos
  pelo agente precisam de revisão específica das premissas.
- **Atribuição:** H3-c a H3-f descrevem a qualidade dos testes e, pela
  rubrica, não confirmam nem refutam a H3. As classificações de H3-d e H3-f
  acima foram redigidas pelo agente avaliado e devem ser conferidas pelo
  avaliador humano.
