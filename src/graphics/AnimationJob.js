
import util from '../graphics/util';

export default class AnimationJob {

    constructor(entity, name) {
        this.mName = name;
        this.mDone = false;
        this.mEntity = entity;
        if (entity) {
            entity.isAnimating = true;
        }
    }

    get name() {
        return this.mName;
    }

    step(msElapsed) {
        this.mEntity._dragStartPosition = [this.mEntity.item.position.x, this.mEntity.item.position.y];
    }

    complete() {
        if (this.mEntity) {
            this.mEntity.isAnimating = false;
        }
        this.mDone = true;
    }

    isDone() {
        return this.mDone;
    }

    nextJob() {
        return null;
    }
}

export class MoveToStartJob extends AnimationJob {
    constructor(entity) {
        super(entity);
        this.mMoveProportion = 0.1;
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
        if (dx === 0 && dy === 0) {
            this.complete();
        }
        super.step();
    }
}

export class CircleAroundJob extends AnimationJob {
    constructor(entity, pivot, name) {
        super(entity, name);
        this.mSpeed = -0.02;
        this.mPivot = pivot;

        this.mTheta = Math.atan2(this.mEntity.item.position.y - this.mPivot.item.position.y, this.mEntity.item.position.x - this.mPivot.item.position.x);
        this.mLength = util.distance(this.mEntity.item.position.x, this.mEntity.item.position.y, this.mPivot.item.position.x,  this.mPivot.item.position.y);

    }

    step(msElapsed) {
        if (this.mDone) {
            return;
        }

        const speed = this.mSpeed * (msElapsed / .02);
        this.mTheta += speed;

        if (this.mTheta > Math.PI * 2.0) {
            this.mTheta -= Math.PI * 2.0;
        }
        if (this.mTheta < - Math.PI * 2.0) {
            this.mTheta += Math.PI * 2.0;
        }

        this.mEntity.item.position = [
            this.mPivot.item.position.x + this.mLength * Math.cos(this.mTheta),
            this.mPivot.item.position.y + this.mLength * Math.sin(this.mTheta)
        ];

        super.step();
    }
}
