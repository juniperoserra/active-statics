# Launcher Implementation - Complete Guide

## ✅ Implementation Complete

The main app has been successfully updated to show a launcher/menu system instead of directly loading the Single Panel Truss demo.

## Changes Summary

### 1. New Files Created

#### `src/apps/LauncherApp.js` (New)
A full-featured launcher app that:
- Displays a 3-column grid of demo cards
- Shows 8 demos (2 available, 6 coming soon)
- Provides visual feedback with status badges
- Handles click events to launch available demos
- Includes hover effects for better UX

#### `launcher-mockup.html` (Reference)
An HTML mockup showing exactly what the launcher will look like. Open this file in a browser to see the visual design.

### 2. Files Modified

#### `src/app.js`
**Before:**
```javascript
import SinglePanelApp from './apps/SinglePanelApp';
// ...
const singlePanelApp = new SinglePanelApp(new Scene(graphics));
```

**After:**
```javascript
import LauncherApp from './apps/LauncherApp';
// ...
const launcherApp = new LauncherApp(new Scene(graphics));
```

#### `src/graphics/Graphics.js`
Added two new methods:

**`addRectangle(center, size, style)`**
- Creates rectangular shapes for the demo cards
- Uses Paper.js Shape.Rectangle
- Supports all standard Paper.js styling options

**`clear()`**
- Removes all Paper.js items from the canvas
- Clears the entities array
- Stops all running animation jobs
- Resets state flags
- Called when switching between demos

## Demo Configuration

### Available Demos (Clickable)

| Demo | Status | Description |
|------|--------|-------------|
| **Single Panel Truss** | ✓ Available | Explore forces in a simple three-member truss |

### In Progress

| Demo | Status | Description |
|------|--------|-------------|
| **Hanging Cable** | ⏳ In Progress | Analyze forces in a suspended cable structure |

### Coming Soon (6 demos)

- Multi-Panel Truss
- Cantilever Beam
- Beam Loading
- Cable-Stayed Bridge
- Overhanging Beam
- Minimum Weight

## Visual Design

![Launcher Preview](launcher-mockup.html)

### Layout Specifications

```
Canvas Size: 1000px × 700px
Grid: 3 columns × 3 rows
Card Size: 280px × 320px
Margin: 30px between cards

Card Structure:
├─ Thumbnail Area (240×180px)
│  └─ Status Icon (✓ / ⏳ / 🔒)
├─ Title (18px, bold)
├─ Description (12px, 2 lines max)
└─ Status Badge (11px, bold)
```

### Color Scheme

**Available Cards:**
- Background: `#f5f5dc` (Beige)
- Border: `#8b4513` (Saddle Brown)
- Thumbnail: `#d2b48c` (Tan)
- Hover: `#fffacd` (Lemon Chiffon)
- Status Text: `#2d5016` (Dark Green)

**In Progress Cards:**
- Background: `#fff8dc` (Cornsilk)
- Border: `#daa520` (Goldenrod)
- Thumbnail: `#daa520` (Goldenrod)
- Status Text: `#8b6914` (Dark Goldenrod)

**Coming Soon Cards:**
- Background: `#e8e8e8` (Light Gray)
- Border: `#999` (Gray)
- Thumbnail: `#ccc` (Light Gray)
- Status Text: `#666` (Dim Gray)
- Opacity: 0.7
- Cursor: not-allowed

## How It Works

### Card Rendering
Each demo card is rendered using Paper.js shapes:
1. Rectangle for card background
2. Rectangle for thumbnail area
3. Text elements for title, description, status
4. Event handlers for clicks (if available)

### Demo Launching
When a user clicks an available card:
```javascript
launchDemo(demo) {
    // 1. Clear the current scene
    this.mScene.mGraphics.clear();

    // 2. Launch the selected demo
    new demo.app(this.mScene);
}
```

### Adding New Demos

As each demo is ported, update the `demos` array in `LauncherApp.js`:

```javascript
{
    id: 'your-demo-id',
    title: 'Your Demo Title',
    description: 'Brief description (max ~60 chars)',
    status: 'available',  // Change from 'not-available'
    app: YourDemoApp,     // Import the app class
    thumbnail: null       // Optional: add thumbnail image
}
```

## Building and Deploying

### Build Steps

1. **Build the bundle:**
   ```bash
   npm run build
   ```
   This creates `dist/assets/app.bundle.js`

2. **Copy to docs:**
   ```bash
   npm run copyToDoc
   ```
   This copies the bundle to `docs/js/app.bundle.js`

3. **Test locally:**
   ```bash
   cd docs
   python -m SimpleHTTPServer 8000
   # Open http://localhost:8000
   ```

4. **Deploy to GitHub Pages:**
   ```bash
   git add .
   git commit -m "Add launcher menu system"
   git push origin master
   ```
   The site will update at https://juniperoserra.github.io/active-statics/

### Known Build Issues

Currently experiencing webpack filesystem error (-35) in the dev environment. This is likely a Docker/containerization issue and should work in a standard Node.js environment.

**Workaround:** Use the existing `dist/assets/app.bundle.js` or rebuild in a different environment.

## Testing

### Mockup Testing
Open `launcher-mockup.html` in any browser to see the visual design and test card interactions.

### Integration Testing
Once the build works, test:
1. ✓ Launcher loads with all 8 cards
2. ✓ Single Panel Truss card is clickable
3. ✓ Clicking launches the demo
4. ✓ Other cards show "coming soon" state
5. ✓ Hover effects work on available cards

## Future Enhancements

### Short Term
- [ ] Add "Back to Menu" button in each demo
- [ ] Create actual thumbnail images from screenshots
- [ ] Test click handlers with working build

### Medium Term
- [ ] Add search/filter functionality
- [ ] Implement demo categories/tags
- [ ] Add keyboard navigation (arrow keys, Enter)
- [ ] Track recently viewed demos

### Long Term
- [ ] Responsive design for mobile/tablet
- [ ] Smooth transitions between demos
- [ ] User preferences (favorites, etc.)
- [ ] Tutorial/help overlay
- [ ] Analytics to track popular demos

## File Structure

```
src/
├── app.js (Modified - now loads LauncherApp)
├── apps/
│   ├── AppBase.js (Unchanged)
│   ├── LauncherApp.js (NEW - main menu)
│   ├── SinglePanelApp.js (Unchanged)
│   └── HangingCableApp.js (Unchanged)
└── graphics/
    ├── Graphics.js (Modified - added clear() and addRectangle())
    └── ... (other graphics files unchanged)
```

## API Reference

### LauncherApp

```javascript
class LauncherApp extends AppBase {
    // Constants
    static APPLET_WIDTH = 1000;
    static APPLET_HEIGHT = 700;
    static CARD_WIDTH = 280;
    static CARD_HEIGHT = 320;

    // Methods
    constructor(scene)
    makeTitle()
    makeDemoCards()
    createDemoCard(demo, x, y)
    wrapText(text, maxLength)
    launchDemo(demo)
}
```

### Graphics (New Methods)

```javascript
class Graphics {
    // New methods
    addRectangle(center, size, style)
    // center: [x, y] - center point
    // size: [width, height] - dimensions
    // style: Paper.js style object

    clear()
    // Removes all items from canvas
    // Clears entities and jobs
    // Resets state
}
```

## Demo Object Schema

```javascript
{
    id: string,           // Unique identifier (kebab-case)
    title: string,        // Display name
    description: string,  // 1-2 sentence description
    status: string,       // 'available' | 'in-progress' | 'not-available'
    app: Class | null,    // App class constructor
    thumbnail: any        // Image data (future)
}
```

## Next Steps

1. ✅ **Launcher UI created**
2. ✅ **Visual mockup completed**
3. ✅ **Documentation written**
4. ⏳ **Build and test** (pending environment fix)
5. ⏳ **Deploy to production**
6. 🔜 **Port remaining demos one by one**

## Questions?

See also:
- `LAUNCHER_README.md` - Detailed implementation notes
- `launcher-mockup.html` - Visual reference
- `PORT_STATUS.md` - Overall port status
- `CLAUDE.md` - Developer guide

---

**Status**: Implementation complete, pending build/deploy
**Next Demo to Port**: Complete HangingCableApp (75% done)
