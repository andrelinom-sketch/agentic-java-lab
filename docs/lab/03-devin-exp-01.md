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

## Baseline financeiro antes da execução

| Item | Valor |
| --- | --- |
| Plano Devin | Livre |
| Saldo inicial | US$ 10,00 |
| Uso sob demanda inicial | US$ 0,00 |
| Limite por mensagem configurado | US$ 5,00 |

## Registro pós-execução

