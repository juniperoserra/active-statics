/**
 * Created by simong on 2/20/17.
 */

import GraphicEntity from './GraphicEntity';
import styles from './styles';
import util from './util';

function getThickTaperPoints(x1, y1, x2, y2, thickness) {
    const taperLength = Math.min(thickness / 1.6, util.distance(x1, y1, x2, y2) / 2.4);
    const dir = util.direction(x1, y1, x2, y2);
    const mX1 = x1 + taperLength * Math.cos(dir);
    const mY1 = y1 + taperLength * Math.sin(dir);
    const mX2 = x2 - taperLength * Math.cos(dir);
    const mY2 = y2 - taperLength * Math.sin(dir);
    const dX = mX2 - mX1;
    const dY = mY2 - mY1;
    const lineLength = Math.sqrt(dX * dX + dY * dY);
    const scale = thickness / (2 * lineLength);
    const ddx = (- scale) * dY;
    const ddy = scale * dX;

    const dx = ddx + ((ddx > 0) ? 0.5 : -0.5);
    const dy = ddy + ((ddy > 0) ? 0.5 : -0.5);
    return [
        [(mX1 + dx), (mY1 + dy)],
        [x1, y1],
        [(mX1 - dx), (mY1 - dy)],
        [(mX2 - dx), (mY2 - dy)],
        [x2, y2],
        [(mX2 + dx), (mY2 + dy)]
    ];
}

export default class TLine extends GraphicEntity {

    static DEFAULT_SIZE = 5;
    static DEFAULT_DASH_LENGTH = 10;
    static DEFAULT_GAP_LENGTH = 10;

    constructor(graphics, startPoint, endPoint, options = {}) {
        super(graphics);

        this.mStartPoint = startPoint;
        this.mEndPoint = endPoint;
        this.mTapered = !!options.tapered;

        //mLocation = new Point();
        this.mOutline = true;
        this.mDashed = false;
        //this.mDashLength = 10;
        //this.mGapLength = 10;

        this.mSize = options.thickness || TLine.DEFAULT_SIZE;
        this.mColor = styles.green;
        this.mLabelText = options.label || null;
        this.mLabelOffset = options.labelOffset || [0, -20];

        if (this.mTapered) {
            const pts = getThickTaperPoints(
                this.mStartPoint.x, this.mStartPoint.y,
                this.mEndPoint.x, this.mEndPoint.y, this.mSize
            );
            this.item = this.mGraphics.addPath(pts, {
                strokeColor: options.strokeColor || 'black',
                fillColor: options.fillColor || styles.green
            });
            this.item.closePath();
            //this.item.smooth({ type: 'catmull-rom', factor: 0.2 });
        }
        else {
            this.item = this.mGraphics.addPath([
                [this.mStartPoint.x, this.mStartPoint.y], [this.mEndPoint.x, this.mEndPoint.y]], {
                strokeColor: options.strokeColor || 'black',
                strokeWidth: this.mSize
            });
        }

        this.item.sendToBack();

        this.update();
        this.item.onMouseDrag = this::this.onMouseDrag;
    }


    update() {
        if (this.mTapered) {
            this.item.fillColor = this.mColor;
        } else {
            this.item.strokeColor = this.mColor;
        }


        if (this.mLabelText && !this.mLabel) {
            this.mLabel = this.mGraphics.addText(this.mLabelOffset, this.mLabelText,
                {fontSize: styles.labelSize});
        }
        if (this.mLabel) {
            this.mLabel.content = this.mLabelText || '';
            this.mLabel.position = [this.mLabelOffset[0] + (this.mStartPoint.x + this.mEndPoint.x) / 2.0,
                this.mLabelOffset[1] + (this.mStartPoint.y + this.mEndPoint.y) / 2.0];
        }

        const endOffset = this.mArrowOffset ? this.mArrowOffset : 0;
        let tempDir = util.direction(this.mEndPoint.x, this.mEndPoint.y, this.mStartPoint.x, this.mStartPoint.y);
        const endPoint = [(this.mEndPoint.x + endOffset * Math.cos(tempDir)),
            (this.mEndPoint.y + endOffset * Math.sin(tempDir))];

        if (this.mTapered) {
            const pts = getThickTaperPoints(
                this.mStartPoint.x, this.mStartPoint.y,
                endPoint[0], endPoint[1], this.mSize
            );
            let i = 0;
            for (let seg of this.item.segments) {
                seg.point.x = pts[i][0];
                seg.point.y = pts[i][1];
                i++;
            }
        }
        else {
            this.item.segments[0].point.x = this.mStartPoint.x;
            this.item.segments[0].point.y = this.mStartPoint.y;
            this.item.segments[1].point.x = endPoint[0];
            this.item.segments[1].point.y = endPoint[1];
        }
    }

    length() {
        return util.distance(this.mStartPoint.x, this.mStartPoint.y, this.mEndPoint.x, this.mEndPoint.y);
    }

    direction() {
        return util.direction(this.mStartPoint.x, this.mStartPoint.y, this.mEndPoint.x, this.mEndPoint.y);
    }

    paramToPoint(t) {
        return {
            x: this.mStartPoint.x + (this.mEndPoint.x - this.mStartPoint.x) * t,
            y: this.mStartPoint.y + (this.mEndPoint.y - this.mStartPoint.y) * t
        };
    }

    pointToParam(p) {
        if (this.mStartPoint.x == this.mEndPoint.x) {
            if (this.mStartPoint.y == this.mEndPoint.y) {
                return 0.0;
            }
            return (p.y - this.mStartPoint.y) / (this.mEndPoint.y - this.mStartPoint.y);
        }
        return (p.x - this.mStartPoint.x) / (this.mEndPoint.x - this.mStartPoint.x);
    }

    perpIntersectPoint(p) {
        if (this.mStartPoint.x == this.mEndPoint.x) {
            return {
                x: this.mStartPoint.x,
                y: p.y
            };
        }
        if (this.mStartPoint.y == this.mEndPoint.y) {
            return {
                y: this.mStartPoint.y,
                X: p.x
            };
        }
        const dX = this.mEndPoint.x - this.mStartPoint.x;
        const dY = this.mEndPoint.y - this.mStartPoint.y;
        const m = (- dX) / dY;
        return TLine.intersection(this.mStartPoint.x, this.mStartPoint.y, this.mEndPoint.x, this.mEndPoint.y, p.x, p.y, p.x + 10, p.y + 10 * m);
    }

    closestPointOnSeg(p) {
        let closest = {};
        if (this.mStartPoint.x == this.mEndPoint.x) {
            closest.x = this.mStartPoint.x;
            closest.y = p.y;
        } else if (this.mStartPoint.y == this.mEndPoint.y) {
            closest.y = this.mStartPoint.y;
            closest.x = p.x;
        } else {
            const dX = this.mEndPoint.x - this.mStartPoint.x;
            const dY = this.mEndPoint.y - this.mStartPoint.y;
            const m = (- dX) / dY;
            closest = TLine.intersection(this.mStartPoint.x, this.mStartPoint.y, this.mEndPoint.x, this.mEndPoint.y, p.x, p.y, p.x + 10, p.y + 10 * m);
        }
        let param = this.pointToParam(closest);
        if (param > 1.0) {
            param = 1.0;
        }
        if (param < 0.0) {
            param = 0.0;
        }
        return this.paramToPoint(param);
    }

    static intersection(x0, y0, x1, y1, x2, y2, x3, y3) {
        const m1 = x1 - (x0 != 0) ? (y1 - y0) / (x1 - x0) : 1.0E10;
        const m2 = x3 - (x2 != 0) ? (y3 - y2) / (x3 - x2) : 1.0E10;
        if (m1 === m2 && TLine.perpDist(x0, y0, x1, y1, x2, y2) === 0) {
            return undefined;
        }
        const a1 = m1;
        const a2 = m2;
        const b1 = -1.0;
        const b2 = -1.0;
        const c1 = y0 - m1 * x0;
        const c2 = y2 - m2 * x2;
        const det_inv = 1.0 / (a1 * b2 - a2 * b1);
        return {
            x: (b1 * c2 - b2 * c1) * det_inv,
            y: (a2 * c1 - a1 * c2) * det_inv
        };
    }

    /*
    static ccw(X1, Y1, X2, Y2, PX, PY) {
        let ccw = (PX -= X1) * (Y2 -= Y1) - (PY -= Y1) * (X2 -= X1);
        if (ccw == 0.0 && (ccw = PX * X2 + PY * Y2) > 0.0 && (ccw = (PX -= X2) * X2 + (PY -= Y2) * Y2) < 0.0) {
            ccw = 0.0;
        }
        return ccw < 0.0 ? -1 : (ccw > 0.0 ? 1 : 0);
    }*/


    static ccw(X1, Y1, X2, Y2, PX, PY) {
        const val = (Y2 - Y1) * (PX - X2) - (X2 - X1) * (PY - Y2);
        if (val === 0) return 0;  // colinear
        return (val > 0)? -1: 1; // clock or counterclock wise
    }

    ccw(PX, PY) {
        return TLine.ccw(this.mStartPoint.x, this.mStartPoint.y, this.mEndPoint.x, this.mEndPoint.y, PX, PY);
    }

    static perpDist(X1, Y1, X2, Y2, PX, PY) {
        if (X1 == X2) {
            return Math.abs(PX - X1);
        }
        if (Y1 == Y2) {
            return Math.abs(PY - Y1);
        }
        const dotprod = (PX -= X1) * (X2 -= X1) + (PY -= Y1) * (Y2 -= Y1);
        const projlenSq = dotprod * dotprod / (X2 * X2 + Y2 * Y2);
        return Math.sqrt(PX * PX + PY * PY - projlenSq);
    }

    perpDist(PX, PY) {
        return TLine.perpDist(this.mStartPoint.x, this.mStartPoint.y, this.mEndPoint.x, this.mEndPoint.y, PX, PY);
    }
    
}