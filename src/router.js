/**
 * Simple hash-based router for client-side navigation
 * Works perfectly with GitHub Pages (no server config needed)
 */

class Router {
    constructor() {
        this.routes = new Map();
        this.currentRoute = null;
        this.init();
    }

    /**
     * Register a route handler
     * @param {string} path - Route path (e.g., '/', '/single-panel')
     * @param {function} handler - Function to call when route matches
     */
    register(path, handler) {
        this.routes.set(path, handler);
        return this;
    }

    /**
     * Navigate to a route
     * @param {string} path - Route path to navigate to
     * @param {boolean} replace - Replace current history entry instead of pushing
     */
    navigate(path, replace = false) {
        const hash = path.startsWith('#') ? path : `#${path}`;

        if (replace) {
            window.location.replace(hash);
        } else {
            window.location.hash = hash;
        }
    }

    /**
     * Get current route path
     */
    getCurrentPath() {
        const hash = window.location.hash;
        return hash ? hash.slice(1) : '/';
    }

    /**
     * Handle route change
     */
    handleRoute() {
        const path = this.getCurrentPath();

        // Find matching route
        const handler = this.routes.get(path) || this.routes.get('/');

        if (handler) {
            this.currentRoute = path;
            handler(path);
        }
    }

    /**
     * Initialize router
     */
    init() {
        // Listen for hash changes
        window.addEventListener('hashchange', () => this.handleRoute());

        // Handle initial route
        window.addEventListener('DOMContentLoaded', () => this.handleRoute());
    }

    /**
     * Get route parameters
     * Future enhancement for routes like '/demo/:id'
     */
    getParams() {
        return {};
    }
}

// Export singleton instance
export default new Router();
