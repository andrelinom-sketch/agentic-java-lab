# Experimento 01 — BMAD

## Objetivo

Avaliar o BMAD Method como ferramenta para estruturar a fase inicial de
engenharia de software antes da implementação.

O objetivo desta fase é transformar a ideia do Agentic Java Lab em:

Ideia → Análise → PRD → Arquitetura → ADRs → Épicos → Stories

Inicialmente serão criadas apenas 3 a 5 stories.

---

## Ambiente

- BMAD Method: 6.12.0
- BMad Core: 6.12.0
- Módulo: BMM
- Integração inicial: Claude Code
- Comunicação dos agentes: Portuguese
- Documentos: Portuguese
- Nível configurado: intermediate

---

## Dependências descobertas

Durante a primeira tentativa de instalação, o BMAD informou que `uv`
era obrigatório para determinados skills que executam scripts Python.

Ambiente identificado:

- Python 3.12.3
- uv 0.12.17

A dependência foi adicionada ao setup reproduzível do laboratório e ao
script `scripts/check-environment.sh`.

---

## Configuração

Diretório BMAD:

`_bmad/`

Skills do Claude Code:

`.claude/skills/`

Artefatos de planejamento:

`_bmad-output/planning-artifacts/`

Artefatos de implementação:

`_bmad-output/implementation-artifacts/`

Conhecimento permanente do projeto:

`docs/`

---

## Decisões tomadas

### BMM como primeiro módulo

Nesta fase será utilizado somente o BMad Method (BMM).

Outros módulos serão adicionados apenas quando houver necessidade
identificada durante os experimentos.

### Claude Code como integração inicial

A integração com Claude Code foi instalada porque ele será o primeiro
coding agent estudado no laboratório.

Nesta fase, entretanto, o foco ainda é planejamento.

### Compatibilidade legada

Os 21 deprecated compatibility shim skills não foram instalados.

O laboratório começa diretamente com os skills atuais do BMAD 6.12.0.

### Configuração intermediária

O nível `intermediate` foi escolhido propositalmente.

Embora o participante possua experiência profissional avançada em
desenvolvimento e arquitetura, nesta fase é desejável que o BMAD explique
parte de sua metodologia para facilitar o aprendizado do processo.

---

## Observações da instalação

O instalador recebeu pela linha de comando:

- user-name: Andrelino
- communication-language: Portuguese
- document-output-language: Portuguese

Durante o fluxo interativo, o idioma de comunicação apareceu inicialmente
como `English` e precisou ser alterado manualmente para `Portuguese`.

Esse comportamento deve ser observado em futuras reinstalações.

---

## Resultado da instalação

Instalação concluída com sucesso.

Foram configurados:

- BMad Core 6.12.0
- BMad Method 6.12.0
- 29 skills para Claude Code
- diretório `_bmad`
- diretório `_bmad-output`
- configurações do BMM

---

## Próximo experimento

Utilizar o BMAD pela primeira vez para entender o fluxo recomendado para
um projeto novo e iniciar a etapa de análise/produto.

Ainda não iniciar implementação Java.

---

## Aprendizados

Até este ponto:

1. BMAD é instalado dentro do contexto do projeto.
2. Os skills são adaptados à ferramenta/agente selecionado.
3. BMAD diferencia configuração do projeto e configuração do usuário.
4. Os artefatos de planejamento e implementação possuem áreas separadas.
5. `uv` é uma dependência necessária para parte dos skills.
6. A instalação deve ser tratada como parte reproduzível do ambiente.
