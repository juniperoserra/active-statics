/**
 * Created by simong on 2/20/17.
 */

import GraphicEntity from './GraphicEntity';

const hitOptions = {
    segments: true,
    stroke: true,
    fill: true,
    tolerance: 5
};

export default class TPoint extends GraphicEntity {

    static DEFAULT_SIZE = 10;
    static LABEL_EXTENT_WIDTH = 30;

    constructor(graphics, x = 0, y = 0) {
        super(graphics);
        this.mSize = TPoint.DEFAULT_SIZE;

        const c1 = graphics.addCircle([x, y], this.mSize,
            {
                fillColor: '#e1a028',
                strokeColor: 'black'
            });
        const c2 = graphics.addCircle([x, y], this.mSize / 1.4,
            {
                fillColor: '#e1a028',
                strokeColor: 'black'
            });

        this.item = graphics.addGroup([c1, c2]);

        this.item.onMouseDown = this::this.onMouseDown;
        this.item.onMouseDrag = this::this.onMouseDown;
    }

    onMouseDown(event) {
        this.item.position = event.point;
    };
}