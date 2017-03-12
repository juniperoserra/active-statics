/**
 * Created by simong on 2/20/17.
 */

import GraphicEntity from './GraphicEntity';
import util from './util';
import styles from './styles';

export default class TText extends GraphicEntity {

    static DEFAULT_SIZE = 14;

    constructor(graphics, [x = 0, y = 0], text, options = {}) {
        super(graphics, options);

        this.mPrefix = options.prefix || '';
        this.mPostfix = options.postfix || '';
        this.mText = text;
        this.item = graphics.addText([x, y], this.mPrefix + text, {
            fontSize: options.fontSize || TText.DEFAULT_SIZE
        });
        this.mLineLength = options.lineLength;
        this.draggable = (options.draggable !== undefined) ? options.draggable : false;
    }

    update() {
        if (this.mLineLength) {
            this.mText = util.tenthStr(this.mLineLength.length() / styles.lengthDivisor);
        }
        this.item.content = this.mPrefix + this.mText + this.mPostfix;
    }
};