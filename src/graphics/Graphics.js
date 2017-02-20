/**
 * Created by simong on 2/20/17.
 */

import Paper from './Paper';

const hitOptions = {
    segments: true,
    stroke: true,
    fill: true,
    tolerance: 5
};

/*
const onMouseDown = (event) => {
    var hitResult = Paper.project.hitTest(event.point, hitOptions);
    if (!hitResult) {
        return;
    }

    const item = hitResult.item;
    if (!item) {
        return;
    }

    item.position = event.point;
};
*/

export default class Graphics {
    constructor() {
        this.paper = Paper;
        //this.paper.project.view.onMouseDown = onMouseDown;
        //this.paper.project.view.onMouseDrag = onMouseDown;

    }

    setSize(w, h) {
        this.paper.view.setViewSize(700, 700);
    }

    setStyle(obj, style) {
        obj.style = style;
    }

    addGroup(items) {
        return new this.paper.Group(items);
    }

    addCircle(center, radius, style) {
        const c = new this.paper.Shape.Circle(center, radius);
        if (style) {
            this.setStyle(c, style);
        }
        return c;
    }
}