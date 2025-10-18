/**
 * LauncherApp - Main menu for selecting demos
 */

import AppBase from './AppBase';
import SinglePanelApp from './SinglePanelApp';
import HangingCableApp from './HangingCableApp';

export default class LauncherApp extends AppBase {

    static APPLET_WIDTH = 1000;
    static APPLET_HEIGHT = 700;
    static CARD_WIDTH = 280;
    static CARD_HEIGHT = 320;
    static CARD_MARGIN = 30;
    static CARDS_PER_ROW = 3;
    static START_X = 60;
    static START_Y = 100;

    constructor(scene) {
        super(scene, [LauncherApp.APPLET_WIDTH, LauncherApp.APPLET_HEIGHT]);

        this.demos = [
            {
                id: 'single-panel',
                title: 'Single Panel Truss',
                description: 'Explore forces in a simple three-member truss',
                status: 'available',
                app: SinglePanelApp,
                thumbnail: null // Will be added later
            },
            {
                id: 'hanging-cable',
                title: 'Hanging Cable',
                description: 'Analyze forces in a suspended cable structure',
                status: 'in-progress',
                app: HangingCableApp,
                thumbnail: null
            },
            {
                id: 'truss',
                title: 'Multi-Panel Truss',
                description: 'Complex truss analysis with multiple panels',
                status: 'not-available',
                app: null,
                thumbnail: null
            },
            {
                id: 'cantilever',
                title: 'Cantilever Beam',
                description: 'Study cantilever beam behavior under load',
                status: 'not-available',
                app: null,
                thumbnail: null
            },
            {
                id: 'beam-load',
                title: 'Beam Loading',
                description: 'Continuous beam with distributed loads',
                status: 'not-available',
                app: null,
                thumbnail: null
            },
            {
                id: 'cable-stay',
                title: 'Cable-Stayed Bridge',
                description: 'Cable-stayed structure analysis',
                status: 'not-available',
                app: null,
                thumbnail: null
            },
            {
                id: 'overhang',
                title: 'Overhanging Beam',
                description: 'Beam with overhanging sections',
                status: 'not-available',
                app: null,
                thumbnail: null
            },
            {
                id: 'min-weight',
                title: 'Minimum Weight',
                description: 'Optimize structure for minimum weight',
                status: 'not-available',
                app: null,
                thumbnail: null
            }
        ];

        this.makeTitle();
        this.makeDemoCards();
    }

    makeTitle() {
        this.mScene.createText([LauncherApp.START_X, 50], 'Active Statics', {
            fontSize: 36
        });

        this.mScene.createText([LauncherApp.START_X, 75],
            'Interactive explorations of graphic statics', {
            fontSize: 16
        });
    }

    makeDemoCards() {
        this.demos.forEach((demo, index) => {
            const row = Math.floor(index / LauncherApp.CARDS_PER_ROW);
            const col = index % LauncherApp.CARDS_PER_ROW;

            const x = LauncherApp.START_X + col * (LauncherApp.CARD_WIDTH + LauncherApp.CARD_MARGIN);
            const y = LauncherApp.START_Y + row * (LauncherApp.CARD_HEIGHT + LauncherApp.CARD_MARGIN);

            this.createDemoCard(demo, x, y);
        });
    }

    createDemoCard(demo, x, y) {
        const cardWidth = LauncherApp.CARD_WIDTH;
        const cardHeight = LauncherApp.CARD_HEIGHT;

        // Card background
        const cardBg = this.mScene.mGraphics.addRectangle(
            [x + cardWidth/2, y + cardHeight/2],
            [cardWidth, cardHeight],
            {
                fillColor: demo.status === 'available' ? '#f5f5dc' :
                           demo.status === 'in-progress' ? '#fff8dc' : '#e8e8e8',
                strokeColor: demo.status === 'available' ? '#8b4513' : '#999',
                strokeWidth: 2
            }
        );

        // Thumbnail area (placeholder)
        const thumbHeight = 180;
        const thumbY = y + 20;
        const thumbnail = this.mScene.mGraphics.addRectangle(
            [x + cardWidth/2, thumbY + thumbHeight/2],
            [cardWidth - 40, thumbHeight],
            {
                fillColor: demo.status === 'available' ? '#d2b48c' :
                           demo.status === 'in-progress' ? '#daa520' : '#ccc',
                strokeColor: '#666',
                strokeWidth: 1
            }
        );

        // Placeholder text in thumbnail
        const placeholderText = demo.thumbnail ? '' :
                               demo.status === 'available' ? '✓' :
                               demo.status === 'in-progress' ? '⏳' : '🔒';
        this.mScene.createText([x + cardWidth/2, thumbY + thumbHeight/2], placeholderText, {
            fontSize: 48,
            draggable: false
        });

        // Title
        this.mScene.createText([x + 20, y + thumbHeight + 35], demo.title, {
            fontSize: 18,
            draggable: false
        });

        // Description
        const descLines = this.wrapText(demo.description, 30);
        descLines.forEach((line, i) => {
            this.mScene.createText([x + 20, y + thumbHeight + 60 + (i * 18)], line, {
                fontSize: 12,
                draggable: false
            });
        });

        // Status badge
        const statusY = y + cardHeight - 35;
        let statusText = '';
        let statusColor = '#666';

        if (demo.status === 'available') {
            statusText = 'AVAILABLE';
            statusColor = '#2d5016';
        } else if (demo.status === 'in-progress') {
            statusText = 'IN PROGRESS';
            statusColor = '#8b6914';
        } else {
            statusText = 'COMING SOON';
            statusColor = '#666';
        }

        this.mScene.createText([x + cardWidth/2, statusY], statusText, {
            fontSize: 11,
            color: statusColor,
            draggable: false
        });

        // Make card clickable if available
        if (demo.status === 'available' && demo.app) {
            const group = this.mScene.mGraphics.addGroup([cardBg, thumbnail]);
            group.onClick = () => this.launchDemo(demo);
            group.style = { cursor: 'pointer' };

            // Visual feedback on hover
            group.onMouseEnter = () => {
                cardBg.fillColor = '#fffacd';
            };
            group.onMouseLeave = () => {
                cardBg.fillColor = '#f5f5dc';
            };
        }
    }

    wrapText(text, maxLength) {
        const words = text.split(' ');
        const lines = [];
        let currentLine = '';

        words.forEach(word => {
            if ((currentLine + word).length <= maxLength) {
                currentLine += (currentLine ? ' ' : '') + word;
            } else {
                if (currentLine) lines.push(currentLine);
                currentLine = word;
            }
        });
        if (currentLine) lines.push(currentLine);

        return lines;
    }

    launchDemo(demo) {
        console.log(`Launching demo: ${demo.title}`);

        // Clear the current scene
        this.mScene.mGraphics.clear();

        // Launch the selected demo
        new demo.app(this.mScene);
    }
}
