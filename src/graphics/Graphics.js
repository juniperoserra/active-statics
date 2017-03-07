/**
 * Created by simong on 2/20/17.
 */

import Paper from './Paper';


const gHitOptions = {
    segments: true,
    stroke: true,
    fill: true,
    tolerance: 5
};

/*
const onMouseDown = (event) => {
    var hitResult = Paper.project.hitTest(event.point, gHitOptions);
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
        this.paper.project.view.onFrame = this::this.onFrame;
        this.mEntities = [];
    }

    onFrame(event) {
        for (let entity of this.mEntities) {
            entity.update();
        }
    }

    setSize(size) {
        this.paper.view.setViewSize(size[0], size[1]);
    }

    getItemHit(point, hitOptions = gHitOptions) {
        var hitResult = Paper.project.hitTest(point, hitOptions);
        if (!hitResult) {
            return null;
        }
        return hitResult.item;
    }

    setStyle(item, style) {
        item.style = style;
    }

    addGroup(items) {
        return new this.paper.Group(items);
    }

    addCircle(center, radius, style) {
        const item = new this.paper.Shape.Circle(center, radius);
        if (style) {
            this.setStyle(item, style);
        }
        return item;
    }

    addText(point, text, style) {
        const item = new this.paper.PointText(point);
        item.content = text;
        if (style) {
            this.setStyle(item, style);
        }
        return item;
    }

    addRect(rect, style) {
        const item = new this.paper.Shape.Rectangle(rect);
        if (style) {
            this.setStyle(item, style);
        }
        return item;
    }

    addPath(points, style) {
        const item = new this.paper.Path(points);
        if (style) {
            this.setStyle(item, style);
        }
        return item;
    }

    addLine(from, to, style) {
        const item = new this.paper.Path.Line(from, to);
        if (style) {
            this.setStyle(item, style);
        }
        return item;
    }
}