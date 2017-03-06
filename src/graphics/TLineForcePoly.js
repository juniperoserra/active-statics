/**
 * Created by simong on 2/20/17.
 */

import TLine from './TLine';
import styles from './styles';
import TLineMember from './TLineMember';
import util from './util';

export default class TLineForcePoly extends TLine {
    static ZERO_LENGTH = 0.3;

    constructor(graphics, startPoint, endPoint, memberStart, memberEnd, member, options = {}) {
        super(graphics, startPoint, endPoint, options);
        this.mMemberStart = memberStart;
        this.mMemberEnd = memberEnd;
        member.setForcePolyMember(this);
        this.mSize = options.size || 3;
        this.update();
    }

    update() {
        if (!this.mMemberStart || !this.mMemberEnd) {
            return;
        }
        if (this.length() <= TLineForcePoly.ZERO_LENGTH) {
            this.mCharacter = TLineMember.NONE;
            this.mColor = TLineMember.ColorZero;
        } else if (util.near(util.direction(this.mMemberStart.x, this.mMemberStart.y, this.mMemberEnd.x, this.mMemberEnd.y), this.direction(), 0.02)) {
            this.mCharacter = TLineMember.TENSILE;
            this.mColor = TLineMember.ColorTensile;
        } else {
            this.mCharacter = TLineMember.COMPRESSIVE;
            this.mColor = TLineMember.ColorCompressive;
        }
        super.update();
    }
}