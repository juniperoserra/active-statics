/**
 * Created by simong on 2/20/17.
 */

import AppBase from './AppBase';
import styles from '../graphics/styles';
import TLine from '../graphics/TLine';
import util from '../graphics/util';

export default class SinglePanelApp extends AppBase {

    static APPLET_WIDTH = 620;
    static APPLET_HEIGHT = 620;
    static PANEL_SIZE = 180;
    static TRUSS_X_START = 160;
    static TRUSS_Y_START = 350;
    static START_FORCE_LENGTH = 165;
    static LOAD_LINE_START_X = 480;
    static LOAD_LINE_START_Y = 120;
    static MAX_WIDTH = 60.0;
    static MIN_WIDTH = 2.0;
    static WIDTH_MULT = 0.1;
    static REPORT_X_START = 400;
    static REPORT_Y_START = 440;
    static REPORT_LINE_SPACE = 17;
    static REPORT_COLUMN_SPACE = 110;
    static BUTTON_WIDTH = 180;
    static BUTTON_START_X = 30;
    static BUTTON_START_Y = 90;
    static BUTTON_Y_OFFSET = 30;

    constructor(scene) {
        super(scene,
            [SinglePanelApp.APPLET_WIDTH, SinglePanelApp.APPLET_HEIGHT]);

        this.makeButtons();
        this.makeNodes();
        this.makeMembers();
        this.makeLoads();
        this.makeRb();
        this.makeLoadLine();
        this.makeRa();
        //this.makeForcePolygon();
        //this.makeTriangleLabels();
        this.makeText();
        //this.makeSupports();
        //this.makeReport();
        //this.makeLinesOfAction();
    }


    globalUpdate() {
        if (this.mLinesOfAction) {
            this.mRaLineOfAction.mInvisible = false;
            this.mRbLineOfAction.mInvisible = false;
            this.mLoadLineOfAction.mInvisible = false;
            this.mActionIntersect.mInvisible = false;
        } else {
            this.mRaLineOfAction.mInvisible = true;
            this.mRbLineOfAction.mInvisible = true;
            this.mLoadLineOfAction.mInvisible = true;
            this.mActionIntersect.mInvisible = true;
        }
        if (!this.mActionIntersect.mExists) {
            this.mRaLineOfAction.mStartPoint = this.mDummyPoint[0];
            this.mRaLineOfAction.mStartPoint.x = this.mRa.mEndPoint.x;
            this.mRaLineOfAction.mStartPoint.y = -5000.0;
            this.mRaLineOfAction.mEndPoint = this.mDummyPoint[1];
            this.mRaLineOfAction.mEndPoint.x = this.mRa.mEndPoint.x;
            this.mRaLineOfAction.mEndPoint.y = 5000.0;
            this.mRbLineOfAction.mStartPoint = this.mDummyPoint[2];
            this.mRbLineOfAction.mStartPoint.x = this.mRb.mEndPoint.x;
            this.mRbLineOfAction.mStartPoint.y = -5000.0;
            this.mRbLineOfAction.mEndPoint = this.mDummyPoint[3];
            this.mRbLineOfAction.mEndPoint.x = this.mRb.mEndPoint.x;
            this.mRbLineOfAction.mEndPoint.y = 5000.0;
            this.mLoadLineOfAction.mStartPoint = this.mDummyPoint[4];
            this.mLoadLineOfAction.mStartPoint.x = this.mTrussNodes[1].x;
            this.mLoadLineOfAction.mStartPoint.y = -5000.0;
            this.mLoadLineOfAction.mEndPoint = this.mDummyPoint[5];
            this.mLoadLineOfAction.mEndPoint.x = this.mTrussNodes[1].x;
            this.mLoadLineOfAction.mEndPoint.y = 5000.0;
            this.mActionIntersect.x = 5000.0;
            this.mActionIntersect.y = 5000.0;
        } else {
            this.mRaLineOfAction.mStartPoint = this.mRa.mEndPoint;
            this.mRaLineOfAction.mEndPoint = this.mActionIntersect;
            this.mRbLineOfAction.mStartPoint = this.mRb.mEndPoint;
            this.mRbLineOfAction.mEndPoint = this.mActionIntersect;
            this.mLoadLineOfAction.mStartPoint = this.mTrussNodes[1];
            this.mLoadLineOfAction.mEndPoint = this.mActionIntersect;
        }
        if (this.mLoadsVertical) {
            this.mForceTail.x = this.mLoad.mEndPoint.x;
        }
        if (this.mVerticalsVertical) {
            this.mTrussNodes[2].x = this.mTrussNodes[1].x;
            this.mTrussNodes[4].x = this.mTrussNodes[3].x;
            this.mTrussNodes[6].x = this.mTrussNodes[5].x;
            this.mTrussNodes[8].x = this.mTrussNodes[7].x;
            this.mTrussNodes[10].x = this.mTrussNodes[9].x;
        }
    }

    makeMembers() {
        this.mMembers = [];

        this.mMembers[0] = this.mScene.createMember(this.mTrussNodes[0], this.mTrussNodes[1],
            {thickness: 20, label: 'A', labelOffset: [-14, -14]});
        this.mMembers[0].dragAlso(this.mTrussNodes[0]);
        this.mMembers[0].dragAlso(this.mTrussNodes[1]);
        this.mMembers[0].dragAlso(this.mTrussNodes[2]);

        this.mMembers[1] = this.mScene.createMember(this.mTrussNodes[1], this.mTrussNodes[2],
            {thickness: 20, label: 'B', labelOffset: [14, -14]});
        this.mMembers[1].dragAlso(this.mTrussNodes[0]);
        this.mMembers[1].dragAlso(this.mTrussNodes[1]);
        this.mMembers[1].dragAlso(this.mTrussNodes[2]);

        this.mMembers[2] = this.mScene.createMember(this.mTrussNodes[2], this.mTrussNodes[0],
            {thickness: 20, label: 'C', labelOffset: [0, 20]});
        this.mMembers[2].dragAlso(this.mTrussNodes[0]);
        this.mMembers[2].dragAlso(this.mTrussNodes[1]);
        this.mMembers[2].dragAlso(this.mTrussNodes[2]);
    }

    makeNodes() {
        this.mTrussNodes = [];
        let x = 150;
        const height = 72.0;
        this.mTrussNodes[0] = this.mScene.createPoint([x, 350.0]);
        this.mTrussNodes[1] = this.mScene.createPoint([x + 90.0, 350 - height]);
        this.mTrussNodes[2] = this.mScene.createPoint([x + 180, 350.0]);
        x = 240;
        this.mForceTail = this.mScene.createPoint([x, 350 - height - 168]);
        this.mTrussNodes[1].dragAlso(this.mForceTail);
    }

    makeLoads() {
        this.mLoad = this.mScene.createLoad(this.mForceTail, this.mTrussNodes[1]);
    }

    makeRb() {
        const app = this;
        this.mRbTail = this.mScene.createPoint([0,0], {
            size: 0,
            update: function () {
                this.moment = app.mLoad.moment(app.mTrussNodes[0]);
                this.moment *= TLine.ccw(app.mTrussNodes[2].x, app.mTrussNodes[2].y - 10,
                    app.mTrussNodes[2].x, app.mTrussNodes[2].y, app.mTrussNodes[0].x, app.mTrussNodes[0].y);
                this.pDist = TLine.perpDist(app.mTrussNodes[2].x, app.mTrussNodes[2].y, app.mTrussNodes[2].x, app.mTrussNodes[2].y + 10,
                    app.mTrussNodes[0].x, app.mTrussNodes[0].y);
                if (Math.abs(this.pDist) < 0.1) {
                    this.pDist = 1.0;
                }
                this.moment /= this.pDist;
                this.item.position = [app.mTrussNodes[2].x, app.mTrussNodes[2].y + Math.abs(this.moment) + app.mRb.mArrowOffset];
                app.mRb.mReverse = (this.moment < 0) ? -1 : 1;
            }
        });

        this.mRb = this.mScene.createReaction(this.mRbTail, this.mTrussNodes[2], {
            arrowOffset: 45, strokeColor: styles.green, label: 'Rb', labelOffset: [20, 0]
        });
    }

    makeRa() {
        const app = this;
        this.mRaTail = this.mScene.createPoint([0,0], {
            size: 0,
            update: function () {
                this.mDir = util.direction(app.mLoadLine[2].x, app.mLoadLine[2].y, app.mLoadLine[0].x, app.mLoadLine[0].y);
                this.mDist = util.distance(app.mLoadLine[2].x, app.mLoadLine[2].y, app.mLoadLine[0].x, app.mLoadLine[0].y) + app.mRa.mArrowOffset;
                this.item.position = [ app.mTrussNodes[0].x - this.mDist * Math.cos(this.mDir),
                    app.mTrussNodes[0].y - this.mDist * Math.sin(this.mDir)];
                console.log(this.item.position.x);
            }
        });

        this.mRa = this.mScene.createReaction(this.mRaTail, this.mTrussNodes[0], {
            arrowOffset: 45, strokeColor: styles.green, label: 'Ra', labelOffset: [-24, 0]
        });
    }

    makeLoadLine() {

        this.mLoadLine = [];
        this.mLoadLine[0] = this.mScene.createPoint([480.0, 120.0], {
            label: 'a', labelOffset: [14, 0], size: 7
        });
        this.mLoadLine[1] = this.mScene.createPointTranslated(this.mLoadLine[0], this.mForceTail, this.mLoad.mArrowHead, {
           label: 'b', labelOffset: [14, 0], size: 7
        });
        this.mLoadLine[1].dragAlso(this.mLoadLine[0]);

        this.mLoadLine[2] = this.mScene.createPointTranslated(this.mLoadLine[1], this.mRb.mArrowTail, this.mRb.mArrowHead, {
            label: 'c', labelOffset: [14, 0], size: 7
        });
        this.mLoadLine[2].dragAlso(this.mLoadLine[0]);

        this.mLoadLineLines = [];
        for (let i = 0; i < 3; i++) {
            this.mLoadLineLines[i] = this.mScene.createLine(this.mLoadLine[i], this.mLoadLine[(i + 1) % 3], {
                thickness: 4, fillColor: 'black'
            });
            this.mLoadLineLines[i].dragAlso(this.mLoadLine[0]);
        }
        this.mLoadLineLines[0].mColor = 'gray'; //this.mLoad.mColor;
        this.mLoadLineLines[1].mColor = styles.green;
        this.mLoadLineLines[2].mColor = styles.green;
    }


    makeButtons() {
        const x = SinglePanelApp.BUTTON_START_X;
        let y = SinglePanelApp.BUTTON_START_Y;
        const moveButton = this.mScene.createButton([x, y], 'Return To Starting Position',
            () => {
                /*
                 if (SinglePanelApplet.this.mCircleLoad != null) {
                 SinglePanelApplet.this.g.mTimer.removeJob(SinglePanelApplet.this.mCircleLoad);
                 SinglePanelApplet.this.mCircleLoad = null;
                 SinglePanelApplet.access$1((SinglePanelApplet)SinglePanelApplet.this).mSelected = false;
                 }
                 JobMoveViewToOrigin originMove = new JobMoveViewToOrigin(SinglePanelApplet.this.g);
                 originMove.mView = SinglePanelApplet.this.mUpdateCanvas;
                 SinglePanelApplet.this.g.mTimer.addJob(originMove);
                 JobMovePointToStart newJob = new JobMovePointToStart(SinglePanelApplet.this.g);
                 newJob.mMovePoint = SinglePanelApplet.this.mLoadLine[0];
                 SinglePanelApplet.this.g.mTimer.addJob(newJob);
                 for (int i = 0; i < 3; ++i) {
                 newJob = new JobMovePointToStart(SinglePanelApplet.this.g);
                 newJob.mMovePoint = SinglePanelApplet.this.mTrussNodes[i];
                 SinglePanelApplet.this.g.mTimer.addJob(newJob);
                 }
                 newJob = new JobMovePointToStart(SinglePanelApplet.this.g);
                 newJob.mMovePoint = SinglePanelApplet.this.mForceTail;
                 SinglePanelApplet.this.g.mTimer.addJob(newJob);
                 */
            }, {width: SinglePanelApp.BUTTON_WIDTH}
        );

        y += SinglePanelApp.BUTTON_Y_OFFSET;
        this.mCircleLoadButton = this.mScene.createButton([x, y], 'Circle Load',
            () => {
                /*
                if (SinglePanelApplet.this.mCircleLoad == null) {
                    SinglePanelApplet.this.mCircleLoad = new JobCirclePoint(SinglePanelApplet.this.g);
                    SinglePanelApplet.this.mCircleLoad.mMovePoint = SinglePanelApplet.this.mForceTail;
                    SinglePanelApplet.this.mCircleLoad.mPivot = SinglePanelApplet.this.mTrussNodes[1];
                    SinglePanelApplet.this.g.mTimer.addJob(SinglePanelApplet.this.mCircleLoad);
                } else {
                    SinglePanelApplet.this.g.mTimer.removeJob(SinglePanelApplet.this.mCircleLoad);
                    SinglePanelApplet.this.mCircleLoad = null;
                }
                */
            }, {isToggle: true, width: SinglePanelApp.BUTTON_WIDTH}
        );

        y += SinglePanelApp.BUTTON_Y_OFFSET;
        this.mLoadsVertCheck = this.mScene.createButton([x, y], 'Keep Load Vertical',
            () => {
                /*
                 SinglePanelApplet.this.mLoadsVertical = SinglePanelApplet.access$2((SinglePanelApplet)SinglePanelApplet.this).mSelected;
                 SinglePanelApplet.this.repaint();
                 SinglePanelApplet.this.mUpdateCanvas.globalUpdate();
                 SinglePanelApplet.this.repaint();
                 */
            }, {isToggle: true, width: SinglePanelApp.BUTTON_WIDTH});

        y += SinglePanelApp.BUTTON_Y_OFFSET;
        this.mLinesOfActionCheck = this.mScene.createButton([x, y], 'Extend Lines of Action',
            () => {
                /*
                 SinglePanelApplet.this.mLinesOfAction = SinglePanelApplet.access$3((SinglePanelApplet)SinglePanelApplet.this).mSelected;
                 SinglePanelApplet.this.repaint();
                 */
            }, {isToggle: true, width: SinglePanelApp.BUTTON_WIDTH}
        );
    }

    makeText() {
        this.mScene.createText([20, 50], 'Single Panel Truss', {fontSize: 24});
        this.mScene.createTextPoint(this.mTrussNodes[1], 'Form Diagram',
            {fontSize: 20, offset: [-155, -20]});


        /*
        TTextPoint forcePoly = new TTextPoint();
        forcePoly.mBasePoint = this.mLoadLine[0];
        forcePoly.mXOffset = -100;
        forcePoly.mYOffset = -20;
        forcePoly.mSize = 20;
        forcePoly.mText = "Force Polygon";
        this.addToDrawList(forcePoly);
        */
    }
}