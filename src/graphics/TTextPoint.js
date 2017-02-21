/**
 * Created by simong on 2/20/17.
 */

import TText from './TText';


export default class TTextPoint extends TText {
    constructor(graphics, [x = 0, y = 0], text, tpoint, options = {}) {
        super(graphics, [0, 0], text, options);
        this.mOffset = [x, y];
        this.mTPoint = tpoint;
        this.update();
    }

    update() {
        console.log(this.item.position);
        this.item.position = this.mTPoint.item.position.add(this.mOffset);

    }
};