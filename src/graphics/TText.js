/**
 * Created by simong on 2/20/17.
 */

import GraphicEntity from './GraphicEntity';
import TLineMember from './TLineMember';
import util from './util';
import styles from './styles';

export default class TText extends GraphicEntity {

    static DEFAULT_SIZE = 14;

    constructor(graphics, [x = 0, y = 0], text, options = {}) {
        super(graphics, options);

        this.mPrefix = options.prefix || '';
        this.mSuffix = options.suffix || '';
        this.mText = text;
        this.mColor = options.color || 'black';
        this.item = graphics.addText([x, y], this.mPrefix + text, {
            fontSize: options.fontSize || TText.DEFAULT_SIZE,
            fillColor: this.mColor
        });
        this.mLineLength = options.lineLength;
        this.draggable = (options.draggable !== undefined) ? options.draggable : false;
    }

    update() {
        if (this.mLineLength) {
            this.mText = util.tenthStr(this.mLineLength.length() / styles.lengthDivisor);

            if (this.mLineLength.mCharacter !== undefined) {
                if (this.mLineLength.mCharacter === TLineMember.NONE) {
                    this.mColor = TLineMember.ColorZero;
                    this.mSuffix = '';
                } else if (this.mLineLength.mCharacter === TLineMember.TENSILE) {
                    this.mColor = TLineMember.ColorTensile;
                    this.mSuffix = ' T';
                } else {
                    this.mColor = TLineMember.ColorCompressive;
                    this.mSuffix = ' C';
                }

            }
        }
        this.item.fillColor = this.mColor;
        this.item.content = this.mPrefix + this.mText + this.mSuffix;
    }
};