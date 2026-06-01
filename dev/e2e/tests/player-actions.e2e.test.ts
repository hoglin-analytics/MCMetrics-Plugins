import { beforeEach, describe, expect, test } from 'vitest';

import { MinecraftBot, uniqueUsername } from '../src/minecraft-bot.js';
import { analyticProperty, HoglinProxyClient } from '../src/proxy-client.js';
import { currentTarget, hasCapability, targetMinecraftVersion } from '../src/targets.js';

const runE2E = process.env.E2E_RUNNING === 'true';
const e2eDescribe = runE2E ? describe : describe.skip;
const target = runE2E ? currentTarget() : undefined;
const expectedInstanceId = process.env.E2E_TARGET_INSTANCE_ID || target?.instanceId;
const proxy = new HoglinProxyClient();

function bot() {
  if (!target) throw new Error('E2E target is not configured');

  return new MinecraftBot({
    host: process.env.E2E_MINECRAFT_HOST || '127.0.0.1',
    port: Number(process.env.E2E_MINECRAFT_PORT || target.defaultPort),
    username: uniqueUsername(),
    version: process.env.E2E_MINECRAFT_VERSION || targetMinecraftVersion(target),
  });
}

e2eDescribe('MCMetrics plugin player action E2E', () => {
  beforeEach(async () => {
    await proxy.health();
    await proxy.reset();
  });

  test.runIf(target && hasCapability(target, 'player.join') && hasCapability(target, 'player.quit'))(
    'captures player join and quit analytics for the target instance',
    async () => {
      const player = bot();
      await player.connect();

      const join = await proxy.waitForEvent('player_join', event => analyticProperty(event, 'instance') === expectedInstanceId);
      expect(analyticProperty(join, 'proxy')).toBe(target?.isProxy ?? false);

      await player.disconnect();

      const quit = await proxy.waitForEvent('player_quit', event => analyticProperty(event, 'instance') === expectedInstanceId);
      expect(analyticProperty(quit, 'sessionId')).toBe(analyticProperty(join, 'sessionId'));
    },
  );

  test.runIf(target && hasCapability(target, 'player.chat'))(
    'captures player chat analytics for standalone server targets',
    async () => {
      const player = bot();
      const message = `mcmetrics e2e ${Date.now()}`;

      await player.connect();
      await player.chat(message);

      const chat = await proxy.waitForEvent('player_chat', event => analyticProperty(event, 'message') === message);
      expect(analyticProperty(chat, 'instance')).toBe(expectedInstanceId);

      await player.disconnect();
    },
  );
});
