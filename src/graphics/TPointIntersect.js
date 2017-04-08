/**
 * Created by simong on 2/20/17.
 */

import TPoint from './TPoint';
import styles from './styles';

export default class TPointIntersect extends TPoint {

    constructor(graphics, line1, line2, options) {
        super(graphics, [0, 0], options);
        this.mLine1 = line1;
        this.mline2 = line2;
        this.update();
    }

    update() {
        this.item.position = [this.mBasePoint.x + (this.mTo.x - this.mFrom.x), this.mBasePoint.y + (this.mTo.y - this.mFrom.y)];
        super.update();
    }

}