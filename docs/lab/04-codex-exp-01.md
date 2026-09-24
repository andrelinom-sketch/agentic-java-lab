# Codex Experiment 01 (CODEX-EXP-01) — Codex revisa, de forma independente, o commit do Devin da Story 1.5

Pré-declaração escrita **antes** de acionar o Codex. O resultado será
registrado depois, em commit separado, na seção "Registro pós-execução"
(`docs/playbook.md`, H5).

## Objetivo

Avaliar se o Codex, sem ter participado da implementação, identifica
problemas relevantes no código de produção e nos testes produzidos pelo
Devin no commit `63a343d`.

## Objeto da revisão

- Commit: `63a343d1de55906d60b9eba4810ad97cb490cab4`
  (`feat(transfer): consultar transferência pelo identificador (Story 1.5)`),
  produzido no DEVIN-EXP-01 ([`03-devin-exp-01.md`](03-devin-exp-01.md)).
- Parent: `68870d2` (`freeze/devin-exp-01`).
- Escopo: produção **e** testes do commit (5 arquivos, +181 / -0).

## Baseline atual

- `main` em `1ba7aa0`.

## Ferramenta avaliada

- Codex CLI `0.156.1`.
- Modelo observado na inicialização: GPT-6-Astra.
- Primeira utilização do Codex neste laboratório.
- Papel: **reviewer independente**.

## Restrições do reviewer

O Codex **não pode**:

- alterar código de produção ou testes;
- criar arquivos deliberadamente;
- corrigir findings;
- criar commits, fazer push ou abrir PR.

O Codex **pode**, se considerar necessário para a revisão, executar
comandos de validação e testes, como `./mvnw -B verify`. A revisão não se
limita à leitura estática.

Ferramentas de build podem gerar artefatos locais, como `target/` (ignorado
pelo `.gitignore`). Esses artefatos não contam como alteração deliberada.
Ao final, `git status` no worktree não deve mostrar arquivos rastreados
alterados nem arquivos novos fora dos gerados pelo build.

O Codex **deve**:

- revisar produção e testes do commit `63a343d`, comparando com o parent;
- apresentar findings concretos, priorizados por severidade, apontando
  arquivo e trecho, com evidência;
- separar findings concretos de sugestões opcionais;
- declarar explicitamente quando não encontrar problemas relevantes.

A ausência de findings é um resultado válido.

## Isolamento da revisão (sem contaminação)

As conclusões anteriores sobre a implementação do Devin **não** são
reveladas ao Codex antes da revisão. Em `main` (`1ba7aa0`), elas estão em
`docs/lab/03-devin-exp-01.md` ("Registro pós-execução", commit `81d15a7`) e
em `docs/playbook.md` (H3, H6 e H7, commit `1ba7aa0`). Este documento também
fica fora do alcance do Codex.

Por isso, a revisão roda num worktree separado, destacado em `63a343d`.
Nesse estado, `03-devin-exp-01.md` contém só a pré-declaração, e o playbook
ainda não menciona o DEVIN-EXP-01:

```bash
git worktree add --detach ../agentic-java-lab-codex-exp-01 63a343d
codex --sandbox workspace-write --ask-for-approval on-request \
  -c sandbox_workspace_write.network_access=true \
  --add-dir ~/.m2 \
  -C ../agentic-java-lab-codex-exp-01
```

- `--sandbox workspace-write` permite que o build escreva `target/` no
  worktree. As proibições de alterar código e testes vêm do prompt, não do
  sandbox.
- `network_access=true` e `--add-dir ~/.m2` permitem que o Maven resolva e
  armazene dependências.
- Os testes usam Testcontainers e precisam do Docker. Se o sandbox bloquear
  o acesso ao Docker, o Codex pede aprovação (`on-request`). O humano
  aprova só comandos de build e teste, e o pedido e a resposta são
  registrados.
- O worktree compartilha as refs do repositório, então `git log main`
  alcançaria os commits posteriores. O prompt restringe a leitura ao commit
  e ao seu parent. Qualquer acesso a commits posteriores será registrado
  como possível contaminação.
- Nenhuma mensagem adicional é enviada ao Codex durante a revisão, exceto
  se ele pedir aprovação; nesse caso, o pedido e a resposta são registrados.

## Prompt exato a ser dado ao Codex

```text
Você é um reviewer independente de código. Você não participou da
implementação que vai revisar.

Revise o commit 63a343d (Story 1.5 — consultar uma transferência pelo
identificador), comparando-o com o seu parent (63a343d^).

Escopo:
- código de produção e testes alterados ou adicionados pelo commit;
- leia o contexto do repositório necessário para julgar o commit, como
  LAB-PLAN.md, CLAUDE.md, docs/lab/baseline.md, o Architecture Spine
  (_bmad-output/planning-artifacts/architecture/), a Story 1.5 em
  _bmad-output/planning-artifacts/epics.md, os testes de referência em
  src/test/java/dev/agenticlab/reference/ e o código existente.
- considere somente o estado do repositório em 63a343d e em seu parent;
  não consulte commits, branches ou tags posteriores.

CLAUDE.md contém instruções para o agente implementador; aqui você não
implementa nada, apenas as usa como contexto.

Foco:
- bugs e comportamento incorreto;
- regressões;
- violações do contrato (story, critérios de aceite, testes de referência)
  ou das decisões arquiteturais;
- qualidade e efetividade dos testes adicionados: se cada teste de fato
  exercita o cenário que declara cobrir e se acrescenta valor.

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

## Resultado esperado

Uma lista de findings priorizados com evidência, ou a declaração explícita
de que não há problemas relevantes, sem alteração de código ou testes.

## Referência para avaliação

Depois da revisão, o resultado do Codex será comparado com a revisão humana
independente registrada em [`03-devin-exp-01.md`](03-devin-exp-01.md),
"Revisão humana independente". Cada finding será classificado como
coincidente com a revisão humana, novo e procedente, ou improcedente.

## Registro pós-execução

### Execução

- Codex CLI `0.156.1`, modelo GPT-6-Astra.
- Revisão no worktree isolado `../agentic-java-lab-codex-exp-01`, em
  detached HEAD `63a343d`.
- Uma única interação com o Codex: o prompt pré-declarado acima.
- Nenhuma intervenção humana durante a revisão.
- Nenhuma solicitação de permissão; nenhuma aprovação concedida.
- O Codex não executou testes, por decisão própria (o prompt permitia).
  Declarou revisão estática restrita a `63a343d` e ao seu parent.
- Ao final, `git status -sb` no worktree: `## HEAD (sem ramo)`, sem
  alterações.
- `main` permaneceu limpa e sincronizada com `origin/main`.
- Não há transcript nem exportação da sessão no repositório
  (`docs/lab/evidence/` não tem diretório do CODEX-EXP-01).

### Resposta do Codex

1. **Finding — severidade baixa.**
   `src/test/java/dev/agenticlab/transfer/TransferQueryApiTest.java`,
   método `queryDoesNotChangeBalances`. As duas chamadas a
   `getTransfer(transferId)` descartam suas respostas, e o teste verifica
   apenas os saldos depois. Os GETs desse cenário poderiam retornar erro e o
   teste continuaria verde. Impacto apontado: falso positivo localizado na
   cobertura de ausência de efeitos colaterais.
2. **Sugestões opcionais:** nenhuma.
3. Nenhum bug de produção, regressão ou violação arquitetural relevante
   identificado. Consulta por id, DTO e `404`/`TRANSFER_NOT_FOUND`
   considerados coerentes com o contrato.

### Comparação com a revisão humana independente

Referência: [`03-devin-exp-01.md`](03-devin-exp-01.md), "Revisão humana
independente".

| Finding do Codex | Classificação | Evidência |
| --- | --- | --- |
| `queryDoesNotChangeBalances` descarta as respostas dos GETs | **Novo e procedente** | Ver abaixo |

- **Procedente:** em `63a343d`, as duas chamadas a `getTransfer` nesse
  método não têm o retorno atribuído nem verificado. As únicas assertions
  são sobre os saldos, obtidos por `GET /accounts/{id}`. O impacto é
  limitado: o status `200` do `GET /transfers/{id}` é verificado em outros
  testes do mesmo commit e na referência.
- **Novo:** a revisão humana registrada não menciona esse teste nem esse
  problema. Ela registra "sobreposição ou baixo valor em 2 dos 6 testes"
  sem identificar quais. O registro não permite afirmar nem excluir que
  `queryDoesNotChangeBalances` estivesse entre eles. A ressalva sobre
  assertions sem verificação prévia de body não nulo não corresponde a este
  finding: o método não faz assertion sobre o body dos GETs.

Ressalvas da revisão humana que o Codex **não** apontou:

- sobreposição ou baixo valor em 2 dos 6 testes adicionais;
- assertions sobre o body sem verificação prévia de que ele não é nulo.

Nas conclusões sobre produção e arquitetura, as duas revisões convergem:
nenhuma encontrou bug de produção nem violação arquitetural, e ambas
consideraram `TRANSFER_NOT_FOUND` coerente com o contrato.

Nenhum finding foi classificado como improcedente.

### O que o experimento demonstra

Nesta execução, o Codex, isolado das conclusões anteriores, revisou o
commit sem alterar o repositório e apontou um problema real num teste do
agente: um teste verde que não verifica parte do que executa. Esse problema
não consta da revisão humana registrada.

### O que o experimento não demonstra

- Que o Codex encontre problemas que um humano não encontraria; a revisão
  humana registrada não identifica os testes de baixo valor, então a
  sobreposição entre as duas não pode ser medida por completo.
- Que a revisão estática baste; o Codex não executou testes.
- Que o Codex detecte problemas de produção; o commit revisado não tinha
  nenhum conhecido.
- Qualquer comparação entre agentes ou modelos.

### Limitações

- Uma única execução, um único commit revisado, pequeno (+181 / -0).
- O isolamento quanto a commits posteriores se apoia na declaração do
  próprio Codex e no relato do avaliador, sem artefato da sessão (H6).
- O Codex não cobriu as duas ressalvas da revisão humana; não se sabe se
  isso decorre do modelo, do prompt ou da não execução de testes.
- A classificação foi feita por um único avaliador, que também desenhou o
  experimento e fez a revisão humana de referência.
- O custo não foi registrado.

### Conclusão

Evidência de uma única execução, sem generalização: uma revisão
independente por outro agente acrescentou um finding de teste procedente,
de severidade baixa, ao que a revisão humana registrou, e não apontou as
ressalvas que a revisão humana registrou. Uma revisão não substituiu a
outra neste caso.
