import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    environment: 'happy-dom',
    include: ['test/integration/**/*.test.js'],
    globals: true,
    setupFiles: [],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'html'],
      exclude: [
        'node_modules/',
        'test/',
        '*.config.js'
      ]
    }
  },
  resolve: {
    alias: {
      '@': '/src'
    }
  }
});
