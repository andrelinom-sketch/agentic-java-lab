# CLAUDE.md — guardrails para Claude Code neste repositório

Este arquivo é o contexto mínimo para qualquer sessão de Claude Code que
implemente uma story neste laboratório. Leia também, nesta ordem:

1. `LAB-PLAN.md` — propósito do laboratório e regras gerais para agentes.
2. `_bmad-output/planning-artifacts/architecture/architecture-agentic-java-lab-2026-09-21/ARCHITECTURE-SPINE.md`
   — decisões arquiteturais (AD-1 a AD-10), todas `[ADOPTED]`.
3. `_bmad-output/planning-artifacts/epics.md` — a story atribuída a você, com
   seus critérios de aceite.
4. `docs/lab/baseline.md` — o que está congelado nesta baseline e o que é
   especificação executável a ser satisfeita.

## O que você deve fazer

Implementar a story atribuída para que os testes de referência relevantes
(pacote `src/test/java/dev/agenticlab/reference`) passem, seguindo as
decisões do Architecture Spine.

## O que você pode e não pode alterar

A única fonte autoritativa dos artefatos **protegidos** e **modificáveis**
é `docs/lab/baseline.md`, na seção do experimento corrente. Este arquivo
não mantém lista própria.

Antes de alterar qualquer arquivo, confira em qual das duas listas ele
está. Os testes de referência (`src/test/java/dev/agenticlab/reference/**`),
inclusive os que ainda estão vermelhos, são a especificação, não um
obstáculo a contornar.

## Antes de considerar a tarefa concluída

Execute `./mvnw -B verify` e confirme que:

- os testes de referência da sua story estão verdes;
- nenhum teste de referência anterior quebrou;
- o build passa sem warnings (`-Xlint:all -Werror`).

## Se algo conflitar

Se a story pedir algo que contradiz uma decisão `[ADOPTED]` do Architecture
Spine, ou exigir alterar um artefato protegido ou que não esteja na lista de
modificáveis de `docs/lab/baseline.md`, **não decida sozinho**: pare e
sinalize o conflito.
