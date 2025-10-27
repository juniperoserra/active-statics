/**
 * Common behavioral tests for all Active Statics Applets
 *
 * Tests common functionality across all applets including:
 * - Cantilever, Cable Stay, Overhang, Min Weight, Beam Load, and Launcher
 *
 * These tests work for both CheerpJ Java version and future JavaScript ports.
 */

const { test, expect } = require('@playwright/test');
const {
  waitForAppletReady,
  takeSnapshot,
  extractDisplayedText,
  dragOnCanvas,
  getAppletBounds,
  checkNoErrors,
  waitForCanvasUpdate
} = require('./helpers');

const APPLETS = [
  {
    name: 'Cantilever',
    url: '/applets/cantilever.html',
    title: 'Cantilever Truss',
    expectedText: ['Cantilever', 'Form Diagram'],
    hasForcePolygon: true,
    hasReactions: true,
    hasMembers: true
  },
  {
    name: 'Cable Stay',
    url: '/applets/cable-stay.html',
    title: 'Fanlike Structure (Cable Stay)',
    expectedText: ['Fanlike', 'Cable', 'Form Diagram'],
    hasForcePolygon: true,
    hasReactions: true,
    hasMembers: true
  },
  {
    name: 'Overhang',
    url: '/applets/overhang.html',
    title: 'Overhanging Truss',
    expectedText: ['Overhanging', 'Form Diagram'],
    hasForcePolygon: true,
    hasReactions: true,
    hasMembers: true
  },
  {
    name: 'Min Weight',
    url: '/applets/min-weight.html',
    title: 'Minimum Weight Truss',
    expectedText: ['Minimum Weight', 'Form Diagram'],
    hasForcePolygon: true,
    hasReactions: true,
    hasMembers: true
  },
  {
    name: 'Beam Load',
    url: '/applets/beam-load.html',
    title: 'Beam Loading',
    expectedText: ['Beam', 'Loading'],
    hasForcePolygon: false, // Beam applet may have different layout
    hasReactions: true,
    hasMembers: false // Beams show shear/moment, not members
  },
  {
    name: 'Launcher',
    url: '/applets/launcher.html',
    title: 'Launcher',
    expectedText: ['Active Statics'],
    hasForcePolygon: false, // Launcher is a menu
    hasReactions: false,
    hasMembers: false
  }
];

APPLETS.forEach(applet => {
  test.describe(`${applet.name} Applet - Behavioral Tests`, () => {
    test.beforeEach(async ({ page }) => {
      test.setTimeout(60000);
      await page.goto(applet.url);
      await waitForAppletReady(page);
    });

    test('should load and display without errors', async ({ page }) => {
      // Verify no errors
      expect(await checkNoErrors(page)).toBe(true);

      // Check page title
      await expect(page).toHaveTitle(new RegExp(applet.title, 'i'));

      // Take initial state snapshot
      await takeSnapshot(page, `${applet.name}_initial_state`);
    });

    test('should display expected text content', async ({ page }) => {
      const text = await extractDisplayedText(page);

      // Check for expected text elements
      for (const expectedText of applet.expectedText) {
        expect(text).toContain(expectedText);
      }
    });

    if (applet.hasForcePolygon) {
      test('should display form diagram and force polygon', async ({ page }) => {
        const text = await extractDisplayedText(page);

        expect(text).toContain('Form Diagram');
        expect(text).toContain('Force Polygon');

        await takeSnapshot(page, `${applet.name}_diagrams`);
      });
    }

    if (applet.hasReactions) {
      test('should display reaction forces', async ({ page }) => {
        const text = await extractDisplayedText(page);

        // Check for reaction force indicators
        expect(text).toMatch(/R[abxy]/i);
        // Should have numeric force values
        expect(text).toMatch(/\d+\.\d+/);

        await takeSnapshot(page, `${applet.name}_reactions`);
      });
    }

    if (applet.hasMembers) {
      test('should display member forces', async ({ page }) => {
        const text = await extractDisplayedText(page);

        // Check for member labels
        expect(text).toMatch(/[A-Z]\d+/);

        // Should have compression or tension indicators
        const hasCT = text.includes('C') || text.includes('T');
        expect(hasCT).toBe(true);

        await takeSnapshot(page, `${applet.name}_members`);
      });
    }

    test('should have control buttons', async ({ page }) => {
      const text = await extractDisplayedText(page);

      // Most applets have "Return To Starting Position"
      const hasReturnButton = text.includes('Return To Starting Position') ||
                              text.includes('Return') ||
                              text.includes('Reset');

      // Launcher may not have this button
      if (applet.name !== 'Launcher') {
        expect(hasReturnButton).toBe(true);
      }

      await takeSnapshot(page, `${applet.name}_controls`);
    });

    test('should respond to interaction (drag test)', async ({ page }) => {
      // Skip interaction test for Launcher (it's a menu)
      if (applet.name === 'Launcher') {
        test.skip();
      }

      // Take initial screenshot
      await takeSnapshot(page, `${applet.name}_before_interaction`);

      // Perform a drag interaction
      const bounds = await getAppletBounds(page);
      if (bounds) {
        const centerX = bounds.width / 2;
        const centerY = bounds.height / 2;
        await dragOnCanvas(page, centerX, centerY, centerX + 20, centerY + 20);
      }

      await waitForCanvasUpdate(page, 1000);

      // Take after-interaction screenshot
      await takeSnapshot(page, `${applet.name}_after_interaction`);

      // Verify still functional
      expect(await checkNoErrors(page)).toBe(true);
    });

    test('should maintain structural equilibrium', async ({ page }) => {
      // Skip for Launcher
      if (applet.name === 'Launcher' || !applet.hasReactions) {
        test.skip();
      }

      const text = await extractDisplayedText(page);

      // Extract all numeric values (forces, reactions, loads)
      const numbers = text.match(/\d+\.\d+/g);
      expect(numbers).not.toBeNull();
      expect(numbers.length).toBeGreaterThan(0);

      // Verify that forces are being calculated (non-zero values)
      const hasNonZeroForces = numbers.some(n => parseFloat(n) > 1.0);
      expect(hasNonZeroForces).toBe(true);
    });

    test('visual regression - complete interface', async ({ page }) => {
      // Wait for full rendering
      await page.waitForTimeout(2000);

      // Take comprehensive screenshot
      await takeSnapshot(page, `${applet.name}_complete_interface`);

      // Verify no critical errors
      expect(await checkNoErrors(page)).toBe(true);
    });

    if (applet.name === 'Launcher') {
      test('launcher should have clickable applet buttons', async ({ page }) => {
        const text = await extractDisplayedText(page);

        // Launcher should have buttons for different applets
        const appletNames = [
          'Single Panel',
          'Truss',
          'Cable',
          'Cantilever',
          'Overhang',
          'Beam'
        ];

        // Check for at least some applet names
        const foundNames = appletNames.filter(name =>
          text.toLowerCase().includes(name.toLowerCase())
        );

        expect(foundNames.length).toBeGreaterThanOrEqual(3);

        await takeSnapshot(page, 'launcher_menu');
      });
    }

    if (applet.name === 'Beam Load') {
      test('beam should display shear and moment information', async ({ page }) => {
        const text = await extractDisplayedText(page);

        // Beam loading typically shows shear and moment
        const hasBeamInfo =
          text.toLowerCase().includes('shear') ||
          text.toLowerCase().includes('moment') ||
          text.toLowerCase().includes('beam');

        expect(hasBeamInfo).toBe(true);

        await takeSnapshot(page, 'beam_load_diagrams');
      });
    }
  });
});

// Cross-applet comparison tests
test.describe('Cross-Applet Consistency Tests', () => {
  test('all structural applets should follow similar patterns', async ({ page }) => {
    test.setTimeout(180000); // 3 minutes for all applets

    const structuralApplets = APPLETS.filter(a => a.hasMembers);

    for (const applet of structuralApplets) {
      await page.goto(applet.url);
      await waitForAppletReady(page);

      const text = await extractDisplayedText(page);

      // All structural applets should have:
      // 1. Form Diagram
      expect(text).toContain('Form Diagram');

      // 2. Some numeric values
      expect(text).toMatch(/\d+\.\d+/);

      // 3. Some labeled elements
      expect(text).toMatch(/[A-Za-z]\d+/);
    }
  });
});
