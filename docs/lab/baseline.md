# Baseline

Este documento lista os artefatos congelados a cada marco (`freeze/exp-NN`)
e a régua comum de verificação (AD-9, AD-10).

## Infraestrutura comum (pré-experimentos)

Artefatos que compõem a régua e não devem ser alterados por um agente
implementador sem aprovação humana:

- `.github/workflows/ci.yml` — CI (GitHub Actions, `./mvnw -B verify`)
- `pom.xml` — plugins de build, JaCoCo (informativo) e dependência ArchUnit
- `src/test/java/dev/agenticlab/reference/**` — testes de referência
  - `ApplicationSmokeTest` — contexto Spring sobe contra PostgreSQL real
  - `ArchitectureTest` — fronteiras arquiteturais mínimas (AD-1, AD-2)
- `src/test/resources/archunit.properties` — configuração do ArchUnit
- `docs/lab/baseline.md` — este arquivo

## Marcos

Nenhum marco (`freeze/exp-NN`) foi criado ainda.

Quando o primeiro marco for criado, registrar aqui, por marco:

```markdown
### freeze/exp-NN

- Data:
- Commit:
- Objetivo do experimento:
- Agente/ferramenta avaliada:
- Story medida:
- Testes de referência adicionados para esta story:
- Desvios registrados durante o experimento:
```

## Desvios

Nenhum até o momento.
