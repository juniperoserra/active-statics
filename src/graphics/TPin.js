/**
 * Created by simong on 2/20/17.
 */

import GraphicEntity from './GraphicEntity';
import styles from './styles';

const deg2rad = Math.PI / 180.0;

export default class TPin extends GraphicEntity {
    static DEFAULT_SIZE = 22;
    static BASE_EXTEND = 8;
    static OFFSET = 8;
    static N_GROUND_LINES = 8;
    static GROUND_LINE_LENGTH = 8;

    draw() {
        const radDir = this.mDir * deg2rad;
        let x = (this.mPoint.x - TPin.OFFSET * Math.cos(radDir));
        let y = (this.mPoint.y - TPin.OFFSET * Math.sin(radDir));
        let radDir1 = (this.mDir - 30) * deg2rad;
        const radDir2 = (this.mDir + 30) * deg2rad;
        const radDir3 = (this.mDir + 90) * deg2rad;
        let x2 = (x - this.mSize * Math.cos(radDir1));
        let y2 = (y - this.mSize * Math.sin(radDir1));
        let x3 = (x - this.mSize * Math.cos(radDir2));
        let y3 = (y - this.mSize * Math.sin(radDir2));

        this.mItems.push(this.mGraphics.addPath([[x2, y2], [x, y], [x3, y3]], { strokeColor: 'gray', strokeWidth: 2 }));

        x2 += (TPin.BASE_EXTEND * Math.cos(radDir3));
        y2 += (TPin.BASE_EXTEND * Math.sin(radDir3));
        x3 -= (TPin.BASE_EXTEND * Math.cos(radDir3));
        y3 -= (TPin.BASE_EXTEND * Math.sin(radDir3));

        this.mItems.push(this.mGraphics.addLine([x2, y2], [x3, y3], { strokeColor: 'gray', strokeWidth: 2 }));

        radDir1 = (this.mDir - 150) * deg2rad;
        const dx = (TPin.GROUND_LINE_LENGTH * Math.cos(radDir1));
        const dy = (TPin.GROUND_LINE_LENGTH * Math.sin(radDir1));

        for (let i = 1; i < TPin.N_GROUND_LINES; i++)
        {
            let gx = x2 + (((x3 - x2) / (TPin.N_GROUND_LINES * 2)) * (2 * i - 1));
            let gy = y2 + (((y3 - y2) / (TPin.N_GROUND_LINES * 2)) * (2 * i - 1));
            this.mItems.push(this.mGraphics.addLine([gx, gy], [gx+dx, gy+dy], { strokeColor: 'black' }));
        }

        this.item = this.mGraphics.addGroup(this.mItems);
        this.mOffset = [this.item.position.x - this.mPoint.x, this.item.position.y - this.mPoint.y];
    }

    constructor(graphics, aPoint = [0, 0], dir = 270, options) {
        super(graphics, options);
        this.mSize = options.size || TPin.DEFAULT_SIZE;
        this.mPoint = aPoint;
        this.mDir = dir;

        this.mItems = [];
        this.draw();
        this.item.sendToBack();
    }

    update() {
        this.item.position = [this.mPoint.x + this.mOffset[0], this.mPoint.y + this.mOffset[1]];
    }
}