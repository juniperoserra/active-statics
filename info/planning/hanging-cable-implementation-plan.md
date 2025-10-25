# Hanging Cable Demo Implementation Plan (TDD)

**Status:** In Progress
**Created:** 2025-10-24
**Approach:** Test-Driven Development with Node Test Runner + Vitest

---

## Current State Analysis

**HangingCableApp.js Status:**
- ❌ **Almost entirely unimplemented** - just a skeleton with commented Java code
- Lines 28-100: Constructor calls 10+ methods that don't exist
- Lines 106-867: Entire Java implementation commented out as reference
- **NO JavaScript methods implemented** - everything needs to be ported from Java

**Reference Implementation:**
- Original Java: `old_java_src/ActiveStatics/src/truss/HangingCableApplet.java`
- Working model: `SinglePanelApp.js` demonstrates the Scene-Graphics-Entity pattern

---

## Testing Strategy

### Test Framework: Hybrid Approach

**Node Test Runner** (`node:test`) for unit tests:
- ✅ Already available (Node v22.20.0)
- ✅ Zero dependencies
- ✅ Fast, simple, built-in
- ✅ Perfect for isolated utility and physics tests

**Vitest** for integration/browser tests:
- DOM/Canvas/Paper.js integration
- Better webpack/ES module support
- Fast HMR during development
- Visual test UI with `@vitest/ui`

---

## Phase 1: Test Infrastructure Setup

### 1.1 Add Test Dependencies
```bash
npm install --save-dev vitest @vitest/ui happy-dom
```

### 1.2 Create vitest.config.js
```javascript
import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    environment: 'happy-dom',
    include: ['test/integration/**/*.test.js'],
    globals: true
  }
});
```

### 1.3 Add Test Scripts to package.json
```json
"scripts": {
  "test": "npm run test:unit && npm run test:integration",
  "test:unit": "node --test test/unit/**/*.test.js",
  "test:integration": "vitest run",
  "test:watch": "vitest watch",
  "test:ui": "vitest --ui"
}
```

### 1.4 Create Test Directory Structure
```
/workspaces/active-statics/test/
├── unit/                          (Node test runner)
│   ├── util.test.js
│   ├── hangingCablePhysics.test.js
│   └── hangingCableConstraints.test.js
└── integration/                   (Vitest)
    └── hangingCableApp.test.js
```

---

## Phase 2: Write Tests First (RED Phase)

### 2.1 Utility Function Tests (`test/unit/util.test.js`)

Using Node test runner, test missing utility functions:

**`slope(x1, y1, x2, y2)`** - NOT currently in util.js
- Test: horizontal line → 0
- Test: vertical line → Infinity handling
- Test: 45° diagonal → 1
- Test: negative slope → correct value
- Test: zero-length line → handle gracefully

**`bound(value, min, max)`** - NOT currently in util.js
- Test: value within bounds → returns value
- Test: value below min → returns min
- Test: value above max → returns max
- Test: value equals min → returns min
- Test: value equals max → returns max

### 2.2 Physics Calculation Tests (`test/unit/hangingCablePhysics.test.js`)

Test core structural calculations:

**`distributeCableNodes()`** - Needs implementation (Java lines 396-403)
```javascript
describe('distributeCableNodes', () => {
  test('evenly spaces 7 intermediate nodes between endpoints', () => {
    // Given cable ends at x=100 and x=500
    // When distributeCableNodes is called
    // Then nodes 1-7 should be at x=150, 200, 250, 300, 350, 400, 450
  });

  test('spacing equals (endX - startX) / 8', () => {
    // Verify increment calculation
  });
});
```

**`findCableYs()`** - Needs implementation (Java lines 387-394)
```javascript
describe('findCableYs', () => {
  test('calculates funicular curve from force polygon', () => {
    // Each cable segment slope should match corresponding force polygon ray slope
  });

  test('preserves start node Y value', () => {
    // Cable start Y should not change
  });

  test('calculates end node Y correctly', () => {
    // Accumulated slope changes should result in correct end Y
  });
});
```

**`isArch()`** - Needs implementation (Java lines 348-350, very simple)
```javascript
describe('isArch', () => {
  test('returns true when O is left of load line (arch in compression)', () => {
    // forcePolyNode.x < loadLine[0].x → true
  });

  test('returns false when O is right of load line (cable in tension)', () => {
    // forcePolyNode.x >= loadLine[0].x → false
  });
});
```

**`findOPrime()`** - Needs implementation (Java lines 342-346)
```javascript
describe('findOPrime', () => {
  test('positions O\' on load line vertical', () => {
    // O'.x should equal loadLine[0].x
  });

  test('calculates O\' y-coordinate from ground slope and O position', () => {
    // O'.y = O.y - slope * (O.x - O'.x)
  });
});
```

**`findO()`** - Needs implementation (Java lines 332-340)
```javascript
describe('findO', () => {
  test('moves O perpendicular to ground line when cable ends move', () => {
    // Direction from O' to O should be perpendicular to ground line
  });

  test('preserves distance between O and O\'', () => {
    // |O - O'| should remain constant when cable ends move
  });

  test('inverts direction for arch mode', () => {
    // When arch, O is on opposite side of O'
  });
});
```

**`findReactions()`** - Needs implementation (Java lines 352-385)
```javascript
describe('findReactions', () => {
  test('calculates Ra from first force polygon line', () => {
    // Ra magnitude = forcePolyLines[0].length() + ARROW_OFFSET
  });

  test('calculates Rb from last force polygon line', () => {
    // Rb magnitude = forcePolyLines[7].length() + ARROW_OFFSET
  });

  test('computes reaction components for Ra', () => {
    // RaX and RaY positioned correctly
  });

  test('computes reaction components for Rb', () => {
    // RbX and RbY positioned correctly
  });

  test('reverses arrow direction for arch vs cable', () => {
    // mReverse = 1 for arch, -1 for cable
  });
});
```

### 2.3 Constraint Behavior Tests (`test/unit/hangingCableConstraints.test.js`)

Test interactive constraints from `globalUpdate()`:

**Horizontal Support Constraint:**
```javascript
test('keeps cable ends level when "Keep supports level" is enabled', () => {
  // When mSupportsHoriz = true and moving left node
  // Then right node Y should match left node Y
});

test('constrains O to O\' y-coordinate when supports are level', () => {
  // When mSupportsHoriz = true
  // Then forcePolyNode.y should equal OPrime.y
});
```

**Force Tail Constraints:**
```javascript
test('prevents force tails from going below cable nodes', () => {
  // When forceTail[i].y > cableNode[i+1].y
  // Then forceTail[i].y is clamped to cableNode[i+1].y
});

test('constrains force tail X to match cable node X', () => {
  // forceTails[i].x should always equal cableNodes[i+1].x
});
```

**Minimum Span Constraint:**
```javascript
test('prevents right cable end from being too close to left end', () => {
  // When cableNodes[8].x < cableNodes[0].x + 8
  // Then cableNodes[8].x is set to cableNodes[0].x + 8
});
```

**Color Switching:**
```javascript
test('switches members to red when structure becomes arch', () => {
  // When isArch() returns true
  // Then all members and force poly lines should be red
});

test('switches members to blue when structure becomes cable', () => {
  // When isArch() returns false
  // Then all members and force poly lines should be blue
});
```

### 2.4 Integration Tests (`test/integration/hangingCableApp.test.js`)

Using Vitest with DOM environment:

```javascript
import { describe, test, expect } from 'vitest';
import { init } from '../../src/graphics/Paper';
import Graphics from '../../src/graphics/Graphics';
import Scene from '../../src/graphics/Scene';
import HangingCableApp from '../../src/apps/HangingCableApp';

describe('HangingCableApp Integration', () => {
  test('initializes without errors', () => {
    init();
    const graphics = new Graphics();
    const scene = new Scene(graphics);

    expect(() => {
      new HangingCableApp(scene);
    }).not.toThrow();
  });

  test('creates all required entities', () => {
    // Verify 9 cable nodes, 7 loads, 2 reactions, etc.
  });

  test('creates all buttons', () => {
    // Verify 4 buttons exist
  });
});
```

---

## Phase 3: Implement Core Features (GREEN Phase)

### 3.1 Fix Constructor (`src/apps/HangingCableApp.js`)

**Current (BROKEN):**
```javascript
constructor() {
    super();  // ❌ Wrong - doesn't match SinglePanelApp pattern
    // ... calls to unimplemented methods
}
```

**Fix to:**
```javascript
constructor(scene) {
    super(scene, [HangingCableApp.APPLET_WIDTH, HangingCableApp.APPLET_HEIGHT]);

    // Initialize state variables
    this.mForceDy = new Array(7);
    this.mIsArch = false;
    this.mUpdatedOnce = false;
    this.mSupportsHoriz = false;
    this.mLinesOfAction = false;

    // Initialize entity arrays
    this.mCableNodes = new Array(9);
    this.mForceTails = new Array(7);
    this.mForceTailStarts = new Array(7);
    this.mEqualTails = new Array(7);
    this.mLoads = new Array(7);
    this.mMembers = new Array(8);
    this.mLoadLine = new Array(8);
    this.mForcePolyLines = new Array(8);

    // Build the demo
    this.makeNodes();
    this.makeRb();
    this.makeLoadLine();
    this.makeRa();
    this.makeForcePolygon();
    this.makeMembers();
    this.makeText();
    this.makeSupports();
    this.makeReport();
    this.makeLinesOfAction();
    this.makeButtons();

    // Register update function
    scene.mGraphics.setAppUpdate(this::this.globalUpdate);
}
```

### 3.2 Add Utility Functions (`src/graphics/util.js`)

Add to existing util object:

```javascript
slope: (x1, y1, x2, y2) => {
    const dx = x2 - x1;
    if (Math.abs(dx) < 1e-10) {
        // Vertical line - return very large number
        return (y2 - y1) > 0 ? 1e10 : -1e10;
    }
    return (y2 - y1) / dx;
},

bound: (value, min, max) => {
    return Math.max(min, Math.min(max, value));
}
```

### 3.3 Implement ALL 19 Methods from Java Reference

Port these methods using Scene-Graphics-Entity pattern:

#### Simple Calculation Methods

1. **`isArch()`** (Java lines 348-350) - TRIVIAL
```javascript
isArch() {
    this.mIsArch = this.mForcePolyNode.x < this.mLoadLine[0].x;
}
```

2. **`distributeCableNodes()`** (Java lines 396-403)
```javascript
distributeCableNodes() {
    const increment = (this.mCableNodes[8].x - this.mCableNodes[0].x) / 8.0;
    let x = this.mCableNodes[0].x + increment;
    for (let i = 1; i < 8; i++) {
        this.mCableNodes[i].x = x;
        x += increment;
    }
}
```

3. **`findOPrime()`** (Java lines 342-346)
```javascript
findOPrime() {
    const slope = util.slope(
        this.mCableNodes[0].x, this.mCableNodes[0].y,
        this.mCableNodes[8].x, this.mCableNodes[8].y
    );
    this.mOPrime.x = this.mLoadLine[0].x;
    this.mOPrime.y = this.mForcePolyNode.y - slope * (this.mForcePolyNode.x - this.mOPrime.x);
}
```

4. **`findO()`** (Java lines 332-340)
```javascript
findO() {
    const len = util.distance(
        this.mForcePolyNode.x, this.mForcePolyNode.y,
        this.mOPrime.x, this.mOPrime.y
    );
    const dir = util.direction(
        this.mCableNodes[8].x, this.mCableNodes[8].y,
        this.mCableNodes[0].x, this.mCableNodes[0].y
    );
    const adjustedLen = this.mIsArch ? len : -len;
    this.mForcePolyNode.x = this.mOPrime.x + adjustedLen * Math.cos(dir);
    this.mForcePolyNode.y = this.mOPrime.y + adjustedLen * Math.sin(dir);
}
```

5. **`findCableYs()`** (Java lines 387-394)
```javascript
findCableYs() {
    let y = this.mCableNodes[0].y;
    const xOver = (this.mCableNodes[8].x - this.mCableNodes[0].x) / 8.0;

    for (let i = 0; i < this.mLoadLine.length - 1; i++) {
        y += util.slope(
            this.mForcePolyNode.x, this.mForcePolyNode.y,
            this.mLoadLine[i].x, this.mLoadLine[i].y
        ) * xOver;
        this.mCableNodes[i + 1].y = y;
    }

    y += util.slope(
        this.mForcePolyNode.x, this.mForcePolyNode.y,
        this.mLoadLine[this.mLoadLine.length - 1].x,
        this.mLoadLine[this.mLoadLine.length - 1].y
    ) * xOver;
    this.mCableNodes[8].y = y;
}
```

6. **`findReactions()`** (Java lines 352-385)
```javascript
findReactions() {
    // Calculate Ra
    let len = this.mForcePolyLines[0].length() + this.mRa.mArrowOffset;
    let dir = this.mForcePolyLines[0].direction();

    if (!this.mIsArch) {
        len = -len;
        this.mRa.mReverse = -1;
    } else {
        this.mRa.mReverse = 1;
    }

    this.mRaTail.item.position.x = this.mCableNodes[0].x + len * Math.cos(dir);
    this.mRaTail.item.position.y = this.mCableNodes[0].y + len * Math.sin(dir);

    if (this.mUpdatedOnce) {
        this.mRaX.item.position.x = this.mRa.mArrowHead.x;
        this.mRaX.item.position.y = this.mRa.mArrowTail.y;
        this.mRaY.item.position.x = this.mRa.mArrowTail.x;
        this.mRaY.item.position.y = this.mRa.mArrowHead.y;
    }

    // Calculate Rb (similar pattern)
    len = this.mForcePolyLines[7].length() + this.mRb.mArrowOffset;
    dir = this.mForcePolyLines[7].direction();

    if (!this.mIsArch) {
        len = -len;
        this.mRb.mReverse = -1;
    } else {
        this.mRb.mReverse = 1;
    }

    this.mRbTail.item.position.x = this.mCableNodes[8].x - len * Math.cos(dir);
    this.mRbTail.item.position.y = this.mCableNodes[8].y - len * Math.sin(dir);

    if (this.mUpdatedOnce) {
        this.mRbX.item.position.x = this.mRb.mArrowHead.x;
        this.mRbX.item.position.y = this.mRb.mArrowTail.y;
        this.mRbY.item.position.x = this.mRb.mArrowTail.x;
        this.mRbY.item.position.y = this.mRb.mArrowHead.y;
    }
}
```

#### Entity Creation Methods

7. **`makeNodes()`** (Java lines 445-466)
8. **`makeRb()`** (Java lines 507-563)
9. **`makeRa()`** (Java lines 565-621)
10. **`makeLoadLine()`** (Java lines 623-654)
11. **`makeForcePolygon()`** (Java lines 656-687)
12. **`makeMembers()`** (Java lines 480-505)
13. **`makeText()`** (Java lines 414-436)
14. **`makeSupports()`** (Java lines 438-443)
15. **`makeReport()`** (Java lines 689-690)
16. **`makeLinesOfAction()`** (Java lines 692-725)
17. **`makeButtons()`** (Java lines 727-836)

#### Main Update Method

18. **`globalUpdate()`** (Java lines 155-272) - COMPLEX
    - Apply horizontal support constraint
    - Toggle lines of action visibility
    - Switch colors for arch/cable mode
    - Constrain force tails
    - Enforce minimum span
    - Call findO() or findOPrime() based on selected entity
    - Distribute cable nodes
    - Calculate funicular curve
    - Update reactions
    - Update member thicknesses

### 3.4 Create Instruction Content

**File:** `src/apps/hangingCableInstructions.js`

Content modeled after `singlePanelInstructions.js`:
- Description of hanging cable vs arch
- Explanation of form diagram and force polygon
- Button descriptions (4 buttons)
- Educational exercises

### 3.5 Update Launcher

**File:** `src/launcher.js` (line 26-29)

Change:
```javascript
{
    id: 'hanging-cable',
    title: 'Hanging Cable',
    description: 'Analyze forces in a suspended cable structure',
    status: 'available',  // Changed from 'in-progress'
    app: HangingCableApp,
    route: '/hanging-cable'
}
```

---

## Phase 4: Fix Issues & Test

### 4.1 Handle Missing Dependencies

**TTextPointLength** - Used for magnitude labels
- Check if exists in `src/graphics/`
- If not, create or use TTextPoint with custom update
- Used for: RaMag, RbMag, RaXMag, RaYMag, RbXMag, RbYMag

**Check Color Constants** in `src/graphics/styles.js`:
- `styles.red` (for arch)
- `styles.blue` (for cable)
- `styles.green` (for reactions)
- `styles.yellow` (for ground line)

**Check Visibility API:**
- Java uses `mInvisible` property
- Check if GraphicEntity supports `visible` or use `item.visible`

### 4.2 Run Tests Incrementally

After each method implementation:
```bash
npm run test:unit  # Run unit tests
```

After all methods:
```bash
npm test  # Run full suite
```

### 4.3 Manual Testing

Start dev server and navigate to hanging cable demo:
```bash
npm run dev
# Navigate to http://localhost:8080/#/hanging-cable
```

---

## Phase 5: Integration & Validation

### 5.1 Manual Testing Checklist

- [ ] App loads without console errors
- [ ] Form diagram displays with 9 cable nodes
- [ ] 8 cable members visible
- [ ] Force polygon displays with O point
- [ ] 7 downward load arrows visible and draggable
- [ ] Cable end nodes (0 and 8) are draggable
- [ ] Dragging loads updates force polygon
- [ ] Dragging cable ends updates funicular curve
- [ ] O point is draggable
- [ ] Structure turns red when O crosses left of load line (arch mode)
- [ ] Structure turns blue when O is right of load line (cable mode)
- [ ] Member thickness changes reflect force magnitudes
- [ ] "Return to Starting Position" button works
- [ ] "Keep Supports Level" button constrains support heights
- [ ] "Equalize Loads" button equalizes load magnitudes
- [ ] "Extend Lines of Action" button shows action lines
- [ ] Instructions display in left text panel
- [ ] Reactions (Ra, Rb) update correctly
- [ ] Reaction component labels display

### 5.2 Regression Testing

- [ ] SinglePanelApp still works
- [ ] Launcher shows both demos
- [ ] Viewport resizing works for both demos
- [ ] Canvas resize bug fix still works

### 5.3 Performance Check

- [ ] No lag when dragging
- [ ] Smooth updates during interaction
- [ ] No memory leaks (check dev tools)

---

## Success Criteria

✅ All unit tests pass (util, physics, constraints)
✅ All integration tests pass
✅ Constructor properly extends AppBase with scene parameter
✅ All 19 methods implemented in JavaScript
✅ Demo runs without console errors
✅ Cable/arch transition works (blue↔red)
✅ Force polygon updates dynamically
✅ All 4 buttons functional
✅ Member thickness visualizes forces
✅ Instructions display properly
✅ No regression in existing demos

---

## Files to Create/Modify

### New Files:
1. `/workspaces/active-statics/vitest.config.js`
2. `/workspaces/active-statics/test/unit/util.test.js`
3. `/workspaces/active-statics/test/unit/hangingCablePhysics.test.js`
4. `/workspaces/active-statics/test/unit/hangingCableConstraints.test.js`
5. `/workspaces/active-statics/test/integration/hangingCableApp.test.js`
6. `/workspaces/active-statics/src/apps/hangingCableInstructions.js`
7. `/workspaces/active-statics/src/graphics/TTextPointLength.js` (if needed)

### Modified Files:
1. `/workspaces/active-statics/package.json` - Add vitest, test scripts
2. `/workspaces/active-statics/src/graphics/util.js` - Add slope(), bound()
3. `/workspaces/active-statics/src/apps/HangingCableApp.js` - **COMPLETE REWRITE**
4. `/workspaces/active-statics/src/launcher.js` - Update status to 'available'
5. `/workspaces/active-statics/src/graphics/styles.js` - Add colors if needed

---

## Estimated Effort

1. **Test Infrastructure** (~30 min)
2. **Write Tests** (~2-3 hours)
3. **Implement Methods** (~4-6 hours)
4. **Fix Issues** (~1-2 hours)
5. **Validation** (~1 hour)

**Total: ~8-12 hours**

---

## TDD Workflow

For each method:
1. 🔴 **RED**: Write failing test
2. 🟢 **GREEN**: Implement to pass
3. 🔵 **REFACTOR**: Clean up
4. ✅ **VERIFY**: Run full suite
5. ➡️ **NEXT**: Move to next method

Start with simplest (isArch, bound, slope) → build to complex (globalUpdate, makeButtons).
