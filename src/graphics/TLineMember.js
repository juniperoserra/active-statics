/**
 * Created by simong on 2/20/17.
 */

import styles from './styles';

export default class TLineMember extends TLine {
    static MAX_WIDTH = 60.0;
    static MIN_WIDTH = 2.0;
    static WIDTH_MULT = 0.1;

    static ColorCompressive = styles.red;
    static ColorTensile = styles.blue;
    static ColorZero = styles.yellow;

    constructor() {
        this.mIsWeightMember = false;
        this.mForcePolyMember = null; //TLineForcePoly
    }

    update() {
        super.update();
        if (!this.mForcePolyMember) {
            return;
        }
        this.item.fillColor = this.mForcePolyMember.mCharacter === 0 ?
            TLineMember.ColorZero : (this.mForcePolyMember.mCharacter === 1 ?
            TLineMember.ColorCompressive : TLineMember.ColorTensile);
        this.mSize = Math.min(Math.max(
            TLineMember.MIN_WIDTH,
            !this.mIsWeightMember ? this.mForcePolyMember.length() * TLineMember.WIDTH_MULT :
                this.mForcePolyMember.length() * this.length() / (styles.lengthDivisor * styles.lengthDivisor) * TLineMember.WIDTH_MULT)
            , TLineMember.MAX_WIDTH);
    }
}