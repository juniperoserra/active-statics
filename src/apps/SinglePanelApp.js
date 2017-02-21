/**
 * Created by simong on 2/20/17.
 */

import AppBase from './AppBase';

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
        //this.makeMembers();
        //this.makeLoads();
        //this.addNodes();
        //this.makeRb();
        //this.makeLoadLine();
        //this.makeRa();
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

    makeNodes() {
        this.mTrussNodes = [];
        let x = 150;
        const height = 72.0;
        this.mTrussNodes[0] = this.mScene.createPoint([x, 350.0]);
        this.mTrussNodes[1] = this.mScene.createPoint([x + 90.0, 350 - height]);
        this.mTrussNodes[2] = this.mScene.createPoint([x + 180, 350.0]);
        x = 240;
        this.mForceTail = this.mScene.createPoint([x, 350 - height - 165]);
        this.mTrussNodes[1].dragAlso(this.mForceTail);
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
        this.mScene.createTextPoint([20, 50], 'Truss Node', this.mTrussNodes[0],
            {fontSize: 14});


        /*
        TTextPoint forcePoly = new TTextPoint();
        forcePoly.mBasePoint = this.mLoadLine[0];
        forcePoly.mXOffset = -100;
        forcePoly.mYOffset = -20;
        forcePoly.mSize = 20;
        forcePoly.mText = "Force Polygon";
        this.addToDrawList(forcePoly);
        TTextPoint formDiag = new TTextPoint();
        formDiag.mBasePoint = this.mTrussNodes[1];
        formDiag.mXOffset = -220;
        formDiag.mYOffset = 0;
        formDiag.mSize = 20;
        formDiag.mText = "Form Diagram";
        this.addToDrawList(formDiag);
        */
    }
}