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


export default class Graphics {
    constructor() {
        this.paper = Paper;
        this.paper.project.view.onMouseDown = this::this.onMouseDown;
        //this.paper.project.view.onMouseDrag = onMouseDown;
        this.paper.project.view.onFrame = this::this.onFrame;
        this.mEntities = [];
        this.mHasRecordedStartingPositions = false;
        this.mJobs = [];
        this.mLastAnimationTime = Date.now();
    }

    updateAnimations() {
        const prevTime = this.mLastAnimationTime;
        this.mLastAnimationTime = Date.now();
        const elapsed = (this.mLastAnimationTime - prevTime) / 1000.0;
        for (let job of this.mJobs) {
            job.step(elapsed);
            if (job.isDone() && job.nextJob()) {
                this.mJobs.push(job.nextJob());
            }
        }
        this.mJobs = this.mJobs.filter(job => !job.isDone());
    }

    clearJobs() {
        for (let job of this.mJobs) {
            job.complete();
        }
    }

    addJob(job) {
        if (!this.mJobs.includes(job)) {
            this.mJobs.push(job);
        }
    }

    hasJob(name) {
        return this.mJobs.some(job => job.name === name);
    }

    onFrame(event) {
        this.updateAnimations();
        for (let entity of this.mEntities) {
            entity.update();
        }
        if (!this.mHasRecordedStartingPositions) {
            for (let entity of this.mEntities) {
                entity._startingPosition = [entity.item.position.x, entity.item.position.y];
            }
            this.mHasRecordedStartingPositions = true;
        }
    }

    onMouseDown(event) {
        this._resetDrag = true;
        this._dragStartPosition = {x: event.point.x, y: event.point.y};
        for (let entity of this.mEntities) {
            entity._dragStartPosition = [entity.item.position.x, entity.item.position.y];
        }
    }

    getDragStartPosition() {
        return this._dragStartPosition;
    }
    isDragReset() {
        return !!this._resetDrag;
    }
    setDragResetOff() {
        this._resetDrag = false;
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