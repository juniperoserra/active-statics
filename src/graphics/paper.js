/**
 * Created by simong on 2/20/17.
 */

import paper from 'paper/dist/paper-core';
import '../app.css';

// Make paper available globally for backward compatibility
window.paper = paper;

export const init = () => {
    var canvas = document.getElementById('myCanvas');
    // Create an empty project and a view for the canvas:
    paper.setup(canvas);
};

export default paper;
