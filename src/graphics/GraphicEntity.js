/**
 * Created by simong on 2/20/17.
 */


export default class GraphicEntity {

    constructor(graphics) {
        this.graphics = graphics;
        this.mDragAlso = [];
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

};