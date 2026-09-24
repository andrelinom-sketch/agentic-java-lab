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

Durante a V1 do laboratório foram utilizados progressivamente:

- BMAD Method
- Claude Code
- Devin
- OpenAI Codex
- desenvolvimento multiagente

As ferramentas **não foram introduzidas todas ao mesmo tempo**.

Cada etapa foi estudada separadamente para observar seus benefícios, limitações e impacto no processo de engenharia. Os resultados estão registrados em `docs/lab/` e consolidados em `docs/playbook.md`.

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

A V1 foi executada de forma incremental. Para compreender a evolução do
laboratório, a sequência principal é:

1. Foundation
2. BMAD
3. Claude Code
4. Devin
5. Codex como reviewer
6. Desenvolvimento multiagente

Cada etapa produziu documentação e evidências antes da introdução da próxima.

Modernização de legado, Strangler Fig, eventos/Kafka, resiliência e
observabilidade permanecem como possibilidades de evolução **pós-V1**. Não
são requisitos para considerar esta primeira versão concluída.

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

BMAD foi utilizado para transformar a ideia inicial em engenharia estruturada.

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

A fase produziu PRD, arquitetura e um backlog inicial pequeno, mantendo o
foco no aprendizado do processo em vez de gerar um backlog excessivo.

---

# Fase 3 — Claude Code

Claude Code foi utilizado como primeiro coding agent medido.

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

Foram observados:

- aderência à arquitetura;
- qualidade do código;
- testes;
- alterações desnecessárias;
- quantidade de intervenção humana;
- necessidade de contexto e guardrails.

---

# Fase 4 — Devin

Uma story pequena foi entregue ao Devin com maior autonomia.

O experimento permitiu observar:

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

Codex foi introduzido como reviewer independente, separando o papel do
implementador do papel de revisão.

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

O reviewer foi orientado a procurar:

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

Esta fase foi executada depois das anteriores, preservando a separação entre
implementação, revisão e decisão humana.

Organização experimental:

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

# Evolução arquitetural pós-V1

A V1 encerra o ciclo experimental principal em BMAD → Claude Code → Devin →
Codex → desenvolvimento multiagente.

Os cenários abaixo permanecem como possibilidades para uma próxima etapa
deliberada do laboratório, e não como pendências da V1.

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

Experimentos principais realizados:

- `CC-EXP-01` — transferência válida com Claude Code;
- `CC-EXP-02` — rejeição de transferências inválidas com Claude Code;
- `CC-EXP-03` — proteção de saldo e concorrência com Claude Code;
- `DEVIN-EXP-01` — consulta de transferência com autonomia até Pull Request;
- `CODEX-EXP-01` — revisão independente da implementação do Devin;
- `MULTI-AGENT-EXP-01` — implementação, revisão humana independente,
  revisão pelo Codex e decisão humana.

Os arquivos correspondentes estão em `docs/lab/`.

Cada experimento registra, quando aplicável:

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

Diário dos experimentos e evidências das execuções.

## docs/playbook.md

Consolidação das recomendações e aprendizados derivados dos experimentos.
A V1 do playbook foi concluída após o experimento multiagente.

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
- [x] Configurar Docker e Docker Compose
- [x] Configurar Node.js/NVM
- [x] Criar setup reproduzível e verificador de ambiente
- [x] Configurar BMAD
- [x] Criar PRD
- [x] Definir arquitetura inicial
- [x] Criar backlog inicial
- [x] Executar experimentos com Claude Code
- [x] Executar experimento com Devin
- [x] Executar code review independente com Codex
- [x] Executar experimento multiagente
- [x] Consolidar Agent Engineering Playbook v1.0
- [x] Atualizar LAB-PLAN para encerramento da V1
- [ ] Executar validação final da aplicação
- [ ] Criar tag de conclusão da V1

### Fora do escopo de conclusão da V1

- modernização com Strangler Fig;
- arquitetura orientada a eventos/Kafka;
- resiliência distribuída;
- observabilidade avançada.

---

# Estado atual

**Status:** V1 em encerramento.

As fases Foundation, BMAD, Claude Code, Devin, Codex como reviewer e
desenvolvimento multiagente foram concluídas.

O Agent Engineering Playbook v1.0 está consolidado e o plano mestre já
reflete o encerramento da V1.

Restam apenas as atividades de fechamento:

1. finalizar este README;
2. executar a validação final;
3. verificar o estado do repositório;
4. criar a tag Git da V1.

---

# Princípio do laboratório

Este projeto não busca demonstrar quantas ferramentas de IA podem ser utilizadas simultaneamente.

O objetivo é compreender:

> Como utilizar agentes de IA de maneira disciplinada dentro de engenharia e arquitetura de software, mantendo qualidade, rastreabilidade, decisões arquiteturais explícitas e supervisão humana.

Para o contexto completo do laboratório, leia:

```text
LAB-PLAN.md
```
