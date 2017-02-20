/**
 * Created by simong on 2/20/17.
 */

export default class AppBase {

    constructor(scene, size) {
        this.mScene = scene;
        this.mUpdateList = [];
        this.mScene.setSize(size);
    }
};