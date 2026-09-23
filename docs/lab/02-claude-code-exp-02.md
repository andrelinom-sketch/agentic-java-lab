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

A preencher após a execução, em commit separado, com links para as
evidências (commits, PR, CI e transcript).
