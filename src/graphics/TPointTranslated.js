/**
 * Created by simong on 2/20/17.
 */

import TPoint from './TPoint';
import styles from './styles';

export default class TPointTranslated extends TPoint {

    constructor(graphics, basePoint, from, to, options) {
        super(graphics, [0, 0], options);
        this.mBasePoint = basePoint;
        this.mFrom = from;
        this.mTo = to;
        this.update();
    }

    update() {
        this.item.position = [this.mBasePoint.x + (this.mTo.x - this.mFrom.x), this.mBasePoint.y + (this.mTo.y - this.mFrom.y)];
        super.update();
    }

}