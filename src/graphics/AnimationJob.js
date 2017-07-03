

export default class AnimationJob {

    step(msElapsed) {}

    isDone() {
        return true;
    }

    nextJob() {
        return null;
    }
}

export class MoveToStartJob extends AnimationJob {
    constructor(entity) {
        super();
        this.mMoveProportion = 0.1;
        this.mEntity = entity;
        this.mDone = false;
        this.mEntityWasDraggable = entity.draggable;
        entity.isAnimating = true;
    }

    step(msElapsed) {
        if (this.mDone) {
            return;
        }

        const proportion = this.mMoveProportion * (msElapsed / .02);
        let dx = this.mEntity.item.position.x - this.mEntity.startingPosition[0];
        let dy = this.mEntity.item.position.y - this.mEntity.startingPosition[1];

        if (Math.abs(dx) > 1.0) {
           dx = dx * proportion;
        }
        if (Math.abs(dy) > 1.0) {
           dy = dy * proportion;
        }

        this.mEntity.item.position = [this.mEntity.item.position.x - dx, this.mEntity.item.position.y - dy];
        this.mDone = dx === 0 && dy === 0;
        if (this.mDone) {
            this.mEntity.isAnimating = false;
        }
    }

    isDone() {
        return this.mDone;
    }
}
