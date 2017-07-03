/**
 * Created by simong on 2/20/17.
 */



const collectDragAlsoRecursive = (entity, dragSet) => {
    if (!dragSet.has(entity)) {
        dragSet.add(entity);
        for (let dragAlso of entity.mDragAlso) {
            collectDragAlsoRecursive(dragAlso, dragSet);
        }
    }
};

export default class GraphicEntity {

    constructor(graphics, options = {}) {
        this.mGraphics = graphics;
        this.mDragAlso = [];
        this.mGraphics.mEntities.push(this);
    }

    update() {}

    dragAlso(item) {
        this.mDragAlso.push(item);
    }

    onMouseDrag(event) {
        // Once we start a drag, fix the drag target. Don't let it get recalculated
        if (this.mGraphics.isDragReset()) {
            this.mGraphics.mDragEntity = this;
            this.mGraphics.setDragResetOff();
        }
        if (this.mGraphics.mDragEntity !== this) {
            return;
        }

        // Always use absolute delta from drag start instead of event delta to prevent "drift"
        const dragSet = new Set();
        collectDragAlsoRecursive(this, dragSet);
        const dragStart = this.mGraphics.getDragStartPosition();
        const delta = [event.point.x - dragStart.x, event.point.y - dragStart.y];
        for (let dragEntity of dragSet) {
            dragEntity.item.position = [dragEntity._dragStartPosition[0] + delta[0], dragEntity._dragStartPosition[1] + delta[1]];
        }
    };

    get visible() {
        return this.item.visible;
    }

    set visible(val) {
        this.item.visible = val;
    }

    set draggable(val) {
        if (this.item) {
            if (val) {
                this.item.onMouseDrag = this::this.onMouseDrag;
            }
            else {
                this.item.onMouseDrag = undefined;
            }
        }
    }

    get draggable() {
        return this.item && this.item.onMouseDrag;
    }

};