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