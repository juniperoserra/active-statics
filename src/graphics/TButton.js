/**
 * Created by simong on 2/20/17.
 */

import GraphicEntity from './GraphicEntity';


function selectedAlpha(selected) {
    return selected ? 0.2 : 0.01;
}

export default class TButton extends GraphicEntity {

    static DEFAULT_SIZE = 12;

    constructor(graphics, [x = 0, y = 0], text, callback, options = {}) {
        super(graphics);
        this.mIsToggle = !!options.isToggle;
        this.mSelected = this.mIsToggle && !!options.selected;
        this.mCallback = callback;

        this.mText = graphics.addText([x, y], text, {
            fontSize: TButton.DEFAULT_SIZE
        });
        const bounds = this.mText.getHandleBounds().expand(24, 8);
        if (options.width) {
            bounds.width = options.width;
        }if (options.height) {
            bounds.height = options.height;
        }

        this.mRect = graphics.addRect(bounds, {
            strokeColor: 'black',
            fillColor: 'gray'
        });
        this.mRect.fillColor.alpha = selectedAlpha(this.mSelected);

        this.item = graphics.addGroup([this.mText, this.mRect]);
        this.item.onMouseDown = this::this.onMouseDown;
        this.item.onMouseDrag = this::this.onMouseDrag;
        this.item.onMouseUp = this::this.onMouseUp;
    }

    onMouseDown(event) {
        this.mRect.fillColor = 'gray';
        if (this.mIsToggle) {
            this.mWasSelected = this.mSelected;
            this.mSelected = !this.mSelected;
        }
        else {
            this.mSelected = true;
        }
        this.mRect.fillColor.alpha = selectedAlpha(this.mSelected);
    };

    onMouseDrag(event) {
        const hitItem = this.graphics.getItemHit(event.point);
        const inButton = (hitItem === this.mRect || hitItem === this.mText);
        if (this.mIsToggle) {
            this.mSelected = inButton ? !this.mWasSelected : this.mWasSelected;
        }
        else {
            this.mSelected = inButton;
        }
        this.mRect.fillColor.alpha = selectedAlpha(this.mSelected);
    };

    onMouseUp(event) {
        this.onMouseDrag(event);
        if (this.mIsToggle) {
            if (this.mCallback && this.mSelected !== this.mWasSelected) {
                this.mCallback(this.mSelected);
            }
        }
        else {
            if (this.mCallback && this.mSelected) {
                this.mCallback();
            }
            this.mSelected = false;
        }
        this.mRect.fillColor.alpha = selectedAlpha(this.mSelected);
    };
}