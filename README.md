# Agentic Java Lab

Laboratório prático para estudar **Agentic Software Engineering aplicado a Java e Arquitetura de Software**.

O objetivo não é apenas construir uma aplicação, mas experimentar de forma controlada como diferentes agentes de IA podem participar do ciclo de desenvolvimento:

```text
Ideia
  ↓
BMAD
  ↓
PRD
  ↓
Arquitetura
  ↓
Stories
  ↓
Coding Agent
  ↓
Testes
  ↓
AI Code Review
  ↓
CI
  ↓
Human Review
```

Durante o laboratório serão utilizados progressivamente:

- BMAD Method
- Claude Code
- Devin
- OpenAI Codex
- desenvolvimento multiagente

As ferramentas **não serão introduzidas todas ao mesmo tempo**.

Cada etapa será estudada separadamente para entender seus benefícios, limitações e impacto no processo de engenharia.

---

## Objetivo do laboratório

Responder, através de experimentos práticos, perguntas como:

- Como transformar uma ideia em requisitos estruturados usando IA?
- Quanto contexto um coding agent precisa?
- Como definir guardrails para agentes?
- Um agente consegue respeitar uma arquitetura existente?
- Qual a diferença entre Claude Code e Devin na execução de uma story?
- Um segundo agente encontra problemas que o implementador não encontrou?
- Code review realizado por outro modelo melhora o resultado?
- Quando desenvolvimento multiagente realmente ajuda?
- Quais decisões devem continuar sendo humanas?
- Como aplicar agentes de IA na modernização de sistemas Java?

---

# Projeto utilizado

O laboratório utiliza uma pequena API de **contas e transferências bancárias**.

Funcionalidades iniciais:

```text
POST /accounts
POST /transfers
GET /transfers/{id}
```

Exemplo:

```text
Conta A
R$ 1.000
    │
    │ transferência R$ 100
    ▼
Conta B
R$ 500
```

Resultado:

```text
Conta A = R$ 900
Conta B = R$ 600
```

O domínio começará simples e será evoluído conforme novos conceitos forem estudados.

---

# Stack

Stack inicial:

```text
Java 21
Spring Boot
Maven
PostgreSQL
Docker
Docker Compose
JUnit
Testcontainers
Git
GitHub
```

Tecnologias como Kafka, observabilidade avançada, arquitetura distribuída e modernização de legado serão introduzidas posteriormente.

---

# Antes de começar

Prepare o ambiente seguindo:

```text
docs/SETUP.md
```

O setup documenta a instalação e configuração de:

```text
Git
Java 21
Maven
Docker
Docker Compose
NVM
Node.js
npm
GitHub CLI
```

Depois de preparar a máquina, execute:

```bash
./scripts/check-environment.sh
```

O ambiente deverá apresentar as principais ferramentas como `OK`.

---

# Clonando o laboratório

```bash
git clone https://github.com/andrelinom-sketch/agentic-java-lab.git
cd agentic-java-lab
```

Configure a versão de Node utilizada pelo projeto:

```bash
nvm install
nvm use
```

Valide o ambiente:

```bash
./scripts/check-environment.sh
```

Se alguma ferramenta estiver ausente, consulte:

```text
docs/SETUP.md
```

---

# Como estudar este laboratório

Este projeto deve ser executado **sequencialmente**.

Não pule diretamente para multiagente, Kafka ou arquiteturas distribuídas.

A sequência planejada é:

```text
Foundation
    ↓
BMAD
    ↓
Claude Code
    ↓
Devin
    ↓
Codex Review
    ↓
Multiagente
    ↓
Modernização
    ↓
Strangler Fig
    ↓
Arquitetura Hexagonal
    ↓
Eventos / Kafka
    ↓
Resiliência
    ↓
Observabilidade
```

Cada etapa deve produzir aprendizado documentado antes da próxima.

---

# Fase 1 — Foundation

Preparar um ambiente reproduzível.

Objetivos:

- configurar Git;
- Java 21;
- Maven;
- Docker;
- Docker Compose;
- Node;
- NVM;
- GitHub;
- documentação do ambiente.

Status:

```text
CONCLUÍDO
```

---

# Fase 2 — BMAD

Utilizar BMAD para transformar a ideia inicial em engenharia estruturada.

Fluxo:

```text
Ideia
  ↓
Análise
  ↓
PRD
  ↓
Arquitetura
  ↓
ADRs
  ↓
Épicos
  ↓
Stories
```

Objetivo inicial:

Criar apenas **3 a 5 stories**.

O objetivo não é gerar um backlog enorme, mas aprender o processo.

---

# Fase 3 — Claude Code

Claude Code será utilizado inicialmente como coding agent.

Fluxo:

```text
Story
  ↓
Claude Code
  ↓
Implementação
  ↓
Testes
  ↓
Pull Request
  ↓
Human Review
```

Serão avaliados:

- aderência à arquitetura;
- qualidade do código;
- testes;
- alterações desnecessárias;
- quantidade de intervenção humana;
- necessidade de contexto e guardrails.

---

# Fase 4 — Devin

Uma story pequena será entregue ao Devin com maior autonomia.

O objetivo será comparar:

```text
Claude Code
     VS
Devin
```

Critérios observados:

- autonomia;
- qualidade;
- aderência arquitetural;
- capacidade de corrigir erros;
- testes;
- necessidade de intervenção;
- qualidade do Pull Request;
- tempo e custo quando aplicável.

---

# Fase 5 — Codex como reviewer

Introduzir separação entre agente implementador e agente revisor.

Exemplo:

```text
Claude Code / Devin
        │
        ▼
   Implementação
        │
        ▼
      Codex
    Code Review
        │
        ▼
    Correções
        │
        ▼
        CI
        │
        ▼
 Human Review
```

O reviewer deverá procurar:

- bugs;
- violações arquiteturais;
- problemas transacionais;
- concorrência;
- segurança;
- casos extremos;
- testes ausentes;
- complexidade desnecessária.

---

# Fase 6 — Multiagente

Somente depois das fases anteriores.

Arquitetura experimental:

```text
                    HUMANO
                       │
                       ▼
                 BMAD / Architect
                       │
                       ▼
                    Stories
                       │
              ┌────────┴────────┐
              ▼                 ▼
        Claude Code           Devin
              │                 │
              └────────┬────────┘
                       ▼
                     Codex
                    Reviewer
                       │
                       ▼
                       CI
                       │
                       ▼
                  Human Review
```

O objetivo não é utilizar o maior número possível de agentes.

A pergunta principal é:

> Em quais situações múltiplos agentes realmente melhoram o processo de engenharia?

---

# Evolução arquitetural

Depois da aplicação básica, o laboratório introduzirá cenários progressivamente mais próximos de sistemas reais.

Entre eles:

```text
Sistema legado
      ↓
Strangler Fig
      ↓
Arquitetura Hexagonal
      ↓
Processamento assíncrono
      ↓
Kafka
      ↓
Retry
      ↓
Timeout
      ↓
Circuit Breaker
      ↓
Idempotência
      ↓
Eventual Consistency
      ↓
DLQ
      ↓
Observabilidade
      ↓
OpenTelemetry
```

Cada conceito deverá existir por uma razão arquitetural concreta.

---

# Diário do laboratório

Os experimentos serão registrados em:

```text
docs/lab/
```

Estrutura planejada:

```text
docs/lab/

01-bmad.md
02-claude-code.md
03-devin.md
04-codex-review.md
05-multi-agent.md
```

Cada experimento deverá registrar, quando aplicável:

```text
Objetivo
Ferramenta
Prompt
Contexto fornecido
Resultado esperado
Resultado obtido
Intervenções humanas
Erros do agente
Decisões
Tempo aproximado
O que funcionou
O que não funcionou
Aprendizados
Próximo experimento
```

---

# Documentação

## README.md

Você está aqui.

É a porta de entrada para quem deseja executar ou estudar o laboratório.

## LAB-PLAN.md

Documento mestre do projeto.

Contém:

- estratégia completa;
- roadmap;
- regras para agentes;
- decisões de aprendizado;
- arquitetura futura;
- estado do laboratório.

Agentes de IA devem ler esse documento antes de executar tarefas relevantes.

## docs/SETUP.md

Procedimento para preparar uma máquina para executar o laboratório.

## docs/lab/

Diário dos experimentos.

## docs/adr/

Decisões arquiteturais relevantes serão registradas através de ADRs.

---

# Fluxo Git

Mesmo sendo um laboratório, será utilizado um fluxo próximo de um projeto real.

```text
Story
  ↓
Branch
  ↓
Implementação
  ↓
Testes
  ↓
Commit
  ↓
Push
  ↓
Pull Request
  ↓
Code Review
  ↓
Merge
```

Exemplos de branches:

```text
feature/story-001
feature/story-002
feature/story-003
```

Isso permitirá posteriormente que agentes trabalhem sobre branches e Pull Requests reais.

---

# Papel humano

A IA não toma a decisão final do projeto.

O participante do laboratório assume os papéis de:

```text
Solution Architect
Tech Lead
Reviewer
Product Owner
```

Os agentes podem:

```text
analisar
propor
implementar
testar
revisar
documentar
```

Decisões arquiteturais significativas continuam sob supervisão humana.

---

# Progresso

- [x] Criar repositório
- [x] Criar plano mestre
- [x] Configurar Git/GitHub
- [x] Configurar Java 21
- [x] Configurar Maven
- [x] Configurar Docker
- [x] Configurar Docker Compose
- [x] Configurar Node.js/NVM
- [x] Criar setup reproduzível
- [x] Criar verificador de ambiente
- [ ] Configurar BMAD
- [ ] Criar PRD
- [ ] Definir arquitetura inicial
- [ ] Criar ADRs iniciais
- [ ] Criar 3–5 stories
- [ ] Primeiro experimento com Claude Code
- [ ] Experimento com Devin
- [ ] Code review com Codex
- [ ] Experimento multiagente
- [ ] Modernização com Strangler Fig
- [ ] Arquitetura orientada a eventos
- [ ] Observabilidade

---

# Estado atual

```text
Milestone: Foundation + BMAD

Foundation:
CONCLUÍDA

Fase atual:
BMAD

Próximo objetivo:
Configurar BMAD e iniciar a criação do PRD.
```

---

# Princípio do laboratório

Este projeto não busca demonstrar quantas ferramentas de IA podem ser utilizadas simultaneamente.

O objetivo é compreender:

> Como utilizar agentes de IA de maneira disciplinada dentro de engenharia e arquitetura de software, mantendo qualidade, rastreabilidade, decisões arquiteturais explícitas e supervisão humana.

Para o contexto completo do laboratório, leia:

```text
LAB-PLAN.md
```
