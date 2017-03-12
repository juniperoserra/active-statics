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
        this.mStabilizeLeft = options.stabilizeLeft;
        this.update();
    }

    update() {
        const previousWidth = this.item.bounds.width;
        super.update();
        if (this.mStabilizeLeft) {
            const newWidth = this.item.bounds.width;
            this.mOffset = [this.mOffset[0] + (newWidth - previousWidth) / 2, this.mOffset[1]];

            if (!this.mHasAdjusted) {
                this.mOffset = [this.mOffset[0] - (previousWidth + newWidth)/2, this.mOffset[1]];
                this.mHasAdjusted = true;
            }

        }
        this.item.position = this.mTPoint.item.position.add(this.mOffset);
    }
};