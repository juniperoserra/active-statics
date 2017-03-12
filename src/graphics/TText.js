/**
 * Created by simong on 2/20/17.
 */

import GraphicEntity from './GraphicEntity';

export default class TText extends GraphicEntity {

    static DEFAULT_SIZE = 14;

    constructor(graphics, [x = 0, y = 0], text, options = {}) {
        super(graphics, options);

        this.mText = graphics.addText([x, y], text, {
            fontSize: options.fontSize || TText.DEFAULT_SIZE
        });
        this.item = this.mText;
        this.draggable = (options.draggable !== undefined) ? options.draggable : false;
    }
};