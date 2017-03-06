/**
 * Created by simong on 2/20/17.
 */

import TPoint from './TPoint';
import TLine from './TLine';
import styles from './styles';

export default class TPointForcePoly extends TPoint {

    constructor(graphics, member1, member2, force1start, force2start, options = {}) {
        super(graphics, [0, 0], options);
        this.mMember1 = member1;
        this.mMember2 = member2;
        this.mMember1ForceBegin = force1start;
        this.mMember2ForceBegin = force2start;
        this.mSize = options.size || 3;
        this.mControlPoint = !!options.controlPoint;
        this.update();
    }
    
    update() {
        const x1 = this.mMember1ForceBegin.x;
        const y1 = this.mMember1ForceBegin.y;
        this.mMember1Dir = this.mMember1.direction();
        const x2 = x1 + 10.0 * Math.cos(this.mMember1Dir);
        const y2 = y1 + 10.0 * Math.sin(this.mMember1Dir);
        const x3 = this.mMember2ForceBegin.x;
        const y3 = this.mMember2ForceBegin.y;
        this.mMember2Dir = this.mMember2.direction();
        const x4 = x3 + 10.0 * Math.cos(this.mMember2Dir);
        const y4 = y3 + 10.0 * Math.sin(this.mMember2Dir);
        if (x1 == x2 && x3 == x4 && x1 == x3) {
            this.item.position = [x3, y3];
        } else {
            this.item.position = TLine.intersection(x1, y1, x2, y2, x3, y3, x4, y4);
        }
        super.update();
    }

}