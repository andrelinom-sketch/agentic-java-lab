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

**Resultado inicial (freeze/exp-01):** 11 testes verdes (`ApplicationSmokeTest`,
`AccountApiReferenceTest`, `ArchitectureTest`) + 2 vermelhos
(`TransferApiReferenceTest`), conforme esperado.

**Resultado final:** `./mvnw -B verify` → `BUILD SUCCESS`,
`Tests run: 13, Failures: 0, Errors: 0, Skipped: 0`. Os 2 testes de
`TransferApiReferenceTest` passaram a verdes; os 11 testes anteriores
continuaram verdes, sem alteração.

**Intervenções humanas durante a implementação:** nenhuma. O build passou
na primeira tentativa, sem iteração de tentativa-erro.

**Arquivos congelados:** nenhum foi alterado (confirmado via `git status
--short` contra a lista de `CLAUDE.md`).

**Decisões autônomas/inferidas identificadas na revisão humana:**

- Uso de `Instant`/`TIMESTAMPTZ` para `createdAt`, inferido de "ISO-8601 UTC"
  (Consistency Conventions do spine), sem AD explícito sobre o tipo Java.
- `@NotNull` nos três campos de `CreateTransferRequest`, além do mínimo
  literalmente pedido pela Story 1.2.
- `FOREIGN KEY` de `transfer` para `account` na migration `V2`, não pedida
  por nenhum AD ou AC.
- Estilo `debit`/`credit` como métodos de domínio na entidade `Account`, em
  vez de um setter genérico.
- Forma de implementar o lock pessimista (`@Lock` + `@Query` JPQL explícita
  em vez de anotação direta sobre `findById`).
- Constante `Transfer.STATUS_COMPLETED` como texto fixo, em vez de enum.

**Limitações conhecidas do resultado (aceitas, não corrigidas neste
experimento):**

- Saldo insuficiente ainda não é rejeitado: a implementação atual pode
  produzir saldo negativo. Essa regra pertence à Story 1.3.
- `GET /transfers/{id}` não existe ainda; pertence à Story 1.5.
- Proteção de concorrência (múltiplas threads) e `CHECK (balance >= 0)` no
  banco pertencem à Story 1.4.

**Tempo aproximado:** 15–20 minutos, da leitura do prompt pré-declarado até
`BUILD SUCCESS`.

**Resultado da revisão humana:** implementação aceita dentro do escopo do
Experimento 01 (Story 1.2), com as limitações acima registradas como
conhecidas e deliberadamente adiadas para as stories seguintes.
