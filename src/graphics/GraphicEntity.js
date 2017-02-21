/**
 * Created by simong on 2/20/17.
 */


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
        this.item.position = this.item.position.add(event.delta);

        for (let dragAlso of this.mDragAlso) {
            dragAlso.item.position = dragAlso.item.position.add(event.delta);
        }

    };

    get visible() {
        return this.item.visible;
    }

    set visible(val) {
        this.item.visible = val;
    }

};