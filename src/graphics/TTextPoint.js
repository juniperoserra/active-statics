/**
 * Created by simong on 2/20/17.
 */

import TText from './TText';
import styles from './styles';
import util from './util';

export default class TTextPoint extends TText {
    constructor(graphics, tpoint, text, options = {}) {
        super(graphics, [0, 0], text, options);
        this.mOffset = options.offset || [0, 0];
        this.mTPoint = tpoint;
        this.mLeftJustify = options.leftJustify;
        this.update();
    }

    update() {
        super.update();
        if (this.mLeftJustify) {
            const trueLeft = this.mTPoint.x - this.mTPoint.item.bounds.width / 2 + this.mOffset[0];
            const currentLeft = this.x - this.item.bounds.width / 2;
            const currentX = this.x;
            this.item.position = [trueLeft + (currentX - currentLeft), this.mTPoint.y + this.mOffset[1]];
        }
        else {
            this.item.position = this.mTPoint.item.position.add(this.mOffset);        }
    }
};