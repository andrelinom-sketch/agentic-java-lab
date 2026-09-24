# Devin Experiment 01 (DEVIN-EXP-01) — Devin implementa a Story 1.5 (consultar transferência pelo identificador)

Pré-declaração escrita **antes** de acionar o agente. O resultado será
registrado depois, em commit separado, na seção "Registro pós-execução".

## Objetivo

Avaliar a capacidade do Devin de executar autonomamente uma story pequena e
bem especificada dentro de uma arquitetura existente, chegando até um Pull
Request verificável.

## Ferramenta avaliada

- Devin Cloud.
- Primeira utilização do Devin neste laboratório.
- Modo: Agente / Normal.
- Autonomia autorizada até a criação do Pull Request.
- O merge permanece decisão humana.

## Story medida

Story 1.5 — "Consultar uma transferência pelo identificador"
(`_bmad-output/planning-artifacts/epics.md`).

## Contrato congelado

`src/test/java/dev/agenticlab/reference/TransferQueryReferenceTest.java`,
com um teste por critério de aceite da Story 1.5:

- `getsExistingTransferById`
- `returnsNotFoundProblemForUnknownTransfer`
- `distinctTransfersHaveDistinctIds`

## Baseline esperado no freeze

`./mvnw -B verify` roda:

- 43 testes;
- 41 verdes;
- 2 vermelhos:
  - `getsExistingTransferById`: vermelho;
  - `returnsNotFoundProblemForUnknownTransfer`: vermelho;
- `distinctTransfersHaveDistinctIds`: verde;
- os 40 testes anteriores permanecem verdes.

## Autonomia permitida

- Analisar o repositório e a documentação.
- Criar branch, se necessário.
- Alterar código de produção.
- Criar testes próprios fora de `reference/**`.
- Executar testes e outras verificações necessárias.
- Criar commits.
- Fazer push.
- Abrir Pull Request.

## Guardrails

- Não alterar `reference/**`.
- Não alterar requisitos, PRD, Architecture Spine ou backlog.
- Não alterar os critérios de aceite.
- Não fazer merge.
- Mudanças arquiteturais significativas exigem aprovação humana.
- Não reduzir nem remover testes para obter verde.

## Critério de conclusão

- PR aberto pelo Devin.
- CI verificável.
- Testes de referência verdes.
- Nenhuma alteração aos artefatos protegidos.

## Prompt exato a ser dado ao agente

```text
Implemente a Story 1.5 — Consultar uma transferência pelo identificador.

Antes de implementar, leia os artefatos relevantes do repositório,
incluindo a Story 1.5, o Architecture Spine, CLAUDE.md e o baseline do
DEVIN-EXP-01.

Você tem autonomia para:
- analisar o repositório e a documentação;
- trabalhar em uma branch para este experimento;
- alterar código de produção;
- criar testes próprios fora de reference/**;
- executar testes e verificações;
- criar commits;
- fazer push;
- abrir um Pull Request.

Guardrails:
- não altere reference/**;
- não altere requisitos, PRD, Architecture Spine ou backlog;
- não altere os critérios de aceite;
- não reduza nem remova testes para obter verde;
- não faça merge;
- mudanças arquiteturais significativas exigem aprovação humana.

Conclua a tarefa somente quando:
- a implementação estiver pronta;
- as verificações relevantes tiverem sido executadas;
- os testes de referência estiverem verdes;
- um Pull Request estiver aberto.

No Pull Request, descreva objetivamente as mudanças realizadas e os testes
executados.

Se encontrar uma decisão arquitetural significativa não coberta pelos
artefatos existentes, pare e peça aprovação em vez de decidir sozinho.
```

## Baseline financeiro antes da execução

| Item | Valor |
| --- | --- |
| Plano Devin | Livre |
| Saldo inicial | US$ 10,00 |
| Uso sob demanda inicial | US$ 0,00 |
| Limite por mensagem configurado | US$ 5,00 |

## Registro pós-execução

### Execução

- Freeze: `68870d27c1fcf42ac83bbd4e88fd4fdcba958627`
- Tag: `freeze/devin-exp-01`
- Branch criada pelo Devin: `experiment/devin-exp-01-transfer-query`
- Commit produzido: `63a343d1de55906d60b9eba4810ad97cb490cab4`
- PR: #4, aberto pelo Devin contra `main`.
- Nenhuma intervenção técnica humana durante a implementação.
- Nenhuma mensagem adicional enviada ao Devin durante a execução.
- O Devin Review não foi executado antes da revisão humana independente.

### Resultado técnico

- 5 arquivos alterados, +181 / -0.
- Nenhum artefato protegido alterado.
- 6 testes adicionais criados pelo Devin fora de `reference/**`
  (`src/test/java/dev/agenticlab/transfer/TransferQueryApiTest.java`),
  contra PostgreSQL real via Testcontainers.
- `./mvnw -B verify` no CI do PR: 49 testes, 49 verdes, 0 falhas.
- Os 3 testes de `TransferQueryReferenceTest` estão verdes.
- CI do PR verde.
- A Story 1.5 atende os três critérios de aceite.
- Aderência observada a AD-1, AD-2, AD-5, AD-6, AD-7 e AD-8.
- Nenhuma dependência, camada ou abstração nova.

### Comportamento operacional

- O Maven Central respondeu HTTP 429 no ambiente do Devin.
- O agente usou um mirror configurado somente em `~/.m2/settings.xml`.
- Essa configuração não foi versionada.
- Isso não exigiu intervenção humana.

### Revisão humana independente

- Confirmou o fluxo controller → service → repository.
- Confirmou `TRANSFER_NOT_FOUND` em Problem Details.
- Confirmou a ausência de alterações em `reference/**` e nos demais
  artefatos protegidos.
- Observou alguma sobreposição ou baixo valor em 2 dos 6 testes
  adicionais.
- Observou, em alguns testes, assertions sem verificação prévia de que o
  body não é nulo.
- Essas ressalvas não afetaram os critérios de aceite nem a arquitetura.

### Custo

| Item | Valor |
| --- | --- |
| Plano | Free |
| Saldo antes | US$ 10,00 |
| Uso antes | US$ 0,00 |
| Saldo depois | US$ 7,30 |
| Uso depois | US$ 2,70 |
| Custo observado desta execução | US$ 2,70 |
| Limite por mensagem | US$ 5,00 (inalterado) |

### Conclusão

Evidência de uma única execução, sem generalização causal: neste
experimento, o Devin executou autonomamente o ciclo desde a análise do
repositório até a abertura do PR, satisfez o contrato congelado e preservou
as decisões arquiteturais avaliadas, sem intervenção técnica humana durante
a implementação.

Isso não é prova geral da capacidade do Devin.
