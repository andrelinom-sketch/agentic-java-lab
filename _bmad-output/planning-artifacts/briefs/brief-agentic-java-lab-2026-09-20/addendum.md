# Addendum: Agentic Java Lab

Material de profundidade que não cabe no brief e deve alimentar o PRD, a arquitetura e as ADRs. Não contém trilha de auditoria (ver `.memlog.md`).

## 1. Instrumentos de medição (para PRD e arquitetura)

- **CI comum.** Deve aplicar as mesmas verificações a qualquer PR, de qualquer agente. Conteúdo mínimo a definir no PRD (por exemplo, build, testes e análise estática). A escolha da ferramenta de análise estática é decisão arquitetural (provável ADR).
- **Criação do CI.** Story específica, sob supervisão humana direta e aprovada pelo autor antes do primeiro experimento de implementação medido. Não conta como execução comparativa de autonomia.
- **Proteção da baseline.** CI, testes de referência, critérios de aceite e demais instrumentos que funcionam como régua passam a fazer parte da baseline. Mudanças durante um experimento exigem aprovação humana e são registradas como alteração de protocolo ou intervenção. O mecanismo técnico (por exemplo, proprietários de código e proteção de branch) será decidido depois.
- **Arquivos de contexto** (`CLAUDE.md`, `AGENTS.md` e equivalentes): variável independente do experimento. Versionados e congelados por experimento.
- **Risco conhecido:** um agente pode enfraquecer testes ou o workflow para passar. Isso deve ser detectável e registrado.

## 2. Rubrica

- Documento versionado. Cada entrada do diário registra a versão usada.
- **Observável, vindo de artefatos:** contexto e prompt fornecidos, quantidade de intervenções, testes criados e seus resultados, tempo, custo quando disponível, commits, diffs e artefatos produzidos.
- **Julgamento, com definição operacional prévia:**
  - *Aderência à arquitetura:* checklist derivado das ADRs e das regras arquiteturais existentes.
  - *Qualidade da entrega:* critérios de aceite, testes, análise estática e checklist de revisão.
  - *Erro:* resultado que viola requisito, critério de aceite, teste, ADR ou regra previamente definida, ou que produz comportamento comprovadamente incorreto.
  - *Decisão:* escolha entre alternativas que altera implementação, arquitetura, comportamento ou escopo e que não estava determinada previamente.
  - *Autocorreção:* o agente identifica e corrige um problema antes de uma intervenção humana apontar a solução específica.
- **Decisões são contadas a partir do diff** contra o que a story determinava, e não pela narração do agente. Registrar também o grau de especificação da story.
- **Intervenção:** usar uma taxonomia fechada (esclarecimento, correção, aprovação, assumir o trabalho). Sempre que possível, contar a partir do histórico de git, PRs e conversas, e não da memória.
- **Variantes por papel** (planejador, implementador, revisor): a rubrica começa pelo implementador. Para o BMAD como planejador e o Codex como revisor ainda não existe rubrica adequada.

## 3. Diário (`docs/lab/`)

Princípio: a evidência mora onde nasce (git, PR, CI, artefatos). O diário interpreta e aponta para ela sem copiar.

Núcleo mínimo sugerido, em dois momentos (dois commits, para o congelamento ser verificável):

- **Pré-declaração (antes da execução):** objetivo e hipótese; ferramenta, modelo, versão e data; contexto e critérios usados (com links para as versões congeladas).
- **Resultado (depois):** resultado contra os critérios; intervenções humanas, falhas e correções (com links); decisões e aprendizados; **desvios do protocolo**; limitações.

Tempo e custo entram quando medidos. Os demais campos do modelo do `LAB-PLAN.md` são opcionais. Este núcleo é uma sugestão a validar no PRD.

## 4. Experimento 01 (BMAD)

- Os critérios de avaliação do BMAD como planejador são **parcialmente post hoc**: limitação metodológica declarada.
- **Linha de base congelada:** o estado do plano no commit `a8e2649`. Antes de o BMAD avançar, registrar a lista de ambiguidades e inconsistências já conhecidas, para medir depois o que o processo acrescentou.
- Ideia inicial de métrica: comparar o `LAB-PLAN.md` com os artefatos produzidos e verificar quais ambiguidades, inconsistências, riscos e decisões foram descobertos. Limites já identificados: contar descobertas premia volume, e falta um contrafactual (por exemplo, o que um único prompt direto teria achado).
- **Circularidade:** a aderência à arquitetura é medida contra ADRs produzidas com o BMAD e aprovadas pelo autor. A qualidade do plano é uma variável de entrada, e a revisão humana dele deve ser registrada.

## 5. Pesquisa da lacuna (status de verificação)

Uma pesquisa exploratória foi feita por um subagente e ficou **incompleta**: buscas e aberturas de páginas foram negadas pelo sistema de permissões no meio do trabalho. Cerca de 8 buscas, todas em inglês, e 4 fontes abertas.

**Abertas e conferidas:**
- Terminal-Bench (arXiv 2601.11868): benchmark de capacidade em terminal.
- Khelifi, Ouni e Khemaja, "Behind Agentic Pull Requests" (MSR 2026): intervenção humana em PRs de agentes reais. Só a página-resumo foi lida, não o artigo.
- METR, atualização de fev/2026 sobre uplift de produtividade.
- "From Prompt to Process" (arXiv 2606.04967): compara BMAD, Spec-Kit e outros por documentação, sem medir execuções.

**Vistas apenas em resumo de busca (não usar como evidência pública):** SWE-Bench Pro, texto da OpenAI sobre o SWE-bench Verified, dataset AIDev, SWE-chat, o artigo original do RCT do METR, textos da Anthropic sobre autonomia e frameworks de supervisão graduada.

**Conclusão provisória:** a hipótese de lacuna se sustenta só em parte. Existe material empírico independente sobre intervenção humana e produtividade. O que parece descoberto é um registro controlado e replicável de uma mesma especificação atravessando níveis de autonomia, e avaliação do BMAD medida em execuções.

**Afirmações a evitar:** "só há demos", "nada mede intervenção humana", "somos os primeiros" e "o BMAD nunca foi avaliado".

**Buscas que ficaram pendentes:** português, Anthropic sobre autonomia, aderência à arquitetura, crítica ao spec-kit, revisão de código por IA e repositórios públicos com diário ou ADRs de agentes.

## 6. Legado e Strangler Fig

Objetivo de aprendizado declarado do laboratório completo, fora da V1. Não introduzir um monólito ou complexidade distribuída artificialmente. Quando chegar o ciclo de modernização, declarar as limitações de um legado sintético: um agente trabalhando em um legado de brinquedo diz pouco sobre um legado real.

## 7. Pendências antes da publicação

1. Verificar as fontes complementares. Só entram como evidência as abertas e conferidas.
2. Escolher a licença do repositório.
3. Corrigir a divergência de numeração de fases entre `README.md` e `LAB-PLAN.md`, e o item "Configurar BMAD" ainda pendente no README.
4. Decidir mesma story ou stories equivalentes quando o segundo agente entrar.
5. Executar o polimento do brief (`bmad-review`, lentes de estrutura e prosa).
