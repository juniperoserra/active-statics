/**
 * Created by simong on 2/20/17.
 */

import TPoint from './TPoint';
import TLine from './TLine';
import styles from './styles';

export default class TPointIntersect extends TPoint {

    constructor(graphics, line1, line2, options) {
        super(graphics, [0, 0], options);
        this.mLine1 = line1;
        this.mLine2 = line2;
        this.update();
    }

    update() {
        const x1 = this.mLine1.mStartPoint.item.position.x;
        const x2 = this.mLine1.mEndPoint.item.position.x;
        const x3 = this.mLine2.mStartPoint.item.position.x;
        const x4 = this.mLine2.mEndPoint.item.position.x;
        const y1 = this.mLine1.mStartPoint.item.position.y;
        const y2 = this.mLine1.mEndPoint.item.position.y;
        const y3 = this.mLine2.mStartPoint.item.position.y;
        const y4 = this.mLine2.mEndPoint.item.position.y;
        const intersection = TLine.intersection(x1, y1, x2, y2, x3, y3, x4, y4);
        if (intersection) {
            this.item.position = intersection;
            this.mValid = true;
        }
        else {
            this.mValid = false;
        }
        super.update();
    }

    get isValid() {
        return this.mValid;
    }
}