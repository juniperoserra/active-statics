/**
 * Created by simong on 2/20/17.
 */

import TPoint from './TPoint';
import TButton from './TButton';
import TText from './TText';
import TTextPoint from './TTextPoint';
import TTextTriangle from './TTextTriangle';
import TLine from './TLine';
import TLineMember from './TLineMember';
import TArrow from './TArrow';
import TLoad from './TLoad';
import TReaction from './TReaction';
import TPointTranslated from './TPointTranslated';
import TPointForcePoly from './TPointForcePoly';
import TPointIntersect from './TPointIntersect';
import TLineForcePoly from './TLineForcePoly';
import TPin from './TPin';
import TRoller from './TRoller';

export default class Scene {

    constructor(graphics) {
        this.mGraphics = graphics;
    }

    setSize(size) {
        this.mGraphics.setSize(size);
    }

    createPoint([x = 0, y = 0], options = {}) {
        return new TPoint(this.mGraphics, [x, y], options);
    }

    createPointTranslated(base, from, to, options = {}) {
        return new TPointTranslated(this.mGraphics, base, from, to, options);
    }

    createPointForcePoly(member1, member2, force1start, force2start, options = {}) {
        return new TPointForcePoly(this.mGraphics, member1, member2, force1start, force2start, options);
    }

    createPointIntersect(line1, line2, options = {}) {
        return new TPointIntersect(this.mGraphics, line1, line2, options);
    }

    createLineForcePoly(start, end, memberStart, memberEnd, member, options = {}) {
        return new TLineForcePoly(this.mGraphics, start, end, memberStart, memberEnd, member, options);
    }

    createButton([x = 0, y = 0], text, callback, options = {}) {
        return new TButton(this.mGraphics, [x, y], text, callback, options);
    }

    createText([x = 0, y = 0], text, options = {}) {
        return new TText(this.mGraphics, [x, y], text, options);
    }

    createTextPoint(tpoint, text, options = {}) {
        return new TTextPoint(this.mGraphics, tpoint, text, options);
    }

    createTextTriangle(p1, p2, p3, text, options = {}) {
        return new TTextTriangle(this.mGraphics, p1, p2, p3, text, options);
    }

    createLine(start, end, options = {}) {
        return new TLine(this.mGraphics, start, end, options);
    }

    createMember(start, end, options = {}) {
        return new TLineMember(this.mGraphics, start, end, options);
    }

    createArrow(start, end, options = {}) {
        return new TArrow(this.mGraphics, start, end, options);
    }

    createLoad(start, end, options = {}) {
        return new TLoad(this.mGraphics, start, end, options);
    }

    createReaction(start, end, options = {}) {
        return new TReaction(this.mGraphics, start, end, options);
    }

    createPin(point = [0, 0], direction = 270, options = {}) {
        return new TPin(this.mGraphics, point, direction, options);
    }

    createRoller(point = [0, 0], direction = 270, options = {}) {
        return new TRoller(this.mGraphics, point, direction, options);
    }

}