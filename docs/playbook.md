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

O CC-EXP-01 é uma única execução de Claude Code sobre a Story 1.2
(transferência válida). O CC-EXP-02 é uma única execução de Claude Code
sobre a Story 1.3 (rejeição de transferências inválidas), desenhada para
testar a H3; só a H3 foi atualizada com ele. As limitações gerais estão no
fim deste documento e valem para todas as recomendações abaixo.

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

**O que futuros experimentos podem mostrar:**
- se o pedido explícito produz testes em outras stories, agentes e modelos;
- se o agente encontra casos além da referência sem conhecer uma checklist
  de avaliação;
- com que frequência testes do agente passam sem exercitar o cenário
  declarado, e que tipo de revisão detecta isso.

---

## H4 — Contrato vermelho + Spine + guardrails como pacote de contexto

**Status:** Hipótese · **Origem:** CC-EXP-01

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

**O que futuros experimentos podem mostrar:**
- execuções com partes do pacote removidas indicariam o que é necessário;
- stories com regra de negócio e concorrência indicariam se o pacote
  continua suficiente;
- outros agentes indicariam se o resultado depende da ferramenta.

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

**O que futuros experimentos podem mostrar:**
- se a evidência preservada permite aplicar a taxonomia de intervenções de
  forma consistente entre avaliadores (SM-2);
- qual é o menor artefato que sustenta essas afirmações.

---

## Limitações gerais desta versão

- Cada recomendação deriva de **uma única execução** por story: um agente,
  um modelo, sem repetição e sem grupo de controle. Só a H3 tem duas
  execuções (CC-EXP-01 e CC-EXP-02), sobre stories diferentes.
- A story medida é pequena, e a parte difícil do domínio ainda não foi
  exercitada.
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
