import { createServer as createHttpServer, type IncomingMessage, type Server, type ServerResponse } from 'node:http';
import type { AddressInfo } from 'node:net';

export interface CapturedHoglinCall {
  id: number;
  timestamp: string;
  method: string;
  path: string;
  query: Record<string, string | string[]>;
  headers: IncomingMessage['headers'];
  rawBody: string;
  jsonBody: unknown;
}

export interface ProxyServerHandle {
  server: Server;
  url: string;
  close: () => Promise<void>;
}

export class CaptureStore {
  private nextId = 1;
  private calls: CapturedHoglinCall[] = [];

  add(call: Omit<CapturedHoglinCall, 'id' | 'timestamp'>): CapturedHoglinCall {
    const captured = {
      id: this.nextId++,
      timestamp: new Date().toISOString(),
      ...call,
    } satisfies CapturedHoglinCall;

    this.calls.push(captured);
    return captured;
  }

  all(): CapturedHoglinCall[] {
    return [...this.calls];
  }

  reset(): void {
    this.calls = [];
    this.nextId = 1;
  }
}

export function createProxyServer(store = new CaptureStore()): Server {
  return createHttpServer(async (req, res) => {
    try {
      const url = new URL(req.url ?? '/', 'http://localhost');

      if (url.pathname.startsWith('/__admin')) {
        await handleAdminRequest(store, req, res, url);
        return;
      }

      const body = await readBody(req);
      const rawBody = body.toString('utf8');
      const jsonBody = parseJson(rawBody);
      const query = queryToRecord(url.searchParams);

      store.add({
        method: req.method ?? 'GET',
        path: url.pathname,
        query,
        headers: req.headers,
        rawBody,
        jsonBody,
      });

      writeJson(res, 200, mockHoglinResponse(req.method ?? 'GET', url.pathname));
    } catch (error) {
      writeJson(res, 500, {
        error: error instanceof Error ? error.message : String(error),
      });
    }
  });
}

export async function startProxyServer(port: number, host = '0.0.0.0'): Promise<ProxyServerHandle> {
  const server = createProxyServer();

  await new Promise<void>((resolve, reject) => {
    server.once('error', reject);
    server.listen(port, host, () => {
      server.off('error', reject);
      resolve();
    });
  });

  const address = server.address() as AddressInfo;
  const urlHost = address.address === '::' || address.address === '0.0.0.0' ? '127.0.0.1' : address.address;
  const url = `http://${urlHost}:${address.port}`;

  return {
    server,
    url,
    close: () => new Promise<void>((resolve, reject) => server.close(error => error ? reject(error) : resolve())),
  };
}

async function handleAdminRequest(store: CaptureStore, req: IncomingMessage, res: ServerResponse, url: URL): Promise<void> {
  if (url.pathname === '/__admin/health' && req.method === 'GET') {
    writeJson(res, 200, { ok: true });
    return;
  }

  if (url.pathname === '/__admin/reset' && req.method === 'POST') {
    await readBody(req);
    store.reset();
    writeJson(res, 200, { ok: true });
    return;
  }

  if (url.pathname === '/__admin/calls' && req.method === 'GET') {
    writeJson(res, 200, { calls: store.all() });
    return;
  }

  writeJson(res, 404, { error: `Unknown admin endpoint: ${req.method} ${url.pathname}` });
}

function mockHoglinResponse(method: string, path: string): unknown {
  if (method === 'GET' && /^\/experiments\/[^/]+$/.test(path)) {
    return [];
  }

  if (method === 'GET' && /^\/experiments\/[^/]+\/[^/]+\/evaluate$/.test(path)) {
    return { inExperiment: false };
  }

  if (method === 'PUT' && /^\/analytics\/[^/]+$/.test(path)) {
    return { ok: true };
  }

  if (method === 'GET' && /^\/visualizations\/imported\/[^/]+\/[^/]+$/.test(path)) {
    return { imported: false };
  }

  if (method === 'POST' && /^\/visualizations\/[^/]+\/import$/.test(path)) {
    return { ok: true };
  }

  return { ok: true };
}

function readBody(req: IncomingMessage): Promise<Buffer> {
  const chunks: Buffer[] = [];
  return new Promise((resolve, reject) => {
    req.on('data', chunk => chunks.push(Buffer.isBuffer(chunk) ? chunk : Buffer.from(chunk)));
    req.on('end', () => resolve(Buffer.concat(chunks)));
    req.on('error', reject);
  });
}

function parseJson(rawBody: string): unknown {
  if (!rawBody) return null;

  try {
    return JSON.parse(rawBody);
  } catch {
    return null;
  }
}

function queryToRecord(params: URLSearchParams): Record<string, string | string[]> {
  const record: Record<string, string | string[]> = {};

  for (const key of params.keys()) {
    const values = params.getAll(key);
    record[key] = values.length > 1 ? values : values[0] ?? '';
  }

  return record;
}

function writeJson(res: ServerResponse, status: number, value: unknown): void {
  const body = JSON.stringify(value);
  res.writeHead(status, {
    'content-type': 'application/json',
    'content-length': Buffer.byteLength(body),
  });
  res.end(body);
}
