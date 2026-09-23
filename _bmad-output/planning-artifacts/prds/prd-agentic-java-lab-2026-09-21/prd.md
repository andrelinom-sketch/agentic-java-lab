---
title: "PRD: Agentic Java Lab"
status: draft
created: 2026-09-21
updated: 2026-09-21
---

# PRD: Agentic Java Lab

## 0. Propósito do documento

Este PRD transforma o Product Brief do laboratório em requisitos verificáveis. Seus leitores são o autor (Tech Lead e Arquiteto), o agente de arquitetura do BMAD e os agentes de código que vão implementar stories.

Ele é parte da **baseline experimental**: os agentes são observados contra as regras que ele fixa. Por isso ele deve ser congelado antes do primeiro experimento medido, e mudanças posteriores são registradas como alteração de protocolo. Ele permanece `draft` enquanto o Product Brief depender de fontes ainda não verificadas.

Ele constrói sobre o brief e o addendum e não os duplica:
- `_bmad-output/planning-artifacts/briefs/brief-agentic-java-lab-2026-09-20/brief.md`
- `_bmad-output/planning-artifacts/briefs/brief-agentic-java-lab-2026-09-20/addendum.md`

**Convenções.**
- Cada requisito funcional (FR) traz a **Camada** (`Método` ou `API`) e a **Origem** (`brief`, `conversa` ou `inferido`).
- Um requisito com origem `inferido` permanece **pendente** até aprovação humana. Depois de aprovado, ele conserva a marca de que nasceu de inferência do agente.
- O congelamento é feito por um marco verificável no Git, antes do primeiro experimento medido. O mecanismo exato será decidido na arquitetura.

## 1. Visão

O Agentic Java Lab é um repositório público em que uma pequena API de contas e transferências é planejada, implementada e revisada com agentes de IA sob supervisão humana. O produto é o registro auditável desse processo e o método para observá-lo. A API é o veículo experimental.

Na V1, uma fatia vertical completa (BMAD → uma story → Claude Code → CI comum → revisão humana → registro completo → primeira atualização do playbook) valida esse método em até 4 semanas. Depois disso, o método é reutilizado com outros agentes e outros cenários.

**Objetivos de visão, fora dos requisitos da V1:** a reutilização da rubrica, do modelo de diário e do método por terceiros, e a aplicação do método a um cenário de modernização de sistemas Java (por exemplo, Strangler Fig), quando houver justificativa arquitetural.

## 2. Usuário-alvo

### 2.1 Jobs to be done

- **Autor (Tech Lead e Arquiteto):** quando delego trabalho a um agente, quero registrar contexto, intervenções e resultados de forma verificável, para julgar com evidência e não com impressão. *Origem: brief.*
- **Leitor Tech Lead ou Arquiteto:** quando avalio incorporar agentes ao processo de engenharia, quero rastrear uma recomendação até os experimentos e evidências que a sustentam, para calibrar autonomia e guardrails. Esse leitor também pode reutilizar a rubrica e o modelo de diário. *Origem: brief, exceto a reutilização, que é **inferido** (aprovado por Andrelino em 2026-09-21).*
- **Desenvolvedor interessado em agentes (secundário):** quando quero praticar um fluxo com agentes, quero um método reutilizável e exemplos honestos, incluindo falhas. *Origem: brief.*

### 2.2 Não usuários (V1)

- Usuários finais de serviços financeiros. A API é exclusivamente um veículo experimental e educacional e não representa um sistema para movimentação de dinheiro real. *Origem: **inferido** (aprovado por Andrelino em 2026-09-21).*
- Quem procura um benchmark ou um ranking de ferramentas. *Origem: brief.*
- Recrutadores e entrevistadores técnicos: beneficiários indiretos, sem influência no desenho. *Origem: brief.*

### 2.3 Jornadas de usuário-chave

Jornadas curtas, de 3 a 5 momentos, sem fluxos nem casos de borda nesta etapa.

- **UJ-1. Andrelino executa e registra um experimento com o Claude Code.** *Origem: conversa.*
  - **Persona e contexto:** Andrelino, no papel de Tech Lead e Arquiteto.
  - **Estado de entrada:** uma story previamente especificada, e critérios, contexto, guardrails e instrumentos de avaliação congelados em um marco verificável no Git.
  - **Caminho:**
    1. Seleciona a story e confirma o congelamento no marco do Git.
    2. Entrega a story ao Claude Code e acompanha a execução sem antecipar soluções, registrando as intervenções humanas quando necessárias.
    3. Ao final, verifica critérios de aceite, testes, CI, aderência às decisões arquiteturais e alterações produzidas.
    4. Registra resultados, erros, decisões do agente, intervenções e desvios do protocolo, vinculando-os às evidências no repositório.
  - **Ponto culminante e resolução:** o experimento é considerado registrado quando outra pessoa, ou um agente em contexto novo, consegue reconstruir o que aconteceu a partir dessas evidências.

- **UJ-2. Um leitor rastreia uma recomendação do playbook até a evidência.** *Origem: **inferido** a partir do critério de sucesso 3 do brief (aprovado por Andrelino em 2026-09-21).*
  - **Persona e contexto:** Camila, Tech Lead que avalia incorporar agentes ao processo da sua equipe e não conhece o autor. A persona é fictícia e serve apenas como recurso narrativo.
  - **Estado de entrada:** acessa o repositório público e abre o playbook.
  - **Caminho:**
    1. Lê uma recomendação e vê sua origem, suas limitações e seu status.
    2. Segue o link até o experimento no diário.
    3. Do diário chega às evidências: commits, PR, resultado do CI e critérios congelados.
    4. Confere se a conclusão se sustenta e o que o experimento não cobre.
  - **Ponto culminante e resolução:** chega às evidências sem ajuda do autor e decide adotar, adaptar ou descartar a recomendação.

## 3. Glossário

Os termos vêm do brief e do addendum, exceto: Experimento, Estudo de caso e Marco de congelamento, de origem **inferido** (aprovado por Andrelino em 2026-09-21); e Contexto e guardrails, Testes de referência, Cliente da API, Conta, Saldo e Transferência (esta em parte, ver a entrada), também de origem **inferido** (aprovado por Andrelino em 2026-09-21). As definições do domínio da API se limitam ao necessário ao PRD e não antecipam decisões da arquitetura.

- **Laboratório** — o repositório público Agentic Java Lab: método, instrumentos, API e registros.
- **Método** — o conjunto de rubrica, diário, CI comum, contexto congelado e playbook usado para observar agentes. Corresponde à camada `Método`.
- **API** — a API de contas e transferências, veículo experimental. Corresponde à camada `API`.
- **Experimento** — execução observada de um protocolo definido. *Origem: inferido (aprovado).*
- **Estudo de caso** — registro e interpretação das evidências produzidas por um ou mais Experimentos, sem pretensão estatística. *Origem: inferido (aprovado).*
- **Story** — unidade de trabalho do backlog, com critérios de aceite, entregue a um agente.
- **Agente sob avaliação** — a ferramenta de IA que executa a story em um Experimento.
- **Baseline experimental** — os instrumentos que funcionam como régua de um Experimento. Dois grupos: **instrumentos de especificação e avaliação** (PRD, rubrica, critérios de aceite, contexto e guardrails) e **instrumentos de verificação** (CI e testes de referência). *Classificação dos itens nos grupos: Origem inferido (aprovado por Andrelino em 2026-09-21).*
- **Contexto e guardrails** — arquivos e instruções fornecidos ao Agente sob avaliação para orientar seu trabalho, versionados como parte da baseline experimental (por exemplo, `CLAUDE.md` e `AGENTS.md`). *Origem: inferido (aprovado por Andrelino em 2026-09-21).*
- **Testes de referência** — testes automatizados que fazem parte da baseline experimental: preparados e versionados sob supervisão humana, aprovados antes de um Experimento medido e usados para verificar o trabalho do Agente sob avaliação. *Origem: inferido (aprovado por Andrelino em 2026-09-21).*
- **Marco de congelamento** — marco verificável no Git que identifica a versão dos artefatos da baseline experimental usada em um Experimento. *Origem: inferido (aprovado).*
- **Rubrica** — documento versionado com os critérios de avaliação aplicados a um Experimento.
- **Intervenção humana** — ação humana que altera o curso do agente durante um Experimento, classificada por uma taxonomia fechada (esclarecimento, correção, aprovação, assumir o trabalho).
- **Decisão (do agente)** — escolha entre alternativas que altera implementação, arquitetura, comportamento ou escopo e que não estava determinada previamente. É identificada pelo diff, não pela narração do agente.
- **Erro** — resultado que viola requisito, critério de aceite, teste, ADR ou regra previamente definida, ou comportamento comprovadamente incorreto.
- **Autocorreção** — o agente identifica e corrige um problema antes de uma intervenção humana apontar a solução específica.
- **Desvio do protocolo** — diferença entre o que foi declarado antes do Experimento e o que ocorreu.
- **Evidência** — artefato verificável (commit, diff, PR, resultado de CI, teste) que sustenta um registro. Fica na fonte em que é gerada.
- **Diário** — o registro em `docs/lab/` que interpreta evidências e aponta para elas, sem copiá-las.
- **Playbook** — guia de adoção evolutivo. Cada recomendação indica origem, limitações e status (hipótese, apoiada, revisada ou removida).
- **Cliente da API** — quem consome a API. Não é usuário final de serviços financeiros. *Origem: inferido (aprovado por Andrelino em 2026-09-21).*
- **Conta** — entidade da API que possui um identificador e um Saldo, criada com Saldo inicial maior ou igual a zero. *Origem: inferido (aprovado por Andrelino em 2026-09-21).*
- **Saldo** — valor monetário mantido em uma Conta. *Origem: inferido (aprovado por Andrelino em 2026-09-21).*
- **Transferência** — operação aceita e persistida que move valor de uma Conta de origem para uma Conta de destino. Possui identificador único e status. Uma solicitação rejeitada não é uma Transferência. *Origem: conversa (aceita e persistida); o restante, inferido (aprovado por Andrelino em 2026-09-21).*
- **V1** — o primeiro ciclo, de 20/09/2026 a 18/10/2026, que percorre uma **fatia vertical** completa: BMAD → uma story → Claude Code → CI comum → revisão humana → registro completo → primeira atualização do playbook.

## 4. Funcionalidades

_[Rascunho dos requisitos funcionais. Itens com Origem **inferido** ficam pendentes até aprovação humana. Não definem formato de identificadores, precisão monetária, valores de status nem códigos HTTP: essas decisões pertencem à arquitetura e dependem de consulta.]_

### 4.1 Congelamento da baseline experimental

**Descrição:** antes de um Experimento medido, os artefatos que servem de régua são fixados em um marco verificável no Git. Realiza UJ-1.

#### FR-1: Congelar a baseline em um marco verificável

**Camada:** Método · **Origem:** brief e conversa

Andrelino pode fixar a baseline experimental em um Marco de congelamento antes de iniciar um Experimento medido.

**Consequências (testáveis):**
- O marco identifica as versões de PRD, rubrica, critérios de aceite, contexto e guardrails, CI e testes de referência usadas no Experimento.
- O histórico do Git mostra que o marco precede o início do Experimento.
- Um Experimento sem Marco de congelamento anterior não conta como Experimento medido. *(Origem: inferido, aprovado)*

**Fora de escopo:** o mecanismo técnico do marco, decidido na arquitetura.

#### FR-2: Registrar alterações da baseline durante um Experimento

**Camada:** Método · **Origem:** conversa

Uma mudança na baseline experimental durante um Experimento exige aprovação humana e é registrada como alteração de protocolo ou como Intervenção humana, conforme o caso.

**Consequências (testáveis):**
- Toda alteração de CI, testes de referência ou critérios de aceite feita durante um Experimento tem aprovação humana registrada.
- Uma alteração sem registro é condição de fracasso da V1 (§7).

#### FR-18: Versionar e aprovar contexto e guardrails antes do Experimento

**Camada:** Método · **Origem:** brief e conversa

Os arquivos de contexto e guardrails fornecidos ao Agente sob avaliação são versionados e preparados ou aprovados sob supervisão humana antes do Experimento medido.

**Consequências (testáveis):**
- Existe registro da aprovação humana anterior ao Marco de congelamento do Experimento.
- A versão usada no Experimento é a identificada pelo Marco de congelamento.

### 4.2 CI comum e proteção da baseline

**Descrição:** um CI único mede o trabalho de todos os agentes, e nenhum agente sob avaliação pode alterar a régua sem que isso seja visto.

#### FR-3: Aplicar as mesmas verificações a todo PR

**Camada:** Método · **Origem:** brief

O CI aplica as mesmas verificações a qualquer PR, de qualquer Agente sob avaliação.

**Consequências (testáveis):**
- Uma verificação executada para um agente é executada da mesma forma para outro.
- O resultado do CI de cada PR é uma Evidência consultável.

**Fora de escopo:** a lista de verificações e suas ferramentas, decididas na arquitetura.

#### FR-4: Preparar o CI e os testes de referência fora da comparação de autonomia

**Camada:** Método · **Origem:** conversa

O CI e os testes de referência são preparados e versionados sob supervisão humana e aprovados por Andrelino antes do primeiro Experimento de implementação medido. O CI é criado em uma story específica, sob supervisão humana direta.

**Consequências (testáveis):**
- A story do CI e a preparação dos testes de referência não são contadas como Experimento comparativo de autonomia.
- Existe registro da aprovação de Andrelino, anterior ao primeiro Marco de congelamento medido, para o CI e para os testes de referência.
- Os testes de referência estão versionados no Git na versão identificada pelo Marco de congelamento.

#### FR-5: Impedir o enfraquecimento silencioso da baseline

**Camada:** Método · **Origem:** conversa

Nenhum elemento da baseline experimental (PRD, rubrica, critérios de aceite, contexto e guardrails, CI e testes de referência) pode ser enfraquecido silenciosamente durante um Experimento medido.

**Consequências (testáveis):**
- Uma alteração de qualquer desses artefatos durante um Experimento medido não passa despercebida e segue o FR-2.

**Fora de escopo:** o mecanismo técnico de proteção, decidido na arquitetura.

### 4.3 Rubrica e registro de experimento

**Descrição:** cada Experimento é avaliado por critérios definidos antes da execução e registrado no Diário, que aponta para as Evidências. Realiza UJ-1.

#### FR-6: Versionar a rubrica e fixar os critérios antes da execução

**Camada:** Método · **Origem:** brief

A rubrica é um documento versionado, e os critérios aplicáveis a um Experimento são fixados antes de sua execução.

**Consequências (testáveis):**
- O registro de cada Experimento identifica a versão da rubrica usada.
- O Experimento 01 (BMAD) é a exceção declarada: seu registro informa que parte dos critérios foi definida durante a execução, como limitação metodológica.

#### FR-7: Registrar o Experimento em dois momentos

**Camada:** Método · **Origem:** brief (aprovado por Andrelino em 2026-09-21; o addendum do brief o marcava como "a validar no PRD")

O Diário registra cada Experimento em uma pré-declaração, escrita antes da execução, e em um resultado, escrito depois, em commits distintos.

**Consequências (testáveis):**
- A pré-declaração contém objetivo e hipótese; ferramenta, modelo, versão e data; contexto e critérios usados, com links para as versões congeladas.
- O resultado contém o resultado contra os critérios; Intervenções humanas, falhas e correções, com links; Decisões e aprendizados; Desvios do protocolo; e limitações. Tempo e custo entram quando medidos.

#### FR-8: Apontar para a Evidência sem copiá-la

**Camada:** Método · **Origem:** brief

O Diário aponta para as Evidências e não as copia.

**Consequências (testáveis):**
- Cada afirmação de resultado tem link para uma Evidência na fonte em que foi gerada (commit, diff, PR, CI ou teste).

#### FR-9: Identificar Decisões e Intervenções por artefatos

**Camada:** Método · **Origem:** brief

As Decisões do agente são identificadas a partir do diff, comparado ao que a story determinava, e as Intervenções humanas são classificadas pela taxonomia fechada.

**Consequências (testáveis):**
- O registro informa o grau de especificação da story.
- Sempre que possível, as Intervenções são contadas a partir do histórico de git, PRs e conversas, e não da memória.

#### FR-10: Considerar o Experimento registrado

**Camada:** Método · **Origem:** conversa. Realiza UJ-1.

Um Experimento é considerado registrado quando outra pessoa, ou um agente em contexto novo, reconstrói o que aconteceu a partir das Evidências.

**Consequências (testáveis):**
- A reconstrução é feita sem consultar o autor.

### 4.4 Playbook e rastreabilidade

**Descrição:** o Playbook sintetiza os aprendizados e permite chegar de cada recomendação até a evidência. Realiza UJ-2.

#### FR-11: Manter recomendações com origem, limitações e status

**Camada:** Método · **Origem:** brief e conversa

Cada recomendação do Playbook indica os Experimentos de origem, suas limitações e seu status (hipótese, apoiada, revisada ou removida).

**Consequências (testáveis):**
- O Playbook é atualizado por marcos, e a V1 termina com sua primeira atualização.
- Uma recomendação sustentada por apenas um Experimento permanece com status *hipótese*. Dois Experimentos não promovem a recomendação automaticamente. *(Origem: inferido, aprovado)*

#### FR-12: Permitir o caminho da recomendação até a Evidência

**Camada:** Método · **Origem:** brief. Realiza UJ-2.

Um leitor pode chegar de uma recomendação do Playbook até as Evidências que a sustentam, passando pelo Experimento no Diário.

**Consequências (testáveis):**
- Um leitor de fora, ou um agente em contexto novo, faz o caminho sem ajuda do autor (critério de sucesso 3 do brief).

### 4.5 Contas

**Descrição:** a API permite criar contas com saldo inicial e consultá-las. Não há depósito nem saque na V1.

#### FR-13: Criar conta com saldo inicial

**Camada:** API · **Origem:** conversa

O Cliente da API pode criar uma Conta informando o saldo inicial.

**Consequências (testáveis):**
- Um saldo inicial maior ou igual a zero é aceito.
- Um saldo inicial menor que zero é rejeitado.
- A Conta criada pode ser consultada com o saldo inicial informado (FR-14).

**Fora de escopo:** depósito e saque.

#### FR-14: Consultar conta e saldo

**Camada:** API · **Origem:** conversa

O Cliente da API pode consultar uma Conta por identificador e obter seu saldo.

**Consequências (testáveis):**
- Após uma Transferência efetivada, o saldo consultado das contas envolvidas reflete o débito e o crédito (FR-16).
- Consultar uma Conta inexistente é tratado como não encontrado.

### 4.6 Transferências

**Descrição:** a API permite transferir valor entre duas Contas e consultar a Transferência. Na V1, uma Transferência representa uma operação aceita e persistida. Solicitações inválidas são rejeitadas sem criar Transferência, portanto sem identificador consultável. Idempotência está fora da V1.

#### FR-15: Criar transferência entre duas contas

**Camada:** API · **Origem:** conversa

O Cliente da API pode criar uma Transferência de uma Conta de origem para uma Conta de destino.

**Consequências (testáveis):**
- É rejeitada quando a Conta de origem não existe.
- É rejeitada quando a Conta de destino não existe.
- É rejeitada quando o valor não é maior que zero.
- É rejeitada quando o saldo da origem é insuficiente.
- É rejeitada quando origem e destino são a mesma Conta.
- Uma solicitação rejeitada não cria Transferência e não gera identificador consultável.

**Fora de escopo:** idempotência; formato de identificadores, precisão monetária e valores de status (decisões da arquitetura).

#### FR-16: Efetivar a transferência

**Camada:** API · **Origem:** conversa e inferido (aprovado). A exigência de que uma solicitação rejeitada não altere saldos foi inferida pelo agente e aprovada por Andrelino em 2026-09-21.

Uma Transferência efetivada debita a Conta de origem e credita a Conta de destino pelo mesmo valor.

**Consequências (testáveis):**
- A soma dos saldos das duas Contas é igual antes e depois.
- Uma solicitação rejeitada não altera nenhum saldo. *(Origem: inferido, aprovado)*
- Nenhuma Transferência produz saldo negativo por insuficiência de fundos, inclusive diante de operações concorrentes. *(Origem: conversa)*

**Fora de escopo:** o mecanismo que garante essa propriedade sob concorrência. Ele é uma questão arquitetural da V1 e deve ser decidido antes da implementação (ver §8).

#### FR-17: Identificar e consultar a transferência

**Camada:** API · **Origem:** conversa

Cada Transferência possui identificador único e status, e pode ser consultada por identificador.

**Consequências (testáveis):**
- Duas Transferências não compartilham o mesmo identificador.
- A consulta retorna o status atual.
- Consultar uma Transferência inexistente é tratado como não encontrado.

**Fora de escopo:** o identificador único **não** implica idempotência. Reenviar a mesma requisição não é tratado como a mesma Transferência na V1.

### 4.7 Requisitos não funcionais transversais

#### NFR-1: Publicação segura

**Camada:** Método · **Origem:** brief

Nenhum artefato publicado contém credenciais, tokens, dados pessoais, caminhos locais desnecessários nem transcrições completas de sessões.

#### NFR-2: Publicar falhas e resultados negativos

**Camada:** Método · **Origem:** brief

Erros de agentes, violações de arquitetura, Intervenções humanas, abordagens abandonadas, prompts que deram mau resultado, decisões do autor que se mostrarem inadequadas e hipóteses não confirmadas são publicados quando relevantes.

#### NFR-3: Decisões arquiteturais não são silenciosas

**Camada:** Método · **Origem:** conversa

Um agente não toma decisão arquitetural significativa em silêncio. A decisão é discutida, justificada, registrada como ADR quando apropriado e aprovada por um humano.

**Consequências (testáveis):**
- Toda decisão arquitetural significativa presente em um PR tem aprovação humana registrada.
- Uma decisão arquitetural significativa sem aprovação registrada é uma violação a ser publicada (NFR-2).

## 5. Não objetivos

- Não é um benchmark e não produz ranking de ferramentas. Os resultados são estudos de caso, sem pretensão estatística. *Origem: brief.*
- Não é um sistema financeiro real: sem dinheiro real, depósito, saque, autenticação nem interface de usuário. *Origem: conversa.*
- Não adiciona tecnologia sem necessidade arquitetural que a justifique (por exemplo, Kafka, Kubernetes, microsserviços e cloud). *Origem: brief e conversa.*
- Não deixa agentes decidirem em silêncio (ver NFR-3). *Origem: conversa.*

## 6. Escopo da V1

**Dentro (20/09/2026 a 18/10/2026):** a fatia vertical completa (BMAD → uma story → Claude Code → CI comum → revisão humana → registro completo → primeira atualização do playbook), cobrindo as funcionalidades 4.1 a 4.6 (FR-1 a FR-18) e os NFR-1 a NFR-3. A story medida é pequena, mas não trivial, e é escolhida no backlog de 3 a 5 stories. *Origem: brief.*

**Fora da V1:** Devin, Codex como revisor, multiagente, legado e Strangler Fig, Kafka, resiliência distribuída, observabilidade avançada, UI, autenticação e cloud *(brief)*; idempotência de transferências, depósito e saque *(conversa)*.

Se a fatia não estiver concluída em 18/10/2026, o resultado registrado é "V1 incompleta", sem extensão de prazo. *Origem: brief.*

## 7. Métricas de sucesso

Seções 5 a 7 aprovadas por Andrelino em 2026-09-21. Os limiares de SM-2 e SM-3 e a unidade de F2 seguem pendentes (addendum do brief, §8); nenhum foi inventado aqui.

**Primárias**
- **SM-1: conclusão da fatia.** A fatia vertical foi percorrida até a primeira atualização do playbook dentro do prazo da V1. Valida FR-1 a FR-12 e FR-18. *Origem: brief.*
- **SM-2: consistência da rubrica.** As classificações de intervenções, erros e decisões são consistentes entre repetições da classificação, pelo mesmo avaliador ou por outro. Valida FR-6 e FR-9. *Origem: brief. Limiar e método pendentes.*
- **SM-3: rastreabilidade.** Alguém de fora, ou um agente em contexto novo, chega de uma recomendação às evidências sem ajuda. Valida FR-10 e FR-12. *Origem: brief. Protocolo pendente.*

**Contra-métricas (não otimizar)**
- **SM-C1: não reduzir o registro só para baixar seu custo.** Não reduzir nem omitir o registro necessário apenas para diminuir o custo de registrar. Equilibra SM-1. *Origem: conversa (aprovado por Andrelino em 2026-09-21). Distinta da condição de fracasso F2.*
- **SM-C2: não enfraquecer a baseline só para facilitar o resultado.** Não enfraquecer a baseline experimental apenas para facilitar a aprovação ou o resultado de um Experimento. Equilibra SM-1. Relacionada a FR-2 e FR-5, mas distinta da condição de fracasso F3. *Origem: conversa (aprovado por Andrelino em 2026-09-21).*
- **SM-C3: não simplificar a rubrica só para aumentar a consistência.** A rubrica não deve ser simplificada apenas para elevar artificialmente a consistência medida em SM-2. Equilibra SM-2. *Origem: **inferido** (aprovado por Andrelino em 2026-09-21).*
- **SM-C4: não acumular recomendações no playbook só para parecer completo.** Recomendações precisam estar vinculadas às evidências e limitações correspondentes. Equilibra SM-1 e FR-11. *Origem: **inferido** (aprovado por Andrelino em 2026-09-21).*

**Condições de fracasso da V1** *(Origem: brief)*

A V1 **falhou** se ocorrer qualquer uma destas:
- **F1:** a rubrica se mostrou inadequada a ponto de impedir uma avaliação coerente do experimento. A evolução normal da rubrica **não** é fracasso.
- **F2:** o custo de registrar superou o custo do experimento. *(Unidade e limiar pendentes.)*
- **F3:** um instrumento da baseline experimental foi alterado sem registro.

Condições de fracasso e contra-métricas são coisas distintas. As primeiras dizem quando a V1 falhou. As segundas dizem o que não otimizar ao perseguir as métricas primárias. Concluir a fatia fora do prazo é um resultado à parte: "V1 incompleta" (§6).

## 8. Questões em aberto

1. **Concorrência (arquitetura, antes da implementação).** Como garantir que nenhuma Transferência produza saldo negativo por insuficiência de fundos, inclusive sob operações concorrentes (FR-16)? Decisão arquitetural da V1, não adiável para revisão posterior. *Responsável: arquitetura.*
2. **Detalhes que exigem consulta antes de fixar:** formato de identificadores, precisão monetária, valores de status e códigos HTTP. *Responsável: arquitetura, com consulta a Andrelino.*
3. **Mecanismo do Marco de congelamento e da proteção da baseline** (FR-1, FR-5). *Responsável: arquitetura.*
4. **Limiares e protocolos** de SM-2, SM-3 e F2. *Responsável: rubrica, antes do primeiro experimento medido.*
5. **Mesma story ou stories equivalentes** entre agentes. *Revisitar quando o segundo agente entrar.*
6. **Licença do repositório.** Decisão pendente antes da publicação e da reutilização por terceiros. *Responsável: Andrelino.*
7. **Fontes do Product Brief.** O brief permanece `draft` até a verificação das fontes complementares, e este PRD permanece `draft` enquanto essa dependência existir. *Responsável: Andrelino.*

*Itens registrados na checagem final (2026-09-21), sem resolução nesta versão:*

8. **Experimento 01 (BMAD) e FR-1, FR-6, FR-7.** Esses requisitos valem para "cada Experimento", mas o Experimento 01 já ocorreu sem Marco de congelamento e sem pré-declaração. Só o FR-6 declara a exceção, e só quanto aos critérios. Definir o tratamento explícito. *Responsável: Andrelino, antes de fechar o diário do Experimento 01.*
9. **Escopo de implementação da V1.** *Resolvido em 2026-09-21 por Andrelino:* todos os FRs da API (FR-13 a FR-17) pertencem ao escopo funcional da V1. O backlog os organiza em 3 a 5 stories pequenas, e apenas uma delas será selecionada como experimento medido com Claude Code (escolha ainda em aberto).
10. **Congelamento e status `draft`.** O FR-1 exige congelar o PRD antes do primeiro Experimento medido, mas o PRD é `draft` enquanto as fontes do brief não forem verificadas. Decidir se a verificação precede o Marco de congelamento ou se o marco congela um rascunho. *Responsável: Andrelino.*
11. **CI e testes de referência.** Conteúdo mínimo das verificações do CI, ferramenta de análise estática (provável ADR) e conteúdo e cobertura dos testes de referência. Hoje aparecem só como "fora de escopo" do FR-3. *Responsável: arquitetura, antes da implementação.*
12. **Termos sem definição no glossário:** Protocolo (base da definição de Experimento e de "Desvio do protocolo"), Experimento medido e CI. Decidir também se Critérios de aceite, ADR e Backlog precisam de entrada própria. *Responsável: Andrelino.*
13. **FR-2 mais estreito que seu enunciado.** A consequência cita só CI, testes de referência e critérios de aceite, enquanto o enunciado e o FR-5 cobrem toda a baseline. *Responsável: Andrelino.*
14. **Nota desatualizada no início da §4.** Ela diz que itens inferidos ficam pendentes, mas a §9 indica que já foram aprovados, exceto os do item 15. *Responsável: Andrelino.*
15. **Consequências inferidas sem marca de origem**, pendentes de aprovação: FR-14 (consultar Conta inexistente é tratado como não encontrado) e NFR-3, 2ª consequência (uma decisão arquitetural sem aprovação registrada é uma violação a ser publicada, ligando-se ao NFR-2). *Responsável: Andrelino.*
    - **FR-17** (consultar Transferência inexistente é tratado como não encontrado). *Resolvido posteriormente por Andrelino:* a consequência foi aprovada pela adoção do AD-7 no Architecture Spine (`GET /transfers/{id}`: 404 se inexistente) e pela aprovação da Story 1.5 no backlog, ambos em 2026-09-21. Registrado neste PRD em 2026-09-23.

## 9. Índice de premissas

Todo item com Origem **inferido**, com seu estado. Um item aprovado conserva a marca de origem por inferência do agente.

**Aprovados por Andrelino em 2026-09-21**
- §2.1: o leitor também pode reutilizar a rubrica e o modelo de diário.
- §2.2: usuários finais de serviços financeiros são não usuários.
- §2.3: UJ-2, o leitor que rastreia uma recomendação até a evidência.
- §3: definições de Experimento, Estudo de caso e Marco de congelamento.
- FR-1: um Experimento sem Marco de congelamento anterior não conta como Experimento medido.
- FR-11: uma recomendação sustentada por apenas um Experimento permanece com status *hipótese*, e dois Experimentos não a promovem automaticamente.
- §3: definições de Contexto e guardrails, Testes de referência, Cliente da API, Conta, Saldo e o que é inferido na definição de Transferência.
- §3: classificação dos itens da baseline entre instrumentos de especificação e avaliação (PRD, rubrica, critérios de aceite, contexto e guardrails) e instrumentos de verificação (CI e testes de referência).
- FR-16: uma solicitação rejeitada não altera nenhum saldo.
- SM-C3: não simplificar a rubrica só para aumentar a consistência.
- SM-C4: não acumular recomendações no playbook só para parecer completo.

**Aprovados posteriormente por Andrelino**
- FR-17: consultar uma Transferência inexistente é tratado como não encontrado. Aprovado pela adoção do AD-7 e pela aprovação da Story 1.5, em 2026-09-21; registrado em 2026-09-23 (ver §8, item 15).

**Pendentes de aprovação**
- Consequências inferidas de FR-14 e NFR-3 (ver §8, item 15).
