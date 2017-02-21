/**
 * Created by simong on 2/20/17.
 */

import TPoint from './TPoint';
import TButton from './TButton';
import TText from './TText';
import TTextPoint from './TTextPoint';
import TLine from './TLine';

export default class Scene {

    constructor(graphics) {
        this.mGraphics = graphics;
    }

    setSize(size) {
        this.mGraphics.setSize(size);
    }

    createPoint([x = 0, y = 0]) {
        return new TPoint(this.mGraphics, [x, y]);
    }

    createButton([x = 0, y = 0], text, callback, options = {}) {
        return new TButton(this.mGraphics, [x, y], text, callback, options);
    }

    createText([x = 0, y = 0], text, options = {}) {
        return new TText(this.mGraphics, [x, y], text, options);
    }

    createTextPoint([x = 0, y = 0], text, tpoint, options = {}) {
        return new TTextPoint(this.mGraphics, [x, y], text, tpoint, options);
    }

    createLine(start, end, options = {}) {
        return new TLine(this.mGraphics, start, end, options);
    }


}