import { once } from 'node:events';
import { setTimeout as delay } from 'node:timers/promises';
import minecraftProtocol from 'minecraft-protocol';

interface MinecraftClient {
  chat(message: string): void;
  end(reason?: string): void;
  on(event: string, listener: (...args: unknown[]) => void): this;
  once(event: string, listener: (...args: unknown[]) => void): this;
  removeListener(event: string, listener: (...args: unknown[]) => void): this;
}

export interface MinecraftBotOptions {
  host: string;
  port: number;
  username: string;
  version: string;
}

export class MinecraftBot {
  private client: MinecraftClient | undefined;

  constructor(private readonly options: MinecraftBotOptions) {}

  async connect(timeoutMs = 30_000): Promise<void> {
    const startedAt = Date.now();
    let lastError: unknown;

    while (Date.now() - startedAt < timeoutMs) {
      try {
        this.client = minecraftProtocol.createClient({
          host: this.options.host,
          port: this.options.port,
          username: this.options.username,
          version: this.options.version,
          auth: 'offline',
        }) as MinecraftClient;

        await waitForOneOf(this.client, ['login', 'spawn'], 15_000);
        await delay(750);
        return;
      } catch (error) {
        lastError = error;
        this.client?.end('retrying e2e connection');
        this.client = undefined;
        await delay(1_000);
      }
    }

    throw new Error(`Failed to connect fake player ${this.options.username} to ${this.options.host}:${this.options.port}: ${lastError instanceof Error ? lastError.message : String(lastError)}`);
  }

  async chat(message: string): Promise<void> {
    if (!this.client) throw new Error('Cannot chat before connecting the bot');
    this.client.chat(message);
    await delay(500);
  }

  async command(command: string): Promise<void> {
    await this.chat(command.startsWith('/') ? command : `/${command}`);
  }

  async disconnect(): Promise<void> {
    if (!this.client) return;

    const client = this.client;
    const ended = waitForOneOf(client, ['end', 'disconnect'], 5_000).catch(() => undefined);
    client.end('e2e complete');
    await ended;
    this.client = undefined;
  }
}

export function uniqueUsername(prefix = 'E2E'): string {
  return `${prefix}${Date.now().toString(36).slice(-8)}`.slice(0, 16);
}

async function waitForOneOf(client: MinecraftClient, events: string[], timeoutMs: number): Promise<void> {
  const waiters = events.map(async event => {
    await once(client as never, event);
  });

  const error = new Promise<never>((_, reject) => {
    client.once('error', reject);
  });

  const timeout = delay(timeoutMs).then(() => {
    throw new Error(`Timed out waiting for Minecraft client event: ${events.join(' or ')}`);
  });

  await Promise.race([...waiters, error, timeout]);
}
