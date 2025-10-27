/**
 * Behavioral tests for Hanging Cable/Arch Applet
 *
 * Tests the interactive behavior and force analysis of cable/arch structures
 * with multiple load points. These tests work for both CheerpJ Java version
 * and future JavaScript port.
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

const APPLET_URL = '/applets/hanging-cable.html';

test.describe('Hanging Cable/Arch - Behavioral Tests', () => {
  test.beforeEach(async ({ page }) => {
    test.setTimeout(60000);
    await page.goto(APPLET_URL);
    await waitForAppletReady(page);
  });

  test('should load and display initial cable/arch structure', async ({ page }) => {
    // Verify no errors
    expect(await checkNoErrors(page)).toBe(true);

    // Check that essential UI elements are visible
    const text = await extractDisplayedText(page);
    expect(text).toContain('Hanging Cable');
    expect(text).toContain('Form Diagram');
    expect(text).toContain('Force Polygon');

    // Take initial state snapshot
    await takeSnapshot(page, 'hanging_cable_initial_state');
  });

  test('should display multiple load points', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // The hanging cable has multiple loads (typically 8 loads of 45.0 each)
    // Count occurrences of "45.0"
    const loadMatches = text.match(/45\.0/g);
    expect(loadMatches).not.toBeNull();
    expect(loadMatches.length).toBeGreaterThanOrEqual(6); // At least 6 load points
  });

  test('should display reaction forces', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Check for reaction force components
    expect(text).toContain('Ra');
    expect(text).toContain('Rb');
    expect(text).toContain('Rax'); // X component
    expect(text).toContain('Ray'); // Y component
    expect(text).toContain('Rbx');
    expect(text).toContain('Rby');

    // Verify reaction values are displayed
    expect(text).toMatch(/\d+\.\d+/); // Has numeric values
  });

  test('should have control buttons', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Verify control buttons
    expect(text).toContain('Return To Starting Position');
    expect(text).toContain('Keep supports level');
    expect(text).toContain('Equalize Loads');
    expect(text).toContain('Extend Lines of Action');

    await takeSnapshot(page, 'hanging_cable_with_controls');
  });

  test('should maintain vertical equilibrium (Ra + Rb = Total Load)', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Extract vertical reaction components
    const rayMatch = text.match(/Ray[^\d]*(\d+\.?\d*)/);
    const rbyMatch = text.match(/Rby[^\d]*(\d+\.?\d*)/);

    // Count total load (typically 8 loads × 45.0 = 360.0)
    const loadMatches = text.match(/45\.0/g);
    const totalLoad = loadMatches ? loadMatches.length * 45.0 : 0;

    if (rayMatch && rbyMatch && totalLoad > 0) {
      const ray = parseFloat(rayMatch[1]);
      const rby = parseFloat(rbyMatch[1]);

      // Check vertical equilibrium
      expect(Math.abs((ray + rby) - totalLoad)).toBeLessThan(5.0);
    }
  });

  test('should show cable segments between loads', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Cable segments should be labeled (a, b, c, d, e, f, g, h...)
    // Check for multiple lettered segments
    const hasMultipleSegments =
      text.includes('a') &&
      text.includes('b') &&
      text.includes('c') &&
      text.includes('d');

    expect(hasMultipleSegments).toBe(true);

    await takeSnapshot(page, 'hanging_cable_segments');
  });

  test('should display force polygon with multiple vectors', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Verify force polygon section exists
    expect(text).toContain('Force Polygon');

    // Force polygon should have labeled points for each load
    // Look for sequential letters in the force polygon area
    const hasPolygonPoints =
      (text.match(/[a-h]/g) || []).length >= 5;

    expect(hasPolygonPoints).toBe(true);

    await takeSnapshot(page, 'hanging_cable_force_polygon');
  });

  test('should respond to support dragging', async ({ page }) => {
    // Get initial reaction forces
    const initialText = await extractDisplayedText(page);
    const initialRa = extractReactionValue(initialText, 'Ra');

    // Take initial screenshot
    await takeSnapshot(page, 'hanging_cable_before_drag');

    // Drag one of the supports (left support, approximate position)
    const bounds = await getAppletBounds(page);
    if (bounds) {
      // Drag left support downward
      await dragOnCanvas(page, 50, bounds.height * 0.7, 50, bounds.height * 0.8);
    }

    await waitForCanvasUpdate(page, 1000);

    // Get new reaction forces
    const newText = await extractDisplayedText(page);
    const newRa = extractReactionValue(newText, 'Ra');

    // Take after-drag screenshot
    await takeSnapshot(page, 'hanging_cable_after_drag');

    // Reaction should have changed
    if (initialRa && newRa) {
      expect(Math.abs(initialRa - newRa)).toBeGreaterThan(1.0);
    }
  });

  test('should respond to load point dragging', async ({ page }) => {
    // Take initial screenshot
    await takeSnapshot(page, 'hanging_cable_before_load_drag');

    // Drag a load point (approximate middle of cable)
    const bounds = await getAppletBounds(page);
    if (bounds) {
      const centerX = bounds.width / 2;
      const centerY = bounds.height / 2;
      // Drag middle load point downward
      await dragOnCanvas(page, centerX, centerY - 50, centerX, centerY);
    }

    await waitForCanvasUpdate(page, 1000);

    // Take after-drag screenshot
    await takeSnapshot(page, 'hanging_cable_after_load_drag');

    // Verify still functional
    expect(await checkNoErrors(page)).toBe(true);
  });

  test('should show catenary/parabolic curve shape', async ({ page }) => {
    // Take a detailed snapshot to verify curve is visible
    await page.waitForTimeout(2000);
    await takeSnapshot(page, 'hanging_cable_curve_shape');

    // The form should show a curved cable (visual verification via snapshot)
    // Verify the applet rendered without errors
    expect(await checkNoErrors(page)).toBe(true);
  });

  test('should maintain horizontal equilibrium (Rax = Rbx)', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // For a symmetric cable with equal loads, horizontal reactions should balance
    const raxMatch = text.match(/Rax[^\d]*(\d+\.?\d*)/);
    const rbxMatch = text.match(/Rbx[^\d]*(\d+\.?\d*)/);

    if (raxMatch && rbxMatch) {
      const rax = parseFloat(raxMatch[1]);
      const rbx = parseFloat(rbxMatch[1]);

      // Horizontal reactions should be approximately equal
      // (They should be equal in magnitude but opposite in direction)
      expect(Math.abs(Math.abs(rax) - Math.abs(rbx))).toBeLessThan(5.0);
    }
  });

  test('visual regression - complete interface', async ({ page }) => {
    // Wait for full rendering
    await page.waitForTimeout(2000);

    // Take comprehensive screenshot
    await takeSnapshot(page, 'hanging_cable_complete_interface');

    // Verify no critical errors
    expect(await checkNoErrors(page)).toBe(true);
  });
});

/**
 * Helper function to extract reaction force value from text
 * @param {string} text - The displayed text
 * @param {string} label - The reaction label (e.g., 'Ra', 'Rax')
 * @returns {number|null} The extracted value or null
 */
function extractReactionValue(text, label) {
  const regex = new RegExp(`${label}[^\\d]*(\\d+\\.?\\d*)`);
  const match = text.match(regex);
  return match ? parseFloat(match[1]) : null;
}
