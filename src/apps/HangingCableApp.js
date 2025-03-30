/**
 * Created by simong on 2/20/17.
 * Converted from HangingCableApplet.java
 */

import AppBase from './AppBase';
import styles from '../graphics/styles'; // Assuming styles.js exists
import util from '../graphics/util';    // Assuming util.js exists
import TLine from '../graphics/TLine';    // Assuming TLine.js exists
import { MoveToStartJob, JobMovePointToPoint } from '../graphics/AnimationJob'; // Assuming AnimationJob.js exists

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

        this.mCableNodes = [];      // TPoint[]
        this.mForceTails = [];      // TPoint[]
        this.mForceTailStarts = []; // TPoint[] - Used for reset, store initial positions
        this.mEqualTails = [];      // TPoint[] - Used for equalize loads, store target positions

        this.mLoads = [];           // TArrow[] (TLoad)
        this.mMembers = [];         // TLine[] (TLineMember - though using TLine here based on Java)
        this.mLoadLine = [];        // TPoint[]
        this.mLoadLineLines = [];   // TLine[]
        this.mForcePolyLines = [];  // TLine[] (TLineForcePoly)

        this.mSupportsHoriz = false;
        this.mLinesOfAction = false;
        this.mIsArch = false;
        this.mUpdatedOnce = false; // To prevent initial calculation issues

        this.mForceDy = new Array(7).fill(0); // float[]

        // --- Order of creation matters due to dependencies ---
        this.makeNodes();
        this.makeRb();          // Depends on mCableNodes[8]
        this.makeLoadLine();    // Depends on mForceTails, mLoads, mRb
        this.makeRa();          // Depends on mLoadLine, mCableNodes[0]
        this.makeForcePolygon();// Depends on mLoadLine
        this.makeMembers();     // Depends on mCableNodes, mForcePolyLines
        // this.addNodes(); // Entities are added to scene during creation by Scene methods
        this.makeText();
        this.makeSupports();
        this.makeReport();
        this.makeLinesOfAction(); // Depends on Ra, Rb, Loads
        this.makeButtons();

        // Initial state setup based on Java jbInit completion
        this._setNodeParams();
        this.mTowerVertCheck.mCallback(true); // Call action to set initial state
        this.mDeckHorizCheck.mCallback(true); // Call action to set initial state
        scene.mGraphics.setAppUpdate(this.globalUpdate.bind(this));
        this.globalUpdate(); // Run once initially to set derived positions
    }

    // --- Helper methods translated from Java ---

    _findO() {
        const len = util.distance(this.mForcePolyNode.x, this.mForcePolyNode.y, this.mOPrime.x, this.mOPrime.y);
        const dir = util.direction(this.mCableNodes[8].x, this.mCableNodes[8].y, this.mCableNodes[0].x, this.mCableNodes[0].y);
        const adjustedLen = this.mIsArch ? len : -len;

        const newX = this.mOPrime.x + adjustedLen * Math.cos(dir);
        const newY = this.mOPrime.y + adjustedLen * Math.sin(dir);
        this.mForcePolyNode.item.position = [newX, newY];
    }

    _findOPrime() {
        const slope = (this.mCableNodes[8].y - this.mCableNodes[0].y) / (this.mCableNodes[8].x - this.mCableNodes[0].x);

        this.mOPrime.item.position = [
            this.mLoadLine[0].x,
            this.mForcePolyNode.y - slope * (this.mForcePolyNode.x - this.mLoadLine[0].x)
        ];
    }

    _isArch() {
        this.mIsArch = this.mForcePolyNode.x < this.mLoadLine[0].x;
    }

    _findReactions() {
        let len = this.mForcePolyLines[0].length() + this.mRa.mArrowOffset;
        let dir = this.mForcePolyLines[0].direction();
        if (!this.mIsArch) {
            len = -len;
            this.mRa.mReverse = -1;
        } else {
            this.mRa.mReverse = 1;
        }
        this.mRaTail.item.position = [
            this.mCableNodes[0].x + len * Math.cos(dir),
            this.mCableNodes[0].y + len * Math.sin(dir)
        ];

        if (this.mUpdatedOnce) {
            this.mRaX.item.position = [this.mRa.mArrowHead.x, this.mRa.mArrowTail.y];
            this.mRaY.item.position = [this.mRa.mArrowTail.x, this.mRa.mArrowHead.y];
        }

        len = this.mForcePolyLines[7].length() + this.mRb.mArrowOffset;
        dir = this.mForcePolyLines[7].direction();
        if (!this.mIsArch) {
            len = -len;
            this.mRb.mReverse = -1;
        } else {
            this.mRb.mReverse = 1;
        }
        this.mRbTail.item.position = [
            this.mCableNodes[8].x - len * Math.cos(dir),
            this.mCableNodes[8].y - len * Math.sin(dir)
        ];

        if (this.mUpdatedOnce) {
            this.mRbX.item.position = [this.mRb.mArrowHead.x, this.mRb.mArrowTail.y];
            this.mRbY.item.position = [this.mRb.mArrowTail.x, this.mRb.mArrowHead.y];
        }
    }

    _findCableYs() {
        let y = this.mCableNodes[0].y;
        const xOver = (this.mCableNodes[8].x - this.mCableNodes[0].x) / 8.0;
        for (let i = 0; i < this.mLoadLine.length - 1; i++) {
            const slope = (this.mLoadLine[i].y - this.mForcePolyNode.y) / (this.mLoadLine[i].x - this.mForcePolyNode.x);
            y += slope * xOver;
            this.mCableNodes[i + 1].item.position = [this.mCableNodes[i+1].x, y];
        }
        // The last segment uses the last point in mLoadLine
        // const slope = (this.mLoadLine[this.mLoadLine.length - 1].y - this.mForcePolyNode.y) / (this.mLoadLine[this.mLoadLine.length - 1].x - this.mForcePolyNode.x);
        // y += slope * xOver;
        // this.mCableNodes[8].item.position = [this.mCableNodes[8].x, y];
         // Let's recalculate based on the constraint instead, handled in global update potentially by findOPrime/findO
    }

    _distributeCableNodes() {
        const increment = (this.mCableNodes[8].x - this.mCableNodes[0].x) / 8.0;
        let x = this.mCableNodes[0].x + increment;
        for (let i = 1; i < 8; i++) {
             this.mCableNodes[i].item.position = [x, this.mCableNodes[i].y]; // Only update x
            x += increment;
        }
    }

     _setNodeParams() {
        for (let i = 1; i < 5; i++) {
            // Store the parameter 't' of the node along the tower line segment
            this.mNodes[i].mParamOnSeg = this.mMembers[0].pointToParam(this.mNodes[i]);
        }
    }

    _adjustMastThicknesses() {
        // This logic determines the thickest parts of the mast based on node order
        // In JS/Paper.js, line thickness is usually uniform unless using complex paths.
        // We'll skip direct thickness adjustment but keep the logic for potential future use
        // or if it influences other calculations. The member creation already sets a base thickness.
        // We'll just ensure the members connect the correct (potentially reordered) nodes.

        let nodesToSort = [this.mNodes[1], this.mNodes[2], this.mNodes[3]];
        nodesToSort.sort((a, b) => a.y - b.y); // Sort by y-coordinate ascending
        const topNode = nodesToSort[0];
        const midNode = nodesToSort[1];
        const bottomNode = nodesToSort[2];

        this.mMembers[13].mStartPoint = topNode;
        this.mMembers[13].mEndPoint = midNode;

        this.mMembers[14].mStartPoint = midNode;
        this.mMembers[14].mEndPoint = bottomNode;

        this.mMembers[15].mStartPoint = bottomNode;
        this.mMembers[15].mEndPoint = this.mNodes[5]; // Connects to the base of the tower
    }

    // --- Scene Creation Methods ---

    makeNodes() {
        const xStart = HangingCableApp.CABLE_X_START;
        const yStart = HangingCableApp.CABLE_Y_START;
        const pSize = HangingCableApp.PANEL_SIZE;
        const fLength = HangingCableApp.START_FORCE_LENGTH;

        // Tower nodes (indices 0-5)
        this.mNodes[0] = this.mScene.createPoint([xStart, yStart]);
        this.mNodes[1] = this.mScene.createPoint([xStart, yStart + pSize], { label: 'A', labelOffset: [15, 0] });
        this.mNodes[2] = this.mScene.createPoint([xStart, yStart + 2 * pSize]); // Intermediate tower points
        this.mNodes[3] = this.mScene.createPoint([xStart, yStart + 3 * pSize]);
        this.mNodes[4] = this.mScene.createPoint([xStart, yStart + 4 * pSize], { label: 'F', labelOffset: [-15, 30] });
        this.mNodes[5] = this.mScene.createPoint([xStart, yStart + 7 * pSize]); // Tower base

        // Deck support node (movable horizontally)
        this.mNodes[6] = this.mScene.createPoint([xStart + 3 * pSize, yStart + 4 * pSize], { label: 'B', labelOffset: [0, 30] });
        this.mNodes[4].dragAlso(this.mNodes[6]); // If F moves, B moves

        // Deck nodes (indices 7-13, calculated dynamically in globalUpdate)
        for (let i = 7; i < 14; i++) {
            const tempX = xStart + (i - 9.5) * pSize; // Initial guess
            const tempY = yStart + 4 * pSize;
            this.mNodes[i] = this.mScene.createPoint([tempX, tempY], { selectable: false, controlPoint: false });
             // Labels C-G (adjusted indices)
            if (i > 6) { // Skip label for node 7 (index starts at C)
                 const labelChar = String.fromCharCode('C'.charCodeAt(0) + (i - 7));
                 this.mNodes[i].mLabelText = labelChar;
                 this.mNodes[i].mLabelOffset = [-15, 30];
             }
        }
       // this.mNodes[10].mLabelXOff = -15; // Center label adjustment for node 10

        // Load application points (indices 14-19, below deck nodes 7-12)
        for (let i = 14; i < 20; i++) {
            this.mNodes[i] = this.mScene.createPoint([this.mNodes[i - 7].x, this.mNodes[i - 7].y + 10], { selectable: false, controlPoint: false });
        }

        // Load tail points (indices 20-25, correspond to loads on nodes 7-12)
        for (let i = 20; i < 26; i++) {
            this.mNodes[i] = this.mScene.createPoint([this.mNodes[i - 13].x, this.mNodes[i - 13].y - fLength], { selectable: false, controlPoint: false });
            this.mForceTails.push(this.mNodes[i]); // Store load tails separately
             // Store initial positions for reset and equalize
            this.mForceTailStarts.push({x: this.mNodes[i].x, y: this.mNodes[i].y});
            this.mEqualTails.push({x: this.mNodes[i].x, y: this.mNodes[i].y}); // Initial equal position
        }

        this.mOPrime = this.mScene.createPoint([0, 0], { size: 7, controlPoint: false, selectable: false }); // Position calculated later
        this.mHorizO = this.mScene.createPoint([0, 0], { size: 0 }); // Invisible point for horizontal constraint
    }

    makeMembers() {
        const memberOpts = { tapered: false }; // Cable stays are typically TLines not TLineMembers

        // Tower (will be adjusted by _adjustMastThicknesses)
        this.mMembers[13] = this.mScene.createLine(this.mNodes[1], this.mNodes[2], memberOpts); // Top section
        this.mMembers[14] = this.mScene.createLine(this.mNodes[2], this.mNodes[3], memberOpts); // Middle section
        this.mMembers[15] = this.mScene.createLine(this.mNodes[3], this.mNodes[5], memberOpts); // Bottom section

        // Cable stays (indices 1-6) - connecting tower points to deck points
        this.mMembers[1] = this.mScene.createLine(this.mNodes[1], this.mNodes[7], memberOpts);
        this.mMembers[2] = this.mScene.createLine(this.mNodes[2], this.mNodes[8], memberOpts);
        this.mMembers[3] = this.mScene.createLine(this.mNodes[3], this.mNodes[9], memberOpts);
        this.mMembers[4] = this.mScene.createLine(this.mNodes[1], this.mNodes[12], memberOpts); // Mirrored side
        this.mMembers[5] = this.mScene.createLine(this.mNodes[2], this.mNodes[11], memberOpts);
        this.mMembers[6] = this.mScene.createLine(this.mNodes[3], this.mNodes[10], memberOpts);

        // Deck segments (indices 7-12)
        this.mMembers[7] = this.mScene.createLine(this.mNodes[13], this.mNodes[6], { ...memberOpts, color: styles.yellow }); // Whole deck reference (can be invisible)
        this.mMembers[7].dragAlso(this.mNodes[6]);
        this.mMembers[7].dragAlso(this.mNodes[4]);


        for (let i = 8; i < 13; i++) {
            this.mMembers[i] = this.mScene.createLine(this.mNodes[i - 1], this.mNodes[i], memberOpts);
            this.mMembers[i].dragAlso(this.mNodes[6]);
            this.mMembers[i].dragAlso(this.mNodes[4]);
            // Assign labels 1-6 to deck segments
            this.mMembers[i].mLabelText = (i - 7).toString();
             this.mMembers[i].mLabelOffset = [(i < 11) ? 8 : -16, -10] // Adjust label position
        }
         this.mMembers[17] = this.mScene.createLine(this.mNodes[12], this.mNodes[6], memberOpts); // Closing deck segment
         this.mMembers[17].dragAlso(this.mNodes[6]);
         this.mMembers[17].dragAlso(this.mNodes[4]);

        // Dragging dependencies
        for (let i = 0; i < 18; i++) {
             if (!this.mMembers[i]) continue; // Skip potentially undefined members
            for (let j = 0; j < 14; j++) { // Only make draggable by main nodes (0-6, 13) initially
                if(this.mNodes[j]) this.mMembers[i].dragAlso(this.mNodes[j]);
            }
        }
        this._setNodeParams(); // Initial parameter calculation
    }


    makeLoads() {
        for (let i = 0; i < 6; i++) {
            // Loads hang from nodes 14-19 down to nodes 20-25
            this.mLoads[i] = this.mScene.createLoad(this.mNodes[i + 20], this.mNodes[i + 14], { color: 'darkGray' });
             // Labels for loads (optional)
             // this.mLoads[i].mLabelText = `P${i+1}`;
             this.mLoads[i].mLabelOffset = [0, 0]; // Adjust as needed
        }
    }

    makeRb() {
        const app = this;
        this.mRbTail = this.mScene.createPoint([0, 0], {
            size: 0, // Invisible point
            update: function() {
                // Rb reaction is vertical, calculated based on moments around Ra (node 6)
                let totalMoment = 0;
                for (let i = 0; i < 6; i++) {
                    totalMoment += app.mLoads[i].moment(app.mNodes[6]); // Moment around node 6
                }
                 const pDist = app.mNodes[5].x - app.mNodes[6].x; // Horizontal distance between supports
                 const reactionForce = Math.abs(pDist) < 0.1 ? 0 : -totalMoment / pDist; // Rb is vertical

                this.item.position = [app.mNodes[5].x, app.mNodes[5].y + reactionForce + app.mRb.mArrowOffset];
                 app.mRb.mReverse = (reactionForce < 0) ? -1 : 1;
            }
        });
        this.mRb = this.mScene.createReaction(this.mRbTail, this.mNodes[5], {
            arrowOffset: 15, strokeColor: styles.green, label: 'Rb' //, labelOffset: [15, 0]
        });

         this.RbMag = this.mScene.createTextPoint(this.mRbTail, '', {
             offset: [-20, 20], lineLength: this.mRb
         });

         // For visual decomposition (optional, based on Java's mRbX/Y)
         this.mRbX = this.mScene.createPoint([0, 0], { size: 0}); // Position updated in globalUpdate or reaction update
         this.mRbY = this.mScene.createPoint([0, 0], { size: 0});
         const rbVert = this.mScene.createLine(this.mRb.mArrowHead, this.mRbX, {color: styles.green, size: 1, dashed: true, dashLength: 7, gapLength: 5});
         const rbHoriz = this.mScene.createLine(this.mRbX, this.mRbTail, {color: styles.green, size: 1, dashed: true, dashLength: 7, gapLength: 5});

         this.mRbYMag = this.mScene.createTextPoint(this.mRbY, '', {
             offset: [-50, 0], lineLength: rbVert, prefix: 'Rby = '
         });
          this.mRbXMag = this.mScene.createTextPoint(this.mRbX, '', {
             offset: [0, 20], lineLength: rbHoriz, prefix: 'Rbx = '
         });
    }

     makeRa() {
        const app = this;
        this.mRaTail = this.mScene.createPoint([0, 0], {
            size: 0, // Invisible point
            update: function() {
                // Ra reaction is vertical, calculated based on sum of forces
                let totalLoad = 0;
                 for (let i = 0; i < 6; i++) {
                     totalLoad += (app.mLoads[i].mEndPoint.y - app.mLoads[i].mStartPoint.y); // Vertical component
                 }
                 const rbForce = (app.mRb.mArrowHead.y - app.mRb.mStartPoint.y);
                 const raForce = -(totalLoad + rbForce); // Sum F_y = 0

                 this.item.position = [app.mNodes[6].x, app.mNodes[6].y + raForce + app.mRa.mArrowOffset];
                 app.mRa.mReverse = (raForce < 0) ? -1 : 1;

                 // Update reaction components visualization points
                 app.mRaX.item.position = [app.mRa.mArrowHead.x, app.mRa.mArrowTail.y];
                 app.mRaY.item.position = [app.mRa.mArrowTail.x, app.mRa.mArrowHead.y];
            }
        });
        this.mRa = this.mScene.createReaction(this.mRaTail, this.mNodes[6], {
            arrowOffset: 15, strokeColor: styles.green, label: 'Ra' //, labelOffset: [-30, 0]
        });

        this.RaMag = this.mScene.createTextPoint(this.mRaTail, '', {
            offset: [-20, 20], lineLength: this.mRa
        });

         // For visual decomposition
         this.mRaX = this.mScene.createPoint([0, 0], { size: 0});
         this.mRaY = this.mScene.createPoint([0, 0], { size: 0});
         const raVert = this.mScene.createLine(this.mRa.mArrowHead, this.mRaX, {color: styles.green, size: 1, dashed: true, dashLength: 7, gapLength: 5});
         const raHoriz = this.mScene.createLine(this.mRaX, this.mRaTail, {color: styles.green, size: 1, dashed: true, dashLength: 7, gapLength: 5});

         this.mRaYMag = this.mScene.createTextPoint(this.mRaY, '', {
             offset: [10, -10], lineLength: raVert, prefix: 'Ray = '
         });
          this.mRaXMag = this.mScene.createTextPoint(this.mRaX, '', {
             offset: [0, 20], lineLength: raHoriz, prefix: 'Rax = '
         });
    }

    makeLoadLine() {
        const x = HangingCableApp.LOAD_LINE_START_X;
        const y = HangingCableApp.LOAD_LINE_START_Y;

        // Load Line points a-g (indices 0-6) representing applied loads
        this.mLoadLine[0] = this.mScene.createPoint([x, y], { label: 'a', labelOffset: [14, 0], size: 7 });
        let prevPoint = this.mLoadLine[0];
        for (let i = 0; i < 6; i++) {
            const nextPoint = this.mScene.createPointTranslated(prevPoint, this.mLoads[i].mStartPoint, this.mLoads[i].mEndPoint, {
                label: String.fromCharCode('b'.charCodeAt(0) + i), labelOffset: [14, 0], size: 7
            });
            nextPoint.dragAlso(this.mLoadLine[0]);
            this.mLoadLine[i + 1] = nextPoint;
            prevPoint = nextPoint;
        }

         // Point h (index 7) representing reaction Rb
        const pointH = this.mScene.createPointTranslated(prevPoint, this.mRb.mArrowTail, this.mRb.mArrowHead, {
            label: 'h', labelOffset: [14, 0], size: 7
        });
        pointH.dragAlso(this.mLoadLine[0]);
        this.mLoadLine[7] = pointH;


        // Lines connecting the load line points
        for (let i = 0; i < 8; i++) {
            this.mLoadLineLines[i] = this.mScene.createLine(this.mLoadLine[i], this.mLoadLine[(i + 1) % 8], {
                strokeColor: 'darkGray', thickness: 4
            });
            this.mLoadLineLines[i].dragAlso(this.mLoadLine[0]);
        }
        // Style reaction lines
        this.mLoadLineLines[6].mColor = styles.green; // Rb line (g-h)
        this.mLoadLineLines[7].mColor = styles.green; // Ra line (h-a)

        // Pole O' for force polygon construction
        this.mOPrime = this.mScene.createPoint([x - 100, y + 157], { size: 7, controlPoint: false, selectable: false }); // Initial arbitrary position
    }


    makeForcePolygon() {
        // Force Polygon Pole O
        this.mForcePolyNode = this.mScene.createPoint([HangingCableApp.LOAD_LINE_START_X - 100, HangingCableApp.LOAD_LINE_START_Y + 157], {
            label: "O", labelOffset: [-14, -8]
        });
        this.mLoadLine[0].dragAlso(this.mForcePolyNode); // Dragging load line drags the pole

        // Ground line visualization (optional)
        this.mScene.createLine(this.mForcePolyNode, this.mOPrime, {
            color: styles.yellow, thickness: 3, dashed: true, dashLength: 7, gapLength: 5
        }).dragAlso(this.mLoadLine[0]);

        // Create force polygon lines (rays from O to load line points)
        for (let i = 0; i < this.mLoadLine.length; i++) {
            // For HangingCable, these are TLines, not TLineForcePoly, as members are cables/deck
            this.mForcePolyLines[i] = this.mScene.createLine(this.mLoadLine[i], this.mForcePolyNode, {
                 thickness: 2
            });
            this.mForcePolyLines[i].dragAlso(this.mLoadLine[0]);
        }
    }

    makeTriangleLabels() {
        // Not applicable/needed for cable-stayed structure visualization in the same way as truss panels.
    }

    makeReport() {
        // Implementation depends heavily on what needs to be reported (e.g., cable tensions).
        // This requires mapping mForcePolyLines lengths back to specific members.
        // Example structure:
        const x = HangingCableApp.REPORT_X_START;
        let y = HangingCableApp.REPORT_Y_START;
        const lineSpace = HangingCableApp.REPORT_LINE_SPACE;

        this.mScene.createText([x, y], 'Cable Tensions', { fontSize: 18 });
        y += lineSpace * 1.2;

        // Map force polygon lines to members (adjust indices based on your makeMembers logic)
        const cableMap = {
            1: 1, // Member 1 corresponds to Force Poly Line 1 (O-b) ? Check this mapping
            2: 2,
            3: 3,
            4: 7, // Mirrored side? (O-h) -> check LoadLine points
            5: 6,
            6: 5
        };

        for (let i = 1; i <= 6; i++) {
            const memberIndex = cableMap[i];
             if(this.mForcePolyLines[memberIndex] && this.mMembers[i]){
                 this.mScene.createTextPoint(this.mMembers[i].mStartPoint, '', { // Anchor text to member
                     offset: [x + 10 - this.mMembers[i].mStartPoint.x, y + (i-1)*lineSpace - this.mMembers[i].mStartPoint.y ],
                     prefix: `${this.mMembers[i].mLabelText || `Cable ${i}`} = `,
                     lineLength: this.mForcePolyLines[memberIndex], // Use the corresponding force line
                     leftJustify: true,
                     draggable: false // Keep report static relative to view usually
                 });
             }
        }
         // Add reports for reactions if needed
        // ...
    }

     makeLinesOfAction() {
        this.mResultantStartNode = this.mScene.createPoint([0,0], {size: 0});
        this.mResultantEndNode = this.mScene.createPoint([0,0], {size: 0});

        this.mActionIntersect = this.mScene.createPointIntersect(this.mRa, this.mRb, {size: 3, controlPoint: false});

        this.mRaLineOfAction = this.mScene.createLine(this.mRa.mEndPoint, this.mActionIntersect, { dashed: true, thickness: 2, maxLength: 100000, color: styles.green });
        this.mRbLineOfAction = this.mScene.createLine(this.mRb.mEndPoint, this.mActionIntersect, { dashed: true, thickness: 2, maxLength: 100000, color: styles.green });

        // Resultant of applied loads (sum of vectors a-b, b-c, ...)
        const app = this;
        this.mLoadLineOfAction = this.mScene.createArrow(this.mResultantStartNode, this.mResultantEndNode, {
            arrowOffset: 0, thickness: 2, dashed: true, maxLength: 100000, color: 'gray',
             update: function() { // Calculate resultant dynamically
                 let totalDx = 0;
                 let totalDy = 0;
                 for(let i=0; i<7; i++) { // Summing load vectors
                      totalDx += app.mLoadLine[i+1].x - app.mLoadLine[i].x;
                      totalDy += app.mLoadLine[i+1].y - app.mLoadLine[i].y;
                 }
                 // Position start/end based on ActionIntersect and resultant vector
                 // This might need refinement based on desired visualization
                 app.mResultantEndNode.item.position = [app.mActionIntersect.x, app.mActionIntersect.y];
                 app.mResultantStartNode.item.position = [app.mActionIntersect.x - totalDx, app.mActionIntersect.y - totalDy];
                  this.update(); // Call TArrow's update
            }
        });
    }

    makeButtons() {
        const x = HangingCableApp.BUTTON_START_X;
        let y = HangingCableApp.BUTTON_START_Y;
        const app = this; // Capture 'this' for callbacks

        this.mOriginalPosButton = this.mScene.createButton([x, y], 'Return To Starting Position',
            () => {
                app.mScene.mGraphics.clearJobs();
                // Add jobs to move elements back to their start positions
                // Need to store start positions for mNodes[0], mNodes[5], mNodes[6], mLoadLine[0], mForcePolyNode
                 app.mScene.mGraphics.addJob(new MoveToStartJob(app.mLoadLine[0]));
                 app.mScene.mGraphics.addJob(new MoveToStartJob(app.mForcePolyNode));
                 app.mScene.mGraphics.addJob(new MoveToStartJob(app.mNodes[0]));
                 app.mScene.mGraphics.addJob(new MoveToStartJob(app.mNodes[5]));
                 app.mScene.mGraphics.addJob(new MoveToStartJob(app.mNodes[6]));
                 // Resetting intermediate nodes requires recalculating their positions based on start points
                 // Or storing their start positions too. Let's assume recalc is acceptable for now.
                // Resetting load tails:
                 for (let i = 0; i < 7; i++) {
                    app.mScene.mGraphics.addJob(new JobMovePointToPoint(app.g, app.mForceTails[i], app.mForceTailStarts[i]));
                 }


                // Reset state variables
                app.mSupportsHoriz = false;
                app.mDeckHorizCheck.mSelected = false;
                app.mTowerVertCheck.mSelected = false;
                app.mTowerVert = false;
                app.mLinesOfActionCheck.mSelected = false;
                app.mLinesOfAction = false;
                 app._setNodeParams(); // Recalculate params after moving
                 app.globalUpdate(); // Run global update to fix positions
            }, { width: 170 }
        );
        y += HangingCableApp.BUTTON_Y_OFFSET;

        this.mDeckHorizCheck = this.mScene.createButton([x, y], 'Keep Deck Horizontal',
            (selected) => {
                app.mDeckHoriz = selected;
                if (selected) { // If turning on, might need to animate deck to horizontal
                    app.globalUpdate(); // Apply constraint immediately
                    app.mScene.mGraphics.addJob(new JobMovePointToPoint(app.g, app.mNodes[6], {x: app.mNodes[6].x, y: app.mNodes[4].y}));
                } else {
                    app.globalUpdate();
                }
            }, { isToggle: true, width: 170 }
        );
        y += HangingCableApp.BUTTON_Y_OFFSET;

        this.mTowerVertCheck = this.mScene.createButton([x, y], 'Keep Tower Vertical',
            (selected) => {
                app.mTowerVert = selected;
                 if (selected) { // If turning on, might need to animate tower to vertical
                    app.globalUpdate(); // Apply constraint immediately
                     app.mScene.mGraphics.addJob(new JobMovePointToPoint(app.g, app.mNodes[5], {x: app.mNodes[0].x, y: app.mNodes[5].y}));
                } else {
                     app.globalUpdate();
                 }
            }, { isToggle: true, width: 170 }
        );
        y += HangingCableApp.BUTTON_Y_OFFSET;

        this.mHarpButton = this.mScene.createButton([x, y], 'Harp Configuration',
            () => {
                // Calculate target positions based on Harp logic (parallel cables)
                 app._findHarpPoints(); // Calculate target points first
                app.mScene.mGraphics.addJob(new JobMovePointToPoint(app.g, app.mNodes[2], app.mHarpPoints[0]));
                app.mScene.mGraphics.addJob(new JobMovePointToPoint(app.g, app.mNodes[3], app.mHarpPoints[1]));
                 app.globalUpdate(); // Update dependent positions
            }, { width: 170 }
        );
        y += HangingCableApp.BUTTON_Y_OFFSET;

        this.mFanButton = this.mScene.createButton([x, y], 'Fan Configuration',
            () => {
                 app.mScene.mGraphics.addJob(new JobMovePointToPoint(app.g, app.mNodes[2], app.mNodes[1]));
                 app.mScene.mGraphics.addJob(new JobMovePointToPoint(app.g, app.mNodes[3], app.mNodes[1]));
                 app.globalUpdate(); // Update dependent positions
            }, { width: 170 }
        );
         y += HangingCableApp.BUTTON_Y_OFFSET;

         this.mLinesOfActionCheck = this.mScene.createButton([x, y], 'Extend Lines of Action',
            (actionLines) => this.mLinesOfAction = actionLines,
            {isToggle: true, width: HangingCableApp.BUTTON_WIDTH}
        );
    }

     _findHarpPoints() {
         // Translate Java logic from makeHarpPoints
         const towerLine = new TLine(this.mScene.mGraphics, this.mNodes[0], this.mNodes[5]); // Temporary line for intersection

        let dx = this.mNodes[7].x - this.mNodes[8].x;
        let dy = this.mNodes[7].y - this.mNodes[8].y;
        const line1 = new TLine(this.mScene.mGraphics, this.mNodes[8], {x: this.mNodes[1].x - dx, y: this.mNodes[1].y - dy});
        this.mHarpPoints[0] = TLine.intersection(towerLine.mStartPoint.x, towerLine.mStartPoint.y, towerLine.mEndPoint.x, towerLine.mEndPoint.y,
                                                  line1.mStartPoint.x, line1.mStartPoint.y, line1.mEndPoint.x, line1.mEndPoint.y) || {x:0, y:0};

        dx = this.mNodes[7].x - this.mNodes[9].x;
        dy = this.mNodes[7].y - this.mNodes[9].y;
        const line2 = new TLine(this.mScene.mGraphics, this.mNodes[9], {x: this.mNodes[1].x - dx, y: this.mNodes[1].y - dy});
        this.mHarpPoints[1] = TLine.intersection(towerLine.mStartPoint.x, towerLine.mStartPoint.y, towerLine.mEndPoint.x, towerLine.mEndPoint.y,
                                                 line2.mStartPoint.x, line2.mStartPoint.y, line2.mEndPoint.x, line2.mEndPoint.y) || {x:0, y:0};
          // Clean up temporary lines if necessary (or ensure they aren't added to scene)
     }

    globalUpdate() {
        // --- Constraint Application ---
         const selectedEntity = this.mScene.mGraphics.selectedEntity; // Get currently selected entity if needed

        // Keep deck horizontal if toggled
        if (this.mDeckHoriz) {
             if (selectedEntity === this.mNodes[6]) { // If deck end is dragged
                 // Recalculate node 4 based on intersection with tower
                 const intersect = TLine.intersection(
                     this.mNodes[0].x, this.mNodes[0].y, this.mNodes[5].x, this.mNodes[5].y, // Tower line
                     this.mNodes[6].x, this.mNodes[6].y, this.mNodes[6].x - 100, this.mNodes[6].y  // Horizontal line from node 6
                 );
                  if (intersect) {
                    let newY = intersect.y;
                     // Clamp Y to tower bounds
                     newY = Math.max(this.mNodes[0].y, Math.min(newY, this.mNodes[5].y));
                      this.mNodes[4].item.position = [intersect.x, newY];
                  }
             } else { // If tower or other points move, keep node 6 level with node 4
                 this.mNodes[6].item.position = [this.mNodes[6].x, this.mNodes[4].y];
             }
         }

        // Keep tower vertical if toggled
        if (this.mTowerVert) {
            if (selectedEntity === this.mNodes[5]) { // If tower base is dragged
                this.mNodes[0].item.position = [this.mNodes[5].x, this.mNodes[0].y]; // Top follows base horizontally
            } else { // If tower top or other points move
                 this.mNodes[5].item.position = [this.mNodes[0].x, this.mNodes[5].y]; // Base follows top horizontally
            }
        }


        // Update node parameters if nodes 1-4 might have moved relative to tower
         if ([this.mNodes[1], this.mNodes[2], this.mNodes[3], this.mNodes[4]].includes(selectedEntity) || !this.mDeckHoriz || !this.mTowerVert) {
             this._setNodeParams();
         }


        // --- Derived Geometry Calculation ---
        this._distributeCableNodes(); // Recalculate deck node X positions
        this._findCableYs();        // Recalculate deck node Y positions based on force polygon

         for (let i = 0; i < 7; i++) {
             this.mForceTails[i].item.position = [this.mNodes[i+1].x, this.mForceTails[i].y]; // Keep load tails above deck nodes
             this.mForceDy[i] = this.mForceTails[i].y - this.mNodes[i+1].y; // Store vertical offset for reset
         }

         // Calculate force polygon pole positions
        if (selectedEntity === this.mNodes[0] || selectedEntity === this.mNodes[8]) {
             this._findO(); // If supports move, recalculate O based on O'
        } else {
             this._findOPrime(); // Otherwise, recalculate O' based on O
        }
         if (this.mSupportsHoriz && this.mScene.mGraphics.mTimer.numJobs() == 0) {
             if (selectedEntity == this.mForcePolyNode) {
                 this.mForcePolyNode.item.position = [this.mForcePolyNode.x, this.mOPrime.y]; // Keep O level with O'
             }
             this.mHorizO.item.position = [this.mForcePolyNode.x, this.mOPrime.y]; // Update helper point
         }


        this._findReactions();      // Recalculate reaction forces and positions

        // --- Visual Updates ---
        this._isArch();             // Determine if it's an arch or cable
        const memberColor = this.mIsArch ? styles.red : styles.blue;
        for (let i = 0; i < 8; i++) { // Update member colors
             if (this.mMembers[i]) this.mMembers[i].mColor = memberColor; // Skip tower for now
             if (this.mForcePolyLines[i]) this.mForcePolyLines[i].mColor = memberColor;
        }
        // You might want specific colors for tower/deck (members 0, 7-12, 13-15, 17)
         if(this.mMembers[0]) this.mMembers[0].mColor = styles.yellow; // Tower
         if(this.mMembers[7]) this.mMembers[7].mColor = styles.yellow; // Deck reference
         for(let i=8; i<=12; i++) if(this.mMembers[i]) this.mMembers[i].mColor = styles.yellow; // Deck segments
         for(let i=13; i<=15; i++) if(this.mMembers[i]) this.mMembers[i].mColor = styles.yellow; // Tower sections
         if(this.mMembers[17]) this.mMembers[17].mColor = styles.yellow; // Deck closing segment


         this._adjustMastThicknesses(); // Adjust visual thickness (if implemented) or member connections

        for (let i = 0; i < 8; i++) { // Update member visual thickness based on force
            if (this.mMembers[i] && this.mForcePolyLines[i]) {
                this.mMembers[i].mSize = Math.max(HangingCableApp.MIN_WIDTH, Math.min(HangingCableApp.MAX_WIDTH,
                    this.mForcePolyLines[i].length() * HangingCableApp.WIDTH_MULT));
            }
        }

        // Update positions for Equalize Loads feature
        for (let i = 0; i < 7; i++) {
             this.mEqualTails[i].x = this.mNodes[i+1].x;
             this.mEqualTails[i].y = this.mNodes[i+1].y - (this.mNodes[1].y - this.mForceTails[0].y); // Match first load's length
        }

         // Update Lines of Action visibility and position
        this.mRaLineOfAction.visible = this.mLinesOfAction;
        this.mRbLineOfAction.visible = this.mLinesOfAction;
        this.mLoadLineOfAction.visible = this.mLinesOfAction;
        this.mActionIntersect.visible = this.mLinesOfAction;

         if (this.mLinesOfAction) {
             // Force update of intersection point first
             this.mActionIntersect.update();
             if (this.mActionIntersect.isValid) {
                // Update resultant load line based on action intersection
                 const totalDx = this.mLoadLine[7].x - this.mLoadLine[0].x; // Ra vector component (h-a)
                 const totalDy = this.mLoadLine[7].y - this.mLoadLine[0].y;
                 this.mResultantEndNode.item.position = [this.mActionIntersect.x, this.mActionIntersect.y];
                  this.mResultantStartNode.item.position = [this.mActionIntersect.x + totalDx, this.mActionIntersect.y + totalDy]; // Check direction
             } else {
                 // Handle parallel case if needed (lines might not be visible)
             }
         }

        if (!this.mUpdatedOnce) this.mUpdatedOnce = true;
    }

}