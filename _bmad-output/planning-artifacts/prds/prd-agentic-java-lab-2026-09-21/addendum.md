# Addendum: PRD Agentic Java Lab

Material que não cabe no corpo do PRD. Não contém trilha de auditoria (ver `.memlog.md`).

## Desvio de protocolo retrospectivo (Experimento 01, BMAD)

**Registrado em:** 2026-09-21. **Natureza:** retrospectiva.

**O que estava previsto.** O addendum do brief (§4) previa registrar, antes de o BMAD avançar, a lista de ambiguidades e inconsistências já conhecidas no estado de referência do plano (`LAB-PLAN.md` no commit `a8e2649`), para depois medir o que o processo acrescentou.

**O que ocorreu.** Essa lista **não foi congelada no momento previsto**. O brief foi escrito e commitado (`b27e474`, 2026-09-21) sem ela.

**Consequência metodológica.** Esta lista, feita depois, **não pode ser usada como evidência forte** para atribuir com certeza o que Andrelino já sabia e o que o BMAD descobriu. Ela não reconstrói a memória do autor.

**Fatos identificáveis nos artefatos.** Somente o que existe no repositório, com a fonte de cada um:

| # | Fato | Presente no estado de referência (`a8e2649`) | Primeiro registrado em |
|---|---|---|---|
| 1 | A numeração de fases diverge: no `LAB-PLAN.md`, Fase 1 é BMAD (§11); no `README.md`, Fase 1 é Foundation e Fase 2 é BMAD. | Sim | Addendum do brief, §7, item 3 (commit `b27e474`) |
| 2 | O `README.md` mantém "Configurar BMAD" como pendente na lista de progresso, embora o BMAD já esteja instalado. | Sim | Addendum do brief, §7, item 3 (commit `b27e474`) |
| 3 | A API do plano tem só `POST /accounts`, `POST /transfers` e `GET /transfers/{id}`, mas o exemplo mostra saldos que mudam (Conta A de R$ 1.000 para R$ 900). Sem consulta de conta e sem depósito, o saldo não é observável pela API nem a conta recebe saldo. | Sim (`LAB-PLAN.md` §5) | `.memlog.md` deste PRD (decisão sobre a API da V1), 2026-09-21 |

**Não registrado aqui.** Qualquer afirmação sobre o que o autor já sabia antes do processo, e qualquer contagem de "descobertas do BMAD". A atribuição fica em aberto até haver evidência congelada.

**Destino.** Este registro deve ser referenciado no diário do Experimento 01 (`docs/lab/01-bmad.md`), que hoje não o menciona.
