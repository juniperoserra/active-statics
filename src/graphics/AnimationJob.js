// --- START OF FILE AnimationJob.js --- ADDING JobMovePointToPoint ---

import util from '../graphics/util';

export default class AnimationJob { // Keep existing base class

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
         // Update the internal drag start position in case the animation is interrupted by a drag
        if (this.mEntity?._startingPosition && this.mEntity?.item?.position) {
           this.mEntity._dragStartPosition = [this.mEntity.x, this.mEntity.y];
        }
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

export class MoveToStartJob extends AnimationJob { // Keep existing class
    constructor(entity) {
        super(entity);
        this.mMoveProportion = 0.1;
    }

    step(msElapsed) {
        if (this.mDone) {
            return;
        }
        // Basic check to prevent animation during drag (though manager should ideally handle this)
        if (this.mEntity?.mGraphics?.selectedEntity === this.mEntity && this.mEntity?.mGraphics?.mouseDown) {
             return;
        }


        const proportion = this.mMoveProportion * (msElapsed / 0.02); // Normalize speed based on typical 50fps
        const startX = this.mEntity.startingPosition[0];
        const startY = this.mEntity.startingPosition[1];
        let currentX = this.mEntity.x;
        let currentY = this.mEntity.y;
        let newX = currentX;
        let newY = currentY;
        let xDone = false;
        let yDone = false;

        let dx = currentX - startX;
        let dy = currentY - startY;

        if (Math.abs(dx) <= 1.0) {
            newX = startX;
            xDone = true;
        } else {
            newX -= dx * proportion;
        }

        if (Math.abs(dy) <= 1.0) {
            newY = startY;
            yDone = true;
        } else {
            newY -= dy * proportion;
        }

        this.mEntity.item.position = [newX, newY];

        if (xDone && yDone) {
            this.complete();
        }
        super.step(msElapsed);
    }
}


export class JobMovePointToPoint extends AnimationJob { // <-- Added Class
    constructor(graphicsContext, entity, destination, options = {}) { // Added graphicsContext (like 'g')
        super(entity, options.name); // Pass name if provided in options
        this.g = graphicsContext; // Store reference like in Java
        this.mMovePoint = entity;
        this.mDestination = destination; // Can be a TPoint entity or an {x, y} object
        this.forceSelectMovingNode = options.forceSelectMovingNode || false;

        this.mMoveProportion = 0.1;
        this.xDone = false;
        this.yDone = false;
        // Note: afterJob logic is typically handled by the job manager/scheduler, not within the job itself.
    }

    step(msElapsed) {
        if (this.mDone) {
            return;
        }

        // Check if the user is currently dragging this point
        if (this.g?.selectedEntity === this.mMovePoint && this.g?.mouseDown) {
            // Don't animate if the user is interacting with it
             super.step(msElapsed); // Still update drag start pos
            return;
        }

        // Optionally force selection (less common in JS UI patterns, but included for translation)
        // if (this.forceSelectMovingNode || (this.g?.selectedEntity == null || !this.g?.selectedEntity.isButton())) {
        //    if (this.g) this.g.selectedEntity = this.mMovePoint;
        // }


        // Determine destination coordinates
        let xDst, yDst;
        if (typeof this.mDestination.x === 'number' && typeof this.mDestination.y === 'number') {
             // It's likely a TPoint entity or similar object with x/y properties
             xDst = this.mDestination.x;
             yDst = this.mDestination.y;
        } else if (Array.isArray(this.mDestination) && this.mDestination.length >= 2) {
             // Treat as [x, y] array
             xDst = this.mDestination[0];
             yDst = this.mDestination[1];
        } else {
             console.error("Invalid destination for JobMovePointToPoint:", this.mDestination);
             this.complete(); // Cannot proceed
             return;
        }


        const proportion = this.mMoveProportion * (msElapsed / 0.02); // Normalize speed
        let currentX = this.mMovePoint.x;
        let currentY = this.mMovePoint.y;
        let newX = currentX;
        let newY = currentY;

        if (!this.xDone) {
            const dx = currentX - xDst;
            if (Math.abs(dx) <= 1.0) {
                newX = xDst;
                this.xDone = true;
            } else {
                newX -= dx * proportion;
            }
        }

        if (!this.yDone) {
            const dy = currentY - yDst;
            if (Math.abs(dy) <= 1.0) {
                newY = yDst;
                this.yDone = true;
            } else {
                newY -= dy * proportion;
            }
        }

        this.mMovePoint.item.position = [newX, newY];

        if (this.xDone && this.yDone) {
            this.complete();
        }
        super.step(msElapsed); // Call base class step
    }
}


export class CircleAroundJob extends AnimationJob { // Keep existing class
    constructor(entity, pivot, name) {
        super(entity, name);
        this.mSpeed = -0.02;
        this.mPivot = pivot;

        this.mTheta = Math.atan2(this.mEntity.y - this.mPivot.y, this.mEntity.x - this.mPivot.x);
        this.mLength = util.distance(this.mEntity.x, this.mEntity.y, this.mPivot.x,  this.mPivot.y);

    }

    step(msElapsed) {
        if (this.mDone) {
            return;
        }
         // Basic check to prevent animation during drag
        if (this.mEntity?.mGraphics?.selectedEntity === this.mEntity && this.mEntity?.mGraphics?.mouseDown) {
             this.start(); // Recalculate length/angle if dragged
             return;
        }
        if (this.mEntity?.mGraphics?.selectedEntity === this.mPivot && this.mEntity?.mGraphics?.mouseDown) {
             this.start(); // Recalculate length/angle if pivot dragged
             return;
        }

        const speed = this.mSpeed * (msElapsed / 0.02); // Normalize speed
        this.mTheta += speed;

        if (this.mTheta > Math.PI * 2.0) {
            this.mTheta -= Math.PI * 2.0;
        }
        if (this.mTheta < - Math.PI * 2.0) {
            this.mTheta += Math.PI * 2.0;
        }

        this.mEntity.item.position = [
            this.mPivot.x + this.mLength * Math.cos(this.mTheta),
            this.mPivot.y + this.mLength * Math.sin(this.mTheta)
        ];

        super.step(msElapsed);
    }

     start() { // Added method to recalculate on drag interruption
        this.mTheta = Math.atan2(this.mEntity.y - this.mPivot.y, this.mEntity.x - this.mPivot.x);
        this.mLength = util.distance(this.mEntity.x, this.mEntity.y, this.mPivot.x,  this.mPivot.y);
    }
}

// --- END OF FILE AnimationJob.js ---