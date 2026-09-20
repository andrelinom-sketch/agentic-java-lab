#!/usr/bin/env bash

set -u

echo "======================================"
echo " Agentic Java Lab - Environment Check"
echo "======================================"
echo

check_command() {
    local command_name="$1"
    local version_command="$2"

    printf "%-18s" "$command_name"

    if command -v "$command_name" >/dev/null 2>&1; then
        echo "OK"
        eval "$version_command"
    else
        echo "NOT INSTALLED"
    fi

    echo
}

check_command "git" "git --version"
check_command "java" "java -version"
check_command "mvn" "mvn -version"
check_command "docker" "docker --version"
check_command "node" "node --version"
check_command "npm" "npm --version"
check_command "python3" "python3 --version"
check_command "uv" "uv --version"

echo "Docker Compose:"
if docker compose version >/dev/null 2>&1; then
    echo "OK"
    docker compose version
else
    echo "NOT INSTALLED"
fi

echo
echo "NVM:"

export NVM_DIR="${NVM_DIR:-$HOME/.nvm}"

if [ -s "$NVM_DIR/nvm.sh" ]; then
    # shellcheck disable=SC1090
    source "$NVM_DIR/nvm.sh"
fi

if command -v nvm >/dev/null 2>&1; then
    echo "OK"
    nvm --version
else
    echo "NOT INSTALLED"
fi

echo
echo "======================================"
echo " Environment check finished"
echo "======================================"
