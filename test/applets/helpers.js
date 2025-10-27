/**
 * Test helpers for Active Statics applet testing
 *
 * These helpers work for both CheerpJ Java applets and JavaScript ports
 */

/**
 * Wait for an applet to fully initialize
 * Works for both CheerpJ (checks status div) and JS versions (checks canvas)
 */
async function waitForAppletReady(page, timeout = 45000) {
  // Check if this is a CheerpJ page (has status div)
  const statusDiv = await page.locator('#status').count();

  if (statusDiv > 0) {
    // CheerpJ applet - wait for success message
    await page.waitForSelector('#status:has-text("Applet loaded successfully")', {
      timeout,
      state: 'visible'
    });
    // Give it extra time for rendering
    await page.waitForTimeout(2000);
  } else {
    // JavaScript version - wait for canvas to be ready
    await page.waitForSelector('canvas', { timeout, state: 'visible' });
    await page.waitForTimeout(1000);
  }
}

/**
 * Get the canvas element (works for both CheerpJ and JS versions)
 */
async function getCanvas(page) {
  return page.locator('canvas').first();
}

/**
 * Take a full-page screenshot for visual regression testing
 */
async function takeSnapshot(page, name) {
  const sanitizedName = name.replace(/[^a-z0-9]/gi, '_').toLowerCase();
  await page.screenshot({
    path: `test/applets/screenshots/${sanitizedName}.png`,
    fullPage: true
  });
}

/**
 * Extract text content from the page (force values, labels, etc.)
 * For CheerpJ, text is rendered in canvas so we use Playwright's accessibility snapshot
 * For JS versions, we use DOM text
 */
async function extractDisplayedText(page) {
  try {
    // Try to get accessibility snapshot which can extract canvas text
    const snapshot = await page.accessibility.snapshot();
    const text = extractTextFromSnapshot(snapshot);
    if (text && text.length > 100) {
      return text;
    }
  } catch (e) {
    // Fallback to body text
  }

  // Fallback: Get all text content from the page
  const bodyText = await page.locator('body').innerText();
  return bodyText;
}

/**
 * Recursively extract text from accessibility snapshot
 */
function extractTextFromSnapshot(node) {
  if (!node) return '';

  let text = '';

  // Add this node's name/value
  if (node.name) text += node.name + ' ';
  if (node.value) text += node.value + ' ';

  // Recursively process children
  if (node.children) {
    for (const child of node.children) {
      text += extractTextFromSnapshot(child) + ' ';
    }
  }

  return text;
}

/**
 * Click a button by its text label
 * Works for both AWT buttons (CheerpJ) and HTML buttons (JS)
 */
async function clickButton(page, buttonText) {
  // Try to find button as text in canvas area first
  const canvasContainer = page.locator('.applet-container, #applet-container').first();

  // For CheerpJ, buttons are rendered in canvas, so we need to click by coordinates
  // We'll use a more robust approach: look for the button text and click near it
  const buttonTextElement = await page.getByText(buttonText, { exact: false }).first();

  if (await buttonTextElement.isVisible()) {
    const box = await buttonTextElement.boundingBox();
    if (box) {
      // Click in the center of the text
      await page.mouse.click(box.x + box.width / 2, box.y + box.height / 2);
    }
  }
}

/**
 * Perform a drag operation on the canvas
 */
async function dragOnCanvas(page, fromX, fromY, toX, toY) {
  const canvas = await getCanvas(page);
  const box = await canvas.boundingBox();

  if (box) {
    const startX = box.x + fromX;
    const startY = box.y + fromY;
    const endX = box.x + toX;
    const endY = box.y + toY;

    await page.mouse.move(startX, startY);
    await page.mouse.down();
    await page.mouse.move(endX, endY, { steps: 10 });
    await page.mouse.up();
    await page.waitForTimeout(500); // Wait for any animations
  }
}

/**
 * Get the bounding box of the main applet/canvas area
 */
async function getAppletBounds(page) {
  const canvas = await getCanvas(page);
  return await canvas.boundingBox();
}

/**
 * Check if applet loaded without errors
 */
async function checkNoErrors(page) {
  // Check for error messages in status div
  const statusDiv = page.locator('#status');
  if (await statusDiv.count() > 0) {
    const statusText = await statusDiv.innerText();
    return !statusText.includes('Error');
  }
  return true;
}

/**
 * Wait for canvas to update (useful after interactions)
 */
async function waitForCanvasUpdate(page, waitTime = 500) {
  await page.waitForTimeout(waitTime);
}

module.exports = {
  waitForAppletReady,
  getCanvas,
  takeSnapshot,
  extractDisplayedText,
  clickButton,
  dragOnCanvas,
  getAppletBounds,
  checkNoErrors,
  waitForCanvasUpdate
};
