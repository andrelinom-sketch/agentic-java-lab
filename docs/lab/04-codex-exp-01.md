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

A preencher após a execução.
