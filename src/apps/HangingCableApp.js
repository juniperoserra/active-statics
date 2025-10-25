/**
 * Hanging Cable / Arch Demo
 * Interactive demonstration of graphic statics for cables and arches
 *
 * Ported from Java to JavaScript using Paper.js
 */

import AppBase from './AppBase';
import styles from '../graphics/styles';
import util from '../graphics/util';
import { MoveToStartJob } from '../graphics/AnimationJob';
import { hangingCableInstructions } from './hangingCableInstructions';

export default class HangingCableApp extends AppBase {

    static APPLET_WIDTH = 820;
    static APPLET_HEIGHT = 620;
    static PANEL_SIZE = 45;
    static CABLE_X_START = 160;
    static CABLE_Y_START = 350;
    static START_FORCE_LENGTH = 60;
    static LOAD_LINE_START_X = 710;
    static LOAD_LINE_START_Y = 40;
    static MAX_WIDTH = 60.0;
    static MIN_WIDTH = 2.0;
    static WIDTH_MULT = 0.1;
    static REPORT_X_START = 60;
    static REPORT_Y_START = 480;
    static REPORT_LINE_SPACE = 17;
    static REPORT_COLUMN_SPACE = 110;
    static BUTTON_START_X = 20;
    static BUTTON_START_Y = 70;
    static BUTTON_Y_OFFSET = 30;

    constructor(scene) {
        super(scene, [HangingCableApp.APPLET_WIDTH, HangingCableApp.APPLET_HEIGHT]);

        this.mScene = scene;

        // Show instructions panel
        this.showInstructions();

        // Initialize state variables
        this.mForceDy = new Array(7);
        this.mIsArch = false;
        this.mUpdatedOnce = false;
        this.mSupportsHoriz = false;
        this.mLinesOfAction = false;

        // Initialize entity arrays
        this.mCableNodes = new Array(9);
        this.mForceTails = new Array(7);
        this.mForceTailStarts = new Array(7);
        this.mEqualTails = new Array(7);
        this.mLoads = new Array(7);
        this.mMembers = new Array(8);
        this.mLoadLine = new Array(8);
        this.mForcePolyLines = new Array(8);

        // Build the demo structure
        this.makeNodes();
        this.makeRb();
        this.makeLoadLine();
        this.makeRa();
        this.makeForcePolygon();
        this.makeMembers();
        this.makeText();
        this.makeSupports();
        this.makeReport();
        this.makeLinesOfAction();
        this.makeButtons();

        // Register update function
        scene.mGraphics.setAppUpdate(this::this.globalUpdate);
    }

    //=========================================================================
    // PHYSICS CALCULATION METHODS
    //=========================================================================

    /**
     * Determine if structure is an arch (compression) or cable (tension)
     * Arch: O is left of load line vertical
     * Cable: O is right of or on load line vertical
     */
    isArch() {
        this.mIsArch = this.mForcePolyNode.x < this.mLoadLine[0].x;
    }

    /**
     * Distribute 7 intermediate cable nodes evenly between the two end nodes
     */
    distributeCableNodes() {
        const increment = (this.mCableNodes[8].x - this.mCableNodes[0].x) / 8.0;
        let x = this.mCableNodes[0].x + increment;
        for (let i = 1; i < 8; i++) {
            this.mCableNodes[i].item.position.x = x;
            x += increment;
        }
    }

    /**
     * Calculate O' position based on O position and ground line slope
     * O' stays on the load line vertical
     */
    findOPrime() {
        const slope = util.slope(
            this.mCableNodes[0].x, this.mCableNodes[0].y,
            this.mCableNodes[8].x, this.mCableNodes[8].y
        );
        this.mOPrime.item.position.x = this.mLoadLine[0].x;
        this.mOPrime.item.position.y = this.mForcePolyNode.y - slope * (this.mForcePolyNode.x - this.mOPrime.x);
    }

    /**
     * Calculate O position when cable ends move
     * Maintains distance to O' and moves perpendicular to ground line
     */
    findO() {
        const len = util.distance(
            this.mForcePolyNode.x, this.mForcePolyNode.y,
            this.mOPrime.x, this.mOPrime.y
        );
        const dir = util.direction(
            this.mCableNodes[8].x, this.mCableNodes[8].y,
            this.mCableNodes[0].x, this.mCableNodes[0].y
        );
        const adjustedLen = this.mIsArch ? len : -len;
        this.mForcePolyNode.item.position.x = this.mOPrime.x + adjustedLen * Math.cos(dir);
        this.mForcePolyNode.item.position.y = this.mOPrime.y + adjustedLen * Math.sin(dir);
    }

    /**
     * Calculate cable node Y positions from force polygon
     * Creates the funicular curve
     */
    findCableYs() {
        let y = this.mCableNodes[0].y;
        const xOver = (this.mCableNodes[8].x - this.mCableNodes[0].x) / 8.0;

        // Calculate Y for intermediate nodes (1-7)
        for (let i = 0; i < this.mLoadLine.length - 1; i++) {
            y += util.slope(
                this.mForcePolyNode.x, this.mForcePolyNode.y,
                this.mLoadLine[i].x, this.mLoadLine[i].y
            ) * xOver;
            this.mCableNodes[i + 1].item.position.y = y;
        }

        // Calculate Y for end node (8)
        y += util.slope(
            this.mForcePolyNode.x, this.mForcePolyNode.y,
            this.mLoadLine[this.mLoadLine.length - 1].x,
            this.mLoadLine[this.mLoadLine.length - 1].y
        ) * xOver;
        this.mCableNodes[8].item.position.y = y;
    }

    /**
     * Calculate reaction forces Ra and Rb from force polygon
     */
    findReactions() {
        // Calculate Ra (left reaction)
        let len = this.mForcePolyLines[0].length() + this.mRa.mArrowOffset;
        let dir = this.mForcePolyLines[0].direction();

        if (!this.mIsArch) {
            len = -len;
            this.mRa.mReverse = -1;
        } else {
            this.mRa.mReverse = 1;
        }

        this.mRaTail.item.position.x = this.mCableNodes[0].x + len * Math.cos(dir);
        this.mRaTail.item.position.y = this.mCableNodes[0].y + len * Math.sin(dir);

        if (this.mUpdatedOnce) {
            this.mRaX.item.position.x = this.mRa.mArrowHead.x;
            this.mRaX.item.position.y = this.mRa.mArrowTail.y;
            this.mRaY.item.position.x = this.mRa.mArrowTail.x;
            this.mRaY.item.position.y = this.mRa.mArrowHead.y;
        }

        // Calculate Rb (right reaction)
        len = this.mForcePolyLines[7].length() + this.mRb.mArrowOffset;
        dir = this.mForcePolyLines[7].direction();

        if (!this.mIsArch) {
            len = -len;
            this.mRb.mReverse = -1;
        } else {
            this.mRb.mReverse = 1;
        }

        this.mRbTail.item.position.x = this.mCableNodes[8].x - len * Math.cos(dir);
        this.mRbTail.item.position.y = this.mCableNodes[8].y - len * Math.sin(dir);

        if (this.mUpdatedOnce) {
            this.mRbX.item.position.x = this.mRb.mArrowHead.x;
            this.mRbX.item.position.y = this.mRb.mArrowTail.y;
            this.mRbY.item.position.x = this.mRb.mArrowTail.x;
            this.mRbY.item.position.y = this.mRb.mArrowHead.y;
        }
    }

    //=========================================================================
    // GLOBAL UPDATE - MAIN CONSTRAINT AND UPDATE LOGIC
    //=========================================================================

    globalUpdate() {
        // Early exit if O equals load line position
        if (this.mForcePolyNode.x === this.mLoadLine[0].x) {
            return;
        }

        // Toggle lines of action visibility
        const linesVisible = !this.mLinesOfAction;
        this.mRaLineOfAction.visible = linesVisible;
        this.mRbLineOfAction.visible = linesVisible;
        this.mLoadLineOfAction.visible = linesVisible;
        this.mActionIntersect.visible = linesVisible;

        // Apply horizontal support constraint if enabled
        if (this.mSupportsHoriz && this.mScene.mGraphics.mJobs.length === 0) {
            // Keep cable ends level
            const selectedEntity = null; // TODO: Get from graphics system
            if (selectedEntity === this.mCableNodes[0]) {
                this.mCableNodes[8].item.position.y = this.mCableNodes[0].item.position.y;
            } else if (selectedEntity === this.mCableNodes[8]) {
                this.mCableNodes[0].item.position.y = this.mCableNodes[8].item.position.y;
            }

            // Constrain O to O' horizontal
            if (selectedEntity === this.mForcePolyNode) {
                this.mForcePolyNode.item.position.y = this.mOPrime.item.position.y;
            }
        }

        // Determine arch vs cable mode and set colors
        this.isArch();
        const memberColor = this.mIsArch ? styles.red : styles.blue;

        for (let i = 0; i < 8; i++) {
            this.mMembers[i].mColor = memberColor;
            this.mForcePolyLines[i].mColor = memberColor;
        }

        // Constrain force tails to not go below cable nodes
        if (this.mUpdatedOnce) {
            for (let i = 0; i < 7; i++) {
                if (this.mForceTails[i].item.position.y > this.mLoads[i].mArrowHead.y) {
                    this.mForceTails[i].item.position.y = this.mLoads[i].mArrowHead.y;
                }
            }
        } else {
            this.mUpdatedOnce = true;
        }

        // Enforce minimum span
        if (this.mCableNodes[8].x < this.mCableNodes[0].x + 8) {
            this.mCableNodes[8].item.position.x = this.mCableNodes[0].x + 8;
        }

        // Update O or O' based on what's being dragged
        const selectedEntity = null; // TODO: Get from graphics system
        if (selectedEntity === this.mCableNodes[0] || selectedEntity === this.mCableNodes[8]) {
            this.findO();
        } else {
            this.findOPrime();
        }

        // Constrain O horizontally if supports are level
        if (this.mSupportsHoriz && this.mScene.mGraphics.mJobs.length === 0) {
            if (selectedEntity === this.mForcePolyNode) {
                this.mForcePolyNode.item.position.y = this.mOPrime.item.position.y;
            }
        }

        // Update cable geometry
        this.distributeCableNodes();

        // Sync force tails with cable nodes and record deltas
        for (let i = 0; i < 7; i++) {
            this.mForceTails[i].item.position.x = this.mCableNodes[i + 1].x;
            this.mForceDy[i] = this.mForceTails[i].item.position.y - this.mCableNodes[i + 1].y;
        }

        // Calculate funicular curve
        this.findCableYs();

        // Update reactions
        this.findReactions();

        // Restore force tail Y positions
        for (let i = 0; i < 7; i++) {
            this.mForceTails[i].item.position.y = this.mForceDy[i] + this.mCableNodes[i + 1].y;
        }

        // Update member thicknesses based on forces
        for (let i = 0; i < 8; i++) {
            this.mMembers[i].mSize = util.bound(
                this.mForcePolyLines[i].length() * HangingCableApp.WIDTH_MULT,
                HangingCableApp.MIN_WIDTH,
                HangingCableApp.MAX_WIDTH
            );
        }

        // Update force tail start positions for reset
        for (let i = 0; i < 7; i++) {
            this.mForceTailStarts[i].item.position.x = this.mCableNodes[i + 1].x;
            this.mForceTailStarts[i].item.position.y = this.mCableNodes[i + 1].y - HangingCableApp.START_FORCE_LENGTH;

            this.mEqualTails[i].item.position.x = this.mCableNodes[i + 1].x;
            this.mEqualTails[i].item.position.y = this.mCableNodes[i + 1].y -
                (this.mCableNodes[1].y - this.mForceTails[0].item.position.y);
        }

        // Update helper points
        this.mHorizO.item.position.x = this.mForcePolyNode.x;
        this.mHorizO.item.position.y = this.mOPrime.item.position.y;

        this.mResultantStartNode.item.position.x = this.mActionIntersect.x;
        this.mResultantStartNode.item.position.y = this.mActionIntersect.y;
        this.mResultantEndNode.item.position.x = this.mActionIntersect.x;
        this.mResultantEndNode.item.position.y = this.mActionIntersect.y +
            (this.mLoadLine[7].y - this.mLoadLine[0].y);
    }

    //=========================================================================
    // ENTITY CREATION METHODS
    //=========================================================================

    makeNodes() {
        const x = HangingCableApp.CABLE_X_START;
        const y = HangingCableApp.CABLE_Y_START;

        // Create cable end nodes (draggable)
        this.mCableNodes[0] = this.mScene.createPoint([x, y]);
        this.mCableNodes[8] = this.mScene.createPoint([x + 8 * HangingCableApp.PANEL_SIZE, y]);

        // Create intermediate nodes, force tails, and loads
        for (let i = 0; i < 7; i++) {
            // Cable node (not selectable)
            this.mCableNodes[i + 1] = this.mScene.createPoint(
                [x + (i + 1) * HangingCableApp.PANEL_SIZE, y],
                { selectable: false, size: 3 }
            );

            // Force tail (draggable)
            this.mForceTails[i] = this.mScene.createPoint(
                [x + (i + 1) * HangingCableApp.PANEL_SIZE, y - HangingCableApp.START_FORCE_LENGTH]
            );

            // Force tail start positions (for reset)
            this.mForceTailStarts[i] = this.mScene.createPoint(
                [x + (i + 1) * HangingCableApp.PANEL_SIZE, y - HangingCableApp.START_FORCE_LENGTH],
                { selectable: false, size: 0 }
            );

            // Equal tail positions (for equalize loads)
            this.mEqualTails[i] = this.mScene.createPoint(
                [x + (i + 1) * HangingCableApp.PANEL_SIZE, y - HangingCableApp.START_FORCE_LENGTH],
                { selectable: false, size: 0 }
            );

            // Create load arrow from force tail to cable node
            this.mLoads[i] = this.mScene.createLoad(this.mForceTails[i], this.mCableNodes[i + 1], {
                strokeColor: 'gray'
            });
        }

        // Label the center cable node as "O" (represents the cable center in form diagram)
        this.mCableNodes[4].mLabelText = "O";
        this.mCableNodes[4].mLabelOffset = [0, 24];

        // Create horizontal O helper point
        this.mHorizO = this.mScene.createPoint([0, 0], { selectable: false, size: 0 });
    }

    makeMembers() {
        // Create ground line (dashed yellow line connecting cable ends)
        const groundLine = this.mScene.createLine(this.mCableNodes[0], this.mCableNodes[8], {
            color: styles.yellow || 'yellow',
            thickness: 3,
            dashed: true
        });

        // Create 8 cable members
        for (let i = 0; i < 8; i++) {
            this.mMembers[i] = this.mScene.createLine(this.mCableNodes[i], this.mCableNodes[i + 1], {
                color: styles.blue,
                thickness: 5,
                label: String.fromCharCode(65 + i), // A, B, C, ...
                labelOffset: [0, -20]
            });

            // Drag members with cable ends
            this.mMembers[i].dragAlso(this.mCableNodes[0]);
            this.mMembers[i].dragAlso(this.mCableNodes[8]);
        }
    }

    makeLoadLine() {
        const x = HangingCableApp.LOAD_LINE_START_X;
        const y = HangingCableApp.LOAD_LINE_START_Y;

        // Create first point of load line
        this.mLoadLine[0] = this.mScene.createPoint([x, y], {
            label: 'a',
            labelOffset: [14, 0],
            size: 7
        });

        // Create remaining 7 points using translated points (move with loads)
        for (let i = 0; i < 7; i++) {
            this.mLoadLine[i + 1] = this.mScene.createPointTranslated(
                this.mLoadLine[i],
                this.mForceTails[i],
                this.mLoads[i].mArrowHead,
                {
                    label: String.fromCharCode(98 + i), // b, c, d, ...
                    labelOffset: [14, 0],
                    size: 7
                }
            );
            this.mLoadLine[i + 1].dragAlso(this.mLoadLine[0]);

            // Create line segment
            const line = this.mScene.createLine(this.mLoadLine[i], this.mLoadLine[i + 1], {
                color: 'gray',
                thickness: 4
            });
            line.dragAlso(this.mLoadLine[0]);
        }

        // Create O' point (pole point projection on load line)
        this.mOPrime = this.mScene.createPoint([0, 0], {
            size: 7,
            selectable: false
        });
    }

    makeForcePolygon() {
        // Create O point (pole point)
        this.mForcePolyNode = this.mScene.createPoint([610, 197], {
            label: 'O',
            labelOffset: [-14, -8]
        });
        this.mLoadLine[0].dragAlso(this.mForcePolyNode);

        // Create ground line in force polygon (O to O')
        const groundLine = this.mScene.createLine(this.mForcePolyNode, this.mOPrime, {
            color: styles.yellow || 'yellow',
            thickness: 3,
            dashed: true
        });
        groundLine.dragAlso(this.mLoadLine[0]);

        // Create force polygon rays from load line points to O
        for (let i = 0; i < this.mLoadLine.length; i++) {
            this.mForcePolyLines[i] = this.mScene.createLine(
                this.mLoadLine[i],
                this.mForcePolyNode,
                {
                    color: styles.blue,
                    thickness: 2
                }
            );
            this.mForcePolyLines[i].dragAlso(this.mLoadLine[0]);
        }
    }

    makeRa() {
        // Create reaction tail point
        this.mRaTail = this.mScene.createPoint([0, 0], { size: 0, selectable: false });

        // Create reaction arrow
        this.mRa = this.mScene.createReaction(this.mRaTail, this.mCableNodes[0], {
            arrowOffset: 45,
            strokeColor: styles.green,
            label: 'Ra',
            labelOffset: [-24, 0]
        });

        // Create component visualization points
        this.mRaX = this.mScene.createPoint([0, 0], { size: 0, selectable: false });
        this.mRaY = this.mScene.createPoint([0, 0], { size: 0, selectable: false });

        // Ra X component line
        this.mScene.createLine(this.mRa.mArrowHead, this.mRaX, {
            color: styles.green,
            thickness: 2,
            dashed: true,
            label: 'Rax',
            labelOffset: [10, 0]
        });

        // Ra Y component line
        this.mScene.createLine(this.mRa.mArrowHead, this.mRaY, {
            color: styles.green,
            thickness: 2,
            dashed: true,
            label: 'Ray',
            labelOffset: [0, -10]
        });
    }

    makeRb() {
        // Create reaction tail point
        this.mRbTail = this.mScene.createPoint([0, 0], { size: 0, selectable: false });

        // Create reaction arrow
        this.mRb = this.mScene.createReaction(this.mRbTail, this.mCableNodes[8], {
            arrowOffset: 45,
            strokeColor: styles.green,
            label: 'Rb',
            labelOffset: [14, 0]
        });

        // Create component visualization points
        this.mRbX = this.mScene.createPoint([0, 0], { size: 0, selectable: false });
        this.mRbY = this.mScene.createPoint([0, 0], { size: 0, selectable: false });

        // Rb X component line
        this.mScene.createLine(this.mRb.mArrowHead, this.mRbX, {
            color: styles.green,
            thickness: 2,
            dashed: true,
            label: 'Rbx',
            labelOffset: [10, 0]
        });

        // Rb Y component line
        this.mScene.createLine(this.mRb.mArrowHead, this.mRbY, {
            color: styles.green,
            thickness: 2,
            dashed: true,
            label: 'Rby',
            labelOffset: [0, -10]
        });
    }

    makeLinesOfAction() {
        // Create intersection point of reaction lines of action
        this.mActionIntersect = this.mScene.createPointIntersect(this.mRa, this.mRb, {
            size: 3
        });

        // Ra line of action
        this.mRaLineOfAction = this.mScene.createLine(this.mCableNodes[0], this.mActionIntersect, {
            color: styles.green,
            thickness: 2,
            dashed: true,
            maxLength: 100000
        });

        // Rb line of action
        this.mRbLineOfAction = this.mScene.createLine(this.mCableNodes[8], this.mActionIntersect, {
            color: styles.green,
            thickness: 2,
            dashed: true,
            maxLength: 100000
        });

        // Resultant load line of action
        this.mResultantStartNode = this.mScene.createPoint([0, 0], { size: 0, selectable: false });
        this.mResultantEndNode = this.mScene.createPoint([0, 0], { size: 0, selectable: false });

        this.mLoadLineOfAction = this.mScene.createArrow(
            this.mResultantStartNode,
            this.mResultantEndNode,
            {
                strokeColor: 'gray',
                thickness: 2,
                dashed: true,
                arrowOffset: 0
            }
        );
    }

    makeButtons() {
        const x = HangingCableApp.BUTTON_START_X;
        let y = HangingCableApp.BUTTON_START_Y;
        const width = 180;

        // Return to Starting Position button
        this.mScene.createButton([x, y], 'Return To Starting Position', () => {
            this.mScene.mGraphics.clearJobs();

            // Reset force tails
            for (let i = 0; i < 7; i++) {
                this.mForceTails[i].item.position.y = this.mForceTailStarts[i].item.position.y;
            }

            // Animate back to start
            this.mScene.mGraphics.addJob(new MoveToStartJob(this.mCableNodes[0]));
            this.mScene.mGraphics.addJob(new MoveToStartJob(this.mCableNodes[8]));
            this.mScene.mGraphics.addJob(new MoveToStartJob(this.mLoadLine[0]));
            this.mScene.mGraphics.addJob(new MoveToStartJob(this.mForcePolyNode));
        }, { width });

        y += HangingCableApp.BUTTON_Y_OFFSET;

        // Keep Supports Level button
        this.mScene.createButton([x, y], 'Keep Supports Level', (isToggled) => {
            this.mSupportsHoriz = isToggled;
            if (isToggled) {
                // Move O to horizontal position
                this.mForcePolyNode.item.position.y = this.mOPrime.item.position.y;
            }
        }, { isToggle: true, width });

        y += HangingCableApp.BUTTON_Y_OFFSET;

        // Equalize Loads button
        this.mScene.createButton([x, y], 'Equalize Loads', () => {
            // Move all force tails to equal positions
            for (let i = 0; i < 7; i++) {
                this.mForceTails[i].item.position.y = this.mEqualTails[i].item.position.y;
            }

            if (this.mSupportsHoriz) {
                this.mForcePolyNode.item.position.y = this.mOPrime.item.position.y;
            }
        }, { width });

        y += HangingCableApp.BUTTON_Y_OFFSET;

        // Extend Lines of Action button
        this.mScene.createButton([x, y], 'Extend Lines of Action', (isToggled) => {
            this.mLinesOfAction = isToggled;
        }, { isToggle: true, width });
    }

    makeText() {
        // Form Diagram label
        this.mScene.createTextPoint(this.mCableNodes[1], 'Form Diagram', {
            fontSize: 20,
            offset: [-180, 0],
            draggable: true
        });

        // Force Polygon label
        this.mScene.createTextPoint(this.mLoadLine[0], 'Force Polygon', {
            fontSize: 20,
            offset: [-160, 0],
            draggable: true
        });
    }

    makeSupports() {
        // Pin supports at both cable ends
        this.mScene.createPin(this.mCableNodes[0]);
        this.mScene.createPin(this.mCableNodes[8]);
    }

    makeReport() {
        // Member force report can be added here if needed
        // For now, leave empty (like Java version)
    }

    showInstructions() {
        const textPanel = document.getElementById('text-panel');
        if (textPanel) {
            textPanel.innerHTML = hangingCableInstructions;
            textPanel.classList.add('visible');
        }
    }
}
