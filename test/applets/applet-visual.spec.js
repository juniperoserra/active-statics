/**
 * Visual Regression Tests for All Applets
 *
 * These tests focus on visual verification and interactions that work with
 * both CheerpJ (canvas-rendered) and JavaScript (DOM-rendered) versions.
 *
 * Text extraction doesn't work reliably with CheerpJ since everything is
 * rendered to canvas. These tests focus on:
 * - Visual snapshots
 * - Interaction testing
 * - Error-free operation
 * - Screenshot comparisons
 */

const { test, expect } = require('@playwright/test');
const {
  waitForAppletReady,
  takeSnapshot,
  dragOnCanvas,
  getAppletBounds,
  checkNoErrors,
  waitForCanvasUpdate
} = require('./helpers');

const APPLETS = [
  {
    name: 'Single Panel Truss',
    url: '/applets/single-panel.html',
    title: 'Active Statics - Single Panel Truss',
    canDrag: true,
    dragCoords: { fromX: 0.5, fromY: 0.15, toX: 0.6, toY: 0.2 } // Relative to canvas
  },
  {
    name: 'Hanging Cable',
    url: '/applets/hanging-cable.html',
    title: 'Active Statics - Hanging Cable/Arch',
    canDrag: true,
    dragCoords: { fromX: 0.5, fromY: 0.5, toX: 0.5, toY: 0.6 }
  },
  {
    name: 'Simple Truss',
    url: '/applets/truss.html',
    title: 'Active Statics - Simple Truss',
    canDrag: true,
    dragCoords: { fromX: 0.5, fromY: 0.3, toX: 0.55, toY: 0.35 }
  },
  {
    name: 'Cantilever',
    url: '/applets/cantilever.html',
    title: 'Active Statics - Cantilever Truss',
    canDrag: true,
    dragCoords: { fromX: 0.7, fromY: 0.3, toX: 0.75, toY: 0.35 }
  },
  {
    name: 'Cable Stay',
    url: '/applets/cable-stay.html',
    title: 'Active Statics - Fanlike Structure (Cable Stay)',
    canDrag: true,
    dragCoords: { fromX: 0.5, fromY: 0.6, toX: 0.5, toY: 0.7 }
  },
  {
    name: 'Overhang',
    url: '/applets/overhang.html',
    title: 'Active Statics - Overhanging Truss',
    canDrag: true,
    dragCoords: { fromX: 0.5, fromY: 0.3, toX: 0.55, toY: 0.35 }
  },
  {
    name: 'Min Weight',
    url: '/applets/min-weight.html',
    title: 'Active Statics - Minimum Weight Truss',
    canDrag: true,
    dragCoords: { fromX: 0.5, fromY: 0.3, toX: 0.55, toY: 0.35 }
  },
  {
    name: 'Beam Load',
    url: '/applets/beam-load.html',
    title: 'Active Statics - Beam Loading',
    canDrag: true,
    dragCoords: { fromX: 0.5, fromY: 0.3, toX: 0.5, toY: 0.4 }
  },
  {
    name: 'Launcher',
    url: '/applets/launcher.html',
    title: 'Active Statics - Launcher',
    canDrag: false,
    dragCoords: null
  }
];

test.describe('Visual Regression - All Applets', () => {
  APPLETS.forEach(applet => {
    test.describe(`${applet.name}`, () => {
      test.beforeEach(async ({ page }) => {
        test.setTimeout(90000);
        await page.goto(applet.url);
        await waitForAppletReady(page);
      });

      test('should load without errors', async ({ page }) => {
        // Verify page loaded
        await expect(page).toHaveTitle(new RegExp(applet.title.replace(/[()]/g, '\\$&'), 'i'));

        // Verify no error messages
        expect(await checkNoErrors(page)).toBe(true);

        // Take initial state snapshot
        await takeSnapshot(page, `${applet.name.replace(/\s+/g, '_').toLowerCase()}_01_initial`);
      });

      test('should display canvas', async ({ page }) => {
        // Verify canvas is visible
        const canvas = page.locator('canvas').first();
        await expect(canvas).toBeVisible();

        // Get canvas size
        const bounds = await getAppletBounds(page);
        expect(bounds).not.toBeNull();
        expect(bounds.width).toBeGreaterThan(400);
        expect(bounds.height).toBeGreaterThan(400);

        await takeSnapshot(page, `${applet.name.replace(/\s+/g, '_').toLowerCase()}_02_canvas`);
      });

      test('should render content in canvas', async ({ page }) => {
        // Wait a bit to ensure rendering is complete
        await page.waitForTimeout(2000);

        // Take snapshot - this will capture the rendered content
        await takeSnapshot(page, `${applet.name.replace(/\s+/g, '_').toLowerCase()}_03_rendered`);

        // Verify still no errors
        expect(await checkNoErrors(page)).toBe(true);
      });

      if (applet.canDrag) {
        test('should respond to drag interactions', async ({ page }) => {
          // Wait for initial render
          await page.waitForTimeout(1000);

          // Take before-drag snapshot
          await takeSnapshot(page, `${applet.name.replace(/\s+/g, '_').toLowerCase()}_04_before_drag`);

          // Perform drag
          const bounds = await getAppletBounds(page);
          if (bounds && applet.dragCoords) {
            const fromX = bounds.width * applet.dragCoords.fromX;
            const fromY = bounds.height * applet.dragCoords.fromY;
            const toX = bounds.width * applet.dragCoords.toX;
            const toY = bounds.height * applet.dragCoords.toY;

            await dragOnCanvas(page, fromX, fromY, toX, toY);
          }

          // Wait for update
          await waitForCanvasUpdate(page, 1500);

          // Take after-drag snapshot
          await takeSnapshot(page, `${applet.name.replace(/\s+/g, '_').toLowerCase()}_05_after_drag`);

          // Verify still functional
          expect(await checkNoErrors(page)).toBe(true);
        });

        test('should handle multiple interactions', async ({ page }) => {
          // Perform multiple drags
          const bounds = await getAppletBounds(page);
          if (bounds && applet.dragCoords) {
            const centerX = bounds.width * 0.5;
            const centerY = bounds.height * 0.4;

            // First drag
            await dragOnCanvas(page, centerX, centerY, centerX + 20, centerY + 10);
            await waitForCanvasUpdate(page, 500);

            // Second drag
            await dragOnCanvas(page, centerX + 20, centerY + 10, centerX - 20, centerY + 20);
            await waitForCanvasUpdate(page, 500);

            // Third drag back
            await dragOnCanvas(page, centerX - 20, centerY + 20, centerX, centerY);
            await waitForCanvasUpdate(page, 500);
          }

          // Take final snapshot
          await takeSnapshot(page, `${applet.name.replace(/\s+/g, '_').toLowerCase()}_06_multi_drag`);

          // Should still be functional
          expect(await checkNoErrors(page)).toBe(true);
        });
      }

      test('should maintain state consistency', async ({ page }) => {
        // Wait for stable state
        await page.waitForTimeout(2000);

        // Take first snapshot
        await takeSnapshot(page, `${applet.name.replace(/\s+/g, '_').toLowerCase()}_07_state_1`);

        // Wait a bit more
        await page.waitForTimeout(1000);

        // Take second snapshot
        await takeSnapshot(page, `${applet.name.replace(/\s+/g, '_').toLowerCase()}_08_state_2`);

        // Should remain error-free
        expect(await checkNoErrors(page)).toBe(true);
      });
    });
  });
});

test.describe('Cross-Applet Visual Comparison', () => {
  test('all applets should load in reasonable time', async ({ page }) => {
    test.setTimeout(600000); // 10 minutes total

    const results = [];

    for (const applet of APPLETS) {
      const startTime = Date.now();

      await page.goto(applet.url);
      await waitForAppletReady(page, 60000);

      const loadTime = Date.now() - startTime;
      results.push({ name: applet.name, loadTime });

      // Verify loaded
      expect(await checkNoErrors(page)).toBe(true);

      // Take snapshot
      await takeSnapshot(page, `comparison_${applet.name.replace(/\s+/g, '_').toLowerCase()}`);
    }

    // Log load times
    console.log('\nApplet Load Times:');
    results.forEach(r => {
      console.log(`  ${r.name}: ${r.loadTime}ms`);
    });

    // All should load in under 60 seconds
    results.forEach(r => {
      expect(r.loadTime).toBeLessThan(60000);
    });
  });
});
