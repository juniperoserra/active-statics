/**
 * Created by simong on 2/20/17.
 */

import Paper from './graphics/Paper';

function drawSomething() {
    // Get a reference to the canvas object
    //var canvas = document.getElementById('myCanvas');
    // Create an empty project and a view for the canvas:
    //paper.setup(canvas);
    // Create a Paper.js Path to draw a line into it:
    var path = new Paper.Path();
    // Give the stroke a color
    path.strokeColor = 'black';
    var start = new Paper.Point(100, 100);
    // Move to start and draw a line from there
    path.moveTo(start);
    // Note that the plus operator on Point objects does not work
    // in JavaScript. Instead, we need to call the add() function:
    path.lineTo(start.add([ 200, -50 ]));
    // Draw the view now:

    new Paper.Path.Circle(new Paper.Point(80, 50), 35);

    Paper.project.activeLayer.fillColor = 'red';

    Paper.view.draw();
}

export const draw = () => {
    drawSomething();
};