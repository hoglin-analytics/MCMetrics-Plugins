#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
REPO_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

cd "$REPO_ROOT"

./gradlew copyBuiltJars

mkdir -p   "$SCRIPT_DIR/plugins/bukkit"   "$SCRIPT_DIR/plugins/bungee"   "$SCRIPT_DIR/plugins/velocity"   "$SCRIPT_DIR/plugins/fabric"

install -m 0644 "$REPO_ROOT/build/release/MCMetrics-Bukkit.jar" "$SCRIPT_DIR/plugins/bukkit/MCMetrics.jar"
install -m 0644 "$REPO_ROOT/build/release/MCMetrics-Bungee.jar" "$SCRIPT_DIR/plugins/bungee/MCMetrics.jar"
install -m 0644 "$REPO_ROOT/build/release/MCMetrics-Velocity.jar" "$SCRIPT_DIR/plugins/velocity/MCMetrics.jar"
install -m 0644 "$REPO_ROOT/build/release/MCMetrics-Fabric.jar" "$SCRIPT_DIR/plugins/fabric/MCMetrics.jar"

printf 'Synced dev plugin jars into %s/plugins
' "$SCRIPT_DIR"
