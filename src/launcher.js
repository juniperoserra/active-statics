/**
 * HTML-based Launcher (replaces Paper.js canvas launcher)
 * Better styling, hover effects, and accessibility
 */

import router from './router';
import SinglePanelApp from './apps/SinglePanelApp';
import HangingCableApp from './apps/HangingCableApp';
import singlePanelThumb from './assets/thumbnails/single-panel-thumb.png';

export default class Launcher {
    constructor() {
        this.demos = [
            {
                id: 'single-panel',
                title: 'Single Panel Truss',
                description: 'Explore forces in a simple three-member truss',
                status: 'available',
                app: SinglePanelApp,
                route: '/single-panel',
                thumbnail: singlePanelThumb
            },
            {
                id: 'hanging-cable',
                title: 'Hanging Cable',
                description: 'Analyze forces in a suspended cable structure',
                status: 'available',
                app: HangingCableApp,
                route: '/hanging-cable'
            },
            {
                id: 'truss',
                title: 'Multi-Panel Truss',
                description: 'Complex truss analysis with multiple panels',
                status: 'locked',
                app: null,
                route: null
            },
            {
                id: 'cantilever',
                title: 'Cantilever Beam',
                description: 'Study cantilever beam behavior under load',
                status: 'locked',
                app: null,
                route: null
            },
            {
                id: 'beam-load',
                title: 'Beam Loading',
                description: 'Continuous beam with distributed loads',
                status: 'locked',
                app: null,
                route: null
            },
            {
                id: 'cable-stay',
                title: 'Cable-Stayed Bridge',
                description: 'Cable-stayed structure analysis',
                status: 'locked',
                app: null,
                route: null
            },
            {
                id: 'overhang',
                title: 'Overhanging Beam',
                description: 'Beam with overhanging sections',
                status: 'locked',
                app: null,
                route: null
            },
            {
                id: 'min-weight',
                title: 'Minimum Weight',
                description: 'Optimize structure for minimum weight',
                status: 'locked',
                app: null,
                route: null
            }
        ];
    }

    /**
     * Render the launcher HTML
     */
    render() {
        const container = document.getElementById('launcher-container');
        if (!container) {
            console.error('Launcher container not found');
            return;
        }

        container.style.display = 'block';
        container.innerHTML = `
            <div class="launcher-container fade-in">
                <div class="launcher-header">
                    <h1 class="launcher-title">Active Statics</h1>
                    <p class="launcher-subtitle">Interactive explorations of graphic statics</p>
                </div>
                <div class="demo-grid">
                    ${this.demos.map(demo => this.renderCard(demo)).join('')}
                </div>
            </div>
        `;

        // Attach click handlers
        this.attachHandlers();

        // Hide canvas and text panel
        this.hideCanvas();
        this.hideTextPanel();
    }

    /**
     * Render a demo card
     */
    renderCard(demo) {
        const statusText = {
            'available': 'AVAILABLE',
            'in-progress': 'IN PROGRESS',
            'locked': 'COMING SOON'
        }[demo.status];

        const icon = {
            'available': '✓',
            'in-progress': '⏳',
            'locked': '🔒'
        }[demo.status];

        // Use thumbnail image if available, otherwise use icon
        const thumbnailContent = demo.thumbnail
            ? `<div class="demo-thumbnail" style="background-image: url('${demo.thumbnail}'); background-size: contain; background-position: center; background-repeat: no-repeat;"></div>`
            : `<div class="demo-thumbnail">${icon}</div>`;

        return `
            <div class="demo-card ${demo.status}" data-demo-id="${demo.id}" ${demo.status === 'available' ? 'data-clickable="true"' : ''}>
                ${thumbnailContent}
                <div class="demo-card-content">
                    <h3 class="demo-card-title">${demo.title}</h3>
                    <p class="demo-card-description">${demo.description}</p>
                    <div class="demo-card-status">${statusText}</div>
                </div>
            </div>
        `;
    }

    /**
     * Attach click handlers to cards
     */
    attachHandlers() {
        const cards = document.querySelectorAll('.demo-card[data-clickable="true"]');
        cards.forEach(card => {
            card.addEventListener('click', () => {
                const demoId = card.dataset.demoId;
                const demo = this.demos.find(d => d.id === demoId);
                if (demo && demo.route) {
                    router.navigate(demo.route);
                }
            });
        });
    }

    /**
     * Hide canvas when showing launcher
     */
    hideCanvas() {
        const canvas = document.getElementById('myCanvas');
        if (canvas) {
            canvas.style.display = 'none';
        }
    }

    /**
     * Hide text panel when showing launcher
     */
    hideTextPanel() {
        const textPanel = document.getElementById('text-panel');
        if (textPanel) {
            textPanel.classList.remove('visible');
            textPanel.innerHTML = '';
        }
    }

    /**
     * Clean up launcher (before showing a demo)
     */
    destroy() {
        const container = document.getElementById('launcher-container');
        if (container) {
            container.innerHTML = '';
            container.style.display = 'none';
        }

        // Show canvas for demos
        const canvas = document.getElementById('myCanvas');
        if (canvas) {
            canvas.style.display = 'block';
        }
    }

    /**
     * Get demo by route
     */
    getDemoByRoute(route) {
        return this.demos.find(d => d.route === route);
    }
}
