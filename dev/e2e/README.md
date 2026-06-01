# MCMetrics plugin E2E tests

The E2E harness is TypeScript because the Minecraft fake-player tooling is better there than in Java right now. The repo can stay Java/Gradle for production code while using TypeScript only for protocol-level test orchestration.

## What the harness does

For one target at a time it:

1. Builds/runs the existing Docker Compose stack for the target.
2. Adds a `hoglin-api-proxy` service that mocks Hoglin endpoints and captures every API call.
3. Forces the plugin to talk to `http://hoglin-api-proxy:8080` with a non-empty E2E server key.
4. Uses `minecraft-protocol` in offline/cracked auth mode to connect a fake player.
5. Runs only tests matching the target capabilities.

These stacks are not public servers. Host port bindings use `127.0.0.1`, and the Minecraft servers/proxies are configured with `online-mode=false` / `enforce-secure-profile=false` so fake offline players can connect.

## Install

```bash
cd dev/e2e
npm ci
```

## Unit tests for the harness

```bash
npm test
npm run build
```

## Run an E2E target

Build/sync plugin jars first, then run a target:

```bash
cd dev
./sync-jars.sh
cd e2e
npm run e2e -- paper
npm run e2e -- fabric
npm run e2e -- velocity
```

The runner creates a unique Compose project, starts the Hoglin proxy first, starts the Minecraft target, runs Vitest, then tears the stack down with `docker compose down -v`.

Set `E2E_KEEP_STACK=true` to leave the stack running after tests for debugging.

## Target matrix

| Target | Plugin type | Capabilities currently covered |
| --- | --- | --- |
| `paper` | Bukkit on Paper | join, quit, chat, player count, performance, purchase command |
| `spigot` | Bukkit on Spigot | join, quit, chat, player count, performance, purchase command |
| `folia` | Bukkit on Folia | join, quit, chat, player count, performance, purchase command |
| `fabric` | Fabric mod | join, quit, chat, player count, performance, purchase command |
| `velocity` | Velocity proxy | join, quit, player count, performance, purchase command |
| `bungee` | BungeeCord proxy | join, quit, player count, performance, purchase command |
| `waterfall` | Waterfall proxy | join, quit, player count, performance, purchase command |

The initial executable E2E tests cover join/quit on all targets and chat only on standalone targets because the proxy plugins do not currently register chat listeners.

## GitHub Actions

The committed workflow at `.github/workflows/e2e.yml` runs this harness in a target matrix.

- `workflow_dispatch` can run either the full matrix or one selected target.
- `pull_request` and `push` run the full matrix when plugin/dev/E2E files change.
- Matrix execution uses `max-parallel: 1` so targets are run one at a time.
- Failed target logs are uploaded as workflow artifacts.

Each matrix target runs:

```bash
./dev/sync-jars.sh
cd dev/e2e
npm ci
npm run e2e -- <target>
```

## Hoglin API proxy

The mock proxy captures all non-admin requests. Admin endpoints used by tests:

- `GET /__admin/health`
- `POST /__admin/reset`
- `GET /__admin/calls`

Mocked Hoglin endpoints:

- `PUT /analytics/:serverKey` returns `{ "ok": true }` and captures the analytic batch.
- `GET /experiments/:serverKey` returns `[]`.
- `GET /experiments/:serverKey/:experimentId/evaluate?playerUUID=...` returns `{ "inExperiment": false }`.

Assertions should inspect captured analytics semantically instead of depending on request order.
