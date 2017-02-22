/**
 * Created by simong on 2/21/17.
 */

import TArrow from './TArrow';
import styles from './styles';
//import util from './util';

export default class TLoad extends TArrow {

    constructor(graphics, start, end, options) {
        super(graphics, start, end, options);
        this.item.fillColor = 'gray';
    }

    update() {
        this.mLabelText = '' + Math.round(this.length() / styles.lengthDivisor);
        this.mReverse = this.mTrueLength < this.mArrowOffset ? -1 : (this.mTrueLength == 0 ? 0 : 1);
        super.update();
        if (this.mLabel) {
            this.mLabel.content = this.mLabelText || '';
            this.mLabel.position = [this.mLabelOffset[0] + this.mStartPoint.x,
                this.mLabelOffset[1] + this.mStartPoint.y];
        }
    }

};