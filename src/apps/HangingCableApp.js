/**
 * Created by simong on 2/20/17.
 * Converted from HangingCableApplet.java
 */

import AppBase from './AppBase';
import styles from '../graphics/styles';
import util from '../graphics/util';
import TLine from '../graphics/TLine'; // Assuming TLine.js exists for reaction component lines
import TArrow from '../graphics/TArrow'; // For mLoadLineOfAction
import { MoveToStartJob, JobMovePointToPoint } from '../graphics/AnimationJob';

export default class HangingCableApp extends AppBase {

    static APPLET_WIDTH = 820;
    static APPLET_HEIGHT = 620;
    static PANEL_SIZE = 45; // Corresponds to SEGMENT_SIZE in Java
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
        this.g = scene.mGraphics; // Reference to the graphics context, similar to 'g' in Java

        this.mCableNodes = [];      // TPoint[]
        this.mForceTails = [];      // TPoint[]
        this.mForceTailStarts = []; // To store initial positions for reset
        this.mEqualTails = [];      // To store target positions for equalize

        this.mLoads = [];           // TArrow[] (TLoad)
        this.mMembers = [];         // TLine[] (Using TLine as per Java)
        this.mLoadLine = [];        // TPoint[]
        this.mLoadLineLines = [];   // TLine[]
        this.mForcePolyLines = [];  // TLine[]

        this.mSupportsHoriz = false;
        this.mLinesOfAction = false;
        this.mIsArch = false;
        this.mUpdatedOnce = false;

        this.mForceDy = new Array(7).fill(0);

        // --- Order of creation matters ---
        this.makeNodes();
        this.makeLoads();
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

        // Initial state setup
        // this._setNodeParams(); // Not needed for HangingCable
        // Initial button states are handled by their default `mSelected` or via explicit calls
        // if necessary after creation, like:
        // this.mHorizButton.mSelected = false; // Default

        scene.mGraphics.setAppUpdate(this.globalUpdate.bind(this));
        this.globalUpdate();
    }

    // --- Helper Methods ---
    // ... (Keep _findO, _findOPrime, _isArch, _findReactions, _findCableYs, _distributeCableNodes from previous version) ...
     _findO() {
        // Recalculate Force Polygon Pole O based on O' and cable slope
        // Ensure mOPrime and mForcePolyNode are valid before calculation
        if (!this.mOPrime || !this.mForcePolyNode || !this.mCableNodes[0] || !this.mCableNodes[8]) return;

        const len = util.distance(this.mForcePolyNode.x, this.mForcePolyNode.y, this.mOPrime.x, this.mOPrime.y);
        const dir = util.direction(this.mCableNodes[8].x, this.mCableNodes[8].y, this.mCableNodes[0].x, this.mCableNodes[0].y);
        const adjustedLen = this.mIsArch ? len : -len;

        const newX = this.mOPrime.x + adjustedLen * Math.cos(dir);
        const newY = this.mOPrime.y + adjustedLen * Math.sin(dir);
        this.mForcePolyNode.item.position = [newX, newY];
    }

    _findOPrime() {
        // Recalculate Force Polygon Pole O' based on O and cable slope
        // Ensure mOPrime and mForcePolyNode are valid before calculation
        if (!this.mOPrime || !this.mForcePolyNode || !this.mLoadLine[0] || !this.mCableNodes[0] || !this.mCableNodes[8]) return;
         if (this.mCableNodes[8].x === this.mCableNodes[0].x) return; // Avoid division by zero

        const slope = (this.mCableNodes[8].y - this.mCableNodes[0].y) / (this.mCableNodes[8].x - this.mCableNodes[0].x);

        this.mOPrime.item.position = [
            this.mLoadLine[0].x,
            this.mForcePolyNode.y - slope * (this.mForcePolyNode.x - this.mLoadLine[0].x)
        ];
    }

    _isArch() {
        // Determine if the structure is acting as an arch (O left of load line) or cable (O right)
         if (!this.mForcePolyNode || !this.mLoadLine[0]) return; // Ensure points exist
        this.mIsArch = this.mForcePolyNode.x < this.mLoadLine[0].x;
    }

    _findReactions() {
         // Ensure all necessary components exist before proceeding
         if (!this.mForcePolyLines[7] || !this.mForcePolyLines[6] || !this.mRa || !this.mRb ||
             !this.mRaTail || !this.mRbTail || !this.mCableNodes[0] || !this.mCableNodes[8] ||
             !this.mLoadLine[0] || !this.mLoadLine[6] || !this.mLoadLine[7] ||
             !this.mRaX || !this.mRaY || !this.mRbX || !this.mRbY) {
             // console.warn("Skipping _findReactions due to missing components.");
             return;
         }

        // Calculate Ra based on closing the force polygon (h-a)
        let lenRa = this.mForcePolyLines[7].length() + this.mRa.mArrowOffset; // Line h-a
        let dirRa = this.mForcePolyLines[7].direction();
        if (!this.mIsArch) {
            lenRa = -lenRa;
            this.mRa.mReverse = -1;
        } else {
            this.mRa.mReverse = 1;
        }
        this.mRaTail.item.position = [
            this.mCableNodes[0].x + lenRa * Math.cos(dirRa), // Opposite direction for tail
            this.mCableNodes[0].y + lenRa * Math.sin(dirRa)
        ];


        // Calculate Rb based on the force polygon line (g-h)
         // The Rb vector in force polygon is g->h (mLoadLine[6] to mLoadLine[7])
         let rbVectorX = this.mLoadLine[7].x - this.mLoadLine[6].x;
         let rbVectorY = this.mLoadLine[7].y - this.mLoadLine[6].y;
         let rbDir = Math.atan2(rbVectorY, rbVectorX);
         let rbLen = Math.sqrt(rbVectorX*rbVectorX + rbVectorY*rbVectorY) + this.mRb.mArrowOffset;

         // Determine reversal based on arch/cable state
         if (!this.mIsArch) {
            // For cable, Rb opposes the force vector g->h
            this.mRb.mReverse = -1;
            rbLen = -rbLen; // Use negative length to position tail correctly
        } else {
             // For arch, Rb aligns with the force vector g->h
            this.mRb.mReverse = 1;
        }

         this.mRbTail.item.position = [
             this.mCableNodes[8].x - rbLen * Math.cos(rbDir), // Tail is opposite direction of force vector for display
             this.mCableNodes[8].y - rbLen * Math.sin(rbDir)
         ];


        // Update reaction components visualization points
        if (this.mUpdatedOnce) {
            this.mRaX.item.position = [this.mRa.mArrowHead.x, this.mRa.mArrowTail.y];
            this.mRaY.item.position = [this.mRa.mArrowTail.x, this.mRa.mArrowHead.y];
            this.mRbX.item.position = [this.mRb.mArrowHead.x, this.mRb.mArrowTail.y];
            this.mRbY.item.position = [this.mRb.mArrowTail.x, this.mRb.mArrowHead.y];
        }
    }

    _findCableYs() {
         if (!this.mCableNodes[0] || !this.mCableNodes[8] || !this.mForcePolyNode) return;

        let y = this.mCableNodes[0].y;
        const xStart = this.mCableNodes[0].x;
        const xEnd = this.mCableNodes[8].x;
        if (xEnd === xStart) return; // Avoid division by zero

        const xOver = (xEnd - xStart) / 8.0;
        for (let i = 0; i < 7; i++) { // Iterate through load line segments a-b, b-c, ... g-h
            if (!this.mLoadLine[i]) continue; // Skip if load line point doesn't exist
            const loadLineX = this.mLoadLine[i].x;
            const forcePolyX = this.mForcePolyNode.x;
             if (forcePolyX === loadLineX) continue; // Avoid vertical slope

            const slope = (this.mLoadLine[i].y - this.mForcePolyNode.y) / (loadLineX - forcePolyX);
            y += slope * xOver;
             if (this.mCableNodes[i + 1]) { // Ensure intermediate node exists
                 this.mCableNodes[i + 1].item.position = [this.mCableNodes[i+1].x, y];
             }
        }
         // Explicitly set the last node's Y based on the constraint or user input
         // This ensures it matches the desired end condition, rather than accumulating slope errors.
         this.mCableNodes[8].item.position = [this.mCableNodes[8].x, this.mCableNodes[8].y];
         if (this.mSupportsHoriz) {
              this.mCableNodes[8].item.position = [this.mCableNodes[8].x, this.mCableNodes[0].y];
         }

    }

    _distributeCableNodes() {
         if (!this.mCableNodes[0] || !this.mCableNodes[8]) return;

        const increment = (this.mCableNodes[8].x - this.mCableNodes[0].x) / 8.0;
        let x = this.mCableNodes[0].x + increment;
        for (let i = 1; i < 8; i++) {
             if (this.mCableNodes[i]) { // Ensure node exists
                this.mCableNodes[i].item.position = [x, this.mCableNodes[i].y]; // Only update x
             }
            x += increment;
        }
    }

     _setNodeParams() {
        // Not used in HangingCableApplet.java
     }

     _adjustMastThicknesses() {
        // Not applicable to HangingCableApplet.java
     }

    // --- Scene Creation Methods ---

    makeNodes() {
        const xStart = HangingCableApp.CABLE_X_START;
        const yStart = HangingCableApp.CABLE_Y_START;
        const pSize = HangingCableApp.PANEL_SIZE;
        const fLength = HangingCableApp.START_FORCE_LENGTH;

        // Cable support points
        this.mCableNodes[0] = this.mScene.createPoint([xStart, yStart]);
        this.mCableNodes[8] = this.mScene.createPoint([xStart + 8 * pSize, yStart]);

        // Intermediate cable nodes (1-7) and load tails (0-6)
        for (let i = 0; i < 7; i++) {
            const nodeX = xStart + (i + 1) * pSize;
            // Create intermediate node (Y position calculated later)
            this.mCableNodes[i + 1] = this.mScene.createPoint([nodeX, yStart], {
                selectable: false,
                controlPoint: false,
                label: String.fromCharCode('A'.charCodeAt(0) + i), // Label A-G
                labelOffset: [0, -20] // Adjust label position
            });
            // Create force tail (draggable)
            this.mForceTails[i] = this.mScene.createPoint([nodeX, yStart - fLength]);
            // Store initial positions for reset/equalize
            this.mForceTailStarts[i] = { x: nodeX, y: yStart - fLength };
            this.mEqualTails[i] = { x: nodeX, y: yStart - fLength }; // Initial equal position
        }
        this.mCableNodes[4].mLabelText = 'O'; // Node 4 is labelled 'O' in the Java code
        this.mCableNodes[4].mLabelXOff = 0;
        this.mCableNodes[4].mLabelYOff = 24;


        // Force polygon poles (O and O')
        this.mOPrime = this.mScene.createPoint([0, 0], { size: 7, controlPoint: false, selectable: false }); // Position calculated later
        this.mForcePolyNode = this.mScene.createPoint([HangingCableApp.LOAD_LINE_START_X - 100, HangingCableApp.LOAD_LINE_START_Y + 157], {
            label: "O", labelOffset: [-14, -8]
        });
        // Make O draggable and link it to load line base point a
        this.mLoadLine[0] = this.mScene.createPoint([HangingCableApp.LOAD_LINE_START_X, HangingCableApp.LOAD_LINE_START_Y]); // Create base point 'a' first
        this.mLoadLine[0].dragAlso(this.mForcePolyNode);


        this.mHorizO = this.mScene.createPoint([0, 0], { size: 0 }); // Invisible helper point
    }

    makeLoads() {
        for (let i = 0; i < 7; i++) {
            this.mLoads[i] = this.mScene.createLoad(this.mForceTails[i], this.mCableNodes[i + 1], { color: 'darkGray' });
        }
    }

    makeMembers() {
        // Draw dashed line between supports
        this.mScene.createLine(this.mCableNodes[0], this.mCableNodes[8], {
            color: styles.yellow, thickness: 3, dashed: true, dashLength: 7, gapLength: 5
        });

        // Create cable segments (Lines, not TLineMembers)
        for (let i = 0; i < 8; i++) {
            this.mMembers[i] = this.mScene.createLine(this.mCableNodes[i], this.mCableNodes[i + 1], {
                // Label A-H based on Java mMembers indices
                label: String.fromCharCode('A'.charCodeAt(0) + i),
                labelOffset: [0, -20]
            });
            // Make segments draggable via end points
            this.mMembers[i].dragAlso(this.mCableNodes[0]);
            this.mMembers[i].dragAlso(this.mCableNodes[8]);
            // Link cable segment visual thickness to force polygon line length
            if (this.mForcePolyLines[i]) {
                const memberLine = this.mMembers[i]; // Capture current member
                const forceLine = this.mForcePolyLines[i]; // Capture corresponding force line
                // Assign a function to calculate size dynamically
                memberLine.mSizeFunc = () => Math.max(HangingCableApp.MIN_WIDTH, Math.min(HangingCableApp.MAX_WIDTH,
                    forceLine.length() * HangingCableApp.WIDTH_MULT));
                // Ensure TLine's update calls mSizeFunc or add a manual updater
                const baseUpdate = memberLine.update.bind(memberLine);
                memberLine.update = () => {
                    if (memberLine.mSizeFunc) {
                        memberLine.mSize = memberLine.mSizeFunc();
                    }
                    baseUpdate(); // Call original update
                }
            }
        }
    }

     makeRb() {
        const app = this;
        this.mRbTail = this.mScene.createPoint([0, 0], { size: 0 });
        this.mRb = this.mScene.createReaction(this.mRbTail, this.mCableNodes[8], {
            arrowOffset: 45, strokeColor: styles.green, label: 'Rb', labelOffset: [15, 0]
        });
        this.RbMag = this.mScene.createTextPoint(this.mRbTail, '', {
            offset: [10, -10], lineLength: this.mRb
        });

        this.mRbX = this.mScene.createPoint([0, 0], { size: 0});
        this.mRbY = this.mScene.createPoint([0, 0], { size: 0});
        const rbVert = this.mScene.createLine(this.mRb.mArrowHead, this.mRbY, {color: styles.green, size: 1, dashed: true}); // Connect to mRbY for vertical component line
        const rbHoriz = this.mScene.createLine(this.mRbY, this.mRbTail, {color: styles.green, size: 1, dashed: true}); // Connect Y to Tail

        this.mRbYMag = this.mScene.createTextPoint(this.mRbY, '', {
            offset: [-50, 0], lineLength: rbVert, prefix: 'Rby = '
        });
        this.mRbXMag = this.mScene.createTextPoint(this.mRbX, '', {
            offset: [10, 20], lineLength: rbHoriz, prefix: 'Rbx = '
        });
    }

    makeRa() {
        const app = this;
        this.mRaTail = this.mScene.createPoint([0, 0], { size: 0 });
        this.mRa = this.mScene.createReaction(this.mRaTail, this.mCableNodes[0], {
            arrowOffset: 45, strokeColor: styles.green, label: 'Ra', labelOffset: [-30, 0]
        });
        this.RaMag = this.mScene.createTextPoint(this.mRaTail, '', {
            offset: [-40, -10], lineLength: this.mRa
        });

        this.mRaX = this.mScene.createPoint([0, 0], { size: 0});
        this.mRaY = this.mScene.createPoint([0, 0], { size: 0});
        const raVert = this.mScene.createLine(this.mRa.mArrowHead, this.mRaY, {color: styles.green, size: 1, dashed: true}); // Connect to mRaY
        const raHoriz = this.mScene.createLine(this.mRaY, this.mRaTail, {color: styles.green, size: 1, dashed: true}); // Connect Y to Tail

        this.mRaYMag = this.mScene.createTextPoint(this.mRaY, '', {
            offset: [10, -10], lineLength: raVert, prefix: 'Ray = '
        });
        this.mRaXMag = this.mScene.createTextPoint(this.mRaX, '', {
            offset: [-50, 0], lineLength: raHoriz, prefix: 'Rax = '
        });
    }

    makeLoadLine() {
        const x = HangingCableApp.LOAD_LINE_START_X;
        const y = HangingCableApp.LOAD_LINE_START_Y;

        // Point a (index 0) - Already created in makeNodes
        this.mLoadLine[0].mLabelText = 'a';
        this.mLoadLine[0].mLabelOffset = [14, 0];
        this.mLoadLine[0].mSize = 7;


        let prevPoint = this.mLoadLine[0];
        // Points b-h (indices 1-7) - Representing load vectors
        for (let i = 0; i < 7; i++) {
            const nextPoint = this.mScene.createPointTranslated(prevPoint, this.mLoads[i].mStartPoint, this.mLoads[i].mEndPoint, {
                label: String.fromCharCode('b'.charCodeAt(0) + i), labelOffset: [14, 0], size: 7
            });
            nextPoint.dragAlso(this.mLoadLine[0]);
            this.mLoadLine[i + 1] = nextPoint;
            prevPoint = nextPoint;
        }

        // Lines connecting the load line points (a-b, b-c, ..., g-h, h-a)
        for (let i = 0; i < 8; i++) {
            this.mLoadLineLines[i] = this.mScene.createLine(this.mLoadLine[i], this.mLoadLine[(i + 1) % 8], {
                thickness: 4
            });
            this.mLoadLineLines[i].dragAlso(this.mLoadLine[0]);
        }
        // Style reaction lines
        this.mLoadLineLines[6].mColor = styles.green; // Rb line (g-h)
        this.mLoadLineLines[7].mColor = styles.green; // Ra line (h-a)
         // Style load lines
         for (let i = 0; i < 6; i++) {
             this.mLoadLineLines[i].mColor = 'darkGray';
         }
    }


    makeForcePolygon() {
        // Force Polygon Pole O - Already created in makeNodes

        // Ground line visualization (optional)
        this.mScene.createLine(this.mForcePolyNode, this.mOPrime, {
            color: styles.yellow, thickness: 3, dashed: true, dashLength: 7, gapLength: 5
        }).dragAlso(this.mLoadLine[0]);

        // Create force polygon lines (rays from O to load line points)
        for (let i = 0; i < this.mLoadLine.length; i++) {
            // For HangingCable, these are TLines, not TLineForcePoly, as members are cables/deck
            this.mForcePolyLines[i] = this.mScene.createLine(this.mLoadLine[i], this.mForcePolyNode, {
                 thickness: 2 // Thinner than load line segments
            });
            this.mForcePolyLines[i].dragAlso(this.mLoadLine[0]);
        }
    }

    // *** makeText method added ***
    makeText() {
        this.mScene.createText([20, 50], 'Hanging Cable/Arch', { fontSize: 24 });

        this.mScene.createTextPoint(this.mLoadLine[0], 'Force Polygon', {
            fontSize: 20, offset: [-160, 0] // Adjusted offset slightly from Java
        });

        // Anchor Form Diagram text relative to one of the cable nodes (e.g., the middle one)
        const formDiagramAnchor = this.mCableNodes[4] || this.mCableNodes[0]; // Use node 4 ('O') if available
        this.mScene.createTextPoint(formDiagramAnchor, 'Form Diagram', {
            fontSize: 20, offset: [-180, 0] // Adjusted offset from Java
        });
    }

    makeTriangleLabels() {
        // Not applicable for HangingCableApplet
    }

    makeReport() {
        // Simplified report focusing on reaction components
        const x = HangingCableApp.REPORT_X_START;
        let y = HangingCableApp.REPORT_Y_START;
        const lineSpace = HangingCableApp.REPORT_LINE_SPACE;

        this.mScene.createText([x, y], 'Reactions', { fontSize: 18 });
        y += lineSpace * 1.2;

        // Position reaction magnitudes using their text objects
        if (this.RaMag) {
            this.RaMag.mPrefix = 'Ra = ';
            this.RaMag.item.position = [x + 50, y]; // Position text directly
        }
         y += lineSpace;
         if (this.mRaXMag) {
              this.mRaXMag.item.position = [x + 50, y];
         }
         y += lineSpace;
         if (this.mRaYMag) {
             this.mRaYMag.item.position = [x + 50, y];
         }

        y += lineSpace * 1.5; // Space before Rb

        if (this.RbMag) {
            this.RbMag.mPrefix = 'Rb = ';
            this.RbMag.item.position = [x + 50, y]; // Position text directly
        }
         y += lineSpace;
         if (this.mRbXMag) {
             this.mRbXMag.item.position = [x + 50, y];
         }
         y += lineSpace;
         if (this.mRbYMag) {
             this.mRbYMag.item.position = [x + 50, y];
         }
    }

    makeLinesOfAction() {
        this.mResultantStartNode = this.mScene.createPoint([0,0], {size: 0});
        this.mResultantEndNode = this.mScene.createPoint([0,0], {size: 0});

        // Intersection point of reactions
        this.mActionIntersect = this.mScene.createPointIntersect(this.mRa, this.mRb, {size: 3, controlPoint: false});

        // Line from Ra support through intersection
        this.mRaLineOfAction = this.mScene.createLine(this.mRa.mEndPoint, this.mActionIntersect, {
            dashed: true, thickness: 2, maxLength: 100000, color: styles.green, visible: false
        });
        // Line from Rb support through intersection
        this.mRbLineOfAction = this.mScene.createLine(this.mRb.mEndPoint, this.mActionIntersect, {
            dashed: true, thickness: 2, maxLength: 100000, color: styles.green, visible: false
        });

        // Line representing resultant of applied loads, passing through intersection
        const app = this;
        this.mLoadLineOfAction = this.mScene.createArrow(this.mResultantStartNode, this.mResultantEndNode, {
            arrowOffset: 0, thickness: 2, dashed: true, maxLength: 100000, color: 'gray', visible: false,
             update: function() { // Calculate resultant dynamically based on load line a-h
                 // The resultant of *applied loads only* is vector a-h
                 const loadDx = app.mLoadLine[7].x - app.mLoadLine[0].x;
                 const loadDy = app.mLoadLine[7].y - app.mLoadLine[0].y;

                 // Position start/end based on ActionIntersect and resultant *load* vector
                 if (app.mActionIntersect.isValid) {
                    app.mResultantEndNode.item.position = [app.mActionIntersect.x, app.mActionIntersect.y];
                    app.mResultantStartNode.item.position = [app.mActionIntersect.x - loadDx, app.mActionIntersect.y - loadDy];
                 } else {
                     // Handle parallel case - maybe position based on midpoint?
                     const midX = (app.mRa.mEndPoint.x + app.mRb.mEndPoint.x) / 2;
                     const midY = (app.mRa.mEndPoint.y + app.mRb.mEndPoint.y) / 2;
                     app.mResultantStartNode.item.position = [midX - loadDx / 2, midY - loadDy / 2]; // Example positioning
                     app.mResultantEndNode.item.position = [midX + loadDx / 2, midY + loadDy / 2];
                 }
                  TArrow.prototype.update.call(this); // Call base TArrow update
            }
        });
         this.mLoadLineOfAction.dragAlso(this.mActionIntersect); // Ensure it updates if intersect moves
    }

    makeSupports() {
        this.mPin = this.mScene.createPin(this.mCableNodes[0]);
        this.mRoller = this.mScene.createRoller(this.mCableNodes[8]); // Using Roller for horizontal movement
    }

    makeButtons() {
        const x = HangingCableApp.BUTTON_START_X;
        let y = HangingCableApp.BUTTON_START_Y;
        const app = this; // Capture 'this' for callbacks

        this.mOriginalPosButton = this.mScene.createButton([x, y], 'Return To Starting Position',
            () => {
                app.mScene.mGraphics.clearJobs();
                // Reset state variables first
                app.mSupportsHoriz = false;
                 if (app.mHorizButton) app.mHorizButton.mSelected = false;
                app.mLinesOfAction = false;
                 if (app.mLinesOfActionCheck) app.mLinesOfActionCheck.mSelected = false;

                // Add jobs to move elements back to their start positions
                app.mScene.mGraphics.addJob(new MoveToStartJob(app.mLoadLine[0]));
                app.mScene.mGraphics.addJob(new MoveToStartJob(app.mForcePolyNode));
                app.mScene.mGraphics.addJob(new MoveToStartJob(app.mNodes[0]));
                app.mScene.mGraphics.addJob(new MoveToStartJob(app.mNodes[8])); // Cable end support

                 // Resetting load tails to original start positions
                 for (let i = 0; i < 7; i++) {
                    // Use JobMovePointToPoint to move to the stored start coordinates
                    app.mScene.mGraphics.addJob(new JobMovePointToPoint(app.g, app.mForceTails[i], app.mForceTailStarts[i]));
                 }

                // Let globalUpdate handle recalculating intermediate node positions
                app.globalUpdate();
            }, { width: 170 }
        );
        y += HangingCableApp.BUTTON_Y_OFFSET;

        this.mHorizButton = this.mScene.createButton([x, y], 'Keep supports level',
            (selected) => {
                app.mSupportsHoriz = selected;
                if (selected) {
                    // Animate pole O to O's y-level if turning on
                    app.globalUpdate(); // Calculate O' first
                    app.mScene.mGraphics.addJob(new JobMovePointToPoint(app.g, app.mForcePolyNode, app.mHorizO)); // Move to helper point
                     // Also level the supports if they aren't already
                     app.mScene.mGraphics.addJob(new JobMovePointToPoint(app.g, app.mCableNodes[8], {x: app.mCableNodes[8].x, y: app.mCableNodes[0].y}));
                } else {
                    app.globalUpdate();
                }
            }, { isToggle: true, width: 170 }
        );
        y += HangingCableApp.BUTTON_Y_OFFSET;

        const mEqualButton = this.mScene.createButton([x, y], 'Equalize Loads',
            () => {
                 app.mScene.mGraphics.clearJobs(); // Clear previous animations first
                let lastJob = null;
                for (let i = 0; i < 7; i++) {
                    // Calculate target Y based on current position of node and desired offset
                     const targetY = app.mNodes[i + 1].y - (app.mNodes[1].y - app.mForceTails[0].y);
                    const target = {x: app.mEqualTails[i].x, y: targetY}; // Use current X, target Y
                    const job = new JobMovePointToPoint(app.g, app.mForceTails[i], target);
                    // Chain jobs if desired (simple chaining, may not be necessary)
                    // if (lastJob) job.afterJob = lastJob; // Assuming Job system handles this
                    app.mScene.mGraphics.addJob(job);
                    lastJob = job;
                }

                // If supports are horizontal, re-animate O after loads settle
                if (app.mSupportsHoriz) {
                     app.mScene.mGraphics.addJob(new JobMovePointToPoint(app.g, app.mForcePolyNode, app.mHorizO));
                    // Could chain this after the last load move job if chaining is implemented
                }
            }, { width: 170 }
        );
        y += HangingCableApp.BUTTON_Y_OFFSET;

         this.mLinesOfActionCheck = this.mScene.createButton([x, y], 'Extend Lines of Action',
            (actionLines) => this.mLinesOfAction = actionLines,
            {isToggle: true, width: HangingCableApp.BUTTON_WIDTH}
        );
    }

    // --- Global Update Logic ---
    globalUpdate() {
        const g = this.g; // Shortcut for graphics context
        const selectedEntity = g.selectedEntity;

        if (this.mForcePolyNode.x == this.mLoadLine[0].x) {
            return;
        }

        // Update Lines of Action visibility based on state
        if (this.mActionIntersect && this.mRaLineOfAction && this.mRbLineOfAction && this.mLoadLineOfAction) {
            const visible = this.mLinesOfAction;
            this.mActionIntersect.visible = visible;
            this.mRaLineOfAction.visible = visible;
            this.mRbLineOfAction.visible = visible;
            this.mLoadLineOfAction.visible = visible;

            if(visible) {
                // Ensure intersection point is up-to-date before positioning lines
                this.mActionIntersect.update();
            }
        }


        // Apply constraints based on toggles and interactions
        if (this.mSupportsHoriz && g.mTimer.numJobs() === 0) {
            if (selectedEntity === this.mCableNodes[0]) {
                this.mCableNodes[8].item.position = [this.mCableNodes[8].x, this.mCableNodes[0].y];
            } else if (selectedEntity === this.mCableNodes[8]) {
                this.mCableNodes[0].item.position = [this.mCableNodes[0].x, this.mCableNodes[8].y];
            } else if (selectedEntity === this.mForcePolyNode) {
                 // Force O's Y to match OPrime's Y if O is dragged while supports are level
                 this.mForcePolyNode.item.position = [this.mForcePolyNode.x, this.mOPrime.y];
            }
            // Update the helper point for animations
            this.mHorizO.item.position = [this.mForcePolyNode.x, this.mOPrime.y];
        }

        // --- Recalculate derived geometry ---
        this._distributeCableNodes(); // Recalculate intermediate deck node X positions

        // Recalculate Pole O or O' based on which part of the structure was potentially moved
        if (selectedEntity === this.mCableNodes[0] || selectedEntity === this.mCableNodes[8]) {
             this._findO(); // If supports move, recalculate O based on O'
        } else {
             this._findOPrime(); // Otherwise, recalculate O' based on O
        }

         this._findCableYs();        // Recalculate intermediate deck node Y positions based on O and load line

        // Update load tail Y positions to maintain load magnitude if they weren't dragged
        // Also update stored start/equal positions
         for (let i = 0; i < 7; i++) {
             if (selectedEntity !== this.mForceTails[i]) { // Don't override user drag
                this.mForceTails[i].item.position = [this.mCableNodes[i+1].x, this.mCableNodes[i+1].y + this.mForceDy[i]];
             } else {
                 this.mForceDy[i] = this.mForceTails[i].y - this.mCableNodes[i+1].y; // Update offset if dragged
             }
            this.mEqualTails[i].x = this.mCableNodes[i+1].x; // Update equalize target X
            // Equalize target Y is calculated dynamically in button action based on first load
        }


        this._findReactions();      // Recalculate reaction forces and positions

        // --- Visual Updates ---
        this._isArch();             // Determine if it's an arch or cable
        const memberColor = this.mIsArch ? styles.red : styles.blue;

        // Update member colors and sizes based on force polygon lines
        for (let i = 0; i < 8; i++) {
             if (this.mMembers[i]) {
                this.mMembers[i].mColor = memberColor;
                // Update size dynamically if mSizeFunc exists
                if (this.mMembers[i].updateSize) this.mMembers[i].updateSize();
             }
            if (this.mForcePolyLines[i]) this.mForcePolyLines[i].mColor = memberColor;
        }

        // Adjust reaction magnitude text positions based on arch/cable state
        if (this.RaMag && this.RbMag) {
            if (this.mIsArch) {
                this.RbMag.mOffset = [-20, 20];
                this.RaMag.mOffset = [-20, 20];
                 if(this.mRaYMag) this.mRaYMag.mOffset = [-50, 0];
                 if(this.mRbYMag) this.mRbYMag.mOffset = [10, 0];
            } else {
                this.RbMag.mOffset = [10, -10];
                this.RaMag.mOffset = [-40, -10];
                 if(this.mRaYMag) this.mRaYMag.mOffset = [10, -10];
                 if(this.mRbYMag) this.mRbYMag.mOffset = [-50, 0];
            }
             // Trigger update for the text points manually if offset change doesn't trigger it
             this.RaMag.update();
             this.RbMag.update();
              if(this.mRaXMag) this.mRaXMag.update();
              if(this.mRaYMag) this.mRaYMag.update();
              if(this.mRbXMag) this.mRbXMag.update();
              if(this.mRbYMag) this.mRbYMag.update();
        }

        if (!this.mUpdatedOnce) this.mUpdatedOnce = true;
    }
}