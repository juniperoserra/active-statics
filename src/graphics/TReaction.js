/**
 * Created by simong on 2/21/17.
 */

import TArrow from './TArrow';
import styles from './styles';
import util from './util';
import TTextPoint from './TTextPoint';

export default class TReaction extends TArrow {
    constructor(graphics, start, end, options) {
        super(graphics, start, end, options);
        new TTextPoint(graphics, start, '', {
            offset: options.valueOffset || [0, 20], lineLength: this
        });
    }
};