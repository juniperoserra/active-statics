/**
 * Created by simong on 2/20/17.
 */

import GraphicEntity from './GraphicEntity';
import styles from './styles';

export default class TPoint extends GraphicEntity {

    static DEFAULT_SIZE = 10;

    constructor(graphics, [x = 0, y = 0] = [0, 0], options = {}) {
        super(graphics);
        this.mSize = (options.size === undefined) ? TPoint.DEFAULT_SIZE : options.size;
        if (options.update) {
            const oldUpdate = this.update.bind(this);
            this.update = () => {
                options.update.bind(this)();
                oldUpdate();
            }
        }

        this.mLabelText = options.label || null;
        this.mLabelOffset = options.labelOffset || [0, -20];

        this.c1 = graphics.addCircle([x, y], this.mSize,
            {
                fillColor: styles.controlPointColor,
                strokeColor: 'black'
            });
        this.c2 = graphics.addCircle([x, y], this.mSize / 1.4,
            {
                fillColor: styles.controlPointColor,
                strokeColor: 'black'
            });

        this.item = graphics.addGroup([this.c1, this.c2]);
        this.item.onMouseDrag = this::this.onMouseDrag;
    }

    get x() {
        return this.item.position.x;
    }

    get y() {
        return this.item.position.y;
    }

    update() {
        this.c1.radius = this.mSize;
        this.c2.radius = this.mSize / 1.4;

        if (this.mLabelText && !this.mLabel) {
            this.mLabel = this.mGraphics.addText(this.mLabelOffset, this.mLabelText,
                {fontSize: styles.labelSize});
        }
        if (this.mLabel) {
            this.mLabel.content = this.mLabelText || '';
            this.mLabel.position = [this.mLabelOffset[0] + this.x,
                this.mLabelOffset[1] + this.y];
        }
    }

}