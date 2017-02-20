/**
 * Created by simong on 2/20/17.
 */

export default class AppBase {

    constructor(graphics, size) {
        this.graphics = graphics;
        this.updateList = [];
        graphics.setSize(size[0], size[1]);
    }
};