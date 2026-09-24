# Multi-Agent Experiment 01 (MULTI-AGENT-EXP-01) — Claude Code implementa a Story 2.1 e Codex revisa antes da decisão humana

Pré-declaração escrita **antes** de acionar os agentes e fixada pela tag
`freeze/multi-agent-exp-01`. O resultado será registrado depois, em commit
separado, na seção "Registro pós-execução" (`docs/playbook.md`, H5).

Multiagente descreve o processo de desenvolvimento deste experimento, não a
aplicação: a API continua um monólito (AD-1; PRD §6, "Evolução pós-V1").

## Objetivo

Avaliar o fluxo em que Claude Code implementa a Story 2.1 e, depois, Codex
atua como reviewer independente da implementação, antes da decisão humana.

## Story

Story 2.1 — "Estornar uma transferência concluída"
(`_bmad-output/planning-artifacts/epics.md`, Epic 2; FR-19 no PRD; AD-3,
AD-4, AD-6 e AD-7 no Architecture Spine, commit `e8791c6`).

## Baseline

- Ponto de partida: `main` com os artefatos BMAD da Story 2.1 (`e8791c6`)
  mais a preparação deste experimento.
- Tag: `freeze/multi-agent-exp-01`. O hash será registrado em commit
  posterior à tag.
- Branch do experimento: `experiment/multi-agent-exp-01-reversal`, criada a
  partir da tag.
- Artefatos protegidos e modificáveis: [`baseline.md`](baseline.md), seção
  MULTI-AGENT-EXP-01 (fonte única, H1). Este documento não mantém lista
  própria.
- Estado dos testes antes do freeze: 56 testes, 49 verdes e 7 vermelhos
  (ver `baseline.md`, seção MULTI-AGENT-EXP-01).

## Contrato congelado

`src/test/java/dev/agenticlab/reference/TransferReversalReferenceTest.java`,
contra PostgreSQL real via Testcontainers (AD-8):

| Teste | Critério de aceite da Story 2.1 |
| --- | --- |
| `reversesCompletedTransfer` | AC1: estorno válido, `REVERSED`, saldos devolvidos, soma preservada; nenhuma Transferência nova (AC7) |
| `getsReversedTransfer` | AC2: consulta após estorno |
| `rejectsSecondReversalWithoutEffect` | AC3: estorno repetido, 422 `TRANSFER_ALREADY_REVERSED`, sem efeito |
| `returnsNotFoundProblemForUnknownTransfer` | AC4: 404 `TRANSFER_NOT_FOUND` |
| `rejectsReversalWhenDestinationLacksFunds` | AC5: 422 `INSUFFICIENT_FUNDS`, Transferência `COMPLETED`, saldos inalterados |
| `concurrentReversalsTakeEffectExactlyOnce` | AC6: exatamente um estorno efetivo sob concorrência; nenhuma Transferência nova (AC7) |
| `statusCheckConstraintRejectsUnknownStatus` | AC8: o esquema restringe o status |

Não são verificados pelos testes de referência e ficam para a revisão
(Codex e humano): a ordem de bloqueio (Transferência antes das Contas, e
Contas em ordem crescente de id) e a atomicidade interna do estorno (AC7,
AD-3 e AD-4). A regra ArchUnit existente continua verificando que
`transfer` não acessa o `repository` nem o `model` de `account` (AD-2).

## Papéis

| Papel | Quem | Pode | Não pode |
| --- | --- | --- | --- |
| Implementador | Claude Code | Alterar os artefatos modificáveis de `baseline.md`, criar testes próprios fora de `reference/**`, executar testes | Alterar artefatos protegidos, commitar, fazer push, abrir PR, fazer merge |
| Reviewer independente | Codex | Ler o repositório no estado revisado, executar build e testes | Alterar código ou testes, criar arquivos deliberadamente, corrigir findings, commitar, fazer push, abrir PR |
| Decisor | Humano (Andrelino) | Commitar a implementação sem modificá-la, revisar, decidir correções, merge ou rejeição | — |

## Protocolo de isolamento entre implementador e reviewer

Ordem aprovada: Claude Code implementa → revisão humana independente é
registrada → Codex revisa sem conhecer essa revisão → comparação humano x
Codex → decisão humana final.

1. **Implementação.** Sessão nova de Claude Code na branch do experimento,
   sem `/resume`, com o prompt do implementador abaixo. O implementador não
   recebe nada do reviewer.
2. **Commit de implementação.** O Claude Code não faz commit. Depois da
   validação, o humano commita na branch, sem modificar nada, todas as
   alterações deixadas pelo implementador. Esse é o **commit de
   implementação**, e seu parent é o commit de `freeze/multi-agent-exp-01`.
   O hash é registrado neste documento.
3. **Revisão humana independente.** O humano revisa o commit de
   implementação e registra sua revisão neste documento, em commit próprio,
   **antes** de acionar o Codex.
4. **Revisão do Codex.** O Codex roda num worktree separado, destacado no
   commit de implementação, como no CODEX-EXP-01
   ([`04-codex-exp-01.md`](04-codex-exp-01.md)), com o prompt do reviewer
   abaixo. Nesse estado, o registro da revisão humana não existe no
   worktree.
5. **O que o reviewer não recebe:** a sessão, o relatório final ou as
   explicações do Claude Code; a revisão humana; qualquer registro
   pós-execução. Só vê o repositório no commit de implementação e no seu
   parent, incluindo as mensagens de commit.
6. **Commits posteriores.** O worktree compartilha as refs do repositório.
   O prompt do reviewer o restringe ao commit de implementação e ao seu
   parent. Qualquer acesso a commits, branches ou tags posteriores é
   registrado como possível contaminação.
7. **Comparação.** Cada finding do Codex é classificado contra a revisão
   humana registrada: coincidente, novo e procedente, ou improcedente.
8. **Findings do reviewer.** Não são aplicados automaticamente. Levar um
   finding ao implementador é decisão humana e conta como intervenção.
9. Nenhuma mensagem adicional é enviada a nenhum dos agentes durante sua
   execução, exceto aprovações de ferramentas, que são registradas.

A atomicidade interna do estorno e a ordem de bloqueio são avaliadas por
revisão estrutural do código (humana e do Codex), sem testes de referência
adicionais para esses detalhes.

## Prompts

Os dois prompts são fixados por este documento no freeze e usados sem
alteração. A única substituição permitida é `<IMPL>`, no prompt do reviewer,
pelo hash completo do commit de implementação registrado no passo 2.

### Prompt exato do implementador (Claude Code)

```text
Leia CLAUDE.md antes de qualquer alteração.

Implemente somente a Story 2.1 — Estornar uma transferência concluída
(_bmad-output/planning-artifacts/epics.md, Epic 2).

Antes de implementar, leia:
- a Story 2.1 e seus critérios de aceite;
- o Architecture Spine
  (_bmad-output/planning-artifacts/architecture/architecture-agentic-java-lab-2026-09-21/ARCHITECTURE-SPINE.md);
- CLAUDE.md;
- docs/lab/baseline.md, seção MULTI-AGENT-EXP-01, que é a baseline
  corrente.

O contrato que sua implementação precisa satisfazer já está escrito em
src/test/java/dev/agenticlab/reference/TransferReversalReferenceTest.java.

Você pode alterar somente os artefatos listados como modificáveis na seção
MULTI-AGENT-EXP-01 de docs/lab/baseline.md. Implemente o código de produção
necessário. Você pode criar seus próprios testes fora de
src/test/java/dev/agenticlab/reference, nos locais permitidos pela
baseline.

Não altere:
- os testes de referência (src/test/java/dev/agenticlab/reference/**);
- os artefatos BMAD (_bmad-output/**);
- docs/lab/baseline.md;
- nenhuma documentação;
- nenhum outro artefato protegido.

Se a story exigir algo que contradiga uma decisão [ADOPTED] do Architecture
Spine, ou exigir alterar um artefato protegido, pare e sinalize o conflito
em vez de decidir sozinho.

Ao terminar, execute ./mvnw -B verify e confirme que:
- os testes de TransferReversalReferenceTest estão verdes;
- os testes que já existiam continuam verdes;
- o build passa sem warnings.

No relatório final, liste:
- os arquivos criados e alterados;
- as decisões que você tomou e que não eram determinadas pela story, pelos
  critérios de aceite ou por uma decisão do Architecture Spine;
- o resultado de ./mvnw -B verify.

Pare depois da implementação e da validação. Não faça commit, não faça
push e não abra Pull Request.
```

### Prompt exato do reviewer (Codex)

Usado somente depois do commit de implementação e do registro da revisão
humana (passos 2 e 3), num worktree destacado no commit de implementação:

```bash
git worktree add --detach ../agentic-java-lab-multi-agent-exp-01 <IMPL>
codex --sandbox workspace-write --ask-for-approval on-request \
  -c sandbox_workspace_write.network_access=true \
  --add-dir ~/.m2 \
  -C ../agentic-java-lab-multi-agent-exp-01
```

```text
Você é um reviewer independente de código. Você não participou da
implementação que vai revisar.

Revise o commit <IMPL> (Story 2.1 — estornar uma transferência concluída),
comparando-o com o seu parent (<IMPL>^).

Escopo:
- código de produção, migrações e testes alterados ou adicionados pelo
  commit;
- leia o contexto do repositório necessário para julgar o commit, como
  CLAUDE.md, docs/lab/baseline.md (seção MULTI-AGENT-EXP-01), o
  Architecture Spine (_bmad-output/planning-artifacts/architecture/), a
  Story 2.1 em _bmad-output/planning-artifacts/epics.md, os testes de
  referência em src/test/java/dev/agenticlab/reference/ e o código
  existente;
- considere somente o estado do repositório em <IMPL> e em seu parent;
  não consulte commits, branches ou tags posteriores.

CLAUDE.md contém instruções para o agente implementador; aqui você não
implementa nada, apenas as usa como contexto.

Foco:
- bugs e comportamento incorreto;
- concorrência: estorno duplicado, deadlock e ordem de bloqueio;
- atomicidade: devolução dos saldos e mudança de status numa única
  transação;
- regressões;
- violações do contrato (story, critérios de aceite, testes de referência)
  ou das decisões arquiteturais;
- efetividade dos testes escritos pelo implementador fora de
  src/test/java/dev/agenticlab/reference/: se cada teste de fato exercita
  o cenário que declara cobrir e se acrescenta valor.

Você pode executar comandos de validação e testes, como ./mvnw -B verify,
se considerar necessário para a revisão. Artefatos gerados pelo build,
como target/, são aceitos.

Restrições:
- não altere código de produção nem testes;
- não crie arquivos deliberadamente;
- não corrija os findings;
- não crie commits, não faça push e não abra Pull Request.

Formato da resposta:
1. Findings concretos, ordenados por severidade (alta, média, baixa). Para
   cada um: arquivo e trecho, descrição do problema, evidência (código,
   contrato ou decisão arquitetural violada) e impacto.
2. Sugestões opcionais, separadas dos findings, sem misturar os dois.
3. Se não encontrar problemas relevantes, diga isso explicitamente.
   A ausência de findings é um resultado válido; não invente problemas.
```

## Critérios de sucesso

**Implementação**
- Os 7 testes de `TransferReversalReferenceTest` ficam verdes.
- Os 49 testes anteriores continuam verdes.
- `./mvnw -B verify` passa, sem warnings (`-Xlint:all -Werror`).
- `git diff freeze/multi-agent-exp-01..<commit> --
  src/test/java/dev/agenticlab/reference` sai vazio, e nenhum outro
  artefato protegido é alterado.
- A implementação segue as decisões aprovadas: bloqueio da Transferência
  antes das Contas, uma única transação, saldo alterado só por `account`,
  `REVERSED` na própria Transferência, restrição de status por migração
  Flyway, sem nova camada, componente ou abstração desnecessária.

**Fluxo multiagente**
- O Codex produz uma revisão sem alterar o repositório.
- Cada finding do Codex é classificado contra a revisão humana
  independente.
- A decisão humana (merge, correção ou rejeição) é registrada, com o papel
  que a revisão do Codex teve nela.

## Registro de intervenções e tempo (H6)

**Regra de classificação:** a mesma do CC-EXP-02 e do CC-EXP-03.
Aprovações normais de ferramentas não contam como intervenção. Conta como
intervenção quando o humano fornece informação, corrige, redireciona ou
influencia tecnicamente a solução. Taxonomia do PRD: esclarecimento,
correção, aprovação ou assumir o trabalho.

**Evidência:** exportar a sessão do Claude Code com `/export` e preservar a
sessão do Codex em `docs/lab/evidence/multi-agent-exp-01/`, revisadas quanto
a segredos (NFR-1), em commit próprio.

### Implementador (Claude Code)

- Versão:
- Modelo:
- Início:
- Fim:
- Commit de implementação (feito pelo humano):

| Horário | O que o humano disse ou fez | Classificação |
| --- | --- | --- |
| | | |

### Reviewer (Codex)

- Versão:
- Modelo:
- Início:
- Fim:
- Commit revisado:

| Horário | O que o humano disse ou fez | Classificação |
| --- | --- | --- |
| | | |

## Limitações aceitas antes da execução

- Os documentos do laboratório, incluindo este, o playbook e o
  CODEX-EXP-01, estão no repositório. O implementador pode saber que será
  revisado, e o reviewer pode conhecer o formato das revisões anteriores.
- A ordem de bloqueio e a atomicidade interna não são verificadas pelos
  testes de referência; dependem da revisão.
- O teste de concorrência depende do escalonamento real de threads e
  transações.
- Uma única execução; o avaliador também desenhou o experimento e os
  testes de referência.

## Registro pós-execução

A preencher após a execução.

### Implementação

### Revisão humana independente

Revisão realizada após o commit de implementação `9a37a93` e antes de executar o Codex.

#### Código de produção

Nenhum finding identificado na revisão humana.

A implementação segue o fluxo arquitetural aprovado: bloqueia a Transferência antes das Contas, verifica o status após o bloqueio, reutiliza `AccountService.transferBalance` com origem e destino invertidos, mantém saldo e mudança para `REVERSED` na mesma transação e não cria nova Transferência.

#### Finding H1 — LOW — efetividade do teste de concorrência

Arquivo: `src/test/java/dev/agenticlab/transfer/TransferReversalApiTest.java`

Teste: `concurrentReversalsAndTransfersInBothDirectionsDoNotDeadlock`

O teste exerce concorrência real e pode detectar falhas observáveis, timeouts e erros durante a execução. Porém, uma execução verde demonstra ausência de deadlock observável naquela execução, não prova geral de impossibilidade de deadlock.

Classificação: qualidade/precisão do teste, não bug de produção.

#### Resultado

- Findings de produção: 0
- Findings de testes: 1 LOW
- Correções antes da revisão do Codex: nenhuma

### Revisão do Codex

O Codex revisou o commit `9a37a93` exclusivamente contra seu parent direto `1c03d17`, em worktree detached, sem acesso à revisão humana posterior.

Resultado:
- findings de produção: 0;
- não identificou violações de arquitetura, atomicidade, ordem de locks ou contrato HTTP;
- observou que o teste concorrente é útil, mas não comprova deterministicamente ausência de deadlock;
- testes executados pelo Codex: nenhum;
- revisão exclusivamente estática;
- não editou ou criou arquivos e não realizou commit, push ou PR.

O Codex utilizou somente operações de leitura do repositório (`git diff`, `git ls-tree` e `git show`).

### Comparação dos findings

A revisão humana e a revisão do Codex convergiram.

- Humano: 0 findings de produção e 1 LOW relacionado à precisão/efetividade do teste de concorrência.
- Codex: 0 findings de produção e a mesma ressalva sobre o teste concorrente.
- Findings coincidentes: H1 — limitação do teste para demonstrar ausência geral de deadlock.
- Findings somente humanos: nenhum.
- Findings somente Codex: nenhum.
- Findings considerados não procedentes: nenhum.

O resultado fornece evidência de que, neste experimento, a revisão independente do Codex chegou à mesma conclusão técnica da revisão humana sem ter acesso a ela.

### Decisão humana

**ACEITO SEM CORREÇÕES.**

A implementação `9a37a93` foi aceita como produzida pelo Claude Code.

Justificativa:
- 62 testes passaram na execução do implementador;
- nenhum finding de produção foi identificado na revisão humana;
- nenhum finding de produção foi identificado pelo Codex;
- humano e Codex convergiram sobre a única ressalva LOW relacionada à capacidade do teste concorrente de demonstrar ausência geral de deadlock;
- a ressalva não representa defeito funcional nem exige alteração da implementação antes do merge.

Nenhuma correção de código foi solicitada após as revisões.

### Conclusão
