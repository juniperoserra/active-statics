/**
 * Created by simong on 2/20/17.
 */

import TText from './TText';
import styles from './styles';
import util from './util';

export default class TTextTriagle extends TText {
    constructor(graphics, p1, p2, p3, text, options = {}) {
        super(graphics, [0, 0], text, options);
        this.mOffset = options.offset || [0, 0];
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.update();
    }

    update() {
        if (!this.p1 || !this.p2 || !this.p3) {
            return;
        }

        this.item.position = [(this.p1.x + this.p2.x + this.p3.x) / 3.0 - 10 * (this.item.content.length - 1),
            (this.p1.y + this.p2.y + this.p3.y) / 3.0];
        super.update();
    }
};