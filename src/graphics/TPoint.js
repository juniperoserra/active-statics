/**
 * Created by simong on 2/20/17.
 */

import GraphicEntity from './GraphicEntity';

const hitOptions = {
    segments: true,
    stroke: true,
    fill: true,
    tolerance: 5
};

export default class TPoint extends GraphicEntity {

    static DEFAULT_SIZE = 10;
    static LABEL_EXTENT_WIDTH = 30;

    constructor(graphics, x = 0, y = 0) {
        super(graphics);
        this.mSize = TPoint.DEFAULT_SIZE;

        const c1 = graphics.addCircle([x, y], this.mSize,
            {
                fillColor: '#e1a028',
                strokeColor: 'black'
            });
        const c2 = graphics.addCircle([x, y], this.mSize / 1.4,
            {
                fillColor: '#e1a028',
                strokeColor: 'black'
            });

        this.item = graphics.addGroup([c1, c2]);
        this.item.onMouseDrag = this::this.onMouseDrag;
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


}