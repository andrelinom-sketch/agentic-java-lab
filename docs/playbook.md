# Playbook — engenharia de software com agentes

Documento evolutivo. Reúne o que o laboratório está aprendendo sobre
trabalhar com agentes de IA em engenharia de software. É atualizado a cada
marco, a partir dos experimentos registrados em `docs/lab/`.

O diário (`docs/lab/`) conta o que aconteceu em cada experimento. Este
playbook só guarda as recomendações que derivam dele e o caminho até as
evidências.

## Como ler

- **Status** (PRD, FR-11): *hipótese*, *apoiada*, *revisada* ou *removida*.
  Uma recomendação sustentada por um único experimento permanece
  *hipótese*. Dois experimentos também não a promovem automaticamente.
- Cada recomendação separa:
  - **Fatos observados**: verificáveis em um artefato Git ou documento
    citado;
  - **Interpretação**: leitura humana desses fatos;
  - **Recomendação**: o que se propõe fazer daqui em diante, ainda a ser
    testado.
- As referências usam hashes de commit, caminhos de arquivo e o PR. Um
  leitor deve conseguir ir de uma recomendação até a evidência sem ajuda
  (FR-12).

## Experimentos de origem

| Id | Registro | Freeze | Implementação | Correção de registro |
| --- | --- | --- | --- | --- |
| CC-EXP-01 | [`docs/lab/02-claude-code-exp-01.md`](lab/02-claude-code-exp-01.md) | `freeze/exp-01` → `2cc86e5` | `df644e4` (PR #1, merge `2b45a49`) | `8040fa0` |
| CC-EXP-02 | [`docs/lab/02-claude-code-exp-02.md`](lab/02-claude-code-exp-02.md) | `freeze/exp-02` → `561944f` | `d6a7d9e` (PR #2, merge `1565c67`) | — (resultado `56cc592`, transcript `b4fde94`) |
| CC-EXP-03 | [`docs/lab/02-claude-code-exp-03.md`](lab/02-claude-code-exp-03.md) | `freeze/exp-03` → `6b72502` | `3fe8876` (PR #3, merge `b521a5a`) | — (rubrica `a47af2a`, resultado `1bb829a`, transcript `8e80a14`) |
| DEVIN-EXP-01 | [`docs/lab/03-devin-exp-01.md`](lab/03-devin-exp-01.md) | `freeze/devin-exp-01` → `68870d2` | `63a343d` (PR #4, merge `4f63243`) | — (resultado `81d15a7`, sem transcript) |
| CODEX-EXP-01 | [`docs/lab/04-codex-exp-01.md`](lab/04-codex-exp-01.md) | — (revisão de `63a343d`, pré-declaração `f3b793b`) | — (sem implementação) | — (sem transcript) |

O CC-EXP-01 é uma única execução de Claude Code sobre a Story 1.2
(transferência válida). O CC-EXP-02 é uma única execução de Claude Code
sobre a Story 1.3 (rejeição de transferências inválidas), desenhada para
testar a H3; só a H3 foi atualizada com ele. O CC-EXP-03 é uma única
execução de Claude Code sobre a Story 1.4 (saldo não negativo sob
concorrência), a partir de uma baseline que já implementava parte da AD-4;
só a H4 foi atualizada com ele. O DEVIN-EXP-01 é uma única execução de
Devin sobre a Story 1.5 (consultar transferência pelo identificador), com
autonomia autorizada até o Pull Request; ele originou a H7 e acrescentou
observações à H3 e à H6. O CODEX-EXP-01 é uma única revisão independente,
pelo Codex, do commit `63a343d`; acrescentou uma observação à H3. As
limitações gerais estão no fim deste documento e valem para todas as
recomendações abaixo.

---

## H1 — Uma única fonte autoritativa para o que está congelado

**Status:** Hipótese · **Origem:** CC-EXP-01

**Recomendação atual:** definir os artefatos congelados de um experimento
em um único lugar. As outras fontes, como o guardrail do agente, apontam
para ele em vez de manter uma lista própria. A baseline também declara
quais pacotes congelados a story pode estender.

**Fatos observados**
- Em `freeze/exp-01`, [`docs/lab/baseline.md`](lab/baseline.md) listava
  `src/main/java/dev/agenticlab/account/**` como congelado. A lista de
  protegidos do `CLAUDE.md` não incluía esse pacote.
- O prompt do experimento mandava não alterar "nenhum arquivo listado em
  CLAUDE.md como protegido".
- `df644e4` estendeu três arquivos de `account/**`, só com acréscimos.
- Até `8040fa0`, o registro pós-execução dizia que nenhum arquivo congelado
  havia sido alterado, e só essa lista tinha sido verificada. O desvio foi
  registrado depois do merge como `DEV-CC-EXP-01-01`.

**Interpretação:** é um desvio de protocolo, causado por uma inconsistência
da baseline. As duas listas divergiam, e a verificação pós-execução usou a
mais estreita. A extensão de `account` era exigida pelo AD-2 e pelo AD-4,
e a baseline não a previa. Não é tratada como falha do agente.

**Limitações:** foi um único caso de divergência. Não se sabe se o agente
teria agido diferente com uma lista unificada. A extensão era necessária de
qualquer forma.

**O que futuros experimentos podem mostrar:**
- se, com uma fonte única e com extensões permitidas declaradas, desvios
  deixam de passar despercebidos na verificação;
- se declarar as extensões permitidas é viável sem antecipar o desenho da
  implementação.

---

## H2 — Pedir ao agente que explicite as decisões que nada determinava

**Status:** Hipótese · **Origem:** CC-EXP-01

**Recomendação atual:** pedir, no prompt, que o agente liste as decisões
tomadas sem determinação de um AD ou critério de aceite. Na revisão,
confrontar essa lista com as decisões identificadas no diff.

**Fatos observados**
- O registro lista seis decisões autônomas identificadas na revisão humana:
  `Instant`/`TIMESTAMPTZ`, `@NotNull`, FK na `V2`, `debit`/`credit`,
  `@Lock` + `@Query` e o status como constante de texto. Ver
  [`02-claude-code-exp-01.md`](lab/02-claude-code-exp-01.md), "Decisões
  autônomas/inferidas".
- O prompt pré-declarado não pedia essa lista.
- Não há registro preservado da sessão que mostre se o agente mencionou
  alguma dessas decisões.

**Interpretação:** nenhuma das seis decisões contradiz um AD. No registro,
elas aparecem apenas como resultado da leitura do diff na revisão humana.

**Limitações:**
- Não há como saber se o agente teria listado bem essas decisões, nem se
  pedir a lista mudaria as próprias decisões.
- A identificação das decisões dependeu de um único revisor.

**O que futuros experimentos podem mostrar:**
- quantas decisões encontradas no diff aparecem na lista do agente;
- se há decisões na lista do agente que o revisor não encontrou;
- se pedir a lista altera o comportamento de implementação.

---

## H3 — Pedir testes do agente de forma explícita, dizendo onde ficam

**Status:** Hipótese · **Origem:** CC-EXP-01 · **Testada em:** CC-EXP-02
(não refutada neste caso)

**Recomendação atual:** quando se quiser que o agente escreva testes
próprios, dizer isso no prompt e indicar a localização, fora de `reference`
(AD-8). Revisar esses testes quanto ao cenário que cada um declara cobrir,
não só quanto à execução verde (aprendizado provisório, ver abaixo).

**Fatos observados — CC-EXP-01**
- `df644e4` não adiciona nem altera nenhum arquivo em `src/test`.
- O AD-8 prevê testes escritos pelo agente, fora do pacote `reference`.
- O prompt pedia para rodar `./mvnw -B verify` e confirmar os testes
  verdes. Não pedia novos testes.

**Fatos observados — CC-EXP-02**
- O prompt pedia explicitamente testes próprios para a Story 1.3 e definia
  onde escrevê-los: `src/test/java/dev/agenticlab/transfer/**`, fora de
  `reference`, seguindo o AD-8. Ver
  [`02-claude-code-exp-02.md`](lab/02-claude-code-exp-02.md), "Prompt exato
  a ser dado ao agente".
- `d6a7d9e` adiciona 18 testes do agente, todos em
  `src/test/java/dev/agenticlab/transfer/TransferRejectionApiTest.java`,
  além dos 6 testes de referência de `TransferRejectionReferenceTest`.
  `git diff freeze/exp-02 -- src/test/java/dev/agenticlab/reference` sai
  vazio.
- Os testes do agente cobrem cenários além da referência: os 5 itens da
  checklist C1–C5 e outros, como o menor valor positivo e a precedência
  entre violações. Pela avaliação registrada, 16 dos 18 não se sobrepõem à
  referência e 15 têm utilidade clara ("Avaliação da H3").
- O agente leu a checklist C1–C5 e a rubrica da H3 **antes** de escrever os
  testes, como declarou no relatório de entrega.
- `./mvnw -B verify` terminou com 37/37 testes verdes. Mesmo assim, durante
  a revisão humana, identificou-se que
  `unknownDestinationIsReportedWhateverItsLockPosition` não garantia as
  duas posições de bloqueio que declarava cobrir. A falha apareceu quando
  o agente explicou o código a pedido do revisor, não pelo build.
- O teste foi corrigido após intervenção humana (classificada como
  *correção*), sem alteração no código de produção. O resultado seguiu
  37/37 verdes.

**Interpretação:**
- No CC-EXP-01, o agente satisfez o contrato existente e não o ampliou. O
  CC-EXP-01 não mostra se isso decorre do prompt ou de outro fator.
- No CC-EXP-02, o pedido explícito com localização foi acompanhado de
  testes próprios no lugar indicado. Pela leitura pré-declarada do
  experimento, a H3 não é refutada neste caso.
- Isso não demonstra causalidade: uma execução, story diferente da do
  CC-EXP-01, sem grupo de controle, e a H1 mudou ao mesmo tempo.
- A cobertura de C1–C5 é evidência fraca de descoberta autônoma de casos,
  porque o agente conhecia a checklist.
- Testes verdes não são evidência suficiente da qualidade das premissas
  dos próprios testes: um teste pode passar sem exercitar o cenário que
  seu nome declara.

**Aprendizado provisório:** testes produzidos pelo agente também precisam
de revisão quanto à qualidade do cenário — se o teste de fato monta a
situação que declara cobrir —, não apenas quanto à execução verde. No
CC-EXP-02, o caso foi um teste de ordenação de bloqueios. Não se sabe se o
mesmo vale para outros tipos de teste.

**Limitações:**
- Há uma execução sem pedido de testes (CC-EXP-01) e uma com pedido
  (CC-EXP-02), sobre stories diferentes. Não é uma comparação A/B.
- A checklist estava no repositório e foi lida pelo agente antes dos
  testes.
- As classificações por teste (sobreposição e utilidade) foram
  originalmente redigidas pelo agente avaliado. Depois do registro do
  experimento, foram conferidas e aprovadas pelo avaliador humano (H3-d e
  H3-f).
- A falha de premissa foi encontrada por uma única revisão, de um único
  avaliador, que também desenhou o experimento. Não se sabe se há outras
  não detectadas.

**Observação complementar — DEVIN-EXP-01** (outro agente; não é um teste
da H3)
- O prompt **autorizava**, sem exigir, testes próprios fora de
  `reference/**`. Ver [`03-devin-exp-01.md`](lab/03-devin-exp-01.md),
  "Prompt exato a ser dado ao agente".
- `63a343d` adiciona 6 testes do agente em
  `src/test/java/dev/agenticlab/transfer/TransferQueryApiTest.java`, contra
  PostgreSQL real via Testcontainers. `reference/**` sem diff.
- Na revisão humana independente, 2 dos 6 testes apresentaram sobreposição
  com a referência ou baixo valor adicional, e alguns fazem assertions sobre
  o body sem verificar antes que ele não é nulo.

Isso é compatível com o aprendizado provisório acima: a execução verde não
bastou para avaliar o valor dos testes do agente, e foi a revisão que
identificou as ressalvas.

**Observação complementar — CODEX-EXP-01** (revisão por outro agente; não
é um teste da H3)
- O Codex revisou `63a343d` de forma independente, sem executar testes. Ver
  [`04-codex-exp-01.md`](lab/04-codex-exp-01.md), "Registro pós-execução".
- Apontou um finding de severidade baixa, classificado como novo e
  procedente: em `queryDoesNotChangeBalances`, as respostas dos dois GETs
  são descartadas, e o teste ficaria verde mesmo se eles retornassem erro.
- Não apontou as duas ressalvas da revisão humana sobre os testes do
  agente.

Isso é outro caso de teste verde que não verifica parte do que executa.
Neste caso, foi encontrado por outro agente atuando como reviewer, não pela
revisão humana registrada, e esse agente não encontrou o que a revisão
humana encontrou.

**O que futuros experimentos podem mostrar:**
- se o pedido explícito produz testes em outras stories, agentes e modelos;
- se o agente encontra casos além da referência sem conhecer uma checklist
  de avaliação;
- com que frequência testes do agente passam sem exercitar o cenário
  declarado, e que tipo de revisão detecta isso (humana, por outro agente ou
  ambas).

---

## H4 — Contrato vermelho + Spine + guardrails como pacote de contexto

**Status:** Hipótese · **Origem:** CC-EXP-01 · **Observada também em:**
CC-EXP-03 (baseline parcialmente implementada)

**Recomendação atual:** usar como ponto de partida o pacote de contexto do
CC-EXP-01:
- testes de referência vermelhos como contrato executável;
- Architecture Spine com decisões `[ADOPTED]`;
- `CLAUDE.md` com a lista de protegidos;
- a story com seus critérios de aceite.

Tratar esse pacote como algo que *foi suficiente uma vez*, não como o
mínimo necessário.

**Fatos observados**
- Com `./mvnw -B verify`, 13 testes passam. Os 2 de
  `TransferApiReferenceTest` passaram a verdes e os 11 anteriores seguiram
  verdes. O CI aprovou o PR #1.
- Nenhum arquivo da lista de protegidos do `CLAUDE.md` foi alterado.
- `TransferService` usa apenas `AccountService` (AD-2), e a regra ArchUnit
  correspondente passa.
- `AccountService.transferBalance` bloqueia as contas em ordem crescente de
  id (AD-4), um comportamento que nenhum teste de referência verifica.
- O registro informa zero intervenções humanas (ver H6 sobre a sustentação
  disso).

**Interpretação:** a implementação está de acordo com o AD-4 num ponto que
os testes de referência não cobrem. O CC-EXP-01 não permite afirmar que
isso se deve à presença do Spine no contexto.

**Limitações:**
- O contexto foi fornecido inteiro e de uma vez, então não é possível
  atribuir o resultado a nenhuma parte dele.
- A story é pequena e muito especificada.
- As partes difíceis do domínio (saldo insuficiente, concorrência, erros
  422) ficaram para as Stories 1.3 a 1.5.
- Houve uma única execução, com um único agente e modelo.

**Fatos observados — CC-EXP-03**
- O experimento avaliou a aderência arquitetural diante de uma
  implementação parcial da Story 1.4, não a capacidade do agente de
  descobrir a estratégia de concorrência sozinho. Ver
  [`rubric.md`](lab/evidence/cc-exp-03/rubric.md), "Objetivos detalhados".
- No freeze, a baseline já tinha o bloqueio pessimista das duas contas em
  ordem fixa e a verificação de saldo depois do bloqueio (AD-4), vindos do
  CC-EXP-01 e do CC-EXP-02. Dois dos três testes de
  `TransferConcurrencyReferenceTest` já passavam; faltava o
  `CHECK (balance >= 0)`.
- O contexto incluía o Spine, o `CLAUDE.md` apontando para
  [`baseline.md`](lab/baseline.md), a story e o teste de referência como
  contrato executável.
- O agente reconheceu que os mecanismos existentes já atendiam parte da
  story e os preservou: `account` e `transfer` ficaram sem diff.
- A única mudança de produção em `3fe8876` foi uma nova migração,
  `V3__add_account_balance_check.sql`, com 2 linhas que acrescentam
  `CHECK (balance >= 0)`. Nenhum mecanismo concorrente alternativo foi
  introduzido: sem `@Version`, isolamento alterado, `synchronized`, advisory
  lock nem retry.
- `./mvnw -B verify`: 40 testes, 0 falhas. `reference/` sem diff. O agente
  não criou testes próprios, e o prompt não os pedia.
- Pela rubrica selada, com hash conferido: A1–A5 atendidos, A6 N/A, A7–A8
  atendidos. Resultado pré-declarado: **Aderência**. Ver
  [`02-claude-code-exp-03.md`](lab/02-claude-code-exp-03.md), "Registro
  pós-execução".

**Interpretação — CC-EXP-03:** diante de arquitetura explícita, de uma
baseline que já cumpria parte da story e de critérios verificáveis, o agente
fez uma mudança pequena e aderente, em vez de redesenhar o mecanismo. O
experimento não mostra o que o agente faria sem a implementação existente.

**Aprendizado provisório:** fornecer arquitetura explícita, uma baseline
parcialmente implementada e critérios verificáveis pode favorecer mudanças
pequenas e aderentes. Isso se baseia em uma única execução. O CC-EXP-03 não
demonstra causalidade nem a capacidade do agente de criar a arquitetura do
zero.

**Limitações — CC-EXP-03:**
- Parte da AD-4 já existia antes da execução.
- Uma única execução, sem grupo de controle.
- O resultado não demonstra que o agente teria criado a estratégia de
  concorrência sozinho.
- Houve um único avaliador, que também desenhou o experimento e a rubrica.

**O que futuros experimentos podem mostrar:**
- execuções com partes do pacote removidas indicariam o que é necessário;
- stories com regra de negócio e concorrência indicariam se o pacote
  continua suficiente;
- outros agentes indicariam se o resultado depende da ferramenta;
- uma execução da Story 1.4 sem o mecanismo da AD-4 na baseline indicaria
  se o agente adota a estratégia de concorrência por conta própria.

---

## H5 — Registro pós-experimento separado e com referências verificáveis

**Status:** Hipótese · **Origem:** CC-EXP-01

**Recomendação atual:**
- registrar o resultado em um commit separado da implementação;
- citar os commits, o PR e o CI de cada afirmação;
- identificar a versão da rubrica e o commit do freeze.

**Fatos observados**
- A pré-declaração (`2cc86e5`) e o resultado (`df644e4`) estão em commits
  distintos, como pede o FR-7. Mas o resultado foi commitado junto com a
  implementação.
- O hash do freeze ficou "a preencher" até `8040fa0`.
- Não existe rubrica versionada (FR-6).
- As afirmações do registro não linkam para evidências (FR-8).
- A afirmação sobre arquivos congelados precisou de correção pós-merge
  (`8040fa0`, ver H1).

**Interpretação:** misturar registro e implementação no mesmo commit
pode dificultar a revisão independente do registro. Sem links, verificar
uma afirmação exige reconstruí-la a partir do histórico. Trata-se de uma
lacuna do método, não do agente.

**Limitações:** não se sabe se um registro separado teria evitado o erro
corrigido em `8040fa0`. O custo de manter links não foi medido.

**O que futuros experimentos podem mostrar:**
- se registros com links permitem que outra pessoa, ou um agente em
  contexto novo, reconstrua o experimento sem ajuda (FR-10);
- se o custo de manter os links é aceitável.

---

## H6 — Preservar evidência da sessão para intervenções e tempo

**Status:** Hipótese · **Origem:** CC-EXP-01

**Recomendação atual:** preservar a sessão do agente (transcript ou
exportação equivalente) ou outro artefato verificável. Afirmações sobre
intervenções humanas e tempo devem apontar para esse artefato.

**Fatos observados**
- O registro afirma "nenhuma intervenção humana" e "15–20 minutos". Não há
  transcript nem outro artefato no repositório que sustente essas
  afirmações.
- O Git mostra o freeze às 12:32 (`2cc86e5`) e o commit de implementação às
  14:45 (`df644e4`). Esses horários não medem a sessão do agente e não
  confirmam nem contradizem os 15–20 minutos.

**Interpretação:** intervenções e tempo, que o `LAB-PLAN.md` (§14)
usa para comparar agentes, dependem hoje do relato do avaliador. O FR-9 pede
que as intervenções sejam contadas a partir de artefatos.

**Limitações:**
- Não há motivo concreto para duvidar do relato. A questão é que ele não
  pode ser verificado.
- Não foi avaliado o que preservar (transcript completo, log de comandos,
  marcações de tempo) nem o custo disso.

**Observação complementar — DEVIN-EXP-01**
- O registro afirma nenhuma intervenção técnica humana e nenhuma mensagem
  adicional ao agente. Não há transcript nem exportação da sessão no
  repositório (`docs/lab/evidence/` não tem diretório do DEVIN-EXP-01).
- O custo (US$ 2,70) foi registrado a partir do saldo e do uso exibidos
  pela ferramenta antes e depois da execução.
- O commit do agente, `63a343d`, tem como author e committer a identidade Git
  do usuário. O Devin aparece só no trailer `Co-Authored-By`. O PR foi aberto
  pelo app `devin-ai-integration`.

Isso reforça a lacuna descrita nesta hipótese: as afirmações sobre
intervenções continuam dependendo do relato do avaliador. A autoria Git,
sozinha, também não distingue o trabalho do agente do trabalho humano.

**O que futuros experimentos podem mostrar:**
- se a evidência preservada permite aplicar a taxonomia de intervenções de
  forma consistente entre avaliadores (SM-2);
- qual é o menor artefato que sustenta essas afirmações.

---

## H7 — Delegar o ciclo completo até o PR, com merge humano

**Status:** Hipótese · **Origem:** DEVIN-EXP-01

**Recomendação atual:** para uma story pequena, com contrato executável
congelado, arquitetura explícita e guardrails claros, considerar delegar a
um agente o ciclo completo até o Pull Request: análise, branch,
implementação, testes, commit, push e abertura do PR. O merge continua sendo
decisão humana, depois de uma revisão independente do diff e do CI.

**Fatos observados — DEVIN-EXP-01**
- Story 1.5, a partir de `freeze/devin-exp-01` (`68870d2`). No freeze,
  `TransferQueryReferenceTest` tinha 2 testes vermelhos e 1 verde. Ver
  [`baseline.md`](lab/baseline.md), seção DEVIN-EXP-01.
- A autonomia foi autorizada no prompt até o PR, com o merge reservado ao
  humano. Ver [`03-devin-exp-01.md`](lab/03-devin-exp-01.md).
- O agente criou a branch `experiment/devin-exp-01-transfer-query`, fez a
  implementação e os testes, criou o commit `63a343d`, fez o push e abriu o
  PR #4.
- O registro informa nenhuma intervenção técnica humana e nenhuma mensagem
  adicional durante a execução (ver H6 sobre a sustentação disso).
- O diff tem 5 arquivos, +181 / -0: `TransferController`, `TransferService`,
  a nova `TransferNotFoundException` e `ApiExceptionHandler`, mais 6 testes
  próprios fora de `reference/**`.
- Nenhum artefato protegido foi alterado. `reference/**` saiu sem diff.
- CI do PR verde: 49/49 testes, incluindo os 3 de
  `TransferQueryReferenceTest`.
- A revisão humana independente observou aderência a AD-1, AD-2, AD-5,
  AD-6, AD-7 e AD-8, com `TRANSFER_NOT_FOUND` em Problem Details, e nenhuma
  dependência, camada ou abstração nova.
- O Maven Central respondeu HTTP 429 no ambiente do agente. O agente
  contornou o erro com um mirror configurado só em `~/.m2/settings.xml`, sem
  versionar essa configuração e sem intervenção humana.
- Custo observado: US$ 2,70.
- O merge foi feito por decisão humana, com merge commit (`4f63243`), depois
  da revisão.

**Interpretação:** nesta execução, o pacote de contexto (story pequena,
contrato vermelho congelado, Spine, `CLAUDE.md` e baseline com as listas de
protegido e modificável) foi suficiente para que o agente fosse da análise
ao PR sem intervenção técnica. O resultado ficou dentro dos guardrails e
das decisões arquiteturais avaliadas. A revisão humana antes do merge ainda
encontrou ressalvas nos testes do agente (ver H3) e na autoria Git (ver H6).

**Limitações:**
- Uma única execução, de um único agente, sem grupo de controle.
- A story é pequena e muito especificada. A consulta por id tinha um padrão
  análogo pronto em `account` (`GET /accounts/{id}` com
  `ACCOUNT_NOT_FOUND`).
- A arquitetura, o tratador global de erros e o contrato já existiam. O
  experimento não mostra o que o agente faria diante de decisões
  arquiteturais abertas.
- A ausência de intervenção não pode ser verificada por um artefato da
  sessão (H6).
- O contorno do HTTP 429 dependeu de uma configuração local não versionada.
  O ambiente do agente diferiu do CI nesse ponto.
- Houve um único avaliador, que também desenhou o experimento e aprovou os
  testes de referência.
- Esta hipótese não compara agentes. O DEVIN-EXP-01 e os CC-EXP diferem em
  story, grau de autonomia e prompt.

**O que futuros experimentos podem mostrar:**
- se o ciclo até o PR se mantém em stories com regra de negócio,
  concorrência ou decisões arquiteturais não cobertas pelos artefatos;
- se o agente para e pede aprovação quando encontra uma decisão
  arquitetural significativa, como o prompt determina;
- quanto da revisão humana antes do merge encontra problemas que o CI não
  encontra;
- como o custo varia com o tamanho e a complexidade da story.

---

## Limitações gerais desta versão

- Cada recomendação deriva de **uma única execução** por story: um agente,
  um modelo, sem repetição e sem grupo de controle. A H3 tem duas
  execuções (CC-EXP-01 e CC-EXP-02), e a H4 também (CC-EXP-01 e CC-EXP-03),
  sempre sobre stories diferentes. A H7 tem uma única execução
  (DEVIN-EXP-01).
- O DEVIN-EXP-01 usou outro agente, outro grau de autonomia e outra story.
  Nenhuma comparação entre agentes é feita neste playbook.
- A story medida é pequena, e a parte difícil do domínio ainda não foi
  exercitada.
- No CC-EXP-03, a concorrência foi verificada pelos testes de referência,
  mas o mecanismo já existia na baseline. O agente não precisou projetá-lo.
- O CC-EXP-01 foi conduzido sem rubrica versionada e sem aplicar a
  taxonomia de intervenções.
- Houve um único avaliador, que também desenhou o experimento.
- Nenhuma relação causal entre o contexto fornecido e o resultado foi
  demonstrada.

## Histórico

| Versão | Data | Mudança | Base |
| --- | --- | --- | --- |
| 0.1 | 2026-09-22 | Primeira versão: H1–H6 registradas como hipóteses | CC-EXP-01 (`2cc86e5`, `df644e4`, `2b45a49`, `8040fa0`) |
| 0.2 | 2026-09-23 | H3 atualizada com o CC-EXP-02; segue hipótese. Aprendizado provisório sobre revisão do cenário dos testes do agente | CC-EXP-02 (`561944f`, `d6a7d9e`, `56cc592`, `b4fde94`, `1565c67`) |
| 0.3 | 2026-09-23 | H4 atualizada com o CC-EXP-03; segue hipótese. Aprendizado provisório sobre baseline parcial com arquitetura explícita | CC-EXP-03 (`6b72502`, `3fe8876`, `a47af2a`, `1bb829a`, `8e80a14`, `b521a5a`) |
| 0.4 | 2026-09-24 | H7 criada como hipótese (delegação do ciclo até o PR, com merge humano). Observações complementares na H3 (testes do agente) e na H6 (evidência da sessão e autoria Git) | DEVIN-EXP-01 (`68870d2`, `63a343d`, `81d15a7`, `4f63243`) |
| 0.5 | 2026-09-24 | Observação complementar na H3: revisão independente pelo Codex encontrou um teste do agente que não verifica as respostas que executa | CODEX-EXP-01 (`f3b793b`, `63a343d`) |
