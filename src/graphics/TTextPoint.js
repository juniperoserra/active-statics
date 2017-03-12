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
        this.update();
    }

    update() {
        this.item.position = this.mTPoint.item.position.add(this.mOffset);
        super.update();
    }
};