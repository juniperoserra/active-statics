/**
 * Main application entry point
 * Handles routing and app initialization
 */
import { init } from './graphics/Paper';
import Graphics from './graphics/Graphics';
import Scene from './graphics/Scene';
import router from './router';
import Launcher from './launcher';
import './design-system.css';

let graphics = null;
let scene = null;
let launcher = null;
let currentApp = null;

/**
 * Initialize the application
 */
window.startApp = function() {
    // Initialize Paper.js
    init();
    graphics = new Graphics();
    scene = new Scene(graphics);
    launcher = new Launcher();

    // Register routes
    router
        .register('/', showLauncher)
        .register('/single-panel', () => showDemo('single-panel'))
        .register('/hanging-cable', () => showDemo('hanging-cable'));

    // Setup window resize handler
    window.addEventListener('resize', handleResize);

    // Initial resize to set correct size
    handleResize();

    // Trigger initial route
    router.handleRoute();
};

/**
 * Show the launcher
 */
function showLauncher() {
    // Clear current app
    if (currentApp) {
        scene.mGraphics.clear();
        currentApp = null;
    }

    // Render launcher
    launcher.render();

    // Update breadcrumb
    updateBreadcrumb([]);

    // Update page title
    document.title = 'Active Statics';
}

/**
 * Show a demo
 */
function showDemo(demoId) {
    const demo = launcher.getDemoByRoute(`/${demoId}`);

    if (!demo || !demo.app) {
        console.error(`Demo not found or not available: ${demoId}`);
        router.navigate('/');
        return;
    }

    // Destroy launcher
    launcher.destroy();

    // Clear any existing app
    if (currentApp) {
        scene.mGraphics.clear();
    }

    // Create new app instance
    currentApp = new demo.app(scene);

    // Resize canvas to fit the window after app initialization
    handleResize();

    // Update breadcrumb
    updateBreadcrumb([
        { label: 'Home', route: '/' },
        { label: demo.title, route: demo.route }
    ]);

    // Update page title
    document.title = `${demo.title} | Active Statics`;
}

/**
 * Update breadcrumb navigation
 */
function updateBreadcrumb(crumbs) {
    const breadcrumbEl = document.getElementById('breadcrumb');
    if (!breadcrumbEl) return;

    if (crumbs.length === 0) {
        breadcrumbEl.innerHTML = '';
        breadcrumbEl.style.display = 'none';
        return;
    }

    breadcrumbEl.style.display = 'block';
    breadcrumbEl.className = 'breadcrumb';

    const html = crumbs.map((crumb, index) => {
        const isLast = index === crumbs.length - 1;

        if (isLast) {
            return `<span class="breadcrumb-current">${crumb.label}</span>`;
        } else {
            return `<a href="#${crumb.route}">${crumb.label}</a><span class="breadcrumb-separator">›</span>`;
        }
    }).join('');

    breadcrumbEl.innerHTML = html;
}

/**
 * Handle window resize
 */
function handleResize() {
    const canvas = document.getElementById('myCanvas');
    const mainEl = document.getElementById('main');

    if (!canvas || !mainEl || !scene) return;

    // Get the available space for the canvas
    const breadcrumbEl = document.getElementById('breadcrumb');
    const launcherContainer = document.getElementById('launcher-container');

    let availableHeight = mainEl.clientHeight;
    let availableWidth = mainEl.clientWidth;

    // Subtract breadcrumb height if visible
    if (breadcrumbEl && breadcrumbEl.offsetHeight > 0) {
        availableHeight -= breadcrumbEl.offsetHeight;
    }

    // If launcher is visible, use launcher container dimensions
    if (launcherContainer && launcherContainer.offsetHeight > 0) {
        availableHeight = launcherContainer.clientHeight;
    }

    // Update canvas size
    scene.setSize([availableWidth, availableHeight]);
}