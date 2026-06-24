#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

usage() {
  cat <<'USAGE'
Usage: ./up.sh <setup> [--no-sync] [docker compose up args...]

Setups:
  paper        Bukkit plugin on Paper
  spigot       Bukkit plugin on Spigot
  folia        Bukkit plugin on Folia
  fabric       Fabric mod on Fabric
  velocity     Velocity plugin on Velocity + Paper backend
  bungee       Bungee plugin on BungeeCord + Paper backend
  waterfall    Bungee plugin on Waterfall + Paper backend

Examples:
  ./up.sh paper
  ./up.sh velocity -d
  ./up.sh fabric --no-sync --pull always

Use raw docker compose commands for logs/down/reset, e.g.:
  docker compose -f compose.bukkit-paper.yml logs -f
  docker compose -f compose.bukkit-paper.yml down -v
USAGE
}

if [[ $# -lt 1 ]]; then
  usage
  exit 1
fi

setup="$1"
shift

compose_file=""
case "$setup" in
  paper|bukkit-paper) compose_file="compose.bukkit-paper.yml" ;;
  spigot|bukkit-spigot) compose_file="compose.bukkit-spigot.yml" ;;
  folia|bukkit-folia) compose_file="compose.bukkit-folia.yml" ;;
  fabric) compose_file="compose.fabric.yml" ;;
  velocity) compose_file="compose.velocity.yml" ;;
  bungee|bungeecord) compose_file="compose.bungee.yml" ;;
  waterfall) compose_file="compose.waterfall.yml" ;;
  -h|--help|help) usage; exit 0 ;;
  *)
    printf 'Unknown setup: %s

' "$setup" >&2
    usage >&2
    exit 1
    ;;
esac

sync_jars=true
if [[ "${1:-}" == "--no-sync" ]]; then
  sync_jars=false
  shift
fi

if [[ "$sync_jars" == true ]]; then
  ./sync-jars.sh
fi

compose_args=()
if [[ -f .env ]]; then
  compose_args+=(--env-file .env)
fi

exec docker compose "${compose_args[@]}" -f "$compose_file" up "$@"
