# Agentic Java Lab

## 1. Propósito deste documento

Este arquivo é o documento mestre do laboratório **Agentic Java Lab**.

Qualquer agente de IA que comece a trabalhar neste repositório deve
**ler este documento antes de executar qualquer tarefa**.

O objetivo é permitir que uma nova sessão de IA compreenda:

-   o propósito do laboratório;
-   a estratégia de aprendizado;
-   a arquitetura pretendida;
-   as tecnologias utilizadas;
-   as fases do projeto;
-   o papel de cada agente de IA;
-   o que já foi realizado;
-   o que ainda deve ser realizado;
-   as regras que devem ser respeitadas;
-   qual é o próximo passo.

Este projeto é, antes de tudo, um **laboratório de aprendizado**.

Não otimizar o projeto apenas para velocidade de entrega. O processo, as
decisões, os erros e os aprendizados são tão importantes quanto o código
produzido.

## 2. Objetivo

Construir progressivamente um pequeno sistema Java enquanto estudamos:

-   desenvolvimento de software assistido por IA;
-   desenvolvimento agentic;
-   BMAD Method;
-   Claude Code;
-   Devin;
-   OpenAI Codex;
-   desenvolvimento multiagente;
-   engenharia de contexto;
-   arquitetura de software;
-   modernização de sistemas;
-   Strangler Fig;
-   arquitetura hexagonal;
-   sistemas orientados a eventos;
-   resiliência;
-   testes automatizados.

O laboratório deve permanecer **simples o suficiente para permitir
entender cada conceito isoladamente**.

Não adicionar tecnologias apenas para tornar a arquitetura mais
sofisticada. Cada nova tecnologia deve responder a uma necessidade
concreta do laboratório.

## 3. Princípio fundamental

O laboratório seguirá esta evolução:

``` text
IDEIA
  ↓
BMAD
  ↓
REQUISITOS
  ↓
ARQUITETURA
  ↓
ÉPICOS
  ↓
STORIES
  ↓
AGENTE IMPLEMENTADOR
  ↓
TESTES
  ↓
AGENTE REVISOR
  ↓
PULL REQUEST
  ↓
REVISÃO HUMANA
  ↓
MERGE
```

A responsabilidade final pelas decisões permanece humana.

Os agentes podem analisar, propor, implementar, testar, revisar e
documentar. Mudanças arquiteturais relevantes não devem ser realizadas
silenciosamente.

## 4. Estratégia de aprendizado

Não instalar ou utilizar todas as ferramentas simultaneamente.

A evolução será incremental:

``` text
BMAD
 ↓
Claude Code
 ↓
Devin
 ↓
Codex
 ↓
Multiagente
```

Cada ferramenta será introduzida somente depois que o fluxo anterior
estiver compreendido.

Isso permite identificar claramente:

-   qual problema cada ferramenta resolve;
-   quanto contexto precisa receber;
-   onde comete erros;
-   quanto trabalho humano economiza;
-   quando sua utilização não compensa;
-   qual agente funciona melhor para determinada atividade.

## 5. Projeto utilizado no laboratório

Será construída uma pequena API de contas e transferências bancárias.

O domínio foi escolhido por ser simples, mas permitir trabalhar
conceitos reais de sistemas financeiros.

Funcionalidades iniciais:

``` text
POST /accounts
POST /transfers
GET /transfers/{id}
```

Exemplo:

``` text
Conta A
Saldo: R$ 1.000

      │
      │ transferência R$ 100
      ▼

Conta B
Saldo: R$ 500
```

Após a operação:

``` text
Conta A = R$ 900
Conta B = R$ 600
```

## 6. Regras iniciais de negócio

O sistema deverá evoluir para contemplar regras como:

-   conta de origem deve existir;
-   conta de destino deve existir;
-   conta de origem deve possuir saldo suficiente;
-   valor da transferência deve ser maior que zero;
-   transferência deve possuir identificador;
-   uma mesma transferência não pode ser processada duas vezes;
-   operações devem possuir status;
-   falhas devem ser tratadas adequadamente.

Não implementar antecipadamente todas essas regras. Elas devem surgir
através das stories do projeto.

## 7. Stack inicial

Utilizar inicialmente:

``` text
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

Evitar inicialmente:

``` text
Kubernetes
Kafka
Cloud
Service Mesh
múltiplos bancos
múltiplos microsserviços
arquiteturas distribuídas complexas
```

Esses elementos poderão ser introduzidos posteriormente.

## 8. Estrutura esperada do repositório

Estrutura aproximada:

``` text
agentic-java-lab/

├── README.md
├── LAB-PLAN.md
├── CLAUDE.md
├── AGENTS.md
│
├── docs/
│   ├── prd.md
│   ├── architecture.md
│   ├── adr/
│   └── lab/
│       ├── 01-bmad.md
│       ├── 02-claude-code.md
│       ├── 03-devin.md
│       ├── 04-codex-review.md
│       └── 05-multi-agent.md
│
├── stories/
├── src/
├── pom.xml
└── docker-compose.yml
```

A estrutura poderá evoluir conforme o BMAD e as decisões arquiteturais.

## 9. Diário do laboratório

O aprendizado deve ser documentado no diretório:

``` text
docs/lab/
```

Cada experimento deve registrar, quando aplicável:

-   Objetivo
-   Ferramenta utilizada
-   Prompt utilizado
-   Contexto fornecido
-   Resultado esperado
-   Resultado obtido
-   Intervenções humanas necessárias
-   Erros cometidos pelo agente
-   Decisões tomadas
-   Tempo aproximado
-   O que funcionou bem
-   O que funcionou mal
-   Aprendizados
-   Próximo experimento

O objetivo é permitir comparar posteriormente diferentes agentes e
estratégias.

## 10. Git como parte do experimento

Mesmo sendo desenvolvido inicialmente por uma pessoa, o projeto deve
utilizar práticas semelhantes às de um time.

Fluxo preferencial:

``` text
main
 │
 ├── feature/story-001
 ├── feature/story-002
 └── feature/story-003
```

Cada mudança relevante deve preferencialmente passar por:

``` text
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
Review
 ↓
Merge
```

Isso permitirá posteriormente que agentes trabalhem sobre branches e
Pull Requests reais.

## 11. FASE 1 --- BMAD

### Objetivo

Aprender como transformar uma ideia relativamente vaga em um plano de
engenharia estruturado.

Nesta fase, o foco **não é programação**.

Fluxo:

``` text
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

Resultado esperado:

-   PRD inicial;
-   arquitetura inicial;
-   decisões arquiteturais importantes;
-   backlog pequeno;
-   aproximadamente 3 a 5 stories iniciais.

O BMAD não deve gerar dezenas de stories para um laboratório simples.
Priorizar simplicidade.

## 12. FASE 2 --- Claude Code

Depois que o planejamento inicial estiver compreendido, utilizar Claude
Code como primeiro agente implementador.

Fluxo:

``` text
BMAD
 ↓
Story
 ↓
Claude Code
 ↓
Implementação
 ↓
Testes
 ↓
Review humano
 ↓
PR
```

Implementar uma story por vez.

Objetivo principal desta fase:

> Descobrir quanto contexto e quais guardrails um coding agent precisa
> para produzir código coerente com a arquitetura definida.

Observar principalmente:

-   aderência à arquitetura;
-   qualidade do código;
-   alterações desnecessárias;
-   entendimento do domínio;
-   testes produzidos;
-   necessidade de intervenção humana;
-   qualidade das decisões tomadas pelo agente.

## 13. CLAUDE.md

Criar posteriormente um `CLAUDE.md`.

Ele deverá conter informações úteis para Claude Code, como:

-   arquitetura;
-   convenções;
-   comandos;
-   regras;
-   padrões Java;
-   estratégia de testes;
-   restrições.

Exemplo conceitual:

``` text
Antes de implementar:

1. Leia LAB-PLAN.md.
2. Leia docs/architecture.md.
3. Leia a story atribuída.
4. Não altere arquitetura sem aprovação.
5. Não introduza dependências sem justificativa.
6. Execute os testes antes de considerar a tarefa concluída.
```

Não copiar automaticamente este exemplo. O arquivo deverá evoluir com o
laboratório.

## 14. FASE 3 --- Devin

Depois de compreender o fluxo com Claude Code, introduzir Devin.

Objetivo: avaliar um agente com maior autonomia.

Fluxo:

``` text
BMAD
 ↓
Story
 ↓
Devin
 ↓
Análise do repositório
 ↓
Implementação
 ↓
Execução dos testes
 ↓
Correções
 ↓
Pull Request
 ↓
Review humano
```

Escolher uma story suficientemente pequena e bem especificada.

Exemplo futuro:

``` text
Implementar idempotência no processamento de transferências.
```

Comparar com Claude Code:

-   autonomia;
-   qualidade;
-   aderência à arquitetura;
-   quantidade de intervenções;
-   capacidade de corrigir seus próprios erros;
-   qualidade dos testes;
-   qualidade do Pull Request;
-   tempo;
-   custo, quando aplicável.

## 15. FASE 4 --- Codex como reviewer

Introduzir um segundo agente independente.

Inicialmente, utilizar Codex prioritariamente como reviewer.

Fluxo:

``` text
BMAD
 ↓
Story
 ↓
Claude Code ou Devin
 ↓
Implementação
 ↓
Codex
 ↓
Code Review
 ↓
Correções
 ↓
CI
 ↓
Review humano
 ↓
Merge
```

Objetivo: estudar separação entre **agente implementador** e **agente
revisor**.

O reviewer deve procurar, entre outros:

-   bugs;
-   violações arquiteturais;
-   problemas de concorrência;
-   problemas de transação;
-   problemas de segurança;
-   testes ausentes;
-   casos extremos;
-   complexidade desnecessária;
-   problemas de manutenção.

## 16. FASE 5 --- Desenvolvimento multiagente

Somente depois das fases anteriores.

Possível organização:

``` text
                HUMANO
                   │
                   ▼
            ARCHITECT AGENT
                 BMAD
                   │
                   ▼
               STORIES
                   │
          ┌────────┴────────┐
          ▼                 ▼
   DEVELOPER AGENT      DEVELOPER AGENT
   Claude Code              Devin
          │                 │
          └────────┬────────┘
                   ▼
            REVIEWER AGENT
                 Codex
                   │
                   ▼
                  CI
                   │
                   ▼
             HUMAN REVIEW
```

O objetivo não é maximizar a quantidade de agentes.

O objetivo é compreender:

> Quando múltiplos agentes realmente melhoram o processo de engenharia?

## 17. Evolução futura --- sistema legado

> **Escopo:** esta seção e as evoluções posteriores não fazem parte do
> critério de conclusão da V1 do laboratório. Permanecem registradas como
> possibilidades para uma próxima etapa deliberada.

Depois que a aplicação básica estiver funcionando, poderá ser introduzido
deliberadamente um cenário de modernização.

Arquitetura conceitual:

``` text
              SISTEMA LEGADO

           ┌─────────────────┐
           │     Monólito    │
           │                 │
           │ Conta           │
           │ Transferência   │
           │ Auditoria       │
           └─────────────────┘
```

Então criar uma iniciativa de modernização.

## 18. Strangler Fig

Objetivo futuro: extrair gradualmente funcionalidades do monólito.

Exemplo:

``` text
                  Gateway
                     │
            ┌────────┴────────┐
            ▼                 ▼
         Legado        Transfer Service
                           Java 21
                              │
                         PostgreSQL
```

O tráfego deverá gradualmente deixar o legado.

Essa fase servirá para estudar:

-   modernização incremental;
-   coexistência legado/novo;
-   roteamento;
-   compatibilidade;
-   migração;
-   rollback;
-   observabilidade.

## 19. Arquitetura Hexagonal

Durante a evolução, introduzir explicitamente conceitos de arquitetura
hexagonal.

Estrutura conceitual:

``` text
              INPUT
                │
                ▼
          REST Controller
                │
                ▼
          Application Port
                │
                ▼
             Domain
                │
                ▼
          Output Port
             /     \
            /       \
      Database      External API
       Adapter        Adapter
```

O domínio não deve depender diretamente de:

-   Spring;
-   banco de dados;
-   HTTP;
-   Kafka.

## 20. Kafka

Kafka será introduzido somente quando houver necessidade arquitetural
clara.

Exemplo:

``` text
Transfer Service
       │
       ▼
     Kafka
       │
       ├────► Audit Service
       └────► Notification Service
```

Essa etapa permitirá estudar:

-   eventos;
-   processamento assíncrono;
-   retry;
-   idempotência;
-   eventual consistency;
-   DLQ;
-   observabilidade.

## 21. Outros conceitos futuros

Depois da base estar madura, considerar:

``` text
Retry
Timeout
Circuit Breaker
Idempotência
Testcontainers
Observabilidade
Tracing
Métricas
Logs estruturados
OpenTelemetry
CI/CD
GitHub Actions
```

Adicionar um conceito de cada vez. Cada conceito deverá possuir
experimento e documentação.

## 22. Princípios arquiteturais

Priorizar:

``` text
simplicidade
clareza
testabilidade
baixo acoplamento
alta coesão
observabilidade
evolução incremental
```

Evitar:

``` text
overengineering
frameworks desnecessários
abstrações prematuras
microsserviços sem necessidade
dependências sem justificativa
arquitetura criada apenas para demonstrar tecnologia
```

## 23. Regras para agentes de IA

Qualquer agente trabalhando neste repositório deve seguir estas regras.

### Regra 1

Leia este documento antes de executar alterações relevantes.

### Regra 2

Identifique a fase atual do laboratório.

Não implemente funcionalidades pertencentes a fases futuras.

### Regra 3

Não adicionar tecnologia apenas porque ela está mencionada neste
roadmap.

Kafka, Kubernetes, cloud etc. são objetivos futuros, não requisitos
atuais.

### Regra 4

Antes de implementar uma story, leia:

``` text
LAB-PLAN.md
docs/architecture.md
ADRs relevantes
story atribuída
instruções específicas do agente
```

### Regra 5

Mudanças arquiteturais significativas devem ser explicitadas. Quando
necessário, criar/propor ADR.

### Regra 6

Não esconder decisões importantes dentro do código. Documentar decisões
relevantes.

### Regra 7

Não refatorar áreas não relacionadas à tarefa sem necessidade.

### Regra 8

Executar os testes aplicáveis antes de declarar uma tarefa concluída.

### Regra 9

Se houver conflito entre uma story e a arquitetura documentada,
sinalizar o conflito em vez de escolher silenciosamente.

### Regra 10

Este é um laboratório educacional. Ao concluir trabalho significativo,
registrar o aprendizado em `docs/lab/`.

## 24. Papel humano

A IA não é responsável pela decisão final.

O humano atua como:

``` text
Solution Architect
Tech Lead
Reviewer
Product Owner do laboratório
```

Responsabilidades:

-   definir objetivos;
-   aprovar arquitetura;
-   avaliar propostas;
-   revisar código;
-   decidir trade-offs;
-   aprovar PRs;
-   analisar comportamento dos agentes.

O objetivo do laboratório também é estudar como o papel do engenheiro
muda quando parte da execução é delegada a agentes.

## 25. Critério de sucesso

O sucesso **não** será medido apenas pela quantidade de código.

O laboratório será considerado bem-sucedido se permitir responder
perguntas como:

-   BMAD melhorou a qualidade da especificação?
-   Stories mais detalhadas melhoraram a implementação dos agentes?
-   Quanto contexto Claude Code realmente precisa?
-   Onde Devin apresenta vantagem pela autonomia?
-   Um segundo agente encontra problemas que o primeiro não encontrou?
-   Code review por outro modelo melhora o resultado?
-   Quais decisões devem permanecer humanas?
-   Quando multiagente compensa?
-   Quando multiagente apenas adiciona complexidade?
-   Como documentar um repositório para que agentes entendam rapidamente
    o projeto?
-   Como aplicar agentes em modernização de sistemas Java reais?

## 26. Estado atual

Estado consolidado ao encerramento da primeira versão do laboratório:

STATUS: **LABORATÓRIO V1 CONCLUÍDO**

- FASE 1 — BMAD: **CONCLUÍDA**
- FASE 2 — Claude Code: **CONCLUÍDA**
- FASE 3 — Devin: **CONCLUÍDA**
- FASE 4 — Codex como reviewer: **CONCLUÍDA**
- FASE 5 — Desenvolvimento multiagente: **CONCLUÍDA**
- Aplicação: **implementada e evoluída pelos experimentos**
- PRD: **criado e utilizado como contrato dos experimentos**
- Arquitetura: **definida e utilizada como guardrail**
- Stories: **backlog V1 implementado e expandido com a Story 2.1**
- Playbook: **versão 1.0 concluída**

A sequência incremental originalmente planejada — BMAD, Claude Code, Devin,
Codex e desenvolvimento multiagente — foi executada.

Os experimentos principais registrados são:

- `CC-EXP-01` — implementação da transferência válida;
- `CC-EXP-02` — rejeição de transferências inválidas;
- `CC-EXP-03` — proteção de saldo e experimento de concorrência;
- `DEVIN-EXP-01` — consulta de transferência com maior autonomia até PR;
- `CODEX-EXP-01` — revisão independente da implementação do Devin;
- `MULTI-AGENT-EXP-01` — implementação, revisão humana independente,
  revisão pelo Codex e decisão humana sobre o merge.

Os resultados detalhados permanecem em `docs/lab/`. As recomendações
derivadas dos experimentos foram consolidadas em `docs/playbook.md`.

A responsabilidade final pelas decisões permaneceu humana durante o
laboratório.

## 27. Primeiro Milestone

### MILESTONE 1 --- Foundation + BMAD

Objetivo:

Preparar o ambiente e utilizar BMAD para transformar a ideia inicial em
um pequeno backlog implementável.

Preparar/verificar:

``` text
Linux
Git
GitHub
Java 21
Maven
Docker
Docker Compose
BMAD
```

Depois executar o processo BMAD.

Resultado esperado:

``` text
Ideia
 ↓
PRD
 ↓
Arquitetura
 ↓
ADRs necessários
 ↓
3–5 stories
```

Nenhuma implementação significativa deve começar antes de entendermos
esse processo.

## 28. Próxima ação

A V1 do Agentic Java Lab foi concluída.

O ciclo Foundation → BMAD → Claude Code → Devin → Codex → Multiagente foi
executado, documentado e consolidado no Agent Engineering Playbook v1.0.

A aplicação passou pela validação final e o repositório foi verificado como
limpo e sincronizado.

Qualquer nova atividade — incluindo modernização de legado, Strangler Fig,
Kafka, sistemas distribuídos ou novos experimentos — pertence a uma etapa
pós-V1 e deve começar como uma decisão deliberada, não como pendência desta
versão.

## 29. Instrução para uma nova IA

Se você é um novo agente ou uma nova sessão de IA lendo este arquivo:

**não reinicie o planejamento do zero.**

Use este documento como contexto-base.

Primeiro determine:

``` text
Qual é o STATUS atual?
Qual é a FASE atual?
Qual foi o último experimento realizado?
Qual é a próxima ação ainda não concluída?
```

Consulte:

``` text
docs/lab/
stories/
docs/adr/
Git history
Pull Requests
```

Continue a partir do último estado conhecido.

Se o campo `Estado atual` estiver desatualizado em relação ao
repositório, considere o estado real do repositório e atualize este
documento.

## 30. Visão de longo prazo

A V1 encerra o ciclo experimental principal em BMAD → Claude Code → Devin →
Codex → desenvolvimento multiagente.

A parte de modernização, Strangler Fig, Kafka, resiliência, eventos e
observabilidade abaixo permanece como visão de evolução posterior, e não
como pendência necessária para declarar a V1 concluída.

A evolução de longo prazo originalmente pretendida é:

``` text
                    AGENTIC JAVA LAB

                          │
                          ▼
                        BMAD
                          │
                   PRD / Arquitetura
                          │
                        Stories
                          │
              ┌───────────┴───────────┐
              ▼                       ▼
         Claude Code                Devin
              │                       │
              └───────────┬───────────┘
                          ▼
                        Codex
                       Review
                          │
                          ▼
                        CI/CD
                          │
                          ▼
                    Human Review

                          │
                          ▼

                 Modernização
                          │
                     Strangler Fig
                          │
                 Arquitetura Hexagonal
                          │
                        Kafka
                          │
                Resiliência / Eventos
                          │
                    Observabilidade
                          │
                          ▼

             AGENTIC SOFTWARE ENGINEERING
```

O objetivo final não é simplesmente aprender ferramentas de IA.

O objetivo é compreender como utilizar agentes de IA de maneira
disciplinada dentro de **engenharia e arquitetura de software**,
mantendo decisões arquiteturais, qualidade, rastreabilidade e supervisão
humana.
