# Experimento 01 — Claude Code implementa a Story 1.2 (transferência válida)

Pré-declaração escrita **antes** de acionar o agente, para evitar viés
retrospectivo na avaliação do resultado.

## Objetivo

Avaliar a aderência de Claude Code à arquitetura aprovada (Architecture
Spine) ao implementar, de forma autônoma, uma story com contrato
pré-definido por testes de referência já escritos e vermelhos.

## Ferramenta avaliada

Claude Code.

## Story medida

Story 1.2 — "Transferir valor entre duas contas existentes"
(`_bmad-output/planning-artifacts/epics.md`). Critérios de aceite completos
nesse documento.

## Ponto de partida

- Tag: `freeze/exp-01` (hash a preencher quando a tag for criada).
- Branch do experimento: `experiment/exp-01-transfer`, criada a partir da tag.

## Contexto fornecido ao agente

- `CLAUDE.md`
- `LAB-PLAN.md`
- `ARCHITECTURE-SPINE.md`
- `_bmad-output/planning-artifacts/epics.md` (Story 1.2)
- `docs/lab/baseline.md`
- Todo o repositório na branch `experiment/exp-01-transfer`, incluindo
  `TransferApiReferenceTest` já vermelho.

## Prompt exato a ser dado ao agente

```text
Leia CLAUDE.md antes de qualquer alteração.

Implemente a Story 1.2 do backlog (_bmad-output/planning-artifacts/epics.md):
transferência de valor entre duas Contas existentes.

O contrato que sua implementação precisa satisfazer já está escrito em
src/test/java/dev/agenticlab/reference/TransferApiReferenceTest.java.
Esses testes estão vermelhos porque POST /transfers ainda não existe.

Não altere nenhum arquivo listado em CLAUDE.md como protegido, incluindo
TransferApiReferenceTest e os demais testes em
src/test/java/dev/agenticlab/reference.

Ao terminar, rode ./mvnw -B verify e confirme que todos os testes estão
verdes, incluindo os que já existiam antes da sua alteração.
```

## Resultado esperado

`TransferApiReferenceTest` (2 testes) passa a verde. Os 11 testes que já
existiam na baseline (`ApplicationSmokeTest`, `AccountApiReferenceTest`,
`ArchitectureTest`) continuam verdes. Nenhum arquivo protegido por
`CLAUDE.md` é alterado.

## O que observar durante a execução

- Aderência às decisões `[ADOPTED]` do Architecture Spine (AD-1 a AD-8, em
  especial AD-2, AD-3, AD-4, AD-5, AD-6, AD-7 para esta story).
- Estrutura de pacotes criada (`transfer/web`, `transfer/service`,
  `transfer/repository`, `transfer/model`).
- Se `transfer` usa só o serviço público de `account`, sem tocar
  `account.repository` nem `account.model` (verificável também pelo
  ArchUnit já existente).
- Testes adicionais escritos pelo próprio agente (fora de `reference`).
- Quantidade e natureza de intervenções humanas necessárias.
- Tempo aproximado.
- Erros cometidos e como foram corrigidos (ou não).

## Registro pós-execução

A preencher depois da execução, conforme os campos do diário do laboratório
(LAB-PLAN.md, seção 9): resultado obtido, intervenções humanas, decisões
tomadas, o que funcionou bem, o que funcionou mal, aprendizados, próximo
experimento.
