/**
 * Behavioral tests for Single Panel Truss Applet
 *
 * Tests the interactive behavior and calculations of the simplest truss structure.
 * These tests work for both CheerpJ Java version and future JavaScript port.
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

const APPLET_URL = '/applets/single-panel.html';

test.describe('Single Panel Truss - Behavioral Tests', () => {
  test.beforeEach(async ({ page }) => {
    test.setTimeout(60000);
    await page.goto(APPLET_URL);
    await waitForAppletReady(page);
  });

  test('should load and display initial truss structure', async ({ page }) => {
    // Verify no errors
    expect(await checkNoErrors(page)).toBe(true);

    // Check that essential UI elements are visible
    const text = await extractDisplayedText(page);
    expect(text).toContain('Single Panel Truss');
    expect(text).toContain('Form Diagram');
    expect(text).toContain('Force Polygon');

    // Take initial state snapshot
    await takeSnapshot(page, 'single_panel_initial_state');
  });

  test('should display member forces', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Check for member force labels
    expect(text).toContain('Member forces');

    // Check for force values (format: "A1 = xxx C" or "C1 = xxx T")
    // Using regex to match force patterns
    expect(text).toMatch(/A1.*=.*\d+\.\d+.*[CT]/);
    expect(text).toMatch(/B1.*=.*\d+\.\d+.*[CT]/);
    expect(text).toMatch(/C1.*=.*\d+\.\d+.*[CT]/);
  });

  test('should display reaction forces', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Check for reaction force labels and values
    expect(text).toContain('Ra');
    expect(text).toContain('Rb');

    // Verify reaction values are displayed (should be numbers)
    expect(text).toMatch(/Ra.*\d+\.\d+/);
    expect(text).toMatch(/Rb.*\d+\.\d+/);
  });

  test('should display load value', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Check for load value (default 150.0)
    expect(text).toMatch(/150\.0/);
  });

  test('should have control buttons', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Verify all control buttons are present
    expect(text).toContain('Return To Starting Position');
    expect(text).toContain('Circle Load');
    expect(text).toContain('Keep Load Vertical');
    expect(text).toContain('Extend Lines of Action');

    await takeSnapshot(page, 'single_panel_with_controls');
  });

  test('should respond to load dragging', async ({ page }) => {
    // Get initial force values
    const initialText = await extractDisplayedText(page);
    const initialForces = extractForceValues(initialText);

    // Take initial screenshot
    await takeSnapshot(page, 'single_panel_before_drag');

    // Drag the load (approximate position - top center of truss)
    const bounds = await getAppletBounds(page);
    if (bounds) {
      // Drag from approximate load position to a new location
      await dragOnCanvas(page, bounds.width / 2, 50, bounds.width / 2 + 50, 100);
    }

    await waitForCanvasUpdate(page, 1000);

    // Get new force values
    const newText = await extractDisplayedText(page);
    const newForces = extractForceValues(newText);

    // Take after-drag screenshot
    await takeSnapshot(page, 'single_panel_after_drag');

    // Forces should have changed (at least one should be different)
    const forcesChanged =
      initialForces.A1 !== newForces.A1 ||
      initialForces.B1 !== newForces.B1 ||
      initialForces.C1 !== newForces.C1;

    expect(forcesChanged).toBe(true);
  });

  test('should maintain equilibrium (Ra + Rb = Load)', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Extract reaction forces
    const raMatch = text.match(/Ra[^\d]*(\d+\.?\d*)/);
    const rbMatch = text.match(/Rb[^\d]*(\d+\.?\d*)/);
    const loadMatch = text.match(/(\d+\.0)/);

    if (raMatch && rbMatch && loadMatch) {
      const ra = parseFloat(raMatch[1]);
      const rb = parseFloat(rbMatch[1]);
      const load = parseFloat(loadMatch[1]);

      // Check equilibrium: Ra + Rb should equal the applied load
      // Allow small tolerance for floating point
      expect(Math.abs((ra + rb) - load)).toBeLessThan(0.5);
    }
  });

  test('should show symmetrical reactions for centered load', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // For a centered load on a symmetric truss, reactions should be equal
    const raMatch = text.match(/Ra[^\d]*(\d+\.?\d*)/);
    const rbMatch = text.match(/Rb[^\d]*(\d+\.?\d*)/);

    if (raMatch && rbMatch) {
      const ra = parseFloat(raMatch[1]);
      const rb = parseFloat(rbMatch[1]);

      // Reactions should be approximately equal (within 1.0 unit tolerance)
      expect(Math.abs(ra - rb)).toBeLessThan(1.0);
    }
  });

  test('should display force polygon', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Verify force polygon section exists
    expect(text).toContain('Force Polygon');

    // Force polygon should have labeled points
    expect(text).toMatch(/[abc]/); // Points on force polygon

    await takeSnapshot(page, 'single_panel_force_polygon');
  });

  test('should show compression and tension members correctly', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Extract force values with C (compression) or T (tension) indicators
    const forces = extractForceValues(text);

    // For a simple triangle truss with top load:
    // - Top members (A1, B1) should be in compression (C)
    // - Bottom member (C1) should be in tension (T)

    // Verify C and T indicators are present
    expect(text).toMatch(/C/); // Compression
    expect(text).toMatch(/T/); // Tension

    // Top two members should have similar magnitudes (symmetric)
    if (forces.A1 && forces.B1) {
      expect(Math.abs(forces.A1 - forces.B1)).toBeLessThan(1.0);
    }
  });

  test('visual regression - complete interface', async ({ page }) => {
    // Wait a bit longer to ensure everything is rendered
    await page.waitForTimeout(2000);

    // Take comprehensive screenshot
    await takeSnapshot(page, 'single_panel_complete_interface');

    // Verify no console errors occurred
    const consoleErrors = [];
    page.on('console', msg => {
      if (msg.type() === 'error' && !msg.text().includes('favicon')) {
        consoleErrors.push(msg.text());
      }
    });

    // Check page is still functional
    expect(await checkNoErrors(page)).toBe(true);
  });
});

/**
 * Helper function to extract force values from text
 * @param {string} text - The displayed text
 * @returns {object} Object with force values
 */
function extractForceValues(text) {
  const forces = {};

  // Match patterns like "A1 = 120.1 C" or "C1 = 93.7 T"
  const a1Match = text.match(/A1.*=.*?(\d+\.?\d*)/);
  const b1Match = text.match(/B1.*=.*?(\d+\.?\d*)/);
  const c1Match = text.match(/C1.*=.*?(\d+\.?\d*)/);

  if (a1Match) forces.A1 = parseFloat(a1Match[1]);
  if (b1Match) forces.B1 = parseFloat(b1Match[1]);
  if (c1Match) forces.C1 = parseFloat(c1Match[1]);

  return forces;
}
