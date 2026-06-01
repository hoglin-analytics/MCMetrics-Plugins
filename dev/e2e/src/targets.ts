export type Capability =
  | 'player.join'
  | 'player.quit'
  | 'player.chat'
  | 'server.player_count'
  | 'server.performance'
  | 'player.purchase_command';

export interface E2ETarget {
  id: string;
  label: string;
  composeFile: string;
  service: string;
  portEnv: string;
  defaultPort: number;
  minecraftVersionEnv: string;
  defaultMinecraftVersion: string;
  instanceEnv: string;
  instanceId: string;
  isProxy: boolean;
  capabilities: readonly Capability[];
}

const standaloneCapabilities = [
  'player.join',
  'player.quit',
  'player.chat',
  'server.player_count',
  'server.performance',
  'player.purchase_command',
] as const satisfies readonly Capability[];

const proxyCapabilities = [
  'player.join',
  'player.quit',
  'server.player_count',
  'server.performance',
  'player.purchase_command',
] as const satisfies readonly Capability[];

export const targets = {
  paper: {
    id: 'paper',
    label: 'Bukkit plugin on Paper',
    composeFile: 'compose.bukkit-paper.yml',
    service: 'paper',
    portEnv: 'PAPER_PORT',
    defaultPort: 25565,
    minecraftVersionEnv: 'PAPER_MC_VERSION',
    defaultMinecraftVersion: '1.21.6',
    instanceEnv: 'MCMETRICS_PAPER_INSTANCE_ID',
    instanceId: 'paper-e2e',
    isProxy: false,
    capabilities: standaloneCapabilities,
  },
  spigot: {
    id: 'spigot',
    label: 'Bukkit plugin on Spigot',
    composeFile: 'compose.bukkit-spigot.yml',
    service: 'spigot',
    portEnv: 'SPIGOT_PORT',
    defaultPort: 25566,
    minecraftVersionEnv: 'SPIGOT_MC_VERSION',
    defaultMinecraftVersion: '1.21.6',
    instanceEnv: 'MCMETRICS_SPIGOT_INSTANCE_ID',
    instanceId: 'spigot-e2e',
    isProxy: false,
    capabilities: standaloneCapabilities,
  },
  folia: {
    id: 'folia',
    label: 'Bukkit plugin on Folia',
    composeFile: 'compose.bukkit-folia.yml',
    service: 'folia',
    portEnv: 'FOLIA_PORT',
    defaultPort: 25567,
    minecraftVersionEnv: 'FOLIA_MC_VERSION',
    defaultMinecraftVersion: '1.21.6',
    instanceEnv: 'MCMETRICS_FOLIA_INSTANCE_ID',
    instanceId: 'folia-e2e',
    isProxy: false,
    capabilities: standaloneCapabilities,
  },
  fabric: {
    id: 'fabric',
    label: 'Fabric mod on Fabric server',
    composeFile: 'compose.fabric.yml',
    service: 'fabric',
    portEnv: 'FABRIC_PORT',
    defaultPort: 25568,
    minecraftVersionEnv: 'FABRIC_MC_VERSION',
    defaultMinecraftVersion: '1.21.1',
    instanceEnv: 'MCMETRICS_FABRIC_INSTANCE_ID',
    instanceId: 'fabric-e2e',
    isProxy: false,
    capabilities: standaloneCapabilities,
  },
  velocity: {
    id: 'velocity',
    label: 'Velocity plugin on Velocity proxy',
    composeFile: 'compose.velocity.yml',
    service: 'proxy',
    portEnv: 'VELOCITY_PORT',
    defaultPort: 25569,
    minecraftVersionEnv: 'PROXY_BACKEND_MC_VERSION',
    defaultMinecraftVersion: '1.21.6',
    instanceEnv: 'MCMETRICS_VELOCITY_INSTANCE_ID',
    instanceId: 'velocity-e2e',
    isProxy: true,
    capabilities: proxyCapabilities,
  },
  bungee: {
    id: 'bungee',
    label: 'Bungee plugin on BungeeCord proxy',
    composeFile: 'compose.bungee.yml',
    service: 'proxy',
    portEnv: 'BUNGEE_PORT',
    defaultPort: 25570,
    minecraftVersionEnv: 'PROXY_BACKEND_MC_VERSION',
    defaultMinecraftVersion: '1.21.6',
    instanceEnv: 'MCMETRICS_BUNGEE_INSTANCE_ID',
    instanceId: 'bungee-e2e',
    isProxy: true,
    capabilities: proxyCapabilities,
  },
  waterfall: {
    id: 'waterfall',
    label: 'Bungee plugin on Waterfall proxy',
    composeFile: 'compose.waterfall.yml',
    service: 'proxy',
    portEnv: 'WATERFALL_PORT',
    defaultPort: 25571,
    minecraftVersionEnv: 'PROXY_BACKEND_MC_VERSION',
    defaultMinecraftVersion: '1.21.6',
    instanceEnv: 'MCMETRICS_WATERFALL_INSTANCE_ID',
    instanceId: 'waterfall-e2e',
    isProxy: true,
    capabilities: proxyCapabilities,
  },
} as const satisfies Record<string, E2ETarget>;

export type TargetId = keyof typeof targets;

export function targetIds(): TargetId[] {
  return Object.keys(targets) as TargetId[];
}

export function getTarget(id: string | undefined): E2ETarget {
  if (!id || !(id in targets)) {
    throw new Error(`Unknown E2E target "${id ?? ''}". Expected one of: ${targetIds().join(', ')}`);
  }

  return targets[id as TargetId];
}

export function currentTarget(): E2ETarget {
  return getTarget(process.env.E2E_TARGET);
}

export function hasCapability(target: E2ETarget, capability: Capability): boolean {
  return target.capabilities.includes(capability);
}

export function targetPort(target: E2ETarget, env: NodeJS.ProcessEnv = process.env): number {
  const raw = env[target.portEnv];
  if (!raw) return target.defaultPort;

  const port = Number(raw);
  if (!Number.isInteger(port) || port < 1 || port > 65535) {
    throw new Error(`Invalid ${target.portEnv}: ${raw}`);
  }

  return port;
}

export function targetMinecraftVersion(target: E2ETarget, env: NodeJS.ProcessEnv = process.env): string {
  return env[target.minecraftVersionEnv] || target.defaultMinecraftVersion;
}
