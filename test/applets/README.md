# Active Statics Applet Test Suite

Comprehensive behavioral test suite for Active Statics applets. These tests verify the correctness of both the CheerpJ Java applets and future JavaScript ports.

## Test Structure

```
test/applets/
├── README.md                           # This file
├── helpers.js                          # Shared test utilities
├── applets.spec.js                     # Basic loading tests
├── applet-visual.spec.js               # ✅ Visual regression tests (works with CheerpJ)
├── single-panel.behavior.spec.js       # ⏳ Behavioral tests (better with JS ports)
├── hanging-cable.behavior.spec.js      # ⏳ Behavioral tests (better with JS ports)
├── truss.behavior.spec.js              # ⏳ Behavioral tests (better with JS ports)
├── all-applets.behavior.spec.js        # ⏳ Common behavioral tests
└── screenshots/                        # Visual regression snapshots
```

### Test Types

#### ✅ **Visual Regression Tests** (`applet-visual.spec.js`)
**Status**: Fully functional with CheerpJ Java applets

These tests work perfectly with the current CheerpJ implementation and focus on:
- Visual rendering verification via screenshots
- Drag interaction testing
- Error-free operation
- Canvas display verification
- State consistency
- Cross-applet load time comparison

**Run these tests now:**
```bash
npx playwright test test/applets/applet-visual.spec.js
```

#### ⏳ **Behavioral Tests** (`*.behavior.spec.js`)
**Status**: Will work better with JavaScript ports

These tests attempt to verify specific behaviors like force calculations and text labels.
They have limited functionality with CheerpJ because:
- CheerpJ renders everything to canvas
- Canvas-rendered text cannot be easily extracted
- Text-based assertions fail

These tests will become fully functional once applets are ported to JavaScript,
where text is rendered as DOM elements that can be queried.

**Use these as specifications** for JS port behavior, then run them after porting.

## Running Tests

### Run All Tests
```bash
npx playwright test test/applets/
```

### Run Specific Test File
```bash
npx playwright test test/applets/single-panel.behavior.spec.js
```

### Run Tests in UI Mode (Interactive)
```bash
npx playwright test --ui
```

### Run Tests with Headed Browser (Watch)
```bash
npx playwright test --headed
```

### Generate Test Report
```bash
npx playwright show-report
```

## Test Categories

### 1. Basic Loading Tests (`applets.spec.js`)
- Page loads successfully
- Correct page titles
- Applet tags present with correct attributes
- CheerpJ initialization
- No critical console errors
- Basic visual snapshots

### 2. Behavioral Tests

#### Single Panel Truss (`single-panel.behavior.spec.js`)
- Initial structure display
- Member force calculations (A1, B1, C1)
- Reaction force calculations (Ra, Rb)
- Load value display
- Control button presence
- Load dragging interaction
- Equilibrium verification (Ra + Rb = Load)
- Symmetry checks
- Force polygon rendering
- Compression/Tension indicators
- Visual regression

#### Hanging Cable/Arch (`hanging-cable.behavior.spec.js`)
- Initial cable/arch structure
- Multiple load point display
- Reaction force components (Ra, Rb, Rax, Ray, Rbx, Rby)
- Control buttons
- Vertical equilibrium (Ray + Rby = Total Load)
- Horizontal equilibrium (Rax = Rbx)
- Cable segment labeling
- Force polygon with multiple vectors
- Support dragging interaction
- Load point dragging
- Catenary curve shape
- Visual regression

#### Complex Truss (`truss.behavior.spec.js`)
- Complex structure with 12 nodes and 21 members
- Multiple member force display
- Compression/Tension indicators
- Multiple load points
- Reaction forces
- Control buttons
- Force polygon with many vectors
- Node dragging interaction
- Member count verification
- Visual regression

#### All Other Applets (`all-applets.behavior.spec.js`)
Common tests for:
- **Cantilever Truss**: Overhanging structural members
- **Cable Stay**: Radiating tension members
- **Overhanging Truss**: Extended overhanging sections
- **Minimum Weight Truss**: Structural optimization
- **Beam Loading**: Beam under various loads
- **Launcher**: Interactive menu

Tests include:
- Error-free loading
- Expected text content
- Form diagram and force polygon (where applicable)
- Reaction forces
- Member forces
- Control buttons
- Interaction response (drag tests)
- Structural equilibrium
- Visual regression
- Applet-specific features

## Test Helpers (`helpers.js`)

### `waitForAppletReady(page, timeout)`
Waits for an applet to fully initialize. Works for both CheerpJ (checks status div) and JS versions (checks canvas).

### `getCanvas(page)`
Returns the main canvas element.

### `takeSnapshot(page, name)`
Takes a full-page screenshot for visual regression testing.

### `extractDisplayedText(page)`
Extracts all visible text content from the page.

### `clickButton(page, buttonText)`
Clicks a button by its text label (works for both AWT and HTML buttons).

### `dragOnCanvas(page, fromX, fromY, toX, toY)`
Performs a drag operation on the canvas.

### `getAppletBounds(page)`
Returns the bounding box of the applet/canvas area.

### `checkNoErrors(page)`
Verifies no error messages are displayed.

### `waitForCanvasUpdate(page, waitTime)`
Waits for canvas to update after interactions.

## Test Philosophy

### 1. Technology Agnostic
Tests are designed to work with both:
- CheerpJ Java applets (current implementation)
- JavaScript ports using Paper.js (future/in-progress)

### 2. Observable Behavior
Tests focus on **observable outputs** rather than implementation details:
- Visual rendering (screenshots)
- Displayed text (force values, labels)
- User interactions (drag, click)
- Structural correctness (equilibrium)

### 3. Visual Regression
Each test generates screenshots that can be used for:
- Visual comparison between implementations
- Regression detection
- Documentation
- Debugging

### 4. Physics Verification
Tests verify fundamental structural engineering principles:
- **Equilibrium**: Sum of forces = 0
- **Symmetry**: Equal loads → equal reactions
- **Force Distribution**: Compression vs tension
- **Conservation**: Total load = sum of reactions

## Screenshot Naming Convention

Screenshots are saved to `test/applets/screenshots/` with names like:
- `{applet_name}_initial_state.png`
- `{applet_name}_before_drag.png`
- `{applet_name}_after_drag.png`
- `{applet_name}_complete_interface.png`
- `{applet_name}_{specific_feature}.png`

## Porting JavaScript Versions

When porting an applet from Java to JavaScript:

1. **Implement the JavaScript version** using Paper.js
2. **Update the URL** in the test file (or use config)
3. **Run the tests** against the new implementation
4. **Compare screenshots** with Java version
5. **Fix any discrepancies** until behavior matches
6. **Verify all tests pass**

### Example Configuration for Testing JS Ports

You can add a test configuration to switch between CheerpJ and JS versions:

```javascript
// In test file
const USE_JS_VERSION = process.env.USE_JS_VERSION === 'true';
const BASE_URL = USE_JS_VERSION ? '/' : '/applets/';
const APPLET_URL = `${BASE_URL}single-panel.html`;
```

Then run tests against JS version:
```bash
USE_JS_VERSION=true npx playwright test
```

## CI/CD Integration

### GitHub Actions Example
```yaml
name: Test Applets
on: [push, pull_request]
jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - uses: actions/setup-node@v3
      - run: npm install
      - run: npx playwright install --with-deps
      - run: npm run dev &
      - run: npx playwright test test/applets/
      - uses: actions/upload-artifact@v3
        if: always()
        with:
          name: playwright-report
          path: playwright-report/
```

## Debugging Tips

### 1. Run in Headed Mode
```bash
npx playwright test --headed --workers=1
```

### 2. Use Debug Mode
```bash
npx playwright test --debug
```

### 3. Check Screenshots
Visual regressions are often easiest to spot in screenshots:
```bash
open test/applets/screenshots/
```

### 4. Increase Timeouts
If tests are flaky due to slow loading:
```javascript
test.setTimeout(120000); // 2 minutes
```

### 5. Console Output
Check console messages for CheerpJ initialization:
```javascript
page.on('console', msg => console.log(msg.text()));
```

## Known Issues

### CheerpJ Initialization Time
- First load takes 5-10 seconds for JVM initialization
- Tests use 60-second timeout to accommodate this
- Subsequent loads are faster due to caching

### Button Clicking in CheerpJ
- AWT buttons are rendered in canvas
- Button clicks use text locator + coordinate calculation
- May need adjustment if button positions change

### Text Extraction
- Java applets render text to canvas
- Text extraction relies on accessibility/OCR-like methods
- Some formatting may be lost

## Future Enhancements

- [ ] Add performance benchmarks (Java vs JS)
- [ ] Add accessibility tests (WCAG compliance)
- [ ] Add mobile/responsive tests
- [ ] Add cross-browser tests (Firefox, Safari)
- [ ] Add animation tests
- [ ] Add keyboard navigation tests
- [ ] Add more precise force calculation verification
- [ ] Add test coverage reporting

## Contributing

When adding new tests:

1. Follow the existing pattern in `helpers.js`
2. Use descriptive test names
3. Include visual snapshots
4. Test both initial state and interactions
5. Verify structural correctness
6. Document expected behavior
7. Add comments for complex logic

## References

- [Playwright Documentation](https://playwright.dev/)
- [CheerpJ Documentation](https://cheerpj.com/docs/)
- [Active Statics Architecture](../../CLAUDE.md)
- [Applet Documentation](../../applets/README.md)
