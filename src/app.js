/**
 * Created by simong on 2/20/17.
 */
import { draw } from './mod1';
import { init } from './graphics/Paper';
import Graphics from './graphics/Graphics';


import SinglePanelApp from './apps/SinglePanelApp';

window.startApp = function() {
    init();
    draw();
    const graphics = new Graphics();
    const singlePanelApp = new SinglePanelApp(graphics);
}