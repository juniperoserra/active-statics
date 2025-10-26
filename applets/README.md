# Active Statics - Java Applets via CheerpJ

This directory contains the original Java applets from Active Statics, running in modern browsers using [CheerpJ 3.0](https://cheerpj.com/).

## Overview

CheerpJ is a WebAssembly-powered Java Virtual Machine that runs Java applications directly in the browser without requiring Java plugins. This allows the original 2001 Java applets to run on modern browsers that no longer support Java plugins.

## Directory Structure

```
applets/
├── index.html                 # Navigation page with links to all applets
├── launcher.html              # LauncherApplet (interactive menu)
├── single-panel.html          # SinglePanelApplet
├── hanging-cable.html         # HangingCableApplet
├── truss.html                 # TrussApplet
├── cantilever.html            # CantileverApplet
├── cable-stay.html            # CableStayApplet
├── overhang.html              # OverhangApplet
├── min-weight.html            # MinWeightApplet
├── beam-load.html             # BeamLoadApplet
├── lib/
│   └── ActiveStatics.jar      # Compiled Java classes
├── images/                    # Original applet images
└── README.md                  # This file
```

## How to Use

1. **Start the development server** (if not already running):
   ```bash
   npm run dev
   ```

2. **Open in browser**:
   ```
   http://localhost:8080/applets/
   ```

3. **Choose an applet** from the index page or navigate directly to any individual applet page.

4. **Wait for initialization** - CheerpJ needs a few seconds to initialize the JVM on first load.

## Applets

### 1. Single Panel Truss (`single-panel.html`)
- Dimensions: 620×620
- The simplest truss structure
- Perfect for learning fundamentals of force distribution

### 2. Simple Truss (`truss.html`)
- Dimensions: 700×740
- Complex truss with 12 nodes and 21 members
- Realistic force distribution demonstration

### 3. Hanging Cable/Arch (`hanging-cable.html`)
- Dimensions: 820×620
- Funicular structures (cables/arches)
- Demonstrates catenary curves and force flow

### 4. Cantilever Truss (`cantilever.html`)
- Dimensions: 840×640
- Overhanging structural members
- Shows force resolution in cantilevers

### 5. Fanlike Structure / Cable Stay (`cable-stay.html`)
- Dimensions: 750×700
- Radiating tension members
- Cable-stayed bridge concept

### 6. Overhanging Truss (`overhang.html`)
- Dimensions: 760×800
- Extended overhanging sections
- Force redistribution beyond supports

### 7. Minimum Weight Truss (`min-weight.html`)
- Dimensions: 880×700
- Structural optimization demonstration
- Minimum weight while maintaining strength

### 8. Beam Loading (`beam-load.html`)
- Dimensions: 820×680
- Beam under various loads
- Moment diagrams and shear forces

### Launcher (`launcher.html`)
- Dimensions: 700×700
- Interactive menu for all applets
- Original windowed interface

## Rebuilding the JAR

If you modify the Java source files in `old_java_src/ActiveStatics/src/truss/`, rebuild the JAR:

```bash
./build-applets.sh
```

This will:
1. Compile all Java source files with `javac`
2. Create a new `applets/lib/ActiveStatics.jar` file

**Requirements**: Java JDK 21 (or compatible version)

## Testing

Run the Playwright test suite to verify all applets load correctly:

```bash
npx playwright test test/applets/applets.spec.js
```

Tests verify:
- All pages load with correct titles
- Applet tags have correct attributes
- CheerpJ initializes successfully
- No critical console errors
- Visual regression (screenshots)

## Technical Details

### CheerpJ Integration

Each applet page includes:

1. **CheerpJ Loader**:
   ```html
   <script src="https://cjrtnc.leaningtech.com/4.2/loader.js"></script>
   ```

2. **Applet Tag**:
   ```html
   <applet
       archive="/app/lib/ActiveStatics.jar"
       code="truss.AppletClassName"
       width="..."
       height="...">
   </applet>
   ```

3. **Initialization**:
   ```javascript
   cheerpjInit().then(() => {
       // Applet loaded successfully
   });
   ```

### Virtual Filesystem

CheerpJ uses a virtual filesystem with mount points:
- `/app/` prefix is required for JAR files
- Images in the JAR are accessible via relative paths

### Java Version Compatibility

- **Source**: Java 1.1.8 (original code from 2001)
- **Compilation**: Java 8 bytecode target (`-source 8 -target 8`)
- **Runtime**: CheerpJ runs Java 8 by default for applets

## Known Limitations

1. **First Load Time**: CheerpJ downloads and initializes the JVM on first visit (~5-10 seconds)
2. **Performance**: May be slower than native Java due to WebAssembly/JS translation layer
3. **AWT Rendering**: Some rendering differences may occur compared to original Java applets
4. **Browser Compatibility**: Modern browsers only (Chrome, Firefox, Edge, Safari)

## Comparison with JavaScript Version

The main Active Statics application has been ported to modern JavaScript using Paper.js (see `../src/`). Key differences:

| Feature | Java Applets | JavaScript Version |
|---------|--------------|-------------------|
| Technology | Java + AWT via CheerpJ | Paper.js + ES6 |
| Load Time | 5-10 seconds (JVM init) | < 1 second |
| Performance | Moderate | Fast |
| Compatibility | All applets | 2 applets ported (more pending) |
| Maintenance | Original code preserved | Modern, maintainable |
| Educational Value | Shows original implementation | Production-ready |

## Credits

- **Original Software**: Simon Greenwold, MIT Media Lab (2001)
- **CheerpJ**: Leaning Technologies Ltd.
- **Port**: Maintained as part of Active Statics project

## License

Original software © 2001 Simon Greenwold, MIT Media Lab

## Links

- [CheerpJ Documentation](https://cheerpj.com/docs/)
- [Active Statics (JavaScript version)](../docs/index.html)
- [Original Java Source](../old_java_src/ActiveStatics/)
