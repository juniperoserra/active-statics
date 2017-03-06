/**
 * Created by simong on 2/20/17.
 */

import TLine from './TLine';
import styles from './styles';

export default class TLineMember extends TLine {
    static MAX_WIDTH = 60.0;
    static MIN_WIDTH = 2.0;
    static WIDTH_MULT = 0.1;

    static COMPRESSIVE = 1;
    static TENSILE = -1;
    static NONE = 0;

    static ColorCompressive = styles.red;
    static ColorTensile = styles.blue;
    static ColorZero = styles.yellow;

    constructor(graphics, startPoint, endPoint, options = {}) {
        super(graphics, startPoint, endPoint, {...options, tapered: true});
        this.mIsWeightMember = false;
        this.mForcePolyMember = null; //TLineForcePoly
    }

    setForcePolyMember(member) {
        this.mForcePolyMember = member;
    }

    update() {
        if (this.mForcePolyMember) {
            this.item.fillColor = this.mForcePolyMember.mCharacter === TLineMember.NONE ?
                TLineMember.ColorZero : (this.mForcePolyMember.mCharacter === TLineMember.COMPRESSIVE ?
                TLineMember.ColorCompressive : TLineMember.ColorTensile);
            this.mSize = Math.min(Math.max(
                TLineMember.MIN_WIDTH,
                !this.mIsWeightMember ? this.mForcePolyMember.length() * TLineMember.WIDTH_MULT :
                    this.mForcePolyMember.length() * this.length() / (styles.lengthDivisor * styles.lengthDivisor) * TLineMember.WIDTH_MULT)
                , TLineMember.MAX_WIDTH);
        }
        super.update();
    }
}