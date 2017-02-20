/**
 * Created by simong on 2/20/17.
 */

import TPoint from './TPoint';
import TButton from './TButton';

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


}