/**
 * Behavioral tests for Truss Applet (Complex Truss)
 *
 * Tests the interactive behavior and force analysis of a complex truss structure
 * with 12 nodes and 21 members. These tests work for both CheerpJ Java version
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

const APPLET_URL = '/applets/truss.html';

test.describe('Complex Truss - Behavioral Tests', () => {
  test.beforeEach(async ({ page }) => {
    test.setTimeout(60000);
    await page.goto(APPLET_URL);
    await waitForAppletReady(page);
  });

  test('should load and display complex truss structure', async ({ page }) => {
    // Verify no errors
    expect(await checkNoErrors(page)).toBe(true);

    // Check that essential UI elements are visible
    const text = await extractDisplayedText(page);
    expect(text).toContain('Simple Truss'); // Note: may be labeled as "Simple" but it's complex
    expect(text).toContain('Form Diagram');
    expect(text).toContain('Force Polygon');

    // Take initial state snapshot
    await takeSnapshot(page, 'truss_initial_state');
  });

  test('should display multiple truss members', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Check for multiple member labels (A1, B1, C1, etc.)
    const memberMatches = text.match(/[A-Z]\d+/g);
    expect(memberMatches).not.toBeNull();
    // Complex truss should have many members (21 members expected)
    expect(memberMatches.length).toBeGreaterThanOrEqual(10);
  });

  test('should display member forces with C/T indicators', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Should have compression (C) and tension (T) indicators
    expect(text).toMatch(/\d+\.\d+\s*C/); // Compression member
    expect(text).toMatch(/\d+\.\d+\s*T/); // Tension member

    // Check for member forces section
    expect(text).toContain('Member forces');
  });

  test('should display multiple load points', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Complex truss typically has multiple loads
    // Look for load values
    const loadMatches = text.match(/\d+\.\d+/g);
    expect(loadMatches).not.toBeNull();
    expect(loadMatches.length).toBeGreaterThan(5);
  });

  test('should display reaction forces', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Check for reactions at supports
    expect(text).toMatch(/R[ab]/); // Ra or Rb reactions
    expect(text).toMatch(/\d+\.\d+/); // Numeric values

    await takeSnapshot(page, 'truss_with_reactions');
  });

  test('should have control buttons', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Verify control buttons
    expect(text).toContain('Return To Starting Position');
    expect(text).toContain('Extend Lines of Action');

    await takeSnapshot(page, 'truss_with_controls');
  });

  test('should show force polygon with many vectors', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Verify force polygon section exists
    expect(text).toContain('Force Polygon');

    // Complex truss should have many labeled points in force polygon
    const letterMatches = text.match(/[a-z]/g);
    expect(letterMatches).not.toBeNull();
    expect(letterMatches.length).toBeGreaterThan(10);

    await takeSnapshot(page, 'truss_force_polygon');
  });

  test('should respond to node dragging', async ({ page }) => {
    // Get initial state
    const initialText = await extractDisplayedText(page);

    // Take initial screenshot
    await takeSnapshot(page, 'truss_before_drag');

    // Drag a node (approximate center top area)
    const bounds = await getAppletBounds(page);
    if (bounds) {
      const x = bounds.width * 0.5;
      const y = bounds.height * 0.3;
      await dragOnCanvas(page, x, y, x + 30, y + 20);
    }

    await waitForCanvasUpdate(page, 1000);

    // Get new state
    const newText = await extractDisplayedText(page);

    // Take after-drag screenshot
    await takeSnapshot(page, 'truss_after_drag');

    // Verify forces changed
    expect(newText).not.toEqual(initialText);
    expect(await checkNoErrors(page)).toBe(true);
  });

  test('should show multiple compression and tension members', async ({ page }) => {
    const text = await extractDisplayedText(page);

    // Count compression and tension members
    const compressionCount = (text.match(/\s+C[\s,]/g) || []).length;
    const tensionCount = (text.match(/\s+T[\s,]/g) || []).length;

    // A realistic truss should have both types
    expect(compressionCount).toBeGreaterThan(0);
    expect(tensionCount).toBeGreaterThan(0);

    // Total members should be significant
    expect(compressionCount + tensionCount).toBeGreaterThanOrEqual(10);
  });

  test('should display complex truss geometry', async ({ page }) => {
    // Wait for full rendering
    await page.waitForTimeout(2000);

    // Take detailed snapshot showing truss geometry
    await takeSnapshot(page, 'truss_geometry');

    // Verify structure rendered
    expect(await checkNoErrors(page)).toBe(true);
  });

  test('visual regression - complete interface', async ({ page }) => {
    // Wait for full rendering
    await page.waitForTimeout(2000);

    // Take comprehensive screenshot
    await takeSnapshot(page, 'truss_complete_interface');

    // Verify no critical errors
    expect(await checkNoErrors(page)).toBe(true);
  });
});
