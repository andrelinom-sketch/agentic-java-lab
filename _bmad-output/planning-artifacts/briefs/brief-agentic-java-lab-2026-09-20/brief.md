---
title: "Product Brief: Agentic Java Lab"
status: draft
created: 2026-09-20
updated: 2026-09-21
---

# Product Brief: Agentic Java Lab

## Resumo Executivo

O Agentic Java Lab é um repositório público em que uma pequena API de contas e transferências (Java 21, Spring Boot) é planejada, implementada e revisada com agentes de IA sob supervisão humana. O produto não é a API. É o **registro auditável de como humanos e agentes chegaram à solução**: contexto fornecido, decisões, intervenções, falhas e correções, e o método para observá-los.

O laboratório é um estudo de caso, sem pretensão estatística, e nunca um ranking de ferramentas. Ele parte da prática de um Tech Lead que delega, supervisiona e mede.

## O Problema

Benchmarks de capacidade e estudos em escala sobre PRs de agentes já existem, e o laboratório não os substitui. O que costuma faltar a um Tech Lead que decide como incorporar agentes é um registro **pequeno, replicável e auditável** de uma mesma especificação atravessando níveis de autonomia: que contexto foi necessário, onde o agente errou, quanta intervenção humana foi preciso e quais decisões continuaram humanas.

_[A verificar: esta afirmação depende de fontes abertas e conferidas antes da publicação. Ver addendum.]_

## A Solução

O laboratório se organiza em três camadas.

**Objeto de estudo.** A API, no menor tamanho que sustente os experimentos, como ambiente controlado para validar o método.

**Instrumentos de observação e controle.**
- Uma cadeia de planejamento feita com o BMAD (brief, PRD, arquitetura, ADRs, backlog pequeno). A aprovação humana desse plano faz parte do experimento.
- Uma rubrica versionada, com critérios congelados antes de cada experimento. O Experimento 01 (BMAD) é uma exceção declarada: parte dos critérios surgiu durante a execução, o que é uma limitação metodológica.
- Um CI comum a todos os agentes e arquivos de contexto versionados. Eles são criados sob supervisão humana e tratados como baseline: nenhum agente sob avaliação pode enfraquecê-los silenciosamente.

**Saídas para o leitor.**
- Repositório público legível, com código, histórico, PRs e ADRs.
- Diário em `docs/lab/`, que interpreta a evidência e aponta para ela sem copiá-la.
- Playbook de adoção evolutivo, atualizado por marcos. Cada recomendação indica os experimentos de origem, suas limitações e o grau de sustentação.

**Capacidade resultante:** rastrear qualquer conclusão ou recomendação até as evidências que a sustentam.

## O Que Torna Isto Diferente

O diferencial é disciplina e transparência, não tecnologia: critérios prévios, evidência que mora onde nasce e rastreabilidade ponta a ponta.

**Compromissos de transparência.** Publicar erros de agentes, violações de arquitetura, intervenções humanas, abordagens abandonadas, prompts que deram mau resultado, decisões do autor que se mostrarem inadequadas e hipóteses não confirmadas. Não publicar transcrições completas, credenciais, dados pessoais nem caminhos locais desnecessários.

## Quem Este Projeto Serve

**Primário:** Tech Leads e Arquitetos de Software que avaliam como incorporar agentes ao processo real de engenharia. **Secundário:** desenvolvedores interessados em agentes. **O primeiro usuário é o autor**, no papel de Tech Lead e Arquiteto. Recrutadores e entrevistadores técnicos são beneficiários indiretos e não orientam o desenho.

## Critérios de Sucesso (V1: 20/09/2026 a 18/10/2026)

**Sucesso:**
1. A fatia vertical foi percorrida até a primeira atualização do playbook.
2. As classificações da rubrica (intervenções, erros, decisões) são consistentes entre repetições.
3. Alguém de fora, ou um agente em contexto novo, chega de uma recomendação do playbook às evidências que a sustentam, sem ajuda.

**Fracasso:**
- a rubrica se mostrou inadequada a ponto de impedir uma avaliação coerente do experimento (a evolução da rubrica não é fracasso);
- o custo de registrar superou o custo do experimento;
- algum instrumento (CI, testes de referência, critérios de aceite) foi alterado sem registro.

Se a fatia não estiver completa em 18/10/2026, o resultado registrado é **"V1 incompleta"**. O prazo não será estendido para transformar o resultado em sucesso.

## Escopo

**V1 (4 semanas, contadas desde o início do BMAD):** BMAD → uma story pequena, mas não trivial (regra de negócio, persistência e testes) → Claude Code → CI comum → revisão humana → registro completo → primeira atualização do playbook. A escolha da story fica para o PRD e o backlog.

**Fora da V1:** Devin, Codex como revisor, multiagente, legado e Strangler Fig, Kafka, resiliência distribuída, observabilidade avançada, UI, autenticação e cloud.

**Em aberto:**
- mesma story ou stories equivalentes entre agentes (decidir quando o segundo agente entrar);
- licença do repositório (decidir antes da publicação).

## Visão

Em 2 a 3 anos, um método prático e reutilizável para experimentar, avaliar e incorporar agentes ao ciclo de engenharia de software, sustentado por vários ciclos documentados e por um playbook evoluído a partir das evidências. Rubrica, modelo de diário e método devem poder ser reutilizados por terceiros.

A modernização de sistemas Java (incluindo Strangler Fig, com as limitações de um legado sintético declaradas) é objetivo declarado do laboratório completo, mas fica fora do sucesso da V1.
