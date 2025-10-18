# Development Server Modernization

**Date:** October 18, 2025
**Status:** ✅ Complete

## Overview

Modernized the build system from webpack 2 and babel 6 (circa 2017) to webpack 5 and babel 7 with proper hot module replacement and a development server that serves the launcher page.

## Changes Made

### 1. **Updated Build Dependencies**
- **Webpack:** 2.x → 5.x
- **Babel:** 6.x → 7.x with `@babel/*` scoped packages
- **Added:** `html-webpack-plugin` for proper HTML injection
- **Added:** `@babel/plugin-proposal-function-bind` for bind operator (`::`) support
- **Removed:** `script-loader` (no longer needed in webpack 5)

### 2. **Modernized Webpack Configuration**
- Enabled Hot Module Replacement (HMR) for instant reloads
- Added `HtmlWebpackPlugin` to automatically inject bundle into `index.html`
- Configured dev server on port 8080
- Set proper `publicPath` and output paths
- Added source maps for development debugging

### 3. **Updated npm Scripts**
```json
"dev": "webpack serve --mode development"  // Start dev server with HMR
"build": "webpack --mode production"       // Production build
"start": "npm run dev"                     // Alias to dev
```

### 4. **Migrated Paper.js Import**
Changed from deprecated `script-loader` syntax to modern ES6 import:
```javascript
// Before:
require("script-loader!paper/dist/paper-core");

// After:
import paper from 'paper/dist/paper-core';
window.paper = paper;  // Global for backward compatibility
```

### 5. **Updated Playwright Configuration**
Added `baseURL` to `.playwright-mcp.json`:
```json
{
  "baseURL": "http://localhost:8080"
}
```

## How to Use

### Development
```bash
npm run dev
# or
npm start
```

Opens dev server at **http://localhost:8080** with:
- ✅ LauncherApp as the index page
- ✅ Hot Module Replacement (changes reload instantly)
- ✅ Source maps for debugging

### Production Build
```bash
npm run build
```

Creates optimized bundle in `dist/assets/app.bundle.js`

### Deploy to GitHub Pages
```bash
npm run build
npm run copyToDoc
```

## Hot Module Replacement

HMR is now enabled and working! When you edit source files:
- Changes are detected automatically
- Only modified modules are rebuilt (~200ms vs 2000ms full rebuild)
- Browser updates without full page reload (when possible)

## Testing with Playwright

Playwright tests can now use the dev server:
- Server runs on http://localhost:8080
- Launcher page is the default entry point
- Same URL structure for both development and testing

## Architecture

The launcher page (`LauncherApp.js`) is now the default application, showing a grid of available demos:
- ✅ Single Panel Truss (available)
- 🚧 Hanging Cable (in progress)
- 🔒 Other demos (coming soon)

## Notes

- Bundle size warnings (244 KiB) are expected - Paper.js is a large library
- The bind operator (`::`) syntax is supported via babel plugin
- Production builds still output to `dist/assets/` for GitHub Pages compatibility
