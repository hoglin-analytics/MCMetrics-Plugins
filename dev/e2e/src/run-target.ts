import { getTarget, targetMinecraftVersion } from './targets.js';
import { createComposeContext, devDir, dockerCompose, run, waitForHttp, waitForTcp } from './docker-compose.js';

const target = getTarget(process.argv[2] || process.env.E2E_TARGET || 'paper');
const ctx = createComposeContext(target);
const proxyUrl = `http://127.0.0.1:${ctx.proxyPort}`;
const minecraftHost = process.env.E2E_MINECRAFT_HOST || '127.0.0.1';
const minecraftVersion = targetMinecraftVersion(target, ctx.env);
const keepStack = process.env.E2E_KEEP_STACK === 'true';

console.log(`Running MCMetrics E2E target: ${target.id}`);
console.log(`Compose project: ${ctx.projectName}`);
console.log(`Minecraft endpoint: ${minecraftHost}:${ctx.minecraftPort} (${minecraftVersion})`);
console.log(`Hoglin API proxy admin: ${proxyUrl}`);

let started = false;
try {
  await dockerCompose(ctx, ['up', '-d', '--build', 'hoglin-api-proxy']);
  await waitForHttp(`${proxyUrl}/__admin/health`, 60_000);

  await dockerCompose(ctx, ['up', '-d']);
  started = true;
  await waitForTcp(minecraftHost, ctx.minecraftPort, 180_000);

  await run('npx', ['vitest', 'run', '--config', 'vitest.config.ts', 'tests/player-actions.e2e.test.ts'], {
    cwd: `${devDir}/e2e`,
    env: {
      ...ctx.env,
      E2E_RUNNING: 'true',
      E2E_TARGET: target.id,
      E2E_TARGET_INSTANCE_ID: ctx.env[target.instanceEnv] || target.instanceId,
      E2E_PROXY_URL: proxyUrl,
      E2E_MINECRAFT_HOST: minecraftHost,
      E2E_MINECRAFT_PORT: String(ctx.minecraftPort),
      E2E_MINECRAFT_VERSION: minecraftVersion,
    },
  });
} finally {
  if (started && keepStack) {
    console.log(`Keeping E2E stack ${ctx.projectName} because E2E_KEEP_STACK=true`);
  } else {
    await dockerCompose(ctx, ['down', '-v', '--remove-orphans']).catch(error => {
      console.error(`Failed to stop E2E stack ${ctx.projectName}:`, error);
    });
  }
}
