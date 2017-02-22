/**
 * Created by simong on 2/21/17.
 */

import TLine from './TLine';
import styles from './styles';
import util from './util';

export default class TArrow extends TLine {

    static ARROW_HEAD_SPREAD = 15.0;
    static ARROW_HEAD_SIZE = 12;
    static ARROW_OFFSET = 18;

    constructor(graphics, start, end, options) {
        super(graphics, start, end, options);

        this.mArrowOffset = (options.arrowOffset !== undefined) ? options.arrowOffset : TArrow.ARROW_OFFSET;
        this.mArrowHeadSpread = TArrow.ARROW_HEAD_SPREAD;
        this.mArrowHeadSize = TArrow.ARROW_HEAD_SIZE;
        this.mReverse = 1;
        this.mTrueLength = 0;

        this.mArrowHead = this.mGraphics.addPath([[0, 0], [1, 0], [0,1]], {
            fillColor: options.fillColor || options.strokeColor || 'gray'
        });
        this.mArrowHead.closePath();
        this.mHeadPoint = [0, 0];
        this.update();
    }

    update() {
        if (!this.mArrowHead) {
            return;
        }

        this.mTrueLength = util.distance(this.mStartPoint.x, this.mStartPoint.y, this.mEndPoint.x, this.mEndPoint.y);
        // Moved this to Load, OK?
        //this.mReverse = this.mTrueLength < this.mArrowOffset ? -1 : (this.mTrueLength == 0 ? 0 : 1);

        let tempDir = util.direction(this.mEndPoint.x, this.mEndPoint.y, this.mStartPoint.x, this.mStartPoint.y);

        this.mHeadPoint = [(this.mEndPoint.x + this.mArrowOffset * Math.cos(tempDir)),
            (this.mEndPoint.y + this.mArrowOffset * Math.sin(tempDir))];

        const arrowPoint = (this.mReverse > 0) ? this.mHeadPoint : [this.mStartPoint.x, this.mStartPoint.y];

        this.mArrowHead.segments[0].point.x = (arrowPoint[0] - (this.mReverse * this.mSize) * Math.cos(tempDir));
        this.mArrowHead.segments[0].point.y = (arrowPoint[1] - (this.mReverse * this.mSize) * Math.sin(tempDir));
        tempDir += 15.0;
        this.mArrowHead.segments[1].point.x = (arrowPoint[0] - (this.mReverse * this.mArrowHeadSize) * Math.cos(tempDir));
        this.mArrowHead.segments[1].point.y = (arrowPoint[1] - (this.mReverse * this.mArrowHeadSize) * Math.sin(tempDir));
        tempDir -= 30.0;
        this.mArrowHead.segments[2].point.x = (arrowPoint[0] - (this.mReverse * this.mArrowHeadSize) * Math.cos(tempDir));
        this.mArrowHead.segments[2].point.y = (arrowPoint[1] - (this.mReverse * this.mArrowHeadSize) * Math.sin(tempDir));
        super.update();

        if (this.mLabel) {
            this.mLabel.content = this.mLabelText || '';
            this.mLabel.position = [this.mLabelOffset[0] + (this.mStartPoint.x + this.mHeadPoint[0]) / 2.0,
                this.mLabelOffset[1] + (this.mStartPoint.y + this.mHeadPoint[1]) / 2.0];
        }
    }

    length() {
        if (!this.mHeadPoint) {
            return 0;
        }
        return util.distance(this.mStartPoint.x, this.mStartPoint.y, this.mHeadPoint[0], this.mHeadPoint[1]);
    }

    moment(pivot) {
        return TLine.ccw(this.mStartPoint.x, this.mStartPoint.y, this.mHeadPoint[0], this.mHeadPoint[1], pivot.x, pivot.y) * this.length() * this.perpDist(pivot.x, pivot.y);
    }
};

