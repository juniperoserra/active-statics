/**
 * Created by simong on 2/20/17.
 */

import TPoint from './TPoint';
import TButton from './TButton';
import TText from './TText';
import TTextPoint from './TTextPoint';
import TLine from './TLine';
import TLineMember from './TLineMember';
import TArrow from './TArrow';
import TLoad from './TLoad';
import TReaction from './TReaction';
import TPointTranslated from './TPointTranslated';

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

    createButton([x = 0, y = 0], text, callback, options = {}) {
        return new TButton(this.mGraphics, [x, y], text, callback, options);
    }

    createText([x = 0, y = 0], text, options = {}) {
        return new TText(this.mGraphics, [x, y], text, options);
    }

    createTextPoint(tpoint, text, options = {}) {
        return new TTextPoint(this.mGraphics, tpoint, text, options);
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

}