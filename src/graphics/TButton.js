/**
 * Created by simong on 2/20/17.
 */

import GraphicEntity from './GraphicEntity';

export default class TButton extends GraphicEntity {

    static DEFAULT_SIZE = 10;
    static LEFT_MARGIN = 12;

    /*
    Color mHighlightColor = new Color(180, 180, 180);
    mDrawOutline = true;
    mFont;
    mSize;
    mOldSize;
    mHitPoint = new Point();
    mText;
    mHighlight = false;
    mIsToggle = false;
    mSelected = false;
    x;
    y;
    mWidth;
    mHeight;
    mAction;
    mMetrics;
*/
    constructor(graphics, [x = 0, y = 0], text) {
        super(graphics);
        this.t = graphics.addText([x, y], text,
            {
                fontSize: 14
            });
        this.r = graphics.addRect(this.t.getHandleBounds().expand(24, 8), {
            strokeColor: 'black',
            fillColor: 'gray'
        });
        this.r.fillColor.alpha = 0.01;

        this.item = graphics.addGroup([this.t, this.r]);
        this.item.onMouseDown = this::this.onMouseDown;
        this.item.onMouseDrag = this::this.onMouseDrag;
        this.item.onMouseUp = this::this.onMouseUp;
    }

    onMouseDown(event) {
        this.r.fillColor = 'gray';
        this.r.fillColor.alpha = 0.2;
    };

    onMouseDrag(event) {
        const hitItem = this.graphics.getItemHit(event.point);
        this.r.fillColor.alpha = (hitItem === this.r || hitItem === this.t)
            ? 0.2 : 0.01;
    };

    onMouseUp(event) {
        this.r.fillColor.alpha = 0.01;
    };
}