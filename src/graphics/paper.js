/**
 * Created by simong on 2/20/17.
 */

require("script-loader!paper");
//require("script-loader!../lib/paper/dist/paper-full");
require('../app.css');

export const init = () => {
    var canvas = document.getElementById('myCanvas');
    // Create an empty project and a view for the canvas:
    paper.setup(canvas);
};

export default paper;
