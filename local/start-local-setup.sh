#!/usr/bin/env bash
cd "$(dirname "$0")" || exit

echo
echo "                                    Port number"
echo " * Postgres                         5432"
echo

docker compose -f ./compose.yaml -p ttt up -d
