/**
 * Created by simong on 2/21/17.
 */

const util = {

    distance: (x1, y1, x2, y2) => {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    },

    near: (first, second, tolerance) => {
        return Math.abs(first - second) <= tolerance;
    },

    direction: (x1, y1, x2, y2) => {
        let dir = Math.atan2(y2 - y1, x2 - x1);
        if (dir < 0) {
            dir += 6.2831855;
        }
        if (util.near(dir, 6.283185307179586, 1.0E-4)) {
            dir = 0.0;
        }
        return dir;
    }
};

export default util;