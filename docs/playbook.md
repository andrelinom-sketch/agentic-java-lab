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

O CC-EXP-01 é uma única execução de Claude Code sobre a Story 1.2
(transferência válida). Suas limitações gerais estão no fim deste
documento e valem para todas as recomendações abaixo.

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

**Status:** Hipótese · **Origem:** CC-EXP-01

**Recomendação atual:** quando se quiser que o agente escreva testes
próprios, dizer isso no prompt e indicar a localização, fora de `reference`
(AD-8).

**Fatos observados**
- `df644e4` não adiciona nem altera nenhum arquivo em `src/test`.
- O AD-8 prevê testes escritos pelo agente, fora do pacote `reference`.
- O prompt pedia para rodar `./mvnw -B verify` e confirmar os testes
  verdes. Não pedia novos testes.

**Interpretação:** o agente satisfez o contrato existente e não o ampliou.
O CC-EXP-01 não mostra se isso decorre do prompt ou de outro fator.

**Limitações:** não há nenhuma execução em que os testes tenham sido
pedidos para comparar. Também não se avaliou se testes adicionais teriam
valor nesta story.

**O que futuros experimentos podem mostrar:**
- se o pedido explícito produz testes;
- onde esses testes são colocados;
- se testam algo além dos testes de referência ou apenas os repetem.

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

- Tudo deriva de **uma única execução**: um agente, um modelo, uma story,
  sem repetição e sem grupo de controle.
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
