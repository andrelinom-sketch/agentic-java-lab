# Agentic Java Lab — Setup do Ambiente

Este documento descreve como preparar uma máquina para executar o
**Agentic Java Lab**.

O objetivo é permitir que outra pessoa clone o repositório e reproduza
o ambiente utilizado durante os experimentos.

> O laboratório foi criado e validado inicialmente no Ubuntu 24.04 LTS amd64.
> Outras distribuições Linux podem funcionar, mas os comandos de instalação
> podem ser diferentes.

---

## 1. Ambiente de referência

| Ferramenta | Requisito do laboratório | Ambiente de referência |
|---|---:|---:|
| Ubuntu | 24.04 LTS | 24.04 LTS |
| Git | 2.x+ | 2.43.0 |
| Java | 21 LTS | OpenJDK 21.0.12 |
| Maven | 3.8+ | 3.8.7 |
| Docker Engine | versão atual compatível | 29.8.1 |
| Docker Compose | Compose v2+ | 5.5.1 |
| Node.js | 22 LTS | 22.23.2 |
| npm | compatível com Node 22 | 10.9.8 |
| NVM | recomendado | 0.40.7 |
| BMAD Method | 6.x | 6.12.0 |

As versões da coluna **Ambiente de referência** registram o ambiente no
qual o laboratório foi iniciado.

Não é obrigatório utilizar exatamente o mesmo patch de todas as
ferramentas, salvo quando algum experimento indicar explicitamente isso.

---

# 2. Clonar o laboratório

Instale Git caso ainda não esteja disponível:

```bash
sudo apt update
sudo apt install -y git
```

Verifique:

```bash
git --version
```

Clone o projeto:

```bash
git clone https://github.com/andrelinom-sketch/agentic-java-lab.git
cd agentic-java-lab
```

---

# 3. Java 21

O laboratório utiliza **Java 21 LTS**.

Verifique primeiro:

```bash
java -version
```

Caso Java 21 não esteja instalado:

```bash
sudo apt update
sudo apt install -y openjdk-21-jdk
```

Verifique novamente:

```bash
java -version
javac -version
```

A versão principal deve ser:

```text
21
```

---

# 4. Maven

Verifique:

```bash
mvn -version
```

Caso não esteja instalado:

```bash
sudo apt update
sudo apt install -y maven
```

Depois:

```bash
mvn -version
```

Confirme também que o Maven está utilizando **Java 21**.

Exemplo:

```text
Java version: 21.x
```

---

# 5. Docker Engine e Docker Compose

O laboratório utiliza Docker para infraestrutura local e posteriormente
para testes com Testcontainers.

## 5.1 Evitar instalação via Snap

Neste laboratório utilizamos os pacotes fornecidos pelo repositório
oficial do Docker.

Não é necessário utilizar Docker Desktop.

## 5.2 Preparar o repositório oficial

Instale os pré-requisitos:

```bash
sudo apt update
sudo apt install -y ca-certificates curl
```

Crie o diretório para as chaves:

```bash
sudo install -m 0755 -d /etc/apt/keyrings
```

Baixe a chave do Docker:

```bash
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg \
  -o /etc/apt/keyrings/docker.asc
```

Configure a permissão:

```bash
sudo chmod a+r /etc/apt/keyrings/docker.asc
```

Adicione o repositório:

```bash
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
```

Atualize os pacotes:

```bash
sudo apt update
```

## 5.3 Instalar Docker

```bash
sudo apt install -y \
  docker-ce \
  docker-ce-cli \
  containerd.io \
  docker-buildx-plugin \
  docker-compose-plugin
```

Verifique:

```bash
docker --version
docker compose version
```

## 5.4 Permitir Docker sem sudo

Adicione o usuário atual ao grupo `docker`:

```bash
sudo usermod -aG docker $USER
```

Aplique a nova associação ao grupo:

```bash
newgrp docker
```

Em uma nova sessão de login essa associação também será carregada
automaticamente.

Teste sem `sudo`:

```bash
docker run --rm hello-world
```

O resultado deve conter:

```text
Hello from Docker!
```

> Atenção: pertencer ao grupo `docker` concede privilégios elevados
> equivalentes, em muitos cenários, ao acesso administrativo da máquina.
> Faça isso somente em um ambiente em que essa configuração seja apropriada.

---

# 6. NVM e Node.js

O laboratório utiliza **NVM** para controlar a versão do Node.js.

Isso evita depender da versão de Node disponível nos repositórios do
Ubuntu.

## 6.1 Instalar NVM

Consulte o projeto oficial do NVM para utilizar a versão corrente do
instalador.

Após instalar, abra um novo terminal ou carregue o NVM:

```bash
export NVM_DIR="$HOME/.nvm"
[ -s "$NVM_DIR/nvm.sh" ] && \. "$NVM_DIR/nvm.sh"
```

Verifique:

```bash
nvm --version
```

## 6.2 Instalar a versão do projeto

O repositório possui:

```text
.nvmrc
```

Portanto, dentro da raiz do projeto execute:

```bash
nvm install
nvm use
```

O NVM utilizará automaticamente a versão registrada no `.nvmrc`.

Verifique:

```bash
node --version
npm --version
```

No ambiente inicial deste laboratório:

```text
Node.js 22.23.2
npm 10.9.8
```

Não é necessário atualizar o npm apenas porque uma versão mais nova
está disponível.

---

# 7. Validar todo o ambiente

O repositório possui um script para verificar as principais ferramentas.

Execute:

```bash
./scripts/check-environment.sh
```

O resultado deverá indicar `OK` para:

```text
Git
Java
Maven
Docker
Node
npm
Docker Compose
NVM
```

Se alguma ferramenta aparecer como ausente, corrija-a antes de iniciar
os experimentos do laboratório.

---

# 8. GitHub CLI

O GitHub CLI não é necessário para executar a aplicação Java, mas será
útil no laboratório para trabalhar com repositórios, branches e Pull
Requests.

Verifique:

```bash
gh --version
```

Se ainda não estiver instalado, consulte a documentação oficial do
GitHub CLI para sua distribuição.

Depois da instalação, autentique:

```bash
gh auth login
```

E confira:

```bash
gh auth status
```

---

# 9. BMAD Method

O BMAD é utilizado na primeira fase do laboratório para transformar a
ideia inicial em:

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

A configuração do BMAD será documentada conforme o experimento for
executado.

A versão utilizada no início do laboratório é:

```text
BMAD Method 6.12.0
```

Não atualize automaticamente o BMAD durante um experimento sem avaliar
se houve mudança de comportamento, estrutura ou workflows.

---

# 10. Verificação manual

Caso queira verificar individualmente as ferramentas:

```bash
git --version
java -version
mvn -version
docker --version
docker compose version
node --version
npm --version
nvm --version
```

---

# 11. Princípio de versionamento

O laboratório diferencia dois conceitos.

### Versão requerida

É a versão principal ou mínima necessária para executar o projeto.

Exemplo:

```text
Java 21
Node 22
Maven 3.8+
```

### Ambiente de referência

É a versão exata utilizada durante determinado experimento.

Exemplo:

```text
Java 21.0.12
Node 22.23.2
Maven 3.8.7
Docker 29.8.1
```

Isso permite reproduzir experimentos sem obrigar todos os participantes
a manter exatamente o mesmo patch de cada ferramenta.

---

# 12. Próximo passo

Quando:

```bash
./scripts/check-environment.sh
```

estiver indicando que o ambiente está correto, volte ao:

```text
README.md
```

para seguir a sequência do laboratório.

Para compreender o projeto completo, decisões, fases e regras para
agentes de IA, consulte:

```text
LAB-PLAN.md
```
