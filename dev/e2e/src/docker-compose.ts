import { spawn } from 'node:child_process';
import { existsSync } from 'node:fs';
import net from 'node:net';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

import type { E2ETarget } from './targets.js';

const e2eDir = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
export const devDir = path.resolve(e2eDir, '..');

export interface ComposeContext {
  target: E2ETarget;
  projectName: string;
  proxyPort: number;
  minecraftPort: number;
  env: NodeJS.ProcessEnv;
}

export function createComposeContext(target: E2ETarget): ComposeContext {
  const proxyPort = numberEnv('HOGLIN_API_PROXY_ADMIN_PORT', 18080);
  const minecraftPort = numberEnv(target.portEnv, target.defaultPort);
  const projectName = process.env.COMPOSE_PROJECT_NAME || `mcmetrics-e2e-${target.id}-${Date.now().toString(36)}`;

  const env: NodeJS.ProcessEnv = {
    ...process.env,
    COMPOSE_PROJECT_NAME: projectName,
    HOGLIN_API_PROXY_ADMIN_PORT: String(proxyPort),
    MCMETRICS_API_SERVER: 'http://hoglin-api-proxy:8080',
    MCMETRICS_SERVER_KEY: process.env.MCMETRICS_SERVER_KEY || 'e2e-server-key',
    MCMETRICS_AUTO_FLUSH_INTERVAL: process.env.MCMETRICS_AUTO_FLUSH_INTERVAL || '1000',
    MCMETRICS_AUTO_FLUSH_MAX_BATCH_SIZE: process.env.MCMETRICS_AUTO_FLUSH_MAX_BATCH_SIZE || '100',
    [target.instanceEnv]: process.env[target.instanceEnv] || target.instanceId,
  };

  return { target, projectName, proxyPort, minecraftPort, env };
}

export function composeOptions(ctx: ComposeContext): string[] {
  const args: string[] = [];
  if (existsSync(path.join(devDir, '.env'))) {
    args.push('--env-file', '.env');
  }

  args.push('-f', ctx.target.composeFile, '-f', 'e2e/compose.e2e.yml');
  return args;
}

export async function dockerCompose(ctx: ComposeContext, args: string[]): Promise<void> {
  await run('docker', ['compose', ...composeOptions(ctx), ...args], {
    cwd: devDir,
    env: ctx.env,
  });
}

export async function run(command: string, args: string[], options: { cwd: string; env: NodeJS.ProcessEnv }): Promise<void> {
  await new Promise<void>((resolve, reject) => {
    const child = spawn(command, args, {
      cwd: options.cwd,
      env: options.env,
      stdio: 'inherit',
    });

    child.on('error', reject);
    child.on('exit', code => {
      if (code === 0) resolve();
      else reject(new Error(`${command} ${args.join(' ')} exited with ${code}`));
    });
  });
}

export async function waitForHttp(url: string, timeoutMs = 60_000): Promise<void> {
  const deadline = Date.now() + timeoutMs;
  let lastError: unknown;

  while (Date.now() < deadline) {
    try {
      const response = await fetch(url);
      if (response.ok) return;
      lastError = new Error(`HTTP ${response.status}`);
    } catch (error) {
      lastError = error;
    }

    await delay(1_000);
  }

  throw new Error(`Timed out waiting for ${url}: ${lastError instanceof Error ? lastError.message : String(lastError)}`);
}

export async function waitForTcp(host: string, port: number, timeoutMs = 120_000): Promise<void> {
  const deadline = Date.now() + timeoutMs;
  let lastError: unknown;

  while (Date.now() < deadline) {
    try {
      await connectTcp(host, port);
      return;
    } catch (error) {
      lastError = error;
      await delay(1_000);
    }
  }

  throw new Error(`Timed out waiting for TCP ${host}:${port}: ${lastError instanceof Error ? lastError.message : String(lastError)}`);
}

function connectTcp(host: string, port: number): Promise<void> {
  return new Promise((resolve, reject) => {
    const socket = net.createConnection({ host, port });
    socket.once('connect', () => {
      socket.end();
      resolve();
    });
    socket.once('error', reject);
    socket.setTimeout(2_000, () => {
      socket.destroy(new Error('TCP connect timeout'));
    });
  });
}

function numberEnv(name: string, fallback: number): number {
  const raw = process.env[name];
  if (!raw) return fallback;

  const value = Number(raw);
  if (!Number.isInteger(value) || value < 1 || value > 65535) {
    throw new Error(`Invalid ${name}: ${raw}`);
  }

  return value;
}

function delay(ms: number): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms));
}
