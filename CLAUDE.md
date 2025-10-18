# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

ActiveStatics is an interactive educational tool for exploring graphic statics, particularly focused on truss analysis and structural engineering concepts. The project is being ported from Java (original source in `old_java_src/`) to JavaScript using Paper.js for graphics rendering.

**Live Site**: https://juniperoserra.github.io/active-statics/

## Build & Development Commands

## Documentation Structure
- Planning docs → `/info/planning/`
- Architecture docs → `/info/architecture/`
- Decision records → `/info/decisions/`
- Working notes → `/info/notes/`

### Development Server
```bash
npm start
# or
npm run dev
```
Starts webpack-dev-server on localhost with hot reloading. Uses `--env=dev --debug --output-pathinfo` for development.

### Production Build
```bash
npm run build
```
Creates optimized production bundle using webpack with `-p` flag. Output goes to `dist/assets/app.bundle.js`.

### Deploy to GitHub Pages
```bash
npm run copyToDoc
```
Copies the built bundle from `dist/assets/app.bundle.js` to `docs/js/` for GitHub Pages deployment.

### Local Documentation Server
```bash
npm run serveDoc
```
Serves the `docs/` directory on port 8000 using Python's SimpleHTTPServer.

## Architecture

### Core Structure

The application follows a **Scene-Graphics-Entity** architecture:

1. **Graphics Layer** (`src/graphics/Graphics.js`)
   - Wraps Paper.js for 2D canvas rendering
   - Manages the entity collection and rendering pipeline
   - Handles mouse events and drag interactions

2. **Scene Layer** (`src/graphics/Scene.js`)
   - Factory for creating graphic entities (points, lines, members, loads, etc.)
   - Provides high-level API for constructing structural diagrams
   - Acts as the primary interface between apps and the graphics system

3. **Entity System** (`src/graphics/GraphicEntity.js`)
   - Base class for all visual elements
   - Implements drag behavior with `dragAlso()` for linked entities
   - Uses `mDragAlso` array for recursive drag propagation
   - Each entity maintains reference to Graphics instance

4. **App Layer** (`src/apps/`)
   - `AppBase.js`: Base class that initializes scene with canvas size
   - `SinglePanelApp.js`: Single panel truss analysis demo
   - `HangingCableApp.js`: Hanging cable analysis demo
   - Apps are instantiated in `src/app.js` via `window.startApp()`

### Entity Types

Scene factory methods create specialized entities:
- **Structural Elements**: `TPoint`, `TLine`, `TLineMember`, `TPin`, `TRoller`
- **Forces**: `TLoad`, `TReaction`, `TArrow`
- **Analysis Tools**: `TPointForcePoly`, `TLineForcePoly`, `TPointIntersect`, `TPointTranslated`
- **UI**: `TButton`, `TText`, `TTextPoint`, `TTextTriangle`

All entities in `src/graphics/` inherit from `GraphicEntity` and use Paper.js internally.

### Webpack Configuration

- **Entry**: `src/app.js`
- **Output**: `dist/assets/app.bundle.js`
- **Babel**: ES2015 + Stage-0 presets (modules: false for tree shaking)
- **CSS**: CSS modules via `css-loader?modules` + `style-loader`
- **Dev**: `inline-source-map` for debugging
- **Prod**: `cheap-module-source-map`

### Entry Point

The app is initialized via `window.startApp()` (defined in `src/app.js`), which:
1. Initializes Paper.js
2. Creates Graphics instance
3. Instantiates the desired App with a Scene

## Java to JavaScript Port Status

Original Java applets are in `old_java_src/ActiveStatics/src/truss/`. The port is in progress:
- ✅ Core graphics entities (TPoint, TLine, TLoad, etc.)
- ✅ SinglePanelApp
- ✅ HangingCableApp
- ⏳ Other applets pending (TrussApplet, CantileverApplet, BeamLoadApplet, etc.)

When porting, maintain the entity-based architecture and use Scene factory methods rather than direct instantiation.

## Key Implementation Details

### Drag System
Entities use a recursive drag propagation system:
- `dragAlso(entity)` creates dependency links
- `collectDragAlsoRecursive()` builds the full drag set
- Drag deltas are computed from absolute drag start position to prevent drift
- `_dragStartPosition` is cached when drag begins

### Global State
`window.appScene` is set globally for convenience (see `AppBase.js:12` - acknowledged as needing improvement).

### Animation
Entities support animation via `mIsAnimating` flag. See `AnimationJob.js` for job-based animations (`MoveToStartJob`, `CircleAroundJob`).
- When adding tests, please add them to a test directory next to src.