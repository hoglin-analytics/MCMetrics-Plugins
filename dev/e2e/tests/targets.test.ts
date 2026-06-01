import { describe, expect, test } from 'vitest';

import { hasCapability, targetIds, targets } from '../src/targets.js';

describe('E2E target matrix', () => {
  test('defines one target for every supported plugin server family', () => {
    expect(targetIds()).toEqual(['paper', 'spigot', 'folia', 'fabric', 'velocity', 'bungee', 'waterfall']);
  });

  test('limits chat coverage to targets that actually register chat listeners', () => {
    expect(hasCapability(targets.paper, 'player.chat')).toBe(true);
    expect(hasCapability(targets.fabric, 'player.chat')).toBe(true);
    expect(hasCapability(targets.velocity, 'player.chat')).toBe(false);
    expect(hasCapability(targets.bungee, 'player.chat')).toBe(false);
    expect(hasCapability(targets.waterfall, 'player.chat')).toBe(false);
  });
});
