import { afterEach, beforeEach, describe, expect, test } from 'vitest';

import { startProxyServer, type ProxyServerHandle } from '../src/proxy-server.js';
import { HoglinProxyClient } from '../src/proxy-client.js';

describe('Hoglin API proxy', () => {
  let handle: ProxyServerHandle;
  let client: HoglinProxyClient;

  beforeEach(async () => {
    handle = await startProxyServer(0, '127.0.0.1');
    client = new HoglinProxyClient(handle.url);
  });

  afterEach(async () => {
    await handle.close();
  });

  test('captures analytics requests while returning a Hoglin-shaped success response', async () => {
    const response = await fetch(`${handle.url}/analytics/e2e-server-key`, {
      method: 'PUT',
      headers: { 'content-type': 'application/json' },
      body: JSON.stringify([
        {
          event_type: 'player_join',
          timestamp: new Date().toISOString(),
          properties: { instance: 'paper-e2e' },
        },
      ]),
    });

    expect(response.ok).toBe(true);
    await expect(response.json()).resolves.toEqual({ ok: true });

    const events = await client.analytics();
    expect(events).toHaveLength(1);
    expect(events[0]?.event_type).toBe('player_join');
    expect(events[0]?.properties?.instance).toBe('paper-e2e');
  });

  test('mocks experiment fetches with an empty experiment list', async () => {
    const response = await fetch(`${handle.url}/experiments/e2e-server-key`);

    expect(response.ok).toBe(true);
    await expect(response.json()).resolves.toEqual([]);

    const calls = await client.calls();
    expect(calls).toMatchObject([
      {
        method: 'GET',
        path: '/experiments/e2e-server-key',
      },
    ]);
  });
});
