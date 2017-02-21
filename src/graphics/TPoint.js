/**
 * Created by simong on 2/20/17.
 */

import GraphicEntity from './GraphicEntity';
import styles from './styles';

export default class TPoint extends GraphicEntity {

    static DEFAULT_SIZE = 10;
    static LABEL_EXTENT_WIDTH = 30;

    constructor(graphics, [x = 0, y = 0]) {
        super(graphics);
        this.mSize = TPoint.DEFAULT_SIZE;

        const c1 = graphics.addCircle([x, y], this.mSize,
            {
                fillColor: styles.controlPointColor,
                strokeColor: 'black'
            });
        const c2 = graphics.addCircle([x, y], this.mSize / 1.4,
            {
                fillColor: styles.controlPointColor,
                strokeColor: 'black'
            });

        this.item = graphics.addGroup([c1, c2]);
        this.item.onMouseDrag = this::this.onMouseDrag;
    }

    get x() {
        return this.item.position.x;
    }

    get y() {
        return this.item.position.y;
    }

}