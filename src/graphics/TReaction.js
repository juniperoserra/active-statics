/**
 * Created by simong on 2/21/17.
 */

import TArrow from './TArrow';
import styles from './styles';
import util from './util';
import TPoint from './TPoint';

export default class TReaction extends TArrow {

    constructor(graphics, start, end, options) {
        super(graphics, start, end, options);
        this.item.strokeColor = options.strokeColor || 'gray';
    }
};