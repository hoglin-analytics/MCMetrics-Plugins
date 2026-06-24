import type { CapturedHoglinCall } from './proxy-server.js';

export interface RecordedAnalytic {
  event_type?: string;
  eventType?: string;
  timestamp?: string;
  properties?: Record<string, unknown>;
}

export class HoglinProxyClient {
  constructor(private readonly baseUrl = process.env.E2E_PROXY_URL ?? 'http://127.0.0.1:18080') {}

  async health(): Promise<void> {
    const response = await fetch(`${this.baseUrl}/__admin/health`);
    if (!response.ok) {
      throw new Error(`Hoglin proxy health check failed with HTTP ${response.status}`);
    }
  }

  async reset(): Promise<void> {
    const response = await fetch(`${this.baseUrl}/__admin/reset`, { method: 'POST' });
    if (!response.ok) {
      throw new Error(`Hoglin proxy reset failed with HTTP ${response.status}: ${await response.text()}`);
    }
  }

  async calls(): Promise<CapturedHoglinCall[]> {
    const response = await fetch(`${this.baseUrl}/__admin/calls`);
    if (!response.ok) {
      throw new Error(`Hoglin proxy call fetch failed with HTTP ${response.status}: ${await response.text()}`);
    }

    const body = await response.json() as { calls: CapturedHoglinCall[] };
    return body.calls;
  }

  async analytics(): Promise<RecordedAnalytic[]> {
    return flattenAnalytics(await this.calls());
  }

  async waitForEvent(
    eventType: string,
    predicate: (event: RecordedAnalytic) => boolean = () => true,
    timeoutMs = 30_000,
  ): Promise<RecordedAnalytic> {
    const deadline = Date.now() + timeoutMs;
    let lastEvents: RecordedAnalytic[] = [];

    while (Date.now() < deadline) {
      lastEvents = await this.analytics();
      const event = lastEvents.find(candidate => analyticEventType(candidate) === eventType && predicate(candidate));
      if (event) return event;
      await delay(500);
    }

    throw new Error(`Timed out waiting for Hoglin analytic "${eventType}". Saw: ${lastEvents.map(analyticEventType).join(', ') || '<none>'}`);
  }
}

export function flattenAnalytics(calls: CapturedHoglinCall[]): RecordedAnalytic[] {
  const events: RecordedAnalytic[] = [];

  for (const call of calls) {
    if (call.method !== 'PUT' || !call.path.startsWith('/analytics/')) continue;

    if (Array.isArray(call.jsonBody)) {
      events.push(...call.jsonBody.filter(isRecordedAnalytic));
    }
  }

  return events;
}

export function analyticEventType(event: RecordedAnalytic): string {
  return event.event_type ?? event.eventType ?? '';
}

export function analyticProperty(event: RecordedAnalytic, key: string): unknown {
  return event.properties?.[key];
}

function isRecordedAnalytic(value: unknown): value is RecordedAnalytic {
  return typeof value === 'object' && value !== null && ('event_type' in value || 'eventType' in value);
}

function delay(ms: number): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms));
}
