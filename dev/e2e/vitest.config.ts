import { defineConfig } from 'vitest/config';

const e2eRunning = process.env.E2E_RUNNING === 'true';

export default defineConfig({
  test: {
    include: ['tests/**/*.test.ts'],
    exclude: e2eRunning ? [] : ['tests/**/*.e2e.test.ts'],
    fileParallelism: false,
    hookTimeout: 120_000,
    testTimeout: e2eRunning ? 120_000 : 30_000,
    sequence: {
      concurrent: false,
    },
  },
});
