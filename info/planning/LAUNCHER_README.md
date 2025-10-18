# Launcher App - Implementation Guide

## Overview

The launcher app has been implemented to replace the direct Single Panel Truss demo with a menu system that allows users to select from all available demos.

## Changes Made

### 1. Created `src/apps/LauncherApp.js`

A new app that displays a grid of demo cards with:
- **Title and description** for each demo
- **Status badges**: "AVAILABLE", "IN PROGRESS", or "COMING SOON"
- **Visual indicators**: ✓ for available, ⏳ for in-progress, 🔒 for locked
- **Clickable cards** - launches the demo when clicked (if available)
- **Hover effects** - cards highlight on mouse hover

### 2. Updated `src/app.js`

Changed the entry point from:
```javascript
import SinglePanelApp from './apps/SinglePanelApp';
// ...
const singlePanelApp = new SinglePanelApp(new Scene(graphics));
```

To:
```javascript
import LauncherApp from './apps/LauncherApp';
// ...
const launcherApp = new LauncherApp(new Scene(graphics));
```

### 3. Enhanced `src/graphics/Graphics.js`

Added two new methods:
- **`addRectangle(center, size, style)`** - Creates rectangular shapes for cards
- **`clear()`** - Clears the canvas when switching between demos

## Demo Configuration

The launcher currently shows 8 demos:

| Demo | Status | App Class | Description |
|------|--------|-----------|-------------|
| **Single Panel Truss** | ✅ Available | `SinglePanelApp` | Explore forces in a simple three-member truss |
| **Hanging Cable** | ⏳ In Progress | `HangingCableApp` | Analyze forces in a suspended cable structure |
| **Multi-Panel Truss** | 🔒 Coming Soon | Not yet ported | Complex truss analysis with multiple panels |
| **Cantilever Beam** | 🔒 Coming Soon | Not yet ported | Study cantilever beam behavior under load |
| **Beam Loading** | 🔒 Coming Soon | Not yet ported | Continuous beam with distributed loads |
| **Cable-Stayed Bridge** | 🔒 Coming Soon | Not yet ported | Cable-stayed structure analysis |
| **Overhanging Beam** | 🔒 Coming Soon | Not yet ported | Beam with overhanging sections |
| **Minimum Weight** | 🔒 Coming Soon | Not yet ported | Optimize structure for minimum weight |

## Layout

The launcher uses a 3-column grid layout:
- **Card dimensions**: 280px × 320px
- **Margin**: 30px between cards
- **Thumbnail area**: 180px height (placeholder for now)
- **Canvas size**: 1000px × 700px

## How to Add Demos

As each demo is ported from Java, update the `demos` array in `LauncherApp.js`:

```javascript
{
    id: 'your-demo',
    title: 'Demo Title',
    description: 'Short description (max 2 lines)',
    status: 'available',  // Change from 'not-available' to 'available'
    app: YourDemoApp,     // Import and reference the app class
    thumbnail: null       // Will be added later
}
```

## Building

Once the filesystem issues are resolved, build with:

```bash
npm run build
```

This will create `dist/assets/app.bundle.js` which can be copied to `docs/js/` for deployment.

## Testing Locally

### Option 1: Using webpack-dev-server
```bash
npm start
# Navigate to http://localhost:8080
```

### Option 2: Using the built bundle
```bash
npm run build
npm run copyToDoc
cd docs
python -m SimpleHTTPServer 8000
# Navigate to http://localhost:8000
```

## Adding Thumbnails

To add thumbnail images for each demo:

1. Create screenshots of each demo (recommended size: 240px × 180px)
2. Save them in `src/images/thumbnails/`
3. Update the `thumbnail` field in the demo configuration:
   ```javascript
   thumbnail: require('../images/thumbnails/single-panel.png')
   ```
4. Update `LauncherApp.js` to display the image instead of the placeholder

## Visual Design

### Color Scheme
- **Available cards**: Beige background (#f5f5dc) with brown border (#8b4513)
- **In-progress cards**: Light yellow background (#fff8dc) with gold accents
- **Coming soon cards**: Gray background (#e8e8e8) with gray border (#999)

### Hover State
- Available cards lighten to lemon chiffon (#fffacd) on hover
- Cursor changes to pointer for available cards

### Typography
- **Main title**: 36px
- **Subtitle**: 16px
- **Card titles**: 18px
- **Descriptions**: 12px
- **Status badges**: 11px

## Future Enhancements

1. **Search/Filter** - Add search box to filter demos by name
2. **Categories** - Group demos by type (trusses, beams, cables, etc.)
3. **Recent/Favorites** - Track user's recently used demos
4. **Back Button** - Add "Return to Menu" button in each demo
5. **Keyboard Navigation** - Allow arrow keys to navigate cards
6. **Accessibility** - Add ARIA labels and keyboard support
7. **Responsive Design** - Adapt grid for smaller screens
8. **Animations** - Add smooth transitions when launching demos

## Known Issues

- Webpack build failing with filesystem error -35 (likely Docker/environment issue)
- HangingCableApp is marked as "in-progress" but not fully functional
- No thumbnail images yet (using placeholders)
- Card click handlers need testing once build works

## Next Steps

1. Resolve build environment issues
2. Test the launcher interface
3. Create thumbnail images for available demos
4. Add "Back to Menu" functionality to demos
5. Port remaining demos one by one
