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

    constructor(graphics) {
        this.mGraphics = graphics;
        this.mDragAlso = [];
        this.mGraphics.mEntities.push(this);
    }

    update() {}

    dragAlso(item) {
        this.mDragAlso.push(item);
    }

    onMouseDrag(event) {
        const dragSet = new Set();
        collectDragAlsoRecursive(this, dragSet);
        for (let dragEntity of dragSet) {
            dragEntity.item.position = dragEntity.item.position.add(event.delta);
        }

    };

    get visible() {
        return this.item.visible;
    }

    set visible(val) {
        this.item.visible = val;
    }

};